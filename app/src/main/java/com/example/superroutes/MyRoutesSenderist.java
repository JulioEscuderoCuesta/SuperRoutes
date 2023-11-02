package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.superroutes.custom_classes.ListAdapterRoutesSenderist;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyRoutesSenderist extends AppCompatActivity {

    private ListView list;
    private TextView noRoutesTextView;
    private ArrayList<String> routesIds;
    private ArrayList<String> routesProposalsIds;

    private ArrayList<String> routesNames;
    private ArrayList<String> datesOfRoutes;
    private ArrayList<String> mainImageOfRoutes;
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
        noRoutesTextView = findViewById(R.id.text_no_routes_joined);
        noRoutesTextView.setVisibility(View.GONE);
        list.setVisibility(View.GONE);

        listAdapterRoutesSenderist =
                new ListAdapterRoutesSenderist(this, routesNames, datesOfRoutes, mainImageOfRoutes, routesWithGuide, difficultyOfRoutes);

        showRoutesInformation();
        progressBar.setVisibility(View.GONE);

        //Make the items clikeable
        /*list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), RouteStartedGuide.class);
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
                        noRoutesTextView.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                        list.setAdapter(listAdapterRoutesSenderist);
                        for(QueryDocumentSnapshot snapshot: value) {
                            RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                            routesProposalsIds.add(snapshot.getId());
                            routesIds.add(routeProposalAux.getRouteId());
                            datesOfRoutes.add(routeProposalAux.getWhichDay());
                            addImageOfRoute(routeProposalAux);
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
                        noRoutesTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void addDateOfRoute(RouteProposal routeProposal) {
        String date = routeProposal.getWhichDay().toString();
        datesOfRoutes.add(date.replace('-', '/'));
    }

    private void addImageOfRoute(RouteProposal routeProposalAux) {
        db.collection("Routes").document(routeProposalAux.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
            Route route = documentSnapshot.toObject(Route.class);
            mainImageOfRoutes.add(route.getImageURL());
        });
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