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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.example.superroutes.custom_classes.ListAdapterRoutesSenderist;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenuSenderist extends AppCompatActivity {

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_senderist);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        routesIds = new ArrayList<>();
        routesProposalsIds = new ArrayList<>();
        routesNames = new ArrayList<>();
        datesOfRoutes = new ArrayList<>();
        mainImageOfRoutes = new ArrayList<>();
        routesWithGuide = new ArrayList<>();
        difficultyOfRoutes = new ArrayList<>();

        showRoutesInformation();
        Log.d("número e elementos", String.valueOf(list.getCount()));

        //Make the items clikeable
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            ShowInformationProposalRouteSenderistFragment fragment = new ShowInformationProposalRouteSenderistFragment();
            Bundle args = new Bundle();
            args.putString("route_code", routesIds.get(i));
            args.putString("route_proposal_code", routesProposalsIds.get(i));
            fragment.setArguments(args);
            fragment.show(getSupportFragmentManager(), "tag");

        });
    }

    private void showRoutesInformation() {
        routesProposalsInDatabase = database.getReference().child("RoutesProposals");
        ArrayList<String> routesNames = new ArrayList<>();
        listAdapterRoutesSenderist =
                new ListAdapterRoutesSenderist(this, routesNames, datesOfRoutes, mainImageOfRoutes, routesWithGuide, difficultyOfRoutes);
        list=findViewById(R.id.list_of_routes_for_senderist);
        list.setAdapter(listAdapterRoutesSenderist);

        routesProposalsInDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshotRouteProposal: snapshot.getChildren()) {
                    RouteProposal routeProposalAux = snapshotRouteProposal.getValue(RouteProposal.class);
                    participantsInProposals = database.getReference().child("Participants").child(snapshotRouteProposal.getKey());
                    participantsInProposals.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            hasAlreadyJoinedThisProposal = false;
                            if(snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot snapshotParticipantsInThisProposal : snapshot.getChildren()) {
                                    if (snapshotParticipantsInThisProposal.getValue(String.class).equals(user.getUid()))
                                        hasAlreadyJoinedThisProposal = true;
                                }
                            }

                            if(!hasAlreadyJoinedThisProposal) {
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
        Log.d("añadida dificultad", "hola");

    }

}