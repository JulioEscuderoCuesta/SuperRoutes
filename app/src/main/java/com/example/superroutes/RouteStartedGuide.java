package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.custom_classes.ParticipantsInRouteGuideAdapter;
import com.example.superroutes.databinding.ActivityRouteStartedGuideBinding;
import com.example.superroutes.model.Rol;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.example.superroutes.model.User;
import com.example.superroutes.model.UserInRoute;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RouteStartedGuide extends AppCompatActivity {

    private static final String ROUTEFINISHED = "Route finished";
    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;
    private TextView timeSpentInRoute;
    private String routeProposalCode;
    private RouteProposal routeProposal;
    private LocationManager locManager;
    private List<UserInRoute> usersInRoute;
    private Handler handlerLoadInterfaceSenderist;
    private Handler handlerSetLocation;
    private Runnable runnable;
    private int hours,minutes,seconds,time=0;
    private Button everybodyReadyButton;
    private ProgressBar progressBar;


    //Route started
    private ImageView chatIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        routeProposalCode = getIntent().getStringExtra("route_proposal_code");
        routeProposal = (RouteProposal) getIntent().getSerializableExtra("route_proposal");
        setContentView(R.layout.activity_waiting_room_guide);
        everybodyReadyButton = findViewById(R.id.button_start_route_waiting_room_guide);
        usersInRoute = new ArrayList<>();
    }


    public void onClickChatIcon(View view) {
        Intent intent = new Intent(this, GroupChat.class);
        intent.putExtra("route_proposal_code", routeProposalCode);
        intent.putExtra("route_proposal", routeProposal);
        startActivity(intent);
    }

    public void onClickEverybodyReadyButton(View view) {
        setContentView(R.layout.activity_route_started_guide);

        //Set proposal to started
        db.collection("RoutesProposals").document(routeProposalCode).update("routeProposalState", RouteProposalState.STARTED);

        CollectionReference reference = db.collection("ParticipantsInRoutes").document(routeProposalCode).collection("participants");

        //Get the initial data
        reference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(QueryDocumentSnapshot snapshot: task.getResult()) {
                    UserInRoute userInRouteAux = snapshot.toObject(UserInRoute.class);
                    usersInRoute.add(userInRouteAux);
                }
                @NonNull ActivityRouteStartedGuideBinding binding = ActivityRouteStartedGuideBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                binding.listOfParticipantsRouteStartedGuide.setAdapter(new ParticipantsInRouteGuideAdapter(usersInRoute));
            }
        });
        usersInRoute.clear();

        //Now open a listener to listen to every change
        reference.addSnapshotListener((value, error) -> {
            usersInRoute.clear();
            for(QueryDocumentSnapshot document: value) {
                UserInRoute userInRouteAux = document.toObject(UserInRoute.class);
                usersInRoute.add(userInRouteAux);
            }
            @NonNull ActivityRouteStartedGuideBinding binding = ActivityRouteStartedGuideBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            binding.listOfParticipantsRouteStartedGuide.setAdapter(new ParticipantsInRouteGuideAdapter(usersInRoute));

        });
    }

    /**
     * Guide presses button Finish Route
     * Marks the proposal as finished and goes back to the menu
     * @param view
     */
    public void onClickFinishRouteButton(View view) {
        db.collection("ParticipantsInGroups").document(routeProposalCode).delete();
        DocumentReference routeProposal = db.collection("RoutesProposals").document(routeProposalCode);
        routeProposal.update("routeProposalState", RouteProposalState.FINISHED).addOnCompleteListener(task -> {
            Toast.makeText(this, ROUTEFINISHED, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainMenuGuide.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerLoadInterfaceSenderist.removeCallbacks(runnable);
        handlerSetLocation.removeCallbacks(runnable);
    }

}

