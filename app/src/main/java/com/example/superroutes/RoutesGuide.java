package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class RoutesGuide extends AppCompatActivity {

    private ArrayList<String> routes;
    private ArrayList<Integer> weatherIcons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_guide);

    }

    public void createNewRoute(View view) {

    }
}