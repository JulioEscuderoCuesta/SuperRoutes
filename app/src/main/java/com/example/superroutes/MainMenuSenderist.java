package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.superroutes.custom_classes.ListAdapterRoutesSenderist;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class MainMenuSenderist extends AppCompatActivity {

    private ImageView imagesOfRoutes;
    private int currentImageIndex = 0;
    private TextView welcomeText;
    private ArrayList<String> URLsImagesOfRoutes;
    private ListView list;
    private TextView noRoutesTextView;
    private ArrayList<String> routesIds;
    private ArrayList<String> routesProposalsIds;
    private ArrayList<String> routesNames;
    private ArrayList<String> datesOfRoutes;
    private ArrayList<String> mainImageOfRoutes;
    private ArrayList<Integer> routesWithGuide;
    private ArrayList<Integer> difficultyOfRoutes;
    private ListAdapterRoutesSenderist listAdapterRoutesSenderist;
    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_senderist);
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        welcomeText = findViewById(R.id.title_text_menu_senderist);
        routesIds = new ArrayList<>();
        routesProposalsIds = new ArrayList<>();
        URLsImagesOfRoutes = new ArrayList<>();
        routesNames = new ArrayList<>();
        datesOfRoutes = new ArrayList<>();
        mainImageOfRoutes = new ArrayList<>();
        routesWithGuide = new ArrayList<>();
        difficultyOfRoutes = new ArrayList<>();
        imagesOfRoutes = findViewById(R.id.image_main_menu_senderist);
        progressBar = findViewById(R.id.progressBar_in_main_menu_senderist);
        noRoutesTextView = findViewById(R.id.text_no_routes_proposed);
        list=findViewById(R.id.list_of_routes_main_menu_senderist);

        noRoutesTextView.setVisibility(View.GONE);

        preloadFirstImage();
        loadImagesOfRoutes();
        showRoutesInformation();
        checkRouteProposalStarted();

        //Make the items clikeable
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            ShowInformationProposalRouteSenderistFragment fragment = new ShowInformationProposalRouteSenderistFragment();
            Bundle args = new Bundle();
            args.putString("route_code", routesIds.get(i));
            args.putString("route_proposal_code", routesProposalsIds.get(i));
            args.putString("rol", "SENDERIST");
            fragment.setArguments(args);
            fragment.show(getSupportFragmentManager(), "tag");

        });
    }

    private void preloadFirstImage() {
        if (!URLsImagesOfRoutes.isEmpty()) {
            Picasso.get().load(URLsImagesOfRoutes.get(0)).fetch();
        }
    }

    public void onClickMyRoutesSenderist(View view) {
        startActivity(new Intent(this, MyRoutesSenderist.class));
    }

    public void onClickMyGroupsSenderist(View view) {
        startActivity(new Intent(this, MyGroupsSenderist.class));
    }

    private void showRoutesInformation() {
        db.collection("RoutesProposals").whereEqualTo("routeProposalState", RouteProposalState.PROPOSED)
                .addSnapshotListener((value, error) -> {
            clearList();
            boolean routesAvailable = false;
            if(value != null && !value.isEmpty()) {
                Log.d("value", value.getDocuments().toString());
                progressBar.setVisibility(View.GONE);
                noRoutesTextView.setVisibility(View.GONE);

                welcomeText.setVisibility(View.VISIBLE);

                listAdapterRoutesSenderist =
                        new ListAdapterRoutesSenderist(this, routesNames, datesOfRoutes, mainImageOfRoutes, routesWithGuide, difficultyOfRoutes);
                list.setVisibility(View.VISIBLE);
                list.setAdapter(listAdapterRoutesSenderist);

                boolean hasJoined = false, isGuide = false, isGroup = false;
                for(QueryDocumentSnapshot snapshot: value) {
                    RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                    for(String id: routeProposalAux.getParticipantsIds()) {
                        if(currentFirebaseUser.getUid().equals(id)) {
                            hasJoined = true;
                            Log.d("hasJoined", "hasJoined");
                        }
                    }

                    if(routeProposalAux.getIdGuide() != null && routeProposalAux.getIdGuide().equals(currentFirebaseUser.getUid()))
                        isGuide = true;
                    if(routeProposalAux.getIdGroup() != null)
                        isGroup = true;

                    if(!hasJoined && !isGroup && !isGuide) {
                        Log.d("routesAvailable", "is true");
                        routesAvailable = true;
                        routesProposalsIds.add(snapshot.getId());
                        routesIds.add(routeProposalAux.getRouteId());
                        datesOfRoutes.add(routeProposalAux.getWhichDay());
                        addImageOfRoute(routeProposalAux);
                        routesWithGuide.add(R.drawable.icons8_tour_guide_48);
                        db.collection("Routes").document(routeProposalAux.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
                            Route routeAux = documentSnapshot.toObject(Route.class);
                            routesNames.add(routeAux.getName());
                            addDifficultyToTheList(routeAux);
                            listAdapterRoutesSenderist.notifyDataSetChanged();
                        });
                    }
                    if(!routesAvailable){
                        Log.d("routesAvailable", "is false");
                        list.setVisibility(View.GONE);
                        welcomeText.setVisibility(View.INVISIBLE);
                        noRoutesTextView.setVisibility(View.VISIBLE);
                    }
                    hasJoined = false;
                }
            } else {
                list.setVisibility(View.GONE);
                welcomeText.setVisibility(View.INVISIBLE);
                noRoutesTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkRouteProposalStarted() {
        db.collection("RoutesProposals").whereEqualTo("routeProposalState", RouteProposalState.WAITING)
                .whereArrayContains("participantsIds", currentFirebaseUser.getUid())
                .addSnapshotListener((value, error) -> {
            if(value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot snapshot : value) {
                    RouteProposal routeProposalAux = snapshot.toObject(RouteProposal.class);
                    showDialogRouteHasStarted(snapshot.getId(), routeProposalAux);
                }
            }
        });
    }

    private void showDialogRouteHasStarted(String idRouteProposal, RouteProposal routeProposal) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("A route you sing up for has already started!");
        builder.setPositiveButton("Go!", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), RouteStartedSenderist.class);
                    intent.putExtra("rol", "SENDERIST");
                    intent.putExtra("route_proposal", routeProposal);
                    intent.putExtra("route_proposal_code", idRouteProposal);
                    startActivity(intent);
                });
        builder.show();
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

    private void addImageOfRoute(RouteProposal routeProposalAux) {
        db.collection("Routes").document(routeProposalAux.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
            Route route = documentSnapshot.toObject(Route.class);
            mainImageOfRoutes.add(route.getImageURL());
        });
    }

    private void addDifficultyToTheList(Route route) {
        switch (route.getDifficulty()) {
            case EASY:
                difficultyOfRoutes.add(R.drawable.difficulty_easy);
                break;
            case MEDIUM:
                difficultyOfRoutes.add(R.drawable.difficulty_moderate);
                break;
            case HARD:
                difficultyOfRoutes.add(R.drawable.difficulty_hard);
                break;
            case EXPERIENCED:
                difficultyOfRoutes.add(R.drawable.difficulty_expert);
                break;
        }
    }

    private void clearList() {
        routesNames.clear();
        routesIds.clear();
        routesProposalsIds.clear();
        datesOfRoutes.clear();
        routesWithGuide.clear();
        difficultyOfRoutes.clear();
        mainImageOfRoutes.clear();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            AuthUI.getInstance().signOut(getApplicationContext());
            startActivity(new Intent(MainMenuSenderist.this, MainActivity.class));
        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}