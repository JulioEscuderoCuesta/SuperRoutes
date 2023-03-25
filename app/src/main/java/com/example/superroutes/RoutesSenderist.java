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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.superroutes.custom_classes.MyListAdapter;
import com.example.superroutes.model.Difficulty;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class RoutesSenderist extends AppCompatActivity {

    private static final String SENDERIST_ADDED_TO_ROUTE = "You are in!";
    private static final String SENDERIST_ADDED_TO_ROUTE_ERROR = "Something bad happened... Please try again";
    private static final String ROUTE_SELECTED = "";
    private ListView list;
    private Route routeSelected;
    private ArrayList<Route> routes;
    private ArrayList<Integer> guidesIcons;
    private ArrayList<Integer> weatherIcons;
    private static FirebaseDatabase database;
    private static FirebaseUser user;
    private DatabaseReference routesInDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_routes);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        routes = new ArrayList<>();
        guidesIcons = new ArrayList<>();
        weatherIcons = new ArrayList<>();

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
                Log.d("ruta i :", routes.get(i).getName());
                Log.d("ruta i :", routes.get(i).getGuide().toString());
                Log.d("ruta i :", routes.get(i).getWhichDay().toString());
                routeSelected = routes.get(i);
                Resources res = getResources();
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.my_selector, null);
                view.setBackground(drawable);

            }
        });
    }

    private void showRoutesInformation() {
        routesInDatabase = database.getReference().child("Routes");
        ArrayList<String> routesNames = new ArrayList<>();
        MyListAdapter myListAdapter = new MyListAdapter(this, routesNames, guidesIcons, weatherIcons);
        list=findViewById(R.id.list);
        list.setAdapter(myListAdapter);

        routesInDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Route route = snapshot.getValue(Route.class);
                routes.add(route);
                routesNames.add(route.getName());
                addGuideToTheList(route);
                addDifficultyToTheList(route);
                myListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                myListAdapter.notifyDataSetChanged();

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
                weatherIcons.add(R.drawable.mountain);
                break;
            case HARD:
                weatherIcons.add(R.drawable.mountains);
                break;
            default:
                weatherIcons.add(R.drawable.ic_launcher_background);
                break;
        }

    }

    public void startRoute(View view) {
        //First add new senderist to the list
        routesInDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean done = false;
                for(DataSnapshot routesSnapshot : snapshot.getChildren()) {
                    Route routeaux = routesSnapshot.getValue(Route.class);
                    if(routeaux.equals(routeSelected)) {
                        User senderist = new User(user.getDisplayName(), user.getEmail(), user.getPhoneNumber());
                        routesInDatabase.child(routesSnapshot.getKey()).child("participants").push().setValue(senderist);
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
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra(ROUTE_SELECTED, routeSelected);
        startActivity(intent);
    }
}