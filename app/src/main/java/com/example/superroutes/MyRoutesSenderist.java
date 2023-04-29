package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.superroutes.custom_classes.ListAdapterRoutesSenderist;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.example.superroutes.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyRoutesSenderist extends AppCompatActivity {

    private static final String SENDERIST_ADDED_TO_ROUTE = "You are in!";
    private static final String SENDERIST_ADDED_TO_ROUTE_ERROR = "Something bad happened... Please try again";
    private ListView list;
    private Route routeSelected;
    private boolean hasAlreadyJoinedThisProposal;
    private ArrayList<String> routesIds;
    private ArrayList<String> routesProposalsIds;

    private ArrayList<String> routesNames;
    private ArrayList<String> datesOfRoutes;
    private ArrayList<Integer> mainImageOfRoutes;
    private ArrayList<Integer> routesWithGuide;
    private ArrayList<Integer> difficultyOfRoutes;


    private ListAdapterRoutesSenderist listAdapterRoutesSenderist;
    private static FirebaseDatabase database;
    private DatabaseReference routesProposalsInDatabase;
    private DatabaseReference participantsInProposals;
    private FirebaseUser user;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes_senderist);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        routesIds = new ArrayList<>();
        routesProposalsIds = new ArrayList<>();
        routesNames = new ArrayList<>();
        datesOfRoutes = new ArrayList<>();
        mainImageOfRoutes = new ArrayList<>();
        routesWithGuide = new ArrayList<>();
        difficultyOfRoutes = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar_in_main_menu_senderist);

        routesProposalsInDatabase = database.getReference().child("RoutesProposals");
        ArrayList<String> routesNames = new ArrayList<>();
        listAdapterRoutesSenderist =
                new ListAdapterRoutesSenderist(this, routesNames, datesOfRoutes, mainImageOfRoutes, routesWithGuide, difficultyOfRoutes);
        list=findViewById(R.id.list_of_routes_for_senderist);
        list.setAdapter(listAdapterRoutesSenderist);

        showRoutesInformation();
        progressBar.setVisibility(View.GONE);

        //Make the items clikeable
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), RouteStarted.class);
            intent.putExtra("rol", "SENDERIST");
            intent.putExtra("route_proposal", routesIds.get(i));
            intent.putExtra("route_proposal_code", routesProposalsIds.get(i));
            startActivity(intent);

        });
    }

    private void showRoutesInformation() {
        routesProposalsInDatabase = database.getReference().child("RoutesProposals");
        routesProposalsInDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> routesNames = new ArrayList<>();
                listAdapterRoutesSenderist =
                        new ListAdapterRoutesSenderist(MyRoutesSenderist.this, routesNames, datesOfRoutes, mainImageOfRoutes, routesWithGuide, difficultyOfRoutes);
                list=findViewById(R.id.list_of_routes_for_senderist);
                list.setAdapter(listAdapterRoutesSenderist);
                for(DataSnapshot snapshotRouteProposal: snapshot.getChildren()) {
                    RouteProposal routeProposalAux = snapshotRouteProposal.getValue(RouteProposal.class);
                    participantsInProposals = database.getReference().child("Participants").child(snapshotRouteProposal.getKey());
                    participantsInProposals.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            hasAlreadyJoinedThisProposal = false;
                            for (DataSnapshot snapshotParticipantsInThisProposal : snapshot.getChildren()) {
                                if (snapshotParticipantsInThisProposal.getKey().equals(user.getUid())) {
                                    routesIds.add(routeProposalAux.getRouteId());
                                    routesProposalsIds.add(snapshotRouteProposal.getKey());
                                    addDateOfRoute(routeProposalAux);
                                    addImageOfRoute();
                                    addGuideOfRoute(routeProposalAux);
                                    database.getReference().child("Routes").child(routeProposalAux.getRouteId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Route route = snapshot.getValue(Route.class);
                                            routesNames.add(route.getName());
                                            addDifficultyToTheList(route);
                                            listAdapterRoutesSenderist.notifyDataSetChanged();

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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addDateOfRoute(RouteProposal routeProposal) {
        String date = routeProposal.getWhichDay().toString();
        datesOfRoutes.add(date.replace('-', '/'));
    }

    private void addImageOfRoute() {
        mainImageOfRoutes.add(R.drawable.mountain);
    }

    private void addGuideOfRoute(RouteProposal routeProposal) {
        if(routeProposal.getGuide() != null)
            routesWithGuide.add(R.drawable.icons8_tour_guide_48);
        else
            routesWithGuide.add(R.drawable.icons8_tourist_guide_50);
    }

    private void addDifficultyToTheList(Route route) {
        switch (route.getDifficulty()) {
            case EASY:
                difficultyOfRoutes.add(R.drawable.difficulty_easy);
                break;
            case MEDIUM:
                difficultyOfRoutes.add(R.drawable.difficulty_moderate);
                break;
            case HARD:
                difficultyOfRoutes.add(R.drawable.difficulty_hard);
                break;
            case EXPERIENCED:
                difficultyOfRoutes.add(R.drawable.difficulty_expert);
                break;
        }
    }

    private void emptyList() {
        routesNames.clear();
        routesProposalsIds.clear();
        datesOfRoutes.clear();
        routesWithGuide.clear();
        difficultyOfRoutes.clear();
        mainImageOfRoutes.clear();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyRoutesSenderist.this, MainMenuSenderist.class));
    }

}