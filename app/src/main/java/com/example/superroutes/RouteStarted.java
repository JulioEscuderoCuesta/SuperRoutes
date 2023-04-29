package com.example.superroutes;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SNIHostName;

public class RouteStarted extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference participantsInRouteProposal;
    private DatabaseReference routeProposalStarted;
    private TextView timeSpentInRoute;
    private String routeProposalCode;
    private RouteProposal routeProposal;
    private LocationManager locManager;
    private List<UserInRoute> usersInRoute;
    private Handler handler;
    private Runnable runnable;
    private int hours,minutes,seconds,time=0;
    private Rol rolOfUser;
    private Button everybodyReadyButton;
    private ProgressBar progressBar;
    private TextView waitingForEverybodyTextView;
    private boolean routeHasStarted;
    private Runnable runnableRouteHasStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
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
            pushSenderistDataToDatabase();
            waitUntilRouteStarts();
            Handler handler = new Handler();
            runnableRouteHasStarted = () -> {
                if(routeHasStarted) {
                    loadSenderistInterface();
                    handler.removeCallbacks(runnableRouteHasStarted);
                }
            };
            handler.postDelayed(runnable, 5000);
        }
        else {
            usersInRoute = new ArrayList<>();
            loadGuideInterface();
        }
    }

    private void waitUntilRouteStarts() {
        database.getReference().child("RoutesProposals").child(routeProposalCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RouteProposal routeProposalAux = snapshot.getValue(RouteProposal.class);
                if (routeProposalAux.getRouteProposalState() == RouteProposalState.STARTED)
                    routeHasStarted = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSenderistInterface() {
        setContentView(R.layout.activity_menu_senderist_in_route);

        timeSpentInRoute = findViewById(R.id.time_spent_in_menu_senerist_in_route);

        //Set name and date of route
        TextView dateOfRoute = findViewById(R.id.date_of_route_in_menu_senerist_in_route);
        dateOfRoute.setText(routeProposal.getWhichDay().toString());
        TextView nameOfRoute = findViewById(R.id.route_title_in_menu_senerist_in_route);

        database.getReference().child("Routes").child(routeProposal.getRouteId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Route routeAux = snapshot.getValue(Route.class);
                nameOfRoute.setText(routeAux.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Set current time in route
        handler = new Handler();
        Runnable updateTimer = new Runnable() {
            @Override
            public void run() {
                time++;
                hours = time / 3600;
                minutes = (time % 3600) / 60;
                seconds = time % 60;
                timeSpentInRoute.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                handler.postDelayed(this, 1000);
            }
        };
        //Start task
        handler.post(updateTimer);

        //Add listener to clicks
        ImageView gpsIcon = findViewById(R.id.gps_icon_in_menu_senderist_in_route);
        gpsIcon.setOnClickListener(view -> {
            startActivity(new Intent(this, Position.class));
        });

        ImageView accelerometerIcon = findViewById(R.id.accelerometer_icon_in_menu_senderist_in_route);
        accelerometerIcon.setOnClickListener(view -> {

        });
    }

    private void pushSenderistDataToDatabase() {
        handler = new Handler(Looper.getMainLooper());

        //Set user as ready
        database.getReference().child("Participants").child(routeProposalCode).child(user.getUid()).child("ready").setValue(true);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Get permissions to access to user position
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        Location firstLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        database.getReference().child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                //Introduce user values in firebase
                UserInRoute userInRoute = new UserInRoute(currentUser.getName(), "42.22", "42.3333");
                database.getReference().child("RouteProposalStarted").child(routeProposalCode).child(user.getUid()).setValue(userInRoute);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                //Get permissions to access to user position
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(RouteStarted.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                database.getReference().child("RouteProposalStarted").child(routeProposalCode).child(user.getUid()).child("latitude").setValue(42.22);
                database.getReference().child("RouteProposalStarted").child(routeProposalCode).child(user.getUid()).child("longitude").setValue(42.22);
                handler.postDelayed(this, 5000);
            }
        };
        //Start task
        handler.post(runnable);


    }

    private void loadGuideInterface() {
        Button everybodyReadyButton = findViewById(R.id.button_start_route_route_started_guide);
        everybodyReadyButton.setVisibility(View.GONE);
        database.getReference().child("Participants").child(routeProposalCode).addListenerForSingleValueEvent(new ValueEventListener() {
        int numberOfParticipantsReady = 0;
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot snapshotParticipantsReady: snapshot.getChildren()) {
                //TODO
                HashMap<String, Boolean> hashMapAux = (HashMap<String, Boolean>) snapshotParticipantsReady.getValue();
                if(hashMapAux.containsValue(true))
                    numberOfParticipantsReady++;
            }
            Log.d("numberOfParticipantsReady", String.valueOf(numberOfParticipantsReady));
            Log.d("getlchildrencount", String.valueOf(snapshot.getChildrenCount()));
            if(numberOfParticipantsReady == snapshot.getChildrenCount()) {
                progressBar.setVisibility(View.GONE);
                everybodyReadyButton.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    }

    public void onClickEverybodyReadyButton(View view) {
        setContentView(R.layout.activity_route_started_guide);
        database.getReference().child("RouteProposalStarted").child(routeProposalCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userInRouteSnapshot: snapshot.getChildren()) {
                    Log.d("que hay", userInRouteSnapshot.toString());
                    UserInRoute userInRouteAux = userInRouteSnapshot.getValue(UserInRoute.class);
                    usersInRoute.add(userInRouteAux);
                }
                @NonNull ActivityRouteStartedGuideBinding binding = ActivityRouteStartedGuideBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                binding.listOfParticipantsRouteStartedGuide.setAdapter(new ParticipantsInRouteGuideAdapter(usersInRoute));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        if(rolOfUser == Rol.SENDERIST)
            startActivity(new Intent(RouteStarted.this, MyRoutesSenderist.class));
        else
            startActivity(new Intent(RouteStarted.this, MainMenuGuide.class));
    }
}

