package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.superroutes.custom_classes.DatePickerFragment;
import com.example.superroutes.model.Difficulty;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class CreateNewRouteProposal extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG_ERROR_NEW_ROUTE = "FirebaseUpdateError";
    private static final String ERROR_NEW_ROUTE = "There was an error creating the new route";
    private static final String SUCCESS_NEW_ROUTE = "New route created";
    private FirebaseDatabase database;
    private FirebaseUser user;

    String spinnerValueString;
    EditText dateOfRoute;
    Route routeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        setContentView(R.layout.activity_create_new_route_proposal);
        user = FirebaseAuth.getInstance().getCurrentUser();

        dateOfRoute = findViewById(R.id.date_route);
        dateOfRoute.setOnClickListener(view -> showDatePicker());
    }

    public void confirmNewRoute(View view) {
        DatabaseReference routesProposalsReference = database.getReference().child("RoutesProposals");
        String key = database.getReference().child("RoutesProposals").push().getKey();

        //Get data to store in database
        //Default route to test
        //DatabaseReference routesReference = database.getReference().child("Routes").child("-NRPcSEhgsyYGjTPhoMh");

        int numberParticipants = Integer.parseInt(((EditText)findViewById(R.id.max_participants_text)).getText().toString());
        User guide = new User(user.getDisplayName(), user.getEmail(), user.getPhoneNumber());
        LocalDate formatDate = getDateOfRoute();
        String comments = ((EditText)findViewById(R.id.comments_edit_text)).getText().toString();

        //Create new route proposal
        RouteProposal newProposal = new RouteProposal("-NRPcSEhgsyYGjTPhoMh", formatDate, numberParticipants, comments, guide);
        Map<String, Object> postValues = newProposal.toMap();

        //Store new route proposal in database
        Map<String, Object> update = new HashMap<>();
        update.put(key, postValues);
        routesProposalsReference.setValue(update);


        /*routesReference.updateChildren(update, (error, ref) -> {
            if(error != null) {
                Log.e(TAG_ERROR_NEW_ROUTE, ERROR_NEW_ROUTE + error.getMessage());
                Toast.makeText(CreateNewRouteProposal.this, ERROR_NEW_ROUTE, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CreateNewRouteProposal.this, SUCCESS_NEW_ROUTE, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("route_proposal_code", key);
                startActivity(new Intent(CreateNewRouteProposal.this, WatchRouteInformation.class));

            }
        });*/
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
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                dateOfRoute.setText(selectedDate);
            }
        });
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private LocalDate getDateOfRoute() {
        LocalDate formatLocalDate = LocalDate.now();
        //Change date format to store in database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date formatDate = dateFormat.parse(dateOfRoute.getText().toString());
            formatLocalDate = formatDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatLocalDate;
    }
}