package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.superroutes.custom_classes.ParticipantsInRouteGuideAdapter;
import com.example.superroutes.databinding.ActivityPositionBinding;
import com.example.superroutes.databinding.ActivityRouteStartedGuideBinding;
import com.example.superroutes.model.Rol;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.example.superroutes.model.User;
import com.example.superroutes.model.UserInRoute;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SNIHostName;

public class RouteStarted extends AppCompatActivity {

    private FirebaseDatabase database;
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
    private Rol rolOfUser;
    private Button everybodyReadyButton;
    private ProgressBar progressBar;
    private TextView waitingForEverybodyTextView;
    private boolean routeHasStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        routeProposalCode = getIntent().getStringExtra("route_proposal_code");
        routeProposal = (RouteProposal) getIntent().getSerializableExtra("route_proposal");
        rolOfUser = Rol.valueOf(this.getIntent().getStringExtra("rol"));
        routeHasStarted = false;
        setContentView(R.layout.activity_route_started_guide_waiting_room);
        progressBar = findViewById(R.id.progressBar_route_started_guide);
        everybodyReadyButton = findViewById(R.id.button_start_route_route_started_guide);
        waitingForEverybodyTextView = findViewById(R.id.waiting_text_view_waiting_room);

        if(rolOfUser == Rol.SENDERIST) {
            everybodyReadyButton.setVisibility(View.GONE);
            waitingForEverybodyTextView.setText(R.string.waiting_text_view_waiting_room_senderist);
            pushSenderistDataToDatabase();
            waitUntilRouteStarts();
            setLocationUpdater();
        }
        else {
            progressBar.setVisibility(View.GONE);
            waitingForEverybodyTextView.setText(R.string.waiting_text_view_waiting_room_guide);
            usersInRoute = new ArrayList<>();
        }
    }

    private void waitUntilRouteStarts() {
        db.collection("RoutesProposals").document(routeProposalCode).addSnapshotListener((value, error) -> {
            RouteProposal routeProposalAux = value.toObject(RouteProposal.class);
            if (routeProposalAux.getRouteProposalState() == RouteProposalState.STARTED)
                loadSenderistInterface();
        });
    }

    private void pushSenderistDataToDatabase() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Get permissions to access to user position
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        Location firstLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        db.collection("Users").document(currentFirebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            UserInRoute userInRoute = new UserInRoute(user.getName(), 42.2, 62.2);
            db.collection("ParticipantsInRoutes").document(routeProposalCode)
                    .collection("participants").document(currentFirebaseUser.getUid()).set(userInRoute);
        });
    }

    private void loadSenderistInterface() {
        setContentView(R.layout.activity_menu_senderist_in_route);

        timeSpentInRoute = findViewById(R.id.time_spent_in_menu_senerist_in_route);

        //Set name and date of route
        TextView dateOfRoute = findViewById(R.id.date_of_route_in_menu_senerist_in_route);
        dateOfRoute.setText(routeProposal.getWhichDay());
        TextView nameOfRoute = findViewById(R.id.route_title_in_menu_senerist_in_route);

        db.collection("Routes").document(routeProposal.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
            Route routeAux = documentSnapshot.toObject(Route.class);
            nameOfRoute.setText(routeAux.getName());
        });

        //Set current time in route
        handlerLoadInterfaceSenderist = new Handler();
        Runnable updateTimer = new Runnable() {
            @Override
            public void run() {
                time++;
                hours = time / 3600;
                minutes = (time % 3600) / 60;
                seconds = time % 60;
                timeSpentInRoute.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                handlerLoadInterfaceSenderist.postDelayed(this, 1000);
            }
        };
        //Start task
        handlerLoadInterfaceSenderist.post(updateTimer);

        //Add listener to clicks
        ImageView gpsIcon = findViewById(R.id.gps_icon_in_menu_senderist_in_route);
        gpsIcon.setOnClickListener(view -> {
            startActivity(new Intent(this, Position.class));
        });

        ImageView accelerometerIcon = findViewById(R.id.accelerometer_icon_in_menu_senderist_in_route);
        accelerometerIcon.setOnClickListener(view -> {

        });
    }

    private void setLocationUpdater() {
        handlerSetLocation = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //Get permissions to access to user position
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(RouteStarted.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                db.collection("ParticipantsInRoutes").document(routeProposalCode)
                        .collection("participants").document(currentFirebaseUser.getUid())
                                .update("latitude", 52.2);
                db.collection("ParticipantsInRoutes").document(routeProposalCode)
                        .collection("participants").document(currentFirebaseUser.getUid())
                        .update("longitude", 42.2);
                handlerSetLocation.postDelayed(this, 5000);
            }
        };
        //Start task
        handlerSetLocation.post(runnable);

    }

    public void onClickEverybodyReadyButton(View view) {
        setContentView(R.layout.activity_route_started_guide);

        //Set proposal to started
        db.collection("RoutesProposals").document(routeProposalCode).update("routeProposalState", RouteProposalState.STARTED);

        CollectionReference reference = db.collection("ParticipantsInRoutes").document(routeProposalCode).collection("participants");

        /*//Get the initial data
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
        usersInRoute.clear();*/

        //Now open a listener to listen to every change
        reference.addSnapshotListener((value, error) -> {
            usersInRoute.clear();
            for(QueryDocumentSnapshot document: value) {
                UserInRoute userInRouteAux = document.toObject(UserInRoute.class);
                usersInRoute.add(userInRouteAux);
            }
            Log.d("usersInRoute", String.valueOf(usersInRoute.size()));
            @NonNull ActivityRouteStartedGuideBinding binding = ActivityRouteStartedGuideBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            binding.listOfParticipantsRouteStartedGuide.setAdapter(new ParticipantsInRouteGuideAdapter(usersInRoute));

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerLoadInterfaceSenderist.removeCallbacks(runnable);
        handlerSetLocation.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        if(rolOfUser == Rol.SENDERIST)
            startActivity(new Intent(RouteStarted.this, MyRoutesSenderist.class));
        else
            startActivity(new Intent(RouteStarted.this, MainMenuGuide.class));
    }
}

