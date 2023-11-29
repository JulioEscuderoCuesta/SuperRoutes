package com.example.superroutes;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.example.superroutes.model.User;
import com.example.superroutes.model.UserInRoute;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class RouteStartedSenderist extends AppCompatActivity implements SensorEventListener {
    private static final String ROUTEFINISHED = "Route finished";

    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;
    private Handler handlerLoadInterfaceSenderist;
    private Handler handlerSetLocation;
    private Runnable runnable;
    private int hours,minutes,seconds,time=0;
    private LocationManager locManager;
    private String routeProposalCode;
    private RouteProposal routeProposal;

    //Variables for the accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LocalTime newFall;
    private LocalTime lastFall;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_waiting_room_senderist);
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        routeProposalCode = getIntent().getStringExtra("route_proposal_code");
        routeProposal = (RouteProposal) getIntent().getSerializableExtra("route_proposal");

        //Listen if the guide starts the route
        db.collection("RoutesProposals").document(routeProposalCode).addSnapshotListener((value, error) -> {
            RouteProposal routeProposal = value.toObject(RouteProposal.class);
            if(routeProposal.getRouteProposalState().equals(RouteProposalState.STARTED)) {
                setContentView(R.layout.activity_route_started_senderist);
                pushSenderistDataToDatabase();
                waitUntilRouteStarts();
                //Register sensor
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                findViewById(R.id.accelerometer_icon_in_menu_senderist_in_route).setOnClickListener(view -> {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    DocumentReference reference = FirebaseFirestore.getInstance().collection("ParticipantsInRoutes").document(routeProposalCode).collection("participants")
                            .document(currentFirebaseUser.toString());
                    reference.get().addOnCompleteListener(task -> {
                        UserInRoute userInRoute = task.getResult().toObject(UserInRoute.class);
                        if(userInRoute.getAccelerometerActivated()) {
                            builder.setTitle("Are you sure you want to turn off the fall detector")
                                    .setMessage("The guide will not know if you fall")
                                    .setPositiveButton("Yes", (dialog, which) -> reference.update("accelerometer", false))
                                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                        }
                        else {
                            builder.setTitle("Turn on the accelerometer")
                                    .setMessage("The guide will know if you fall")
                                    .setPositiveButton("Yes", (dialog, which) -> reference.update("accelerometer", true))
                                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                        }
                        builder.show();
                    });
                });
            }
        });


        //Listen if the route finishes
        db.collection("RoutesProposals").document(routeProposalCode).addSnapshotListener((value, error) -> {
            RouteProposal routeProposal = value.toObject(RouteProposal.class);
            if(routeProposal.getRouteProposalState().equals(RouteProposalState.FINISHED)) {
                Toast.makeText(this, ROUTEFINISHED, Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainMenuSenderist.class));
            }
        });
    }

    //First set the actual location of the senderist in the database, previous to showing their interface
    private void pushSenderistDataToDatabase() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Get permissions to access to user position (just in case)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        Location firstLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        db.collection("Users").document(currentFirebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            UserInRoute userInRoute = new UserInRoute(user.getName(), user.getImageURL(), firstLocation.getLatitude(), firstLocation.getLongitude());
            db.collection("ParticipantsInRoutes").document(routeProposalCode)
                    .collection("participants").document(currentFirebaseUser.getUid()).set(userInRoute);
        });
    }

    //Wait until guide starts the route
    private void waitUntilRouteStarts() {
        db.collection("RoutesProposals").document(routeProposalCode).addSnapshotListener((value, error) -> {
            RouteProposal routeProposalAux = value.toObject(RouteProposal.class);
            if (routeProposalAux.getRouteProposalState() == RouteProposalState.STARTED) {
                loadSenderistInterface();
                setLocationUpdater();
            }
        });
    }

    //Once the route has started, load senderist's interface
    private void loadSenderistInterface() {
        setContentView(R.layout.activity_route_started_senderist);

        TextView timeSpentInRoute = findViewById(R.id.time_spent_in_menu_senerist_in_route);

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
        handlerLoadInterfaceSenderist.post(updateTimer);

        //Add listener to clicks
        ImageView gpsIcon = findViewById(R.id.gps_icon_in_menu_senderist_in_route);
        gpsIcon.setOnClickListener(view -> startActivity(new Intent(this, Position.class)));

        ImageView accelerometerIcon = findViewById(R.id.accelerometer_icon_in_menu_senderist_in_route);
        accelerometerIcon.setOnClickListener(view -> {

        });
    }

    //Update senderist's location once every 5 seconds
    private void setLocationUpdater() {
        handlerSetLocation = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //Get permissions to access to user position (just in case)
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(RouteStartedSenderist.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                db.collection("ParticipantsInRoutes").document(routeProposalCode)
                        .collection("participants").document(currentFirebaseUser.getUid())
                        .update("latitude", location.getLatitude());
                db.collection("ParticipantsInRoutes").document(routeProposalCode)
                        .collection("participants").document(currentFirebaseUser.getUid())
                        .update("longitude", location.getLongitude());
                handlerSetLocation.postDelayed(this, 5000);
            }
        };
        handlerSetLocation.post(runnable);
    }

    //Register changes in the accelerometer
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        newFall = LocalTime.now();
        long timeInMinutesBetweenFalls = ChronoUnit.MINUTES.between(newFall, lastFall);
        Log.d("sensorEventvalues", String.valueOf(sensorEvent.values));
        //If a fall was detected, update database and play the warning audio
        if(sensorEvent.values[1] > 10 && timeInMinutesBetweenFalls < 30) {
            db.collection("ParticipantsInRoutes").document(routeProposalCode).collection("participants").document(currentFirebaseUser.toString()).update("fall", true);
            lastFall = newFall;
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.new_fall_audio);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                int playCount = 0;

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (playCount < 4) {
                        playCount++;
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                }
            });
            mediaPlayer.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerLoadInterfaceSenderist.removeCallbacks(runnable);
        handlerSetLocation.removeCallbacks(runnable);
    }


}
