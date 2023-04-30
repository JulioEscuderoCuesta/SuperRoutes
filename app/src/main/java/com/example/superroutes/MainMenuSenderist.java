package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.superroutes.custom_classes.ListAdapterRoutesSenderist;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainMenuSenderist extends AppCompatActivity {

    private ListView list;
    private TextView noRoutesTextView;
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
        setContentView(R.layout.activity_main_menu_senderist);
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
        noRoutesTextView = findViewById(R.id.text_no_routes_proposed);
        list=findViewById(R.id.list_of_routes_for_senderist);
        noRoutesTextView.setVisibility(View.GONE);
        list.setVisibility(View.GONE);

        listAdapterRoutesSenderist =
                new ListAdapterRoutesSenderist(this, routesNames, datesOfRoutes, mainImageOfRoutes, routesWithGuide, difficultyOfRoutes);

        showRoutesInformation();
        checkRouteProposalStarted();

        //Make the items clikeable
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            ShowInformationProposalRouteSenderistFragment fragment = new ShowInformationProposalRouteSenderistFragment();
            Bundle args = new Bundle();
            args.putString("route_code", routesIds.get(i));
            args.putString("route_proposal_code", routesProposalsIds.get(i));
            args.putString("rol", "SENDERIST");
            fragment.setArguments(args);
            fragment.show(getSupportFragmentManager(), "tag");

        });
    }

    private void showRoutesInformation() {
        db.collection("RoutesProposals").whereEqualTo("routeProposalState", RouteProposalState.PROPOSED)
                //.whereNotEqualTo("idGuide", currentFirebaseUser.getUid())
                .addSnapshotListener((value, error) -> {
            clearList();
            if(value != null && !value.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                noRoutesTextView.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                list.setAdapter(listAdapterRoutesSenderist);
                boolean hasJoined = false;
                for(QueryDocumentSnapshot snapshot: value) {
                    RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                    for(String id: routeProposalAux.getParticipantsIds()) {
                        if(currentFirebaseUser.getUid().equals(id))
                            hasJoined = true;
                    }
                    if(!hasJoined) {
                        routesProposalsIds.add(snapshot.getId());
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
                    hasJoined = false;
                }
                if(routesNames.isEmpty())
                    noRoutesTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkRouteProposalStarted() {
        db.collection("RoutesProposals").whereEqualTo("routeProposalState", RouteProposalState.WAITING)
                .whereArrayContains("participantsIds", currentFirebaseUser.getUid())
                .addSnapshotListener((value, error) -> {
            if(value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot snapshot : value) {
                    RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                    showDialogRouteHasStarted(snapshot.getId(), routeProposalAux);
                }
            }
        });
    }

    private void showDialogRouteHasStarted(String idRouteProposal, RouteProposal routeProposal) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("A route you sing up for has already started!\n" +
                "Would you like to join it?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), RouteStarted.class);
                    intent.putExtra("rol", "SENDERIST");
                    intent.putExtra("route_proposal", routeProposal);
                    intent.putExtra("route_proposal_code", idRouteProposal);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
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
    public void onClickMyRoutesSenderist(View view) {
        startActivity(new Intent(this, MyRoutesSenderist.class));
    }
    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            AuthUI.getInstance().signOut(getApplicationContext());
            startActivity(new Intent(MainMenuSenderist.this, MainActivity.class));
        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}