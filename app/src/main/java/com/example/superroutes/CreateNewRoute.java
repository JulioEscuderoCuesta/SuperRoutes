package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.superroutes.custom_classes.DatePickerFragment;
import com.example.superroutes.model.Difficulty;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
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
    EditText dateOfRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        setContentView(R.layout.activity_create_new_route);
        user = FirebaseAuth.getInstance().getCurrentUser();

        spinner = findViewById(R.id.difficulty_route_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels_of_difficulty_route, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        dateOfRoute = findViewById(R.id.date_route);
        dateOfRoute.setOnClickListener(view -> showDatePicker());
    }

    public void confirmNewRoute(View view) {
        DatabaseReference routesReference = database.getReference().child("Routes");
        String key = database.getReference().child("Routes").push().getKey();

        //Get data to store in database
        String routeName = ((EditText)findViewById(R.id.name_route_text)).getText().toString();
        Difficulty difficulty = Difficulty.valueOf(spinnerValueString);
        double durationRoute = Double.parseDouble(((EditText)findViewById(R.id.duration_hours_text)).getText().toString());
        int numberParticipants = Integer.parseInt(((EditText)findViewById(R.id.max_participants_text)).getText().toString());
        User guide = new User(user.getDisplayName(), user.getEmail(), user.getPhoneNumber());
        LocalDate formatDate = getDateOfRoute();

        //Create the new route
        Route newRoute = new Route(routeName, formatDate, difficulty, numberParticipants, durationRoute, guide);
        Map<String, Object> postValues = newRoute.toMap();

        //Store new route in database
        Map<String, Object> update = new HashMap<>();
        update.put(key, postValues);
        routesReference.updateChildren(update, (error, ref) -> {
            if(error != null) {
                Log.e(TAG_ERROR_NEW_ROUTE, ERROR_NEW_ROUTE + error.getMessage());
                Toast.makeText(CreateNewRoute.this, ERROR_NEW_ROUTE, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CreateNewRoute.this, SUCCESS_NEW_ROUTE, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateNewRoute.this, RoutesGuide.class));

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        spinnerValueString = adapterView.getItemAtPosition(i).toString().toUpperCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void showDatePicker() {
        DatePickerFragment dateFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //+1 because January is zero
                final String selectedDate = day + " - " + (month+1) + " - " + year;
                dateOfRoute.setText(selectedDate);
            }
        });
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private LocalDate getDateOfRoute() {
        LocalDate formatLocalDate = LocalDate.now();
        //Change date format to store in database
        SimpleDateFormat ISO_8601_format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date formatDate = ISO_8601_format.parse(dateOfRoute.getText().toString());
            formatLocalDate = formatDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatLocalDate;
    }
}