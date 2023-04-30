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
import java.util.Arrays;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyRoutesSenderist extends AppCompatActivity {

    private ListView list;
    private ArrayList<String> routesIds;
    private ArrayList<String> routesProposalsIds;

    private ArrayList<String> routesNames;
    private ArrayList<String> datesOfRoutes;
    private ArrayList<Integer> mainImageOfRoutes;
    private ArrayList<Integer> routesWithGuide;
    private ArrayList<Integer> difficultyOfRoutes;


    private ListAdapterRoutesSenderist listAdapterRoutesSenderist;
    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes_senderist);
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        routesIds = new ArrayList<>();
        routesProposalsIds = new ArrayList<>();
        routesNames = new ArrayList<>();
        datesOfRoutes = new ArrayList<>();
        mainImageOfRoutes = new ArrayList<>();
        routesWithGuide = new ArrayList<>();
        difficultyOfRoutes = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar_in_main_menu_senderist);
        list=findViewById(R.id.list_of_routes_for_senderist);

        ArrayList<String> routesNames = new ArrayList<>();
        listAdapterRoutesSenderist =
                new ListAdapterRoutesSenderist(this, routesNames, datesOfRoutes, mainImageOfRoutes, routesWithGuide, difficultyOfRoutes);

        showRoutesInformation();
        progressBar.setVisibility(View.GONE);

        //Make the items clikeable
        /*list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), RouteStarted.class);
            intent.putExtra("rol", "SENDERIST");
            intent.putExtra("route_proposal", routesIds.get(i));
            intent.putExtra("route_proposal_code", routesProposalsIds.get(i));
            startActivity(intent);

        });*/
    }

    private void showRoutesInformation() {
        db.collection("RoutesProposals").whereEqualTo("routeProposalState", RouteProposalState.PROPOSED)
                .whereArrayContains("participantsIds", currentFirebaseUser.getUid())
                .addSnapshotListener((value, error) -> {
                    clearList();
                    if(value != null && !value.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        //noRoutesTextView.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                        list.setAdapter(listAdapterRoutesSenderist);
                        for(QueryDocumentSnapshot snapshot: value) {
                            RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                            routesIds.add(routeProposalAux.getRouteId());
                            datesOfRoutes.add(routeProposalAux.getWhichDay());
                            addImageOfRoute();
                            routesWithGuide.add(R.drawable.icons8_tour_guide_48);
                            db.collection("Routes").document(routeProposalAux.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
                                Route routeAux = documentSnapshot.toObject(Route.class);
                                routesNames.add(routeAux.getName());
                                addDifficultyToTheList(routeAux);
                                listAdapterRoutesSenderist.notifyDataSetChanged();
                            });
                        }
                    }
                    else {
                        list.setVisibility(View.GONE);
                        //noRoutesTextView.setVisibility(View.VISIBLE);
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

    private void clearList() {
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