package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.custom_classes.DatePickerFragment;
import com.example.superroutes.custom_classes.ListAdapterRoutesAvailable;
import com.example.superroutes.interfaces.DiffcultyInterface;
import com.example.superroutes.interfaces.OnItemRecyclerViewClickListener;
import com.example.superroutes.model.Rol;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteInformation;
import com.example.superroutes.model.RouteProposal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDate;

public class CreateNewRouteProposal extends AppCompatActivity implements OnItemRecyclerViewClickListener, DiffcultyInterface {

    private static final String NO_ROUTE_SELECTED = "You need to select a route";
    private static final String NO_DAY_OR_PARTICIPANTS = "You need to specify a day and the number of participants";
    private static final String ERROR_NEW_ROUTE = "There was an error creating the new route";
    private static final String SUCCESS_NEW_ROUTE = "New route created";
    private FirebaseDatabase database;
    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<RouteInformation> routesAvailable;
    private ArrayList<RouteInformation> searchList;

    private ImageView imageRouteSelected;
    private TextView nameRouteSelected;
    private TextView locationRouteSelected;
    private ImageView difficultyRouteSelected;

    //List of items for the adapter
    public ArrayList<String> namesOfRoutes;
    public ArrayList<String> locationsOfRoutes;
    public ArrayList<Integer> difficultiesOfRoutes;
    public ArrayList<Integer> imagesOfRoutes;
    private EditText dateOfRoute;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        setContentView(R.layout.activity_create_new_route_proposal);

        searchView = findViewById(R.id.searchView_my_groups);
        recyclerView = findViewById(R.id.recyclerView_my_groups);
        imageRouteSelected = findViewById(R.id.image_route_selected);
        nameRouteSelected = findViewById(R.id.name_route_selected);
        locationRouteSelected = findViewById(R.id.location_route_selected);
        difficultyRouteSelected = findViewById(R.id.difficulty_route_selected);

        imageRouteSelected.setVisibility(View.GONE);
        nameRouteSelected.setVisibility(View.GONE);
        locationRouteSelected.setVisibility(View.GONE);
        difficultyRouteSelected.setVisibility(View.GONE);

        routesAvailable = new ArrayList<>();
        searchList = new ArrayList<>();

        recyclerView.setVisibility(View.INVISIBLE);

        namesOfRoutes = new ArrayList<>();
        locationsOfRoutes = new ArrayList<>();
        imagesOfRoutes = new ArrayList<>();
        difficultiesOfRoutes = new ArrayList<>();

        groupId = getIntent().getStringExtra("group_id");
        if(groupId != null) {
            findViewById(R.id.max_participants_text).setVisibility(View.GONE);
            findViewById(R.id.max_participants_checkbox).setVisibility(View.GONE);
        }

        getRoutes();
        addSearchViewListener();

        dateOfRoute = findViewById(R.id.date_route);
        dateOfRoute.setShowSoftInputOnFocus(true);
        dateOfRoute.setOnClickListener(view -> showDatePicker());
    }

    public void confirmNewRoute(View view) {
        EditText numberParticipantsEditText = findViewById(R.id.max_participants_text);

        if(imageRouteSelected.getVisibility() == View.GONE)
            Toast.makeText(this, NO_ROUTE_SELECTED, Toast.LENGTH_SHORT).show();

        else if (groupId == null && (numberParticipantsEditText.getText().toString().isEmpty()
                || dateOfRoute.getText().toString().isEmpty())) {
            Toast.makeText(this, NO_DAY_OR_PARTICIPANTS, Toast.LENGTH_SHORT).show();
        }

        else {
            LocalDate formatDate = getDateOfRoute();
            String formatDateString = reverseFormat(formatDate);
            String comments = ((EditText) findViewById(R.id.comments_edit_text)).getText().toString();

            db.collection("Routes").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        Route route = document.toObject(Route.class);
                        if(route.getName() == nameRouteSelected.getText().toString() && route.getLocation() == locationRouteSelected.getText().toString()) {
                            String idRouteSelected = document.getId();
                            RouteProposal newProposal;
                            if(groupId != null)
                                newProposal = new RouteProposal(idRouteSelected, formatDateString, 10000, comments, null, groupId);
                            else {
                                int numberParticipants = Integer.parseInt(((EditText) findViewById(R.id.max_participants_text)).getText().toString());
                                newProposal = new RouteProposal(idRouteSelected, formatDateString, numberParticipants, comments, currentFireBaseUser.getUid(), null);
                            }

                            db.collection("RoutesProposals").add(newProposal).addOnSuccessListener(documentReference -> {
                                Toast.makeText(CreateNewRouteProposal.this, SUCCESS_NEW_ROUTE, Toast.LENGTH_SHORT).show();
                                if(groupId != null) {
                                    db.collection("Groups").document(groupId).update("listOfIdsRoutesProposals", FieldValue.arrayUnion(documentReference.getId()));
                                    Intent intent = new Intent(this, GroupChat.class);
                                    intent.putExtra("group_id", groupId);
                                    intent.putExtra("rol", String.valueOf(Rol.SENDERIST));
                                    startActivity(intent);
                                }
                                else
                                    startActivity(new Intent(this, MainMenuGuide.class));
                            }).addOnFailureListener(e ->
                                    Toast.makeText(CreateNewRouteProposal.this, ERROR_NEW_ROUTE, Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            });

        }
    }

    private void getRoutes() {
        db.collection("Routes").addSnapshotListener((value, error) -> {
            if(value != null && !value.isEmpty()) {
                for(QueryDocumentSnapshot snapshot: value) {
                    Route route = snapshot.toObject(Route.class);
                    RouteInformation routeInformation = new RouteInformation(route.getName(), route.getLocation(), route.getDifficulty().toString(), route.getImageURL());
                    routesAvailable.add(routeInformation);
                }
            }
        });
    }

    private void addSearchViewListener() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter listAdapterRoutesAvailable = new ListAdapterRoutesAvailable(this, routesAvailable);
        recyclerView.setAdapter(listAdapterRoutesAvailable);

        ((ListAdapterRoutesAvailable) listAdapterRoutesAvailable).setOnItemRecyclerViewClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                recyclerView.setVisibility(View.VISIBLE);
                searchList.clear();
                if(query.length() > 0) {
                    for(int i = 0; i < routesAvailable.size(); i++) {
                        if(routesAvailable.get(i).getName().toUpperCase().contains(query.toUpperCase())) {
                            RouteInformation routeInformation = new RouteInformation();
                            routeInformation.setName(routesAvailable.get(i).getName());
                            routeInformation.setLocation(routesAvailable.get(i).getLocation());
                            routeInformation.setImageURL(routesAvailable.get(i).getImageURL());
                            routeInformation.setDifficulty(routesAvailable.get(i).getDifficulty());
                            searchList.add(routeInformation);
                        }
                    }
                     ((ListAdapterRoutesAvailable) listAdapterRoutesAvailable).updateData(searchList);

                }
                else if(query.length() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    searchList.clear();
                    ((ListAdapterRoutesAvailable) listAdapterRoutesAvailable).updateData(routesAvailable);

                }
                listAdapterRoutesAvailable.notifyDataSetChanged();
                return false;
            }
        });
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
    public void onItemClick(RouteInformation route) {
        //Remove text from searchView
        searchView.setQuery("", false);

        searchView.clearFocus();

        Picasso.get().load(route.getImageURL()).into(imageRouteSelected);
        nameRouteSelected.setText(route.getName());
        locationRouteSelected.setText(route.getLocation());
        difficultyRouteSelected.setImageResource(getDifficultyDrawable(route.getDifficulty()));

        recyclerView.setVisibility(View.GONE);
        imageRouteSelected.setVisibility(View.VISIBLE);
        nameRouteSelected.setVisibility(View.VISIBLE);
        locationRouteSelected.setVisibility(View.VISIBLE);
        difficultyRouteSelected.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?\n" +
                "The new proposal won't be saved.");
        builder.setPositiveButton("Yes", (dialog, which) -> {
                    if(groupId == null) {
                        startActivity(new Intent(CreateNewRouteProposal.this, MainMenuGuide.class));
                    }
                    else {
                        Intent intent = new Intent(this, GroupChat.class);
                        intent.putExtra("group_id", groupId);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}