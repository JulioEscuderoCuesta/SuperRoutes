package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.superroutes.custom_classes.ParticipantsInRouteGuideAdapter;
import com.example.superroutes.databinding.ActivityMainBinding;
import com.example.superroutes.databinding.ActivityRouteStartedGuideBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RouteStartedGuide extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference routeProposalStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @NonNull ActivityRouteStartedGuideBinding binding = ActivityRouteStartedGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.listOfParticipantsRouteStartedGuide.setAdapter(new ParticipantsInRouteGuideAdapter());
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        addRouteProposalStarted(getIntent().getStringExtra("route_proposal_code"));

    }

    private void addRouteProposalStarted(String routeProposalCode) {
        routeProposalStarted = database.getReference().child("RoutesProposalsStarted").child(routeProposalCode);

    }
}

