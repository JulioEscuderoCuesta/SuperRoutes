package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.superroutes.custom_classes.ListAdapterRoutesGuide;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainMenuGuide extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference routesInDatabase;
    private DatabaseReference routesProposalsInDatabase;
    private DatabaseReference senderistInRoutesDatabase;

    private ListView list;
    private ArrayList<String> routesIds;
    private ArrayList<String> routesProposalsIds;

    private ArrayList<String> routesNames;
    private ArrayList<String> numberOfParticipantsSlashTotal;
    private ArrayList<Integer> mainImageOfRoute;
    private ArrayList<String> datesOfRoutes;
    private Route routeSelected;

    private ListAdapterRoutesGuide listAdapterRoutesGuide;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_guide);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        routesInDatabase = database.getReference().child("Routes");
        routesProposalsInDatabase = database.getReference().child("RoutesProposals");
        senderistInRoutesDatabase = database.getReference().child("SenderistInRoutes");
        routesIds = new ArrayList<>();
        routesProposalsIds = new ArrayList<>();
        routesNames = new ArrayList<>();
        numberOfParticipantsSlashTotal = new ArrayList<>();
        mainImageOfRoute = new ArrayList<>();
        datesOfRoutes = new ArrayList<>();

        showRoutesInformation();

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            ShowInformationProposalRouteGuideFragment fragment = new ShowInformationProposalRouteGuideFragment();
            Bundle args = new Bundle();
            args.putString("route_proposal_code", routesProposalsIds.get(i));
            args.putString("route_code", routesIds.get(i));
            fragment.setArguments(args);
            fragment.show(getSupportFragmentManager(), "tag");
        });

    }


    /**
     * User cliks on the "Start Route" button.
     * It launches the activity to create a new route.
     * @param view
     */
    public void createNewRoute(View view) {
        startActivity(new Intent(this, CreateNewRouteProposal.class));
    }

    /**
     * User cliks on the "My Routes" button.
     * It launches the activity to see pending routes.
     * @param view
     */
    public void seeMyRoutesButton(View view) {
        startActivity(new Intent(this, CreateNewRouteProposal.class));
    }

    private void showRoutesInformation() {
        listAdapterRoutesGuide = new ListAdapterRoutesGuide(this, routesNames, numberOfParticipantsSlashTotal, mainImageOfRoute, datesOfRoutes);
        list=findViewById(R.id.list_of_routes_for_guide);
        list.setAdapter(listAdapterRoutesGuide);

        routesProposalsInDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot routesSnapshot : snapshot.getChildren()) {
                    RouteProposal routeProposalAux = routesSnapshot.getValue(RouteProposal.class);
                    routesProposalsIds.add(routesSnapshot.getKey());
                    if (routeProposalAux.getGuide().getEmail().equals(user.getEmail())) {
                        routesInDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot routeSnapshot: snapshot.getChildren()) {
                                    if(routeSnapshot.getKey().equals(routeProposalAux.getRouteId())) {
                                        Route routeAux = routeSnapshot.getValue(Route.class);
                                        routesIds.add(routeProposalAux.getRouteId());
                                        routesNames.add(routeAux.getName());
                                        int numberOfParticipants = getNumberOfParticipants(routesSnapshot.getKey());
                                        showNumberOfParticipantsSlashTotal(numberOfParticipants, routeProposalAux.getMaxParticipants());
                                        showMainImageOfRoutes();
                                        datesOfRoutes.add(routeProposalAux.getWhichDay().toString());
                                        listAdapterRoutesGuide.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
    private int getNumberOfParticipants(String key) {
        final int[] numberOfParticipants = {0};
        senderistInRoutesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot routeProposal : snapshot.getChildren()) {
                    if (routeProposal.getKey().equals(key)) {
                        numberOfParticipants[0] = Math.toIntExact(snapshot.getChildrenCount());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return numberOfParticipants[0];
    }

    private void showNumberOfParticipantsSlashTotal(int numberOfParticipants, int numberOfMaxParticipants) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(numberOfParticipants).append("/").append(numberOfMaxParticipants);
        numberOfParticipantsSlashTotal.add(stringBuilder.toString());
    }

    private void showMainImageOfRoutes() {
        mainImageOfRoute.add(R.drawable.mountain);
    }

}