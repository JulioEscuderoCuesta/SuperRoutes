package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.superroutes.custom_classes.ListAdapterRoutesSenderist;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoutesSenderist extends AppCompatActivity {

    private static final String SENDERIST_ADDED_TO_ROUTE = "You are in!";
    private static final String SENDERIST_ADDED_TO_ROUTE_ERROR = "Something bad happened... Please try again";
    private ListView list;
    private Route routeSelected;
    private ArrayList<Route> routes;
    private ArrayList<Integer> guidesIcons;
    private ArrayList<Integer> difficultyIcons;
    private static FirebaseDatabase database;
    private static FirebaseUser user;
    private DatabaseReference routesInDatabase;
    private DatabaseReference senderistInRoutesDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_senderist);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        routes = new ArrayList<>();
        guidesIcons = new ArrayList<>();
        difficultyIcons = new ArrayList<>();

        //Capture the layout's TextView and set the string as its text
        TextView welcome_text = findViewById(R.id.welcome_text);
        //Make the button disabled. No route has been selected yet
        Button selectRouteButton = findViewById(R.id.select_route_button);
        selectRouteButton.setClickable(false);

        showRoutesInformation();

        //Make the items clikeable
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!selectRouteButton.isClickable())
                    selectRouteButton.setClickable(true);
                view.setSelected(true);
                routeSelected = routes.get(i);
                Resources res = getResources();
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.my_selector, null);
                view.setBackground(drawable);

            }
        });
    }

    /**
     * User cliks on the "Start Route" button.
     * It adds the current user to the list of senderist of the route selected.
     * @param view
     */
    public void pickRoute(View view) {
        //First add new senderist to the list
        routesInDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean done = false;
                for(DataSnapshot routesSnapshot : snapshot.getChildren()) {
                    Route routeaux = routesSnapshot.getValue(Route.class);
                    if(routeaux.equals(routeSelected)) {
                        User senderist = new User(user.getDisplayName(), user.getEmail(), user.getPhoneNumber());
                        senderistInRoutesDatabase = database.getReference().child("SenderistInRoutes");
                        senderistInRoutesDatabase.child(routesSnapshot.getKey()).child(user.getUid()).setValue(senderist);
                        done = true;
                        Toast.makeText(RoutesSenderist.this, SENDERIST_ADDED_TO_ROUTE, Toast.LENGTH_SHORT).show();
                    }
                }
                if(!done)
                    Toast.makeText(RoutesSenderist.this, SENDERIST_ADDED_TO_ROUTE_ERROR, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showRoutesInformation() {
        routesInDatabase = database.getReference().child("Routes");
        ArrayList<String> routesNames = new ArrayList<>();
        ListAdapterRoutesSenderist listAdapterRoutesSenderist = new ListAdapterRoutesSenderist(this, routesNames, guidesIcons, difficultyIcons);
        list=findViewById(R.id.list);
        list.setAdapter(listAdapterRoutesSenderist);

        routesInDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Route route = snapshot.getValue(Route.class);
                routes.add(route);
                routesNames.add(route.getName());
                addGuideToTheList(route);
                addDifficultyToTheList(route);
                listAdapterRoutesSenderist.notifyDataSetChanged();

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listAdapterRoutesSenderist.notifyDataSetChanged();

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addGuideToTheList(Route route) {
        if(route.getGuide() != null)
            guidesIcons.add(R.drawable.icons8_tour_guide_48);
        else
            guidesIcons.add(R.drawable.icons8_tourist_guide_50);
    }

    private void addDifficultyToTheList(Route route) {
        switch (route.getDifficulty()) {
            case EASY:
                difficultyIcons.add(R.drawable.green_square);
                break;
            case MEDIUM:
                difficultyIcons.add(R.drawable.yellow_square);
                break;
            case HARD:
                difficultyIcons.add(R.drawable.red_square);
                break;
            case EXPERIENCED:
                difficultyIcons.add(R.drawable.black_square);
                break;
        }

    }

}