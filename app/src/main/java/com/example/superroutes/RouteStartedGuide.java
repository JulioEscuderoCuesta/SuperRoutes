package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RouteStartedGuide extends AppCompatActivity {

    private FirebaseDatabase database;

    private DatabaseReference routeProposalStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_started_guide);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        addRouteProposalStarted(getIntent().getStringExtra("route_proposal_code"));

    }

    private void addRouteProposalStarted(String routeProposalCode) {
        routeProposalStarted = database.getReference().child("RoutesProposalsStarted").child(routeProposalCode);

    }
}