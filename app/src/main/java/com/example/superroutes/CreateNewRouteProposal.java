package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class CreateNewRouteProposal extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String ERROR_NEW_ROUTE = "There was an error creating the new route";
    private static final String SUCCESS_NEW_ROUTE = "New route created";
    private FirebaseDatabase database;
    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    String spinnerValueString;
    EditText dateOfRoute;
    Route routeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        setContentView(R.layout.activity_create_new_route_proposal);

        dateOfRoute = findViewById(R.id.date_route);
        dateOfRoute.setShowSoftInputOnFocus(false);
        dateOfRoute.setOnClickListener(view -> showDatePicker());
    }

    public void confirmNewRoute(View view) {
        int numberParticipants = Integer.parseInt(((EditText)findViewById(R.id.max_participants_text)).getText().toString());
        LocalDate formatDate = getDateOfRoute();
        String formatDateString = reverseFormat(formatDate);
        String comments = ((EditText)findViewById(R.id.comments_edit_text)).getText().toString();
        RouteProposal newProposal = new RouteProposal("-NRPcSEhgsyYGjTPhoMh", formatDateString, numberParticipants, comments, currentFireBaseUser.getUid());
        db.collection("RoutesProposals").add(newProposal).addOnCompleteListener(task -> {
            Toast.makeText(CreateNewRouteProposal.this, SUCCESS_NEW_ROUTE, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateNewRouteProposal.this, MainMenuGuide.class));
        }).addOnFailureListener(e -> Toast.makeText(CreateNewRouteProposal.this, ERROR_NEW_ROUTE, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinnerValueString = adapterView.getItemAtPosition(i).toString().toUpperCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void showDatePicker() {
        DatePickerFragment dateFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            //+1 because January is zero
            final String selectedDate = day + "/" + (month+1) + "/" + year;
            dateOfRoute.setText(selectedDate);
        });
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private LocalDate getDateOfRoute() {
        LocalDate formatLocalDate;
        //Change date format to store in database
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d/M/yyyy");
        formatLocalDate = LocalDate.parse(dateOfRoute.getText().toString(), dateFormat);
        return formatLocalDate;
    }

    private String reverseFormat(LocalDate formatDate) {
        StringBuilder stringDate = new StringBuilder();
        String splitString[] = formatDate.toString().split("-");
        stringDate
                .append(splitString[2])
                .append("/")
                .append(splitString[1])
                .append("/")
                .append(splitString[0]);
        return stringDate.toString();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?\n" +
                "The new proposal won't be saved.");
        builder.setPositiveButton("Yes", (dialog, which) -> {
                    AuthUI.getInstance().signOut(getApplicationContext());
                    startActivity(new Intent(CreateNewRouteProposal.this, MainMenuGuide.class));
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}