package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.superroutes.custom_classes.ListAdapterRoutesGuide;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class MainMenuGuide extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;

    private DatabaseReference routesInDatabase;
    private DatabaseReference routesProposalsInDatabase;
    private DatabaseReference participants;

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
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        routesInDatabase = database.getReference().child("Routes");
        routesProposalsInDatabase = database.getReference().child("RoutesProposals");
        participants = database.getReference().child("Participants");
        routesIds = new ArrayList<>();
        routesProposalsIds = new ArrayList<>();
        routesNames = new ArrayList<>();
        numberOfParticipantsSlashTotal = new ArrayList<>();
        mainImageOfRoute = new ArrayList<>();
        datesOfRoutes = new ArrayList<>();
        list=findViewById(R.id.list_of_routes_for_guide);

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            ShowInformationProposalRouteGuideFragment fragment = new ShowInformationProposalRouteGuideFragment();
            Bundle args = new Bundle();
            args.putString("route_proposal_code", routesProposalsIds.get(i));
            args.putString("route_code", routesIds.get(i));
            fragment.setArguments(args);
            fragment.show(getSupportFragmentManager(), "tag");
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        showRoutesInformation();
    }


    /**
     * User cliks on the "Start Route" button.
     * It launches the activity to create a new route.
     * @param view
     */
    public void createNewRoute(View view) {
        startActivity(new Intent(this, CreateNewRouteProposal.class));
    }

    private void showRoutesInformation() {
        listAdapterRoutesGuide = new ListAdapterRoutesGuide(this, routesNames, numberOfParticipantsSlashTotal, mainImageOfRoute, datesOfRoutes);
        list.setAdapter(listAdapterRoutesGuide);
        db.collection("RoutesProposals").whereEqualTo("idGuide", currentFirebaseUser.getUid()).addSnapshotListener((value, error) -> {
            clearList();
            if(value != null && !value.isEmpty()) {
                for(QueryDocumentSnapshot snapshot: value) {
                    RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                    Log.d("routeProposalAux", routeProposalAux.getWhichDay());
                    routesProposalsIds.add(snapshot.getId());
                    routesIds.add(routeProposalAux.getRouteId());
                    showNumberOfParticipantsSlashTotal(routeProposalAux.getParticipantsIds().size(), routeProposalAux.getMaxParticipants());
                    showMainImageOfRoutes();
                    datesOfRoutes.add(routeProposalAux.getWhichDay());
                    db.collection("Routes").document(routeProposalAux.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
                        Route routeAux = documentSnapshot.toObject(Route.class);
                        routesNames.add(routeAux.getName());
                        listAdapterRoutesGuide.notifyDataSetChanged();
                    });
                }
            } else {
                Log.d("no hay nada", "hola");
            }
        });
    }

    private void showNumberOfParticipantsSlashTotal(int numberOfParticipants, int numberOfMaxParticipants) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(numberOfParticipants).append("/").append(numberOfMaxParticipants);
        numberOfParticipantsSlashTotal.add(stringBuilder.toString());
    }

    private void showMainImageOfRoutes() {
        mainImageOfRoute.add(R.drawable.mountain);
    }

    private void clearList() {
        listAdapterRoutesGuide.clear();
        routesNames.clear();
        routesIds.clear();
        mainImageOfRoute.clear();
        datesOfRoutes.clear();
        numberOfParticipantsSlashTotal.clear();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            AuthUI.getInstance().signOut(getApplicationContext());
            startActivity(new Intent(MainMenuGuide.this, MainActivity.class));
        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}