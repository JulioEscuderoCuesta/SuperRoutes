package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.superroutes.model.Difficulty;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateNewRoute extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG_ERROR_NEW_ROUTE = "FirebaseUpdateError";
    private static final String ERROR_NEW_ROUTE = "There was an error creating the new route";
    private static final String SUCCESS_NEW_ROUTE = "New route created";
    private FirebaseDatabase database;
    private DatabaseReference routes;
    private FirebaseUser user;

    Spinner spinner;
    String spinnerValueString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);
        user = FirebaseAuth.getInstance().getCurrentUser();

        spinner = findViewById(R.id.difficulty_route_spinner);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.levels_of_difficulty_route, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);


    }

    public void confirmNewRoute(View view) {
        DatabaseReference routesReference = database.getReference().child("Routes");
        String routeName = findViewById(R.id.name_route_text).toString();
        Difficulty difficulty = Difficulty.valueOf(spinnerValueString);
        double durationRoute = Double.valueOf(findViewById(R.id.duration_hours_text).toString());
        int numberParticipants = Integer.valueOf(findViewById(R.id.max_participants_text).toString());
        DatePicker datepicker = findViewById(R.id.datepicker_new_route);
        int year = datepicker.getYear();
        int month = datepicker.getMonth();
        int day = datepicker.getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        User currentUser = new User(user.getDisplayName(), user.getEmail(), user.getPhoneNumber());

        Date dateRoute = calendar.getTime();

        String key = database.getReference().child("Routes").push().getKey();

        Route newRoute = new Route(routeName, dateRoute, difficulty, numberParticipants, durationRoute, currentUser);
        Map<String, Object> postValues = newRoute.toMap();

        Map<String, Object> update = new HashMap<>();
        update.put("/Routes/" + key, postValues);
        routesReference.updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error != null) {
                    Log.e(TAG_ERROR_NEW_ROUTE, ERROR_NEW_ROUTE + error.getMessage());
                    Toast.makeText(CreateNewRoute.this, ERROR_NEW_ROUTE, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CreateNewRoute.this, SUCCESS_NEW_ROUTE, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateNewRoute.this, CreateNewRoute.class));
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinnerValueString = adapterView.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}