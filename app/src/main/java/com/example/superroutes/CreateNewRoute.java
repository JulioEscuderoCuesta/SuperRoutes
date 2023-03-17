package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.superroutes.model.Route;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNewRoute extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference routes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);
    }

    public void confirmNewRoute(View view) {
        String key = database.getReference().child("Routes").push().getKey();

        //Route route = new Route()
        startActivity(new Intent(this, CreateNewRoute.class));
    }
}