package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.custom_classes.ListAdapterRoutesGuide;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainMenuGuide extends AppCompatActivity {

    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private ImageView imagesOfRoutes;
    private int currentImageIndex = 0;
    private ArrayList<String> URLsImagesOfRoutes;
    private TextView noRoutesAssigned;
    private ListView list;
    private ArrayList<String> routesIds;
    private ArrayList<String> routesProposalsIds;
    private ArrayList<String> routesNames;
    private ArrayList<String> locationsOfRoutes;
    private ArrayList<String> numberOfParticipantsSlashTotal;
    private ArrayList<String> mainImageOfRoute;
    private ArrayList<String> datesOfRoutes;
    private Button seeRoutesDone;

    private ListAdapterRoutesGuide listAdapterRoutesGuide;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_guide);
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        seeRoutesDone = findViewById(R.id.see_previous_routes_guide_menu);
        noRoutesAssigned = findViewById(R.id.no_routes_assigned_menu_guide);
        URLsImagesOfRoutes = new ArrayList<>();
        routesIds = new ArrayList<>();
        routesProposalsIds = new ArrayList<>();
        routesNames = new ArrayList<>();
        locationsOfRoutes = new ArrayList<>();
        numberOfParticipantsSlashTotal = new ArrayList<>();
        mainImageOfRoute = new ArrayList<>();
        datesOfRoutes = new ArrayList<>();
        imagesOfRoutes = findViewById(R.id.image_main_menu_guide);
        list=findViewById(R.id.list_of_routes_for_guide);

        seeRoutesDone.setOnClickListener(view -> { Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show(); });

        preloadFirstImage();
        loadImagesOfRoutes();
        showRoutesInformation();

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            ShowInformationProposalRouteGuideFragment fragment = new ShowInformationProposalRouteGuideFragment();
            Bundle args = new Bundle();
            args.putString("route_proposal_code", routesProposalsIds.get(i));
            args.putString("route_code", routesIds.get(i));
            fragment.setArguments(args);
            fragment.show(getSupportFragmentManager(), "tag");
        });

    }

    private void preloadFirstImage() {
        if (!URLsImagesOfRoutes.isEmpty()) {
            Picasso.get().load(URLsImagesOfRoutes.get(0)).fetch();
        }
    }

    /**
     * User cliks on the "Start Route" button.
     * It launches the activity to create a new route.
     * @param view
     */
    public void createNewRoute(View view) {
        startActivity(new Intent(this, CreateNewRouteProposal.class));
    }

    private void showRoutesInformation() {
        listAdapterRoutesGuide = new ListAdapterRoutesGuide(this, routesNames, locationsOfRoutes, numberOfParticipantsSlashTotal, mainImageOfRoute, datesOfRoutes);
        list.setAdapter(listAdapterRoutesGuide);

        db.collection("RoutesProposals").whereEqualTo("idGuide", currentFirebaseUser.getUid()).addSnapshotListener((value, error) -> {
            clearList();
            if (value != null && !value.isEmpty()) {
                noRoutesAssigned.setVisibility(View.GONE);
                for (QueryDocumentSnapshot snapshot : value) {
                    RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                    if(routeProposalAux.getRouteProposalState() == RouteProposalState.PROPOSED) {
                        routesProposalsIds.add(snapshot.getId());
                        routesIds.add(routeProposalAux.getRouteId());
                        showNumberOfParticipantsSlashTotal(routeProposalAux.getParticipantsIds().size(), Integer.valueOf(routeProposalAux.getMaxParticipants()));
                        showMainImageOfRoutes(routeProposalAux);
                        datesOfRoutes.add(routeProposalAux.getWhichDay());
                        db.collection("Routes").document(routeProposalAux.getRouteId()).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Route routeAux = document.toObject(Route.class);
                                    routesNames.add(routeAux.getName());
                                    locationsOfRoutes.add(routeAux.getLocation());
                                    listAdapterRoutesGuide.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            } else {
                list.setVisibility(View.GONE);
                noRoutesAssigned.setVisibility(View.VISIBLE);
                TextView textRoutesMainMenuGuide = findViewById(R.id.text_routes_main_menu_guide);
                textRoutesMainMenuGuide.setVisibility(View.GONE);

            }

        });
    }

    private void showNumberOfParticipantsSlashTotal(int numberOfParticipants, int numberOfMaxParticipants) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(numberOfParticipants).append("/").append(numberOfMaxParticipants);
        numberOfParticipantsSlashTotal.add(stringBuilder.toString());
    }

    private void showMainImageOfRoutes(RouteProposal routeProposalAux) {
        db.collection("Routes").document(routeProposalAux.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
            Route route = documentSnapshot.toObject(Route.class);
            mainImageOfRoute.add(route.getImageURL());
        });
    }

    private void loadImagesOfRoutes() {
        db.collection("Routes").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(QueryDocumentSnapshot document: task.getResult()) {
                    Route route = document.toObject(Route.class);
                    URLsImagesOfRoutes.add(route.getImageURL());
                }
                startImageTransition();
            }
        });
    }

    //Handler to transition every 5 seconds
    private void startImageTransition() {
        if(currentImageIndex == 0)
            transitionImages();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            transitionImages();
            startImageTransition();
        }, 5000);

    }

    //Animate transition between images of routes
    private void transitionImages() {
        Animation slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        Animation slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                // Cambiar la imagen
                currentImageIndex = (currentImageIndex + 1) % URLsImagesOfRoutes.size();
                Picasso.get().load(URLsImagesOfRoutes.get(currentImageIndex)).into(imagesOfRoutes);
                imagesOfRoutes.startAnimation(slideInRight);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        imagesOfRoutes.startAnimation(slideOutLeft);
    }

    private void clearList() {
        listAdapterRoutesGuide.clear();
        routesNames.clear();
        locationsOfRoutes.clear();
        routesIds.clear();
        mainImageOfRoute.clear();
        datesOfRoutes.clear();
        numberOfParticipantsSlashTotal.clear();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            AuthUI.getInstance().signOut(getApplicationContext());
            startActivity(new Intent(MainMenuGuide.this, MainActivity.class));
        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}