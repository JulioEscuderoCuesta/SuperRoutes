package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.superroutes.custom_classes.ParticipantsInRouteGuideAdapter;
import com.example.superroutes.databinding.ActivityRouteStartedGuideBinding;
import com.example.superroutes.model.Rol;
import com.example.superroutes.model.UserInRoute;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RouteStarted extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference participantsInRouteProposal;
    private DatabaseReference routeProposalStarted;
    private LocationManager locManager;
    private int numberOfParticipants = 0;
    private List<UserInRoute> usersInRoute;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String routeProposalCode = getIntent().getStringExtra("route_proposal_code");
        Rol rolOfUser = Rol.valueOf(this.getIntent().getStringExtra("rol"));
        if(rolOfUser == Rol.SENDERIST)
            pushSenderistDataToDatabase(routeProposalCode);
        else {
            usersInRoute = new ArrayList<>();
            loadGuideInterface(routeProposalCode);
        }



    }

    private void pushSenderistDataToDatabase(String routeProposalCode) {
        setContentView(R.layout.activity_menu_senderist_in_route);
        handler = new Handler(Looper.getMainLooper());

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Get permissions to access to user position
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        Location firstLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Introduce user values in firebase
        UserInRoute userInRoute = new UserInRoute(user.getDisplayName(), firstLocation.getLatitude(), firstLocation.getLongitude());
        database.getReference().child("RouteProposalStarted").child(routeProposalCode).child(user.getUid()).setValue(userInRoute);

        runnable = new Runnable() {
            @Override
            public void run() {
                //Get permissions to access to user position
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(RouteStarted.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                database.getReference().child("RouteProposalStarted").child(routeProposalCode).child(user.getUid()).child("latitude").setValue(location.getLatitude());
                database.getReference().child("RouteProposalStarted").child(routeProposalCode).child(user.getUid()).child("longitude").setValue(location.getLongitude());
                handler.postDelayed(this, 5000);
            }
        };
        //Start task
        handler.post(runnable);


    }

    private void loadGuideInterface(String routeProposalCode) {
        database.getReference().child("Participants").child(routeProposalCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numberOfParticipants = (int) snapshot.getChildrenCount();
                Log.d("numberOfParticipants", String.valueOf(numberOfParticipants));
                database.getReference().child("RouteProposalStarted").child(routeProposalCode).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("snapshot es", snapshot.getKey());
                        for(DataSnapshot userInRouteSnapshot: snapshot.getChildren()) {
                            UserInRoute userInRouteAux = userInRouteSnapshot.getValue(UserInRoute.class);
                            usersInRoute.add(userInRouteAux);
                        }
                        Log.d("cuantos participantes", String.valueOf(usersInRoute.size()));
                        if(numberOfParticipants == usersInRoute.size()) {
                            @NonNull ActivityRouteStartedGuideBinding binding = ActivityRouteStartedGuideBinding.inflate(getLayoutInflater());
                            setContentView(binding.getRoot());
                            Log.d("cuantos participantes", String.valueOf(usersInRoute.size()));
                            binding.listOfParticipantsRouteStartedGuide.setAdapter(new ParticipantsInRouteGuideAdapter(usersInRoute));
                            Log.d("numbertOfParticipants es ahora: ", String.valueOf(numberOfParticipants));
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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


}

