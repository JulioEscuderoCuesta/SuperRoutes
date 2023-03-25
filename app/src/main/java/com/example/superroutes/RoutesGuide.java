package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.superroutes.custom_classes.ListAdapterRoutesGuide;
import com.example.superroutes.custom_classes.ListAdapterRoutesSenderist;
import com.example.superroutes.model.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RoutesGuide extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference routesInDatabase;
    private DatabaseReference senderistInRoutesDatabase;

    private ListView list;
    private ArrayList<Route> routes;
    private ArrayList<String> numberOfSenderist;
    private ArrayList<Integer> weatherIcons;
    private Route routeSelected;

    private ListAdapterRoutesGuide listAdapterRoutesGuide;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_guide);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        routesInDatabase = database.getReference().child("Routes");
        senderistInRoutesDatabase = database.getReference().child("SenderistInRoutes");
        routes = new ArrayList<>();
        numberOfSenderist = new ArrayList<>();
        weatherIcons = new ArrayList<>();

        showRoutesInformation();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                routeSelected = routes.get(i);
            }
        });

    }

    /**
     * User cliks on the "Start Route" button.
     * It launches the activity to create a new route.
     * @param view
     */
    public void createNewRoute(View view) {
        startActivity(new Intent(this, CreateNewRoute.class));
    }


    private void showRoutesInformation() {
        ArrayList<String> routesNames = new ArrayList<>();
        listAdapterRoutesGuide = new ListAdapterRoutesGuide(this, routesNames, numberOfSenderist, weatherIcons);
        list=findViewById(R.id.list_of_routes_for_guide);
        list.setAdapter(listAdapterRoutesGuide);

        routesInDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot routesSnapshot : snapshot.getChildren()) {
                    Route routeaux = routesSnapshot.getValue(Route.class);
                    if (routeaux.getGuide().getEmail().equals(user.getEmail())) {
                        routesNames.add(routeaux.getName());
                        numberOfSenderist.add("5");
                        showNumberOfSenderist(routesSnapshot.getKey());
                        addWeatherIconsToTheList(routeaux);
                        listAdapterRoutesGuide.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void showNumberOfSenderist(String key) {
        senderistInRoutesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                    if (routeSnapshot.getKey().equals(key)) {
                        HashMap<String, Object> objectHashMap = (HashMap<String, Object>) routeSnapshot.getValue();
                        numberOfSenderist.remove(numberOfSenderist.size() - 1);
                        numberOfSenderist.add(String.valueOf(objectHashMap.size()));
                        listAdapterRoutesGuide.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addWeatherIconsToTheList(Route route) {
        switch (route.getDifficulty()) {
            case EASY:
                weatherIcons.add(R.drawable.green_square);
                break;
            case MEDIUM:
                weatherIcons.add(R.drawable.yellow_square);
                break;
            case HARD:
                weatherIcons.add(R.drawable.red_square);
                break;
            case EXPERIENCED:
                weatherIcons.add(R.drawable.black_square);
                break;
        }
    }
}