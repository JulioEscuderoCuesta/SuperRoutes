package com.example.superroutes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.custom_classes.ListAdapterRoutesAvailable;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteInformation;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.example.superroutes.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShowInformationProposalRouteSenderistFragment extends DialogFragment {

    private static final String JOINED = "You joined this proposal!";
    private String routeCode;
    private String routeProposalCode;
    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;

    public ShowInformationProposalRouteSenderistFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Bundle args = getArguments();
        routeCode = args.getString("route_code");
        routeProposalCode = args.getString("route_proposal_code");
        return showCardInformationProposal();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_information_proposal_route_senderist, container, false);
    }


    private Dialog showCardInformationProposal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_show_information_proposal_route_senderist, null);
        builder.setView(v);

        Button joinRouteButton = v.findViewById(R.id.join_route_senderist_button);
        Button cancelProposalButton = v.findViewById(R.id.close_proposal_button);
        joinRouteButton.setOnClickListener(view -> {
            confirmJoinRouteProposal();
        });
        cancelProposalButton.setOnClickListener(view -> {
            dismiss();
        });

        getRouteProposalData(v);

        return builder.create();
    }

    private AlertDialog confirmJoinRouteProposal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?")
                .setMessage("Once you select this option, you will be a participant of the route.")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dismiss())
                .setPositiveButton("Go!", (dialogInterface, i) -> joinRouteProposal());
        return builder.show();
    }


    private void joinRouteProposal() {
        DocumentReference reference = db.collection("RoutesProposals").document(routeProposalCode);
        reference.get().addOnSuccessListener(documentSnapshot -> {
            RouteProposal routeProposalAux = documentSnapshot.toObject(RouteProposal.class);
            List<String> participantsIds = routeProposalAux.getParticipantsIds();
            participantsIds.add(currentFirebaseUser.getUid());
            reference.update("participantsIds", participantsIds);
            startActivity(new Intent(requireContext(), MyRoutesSenderist.class));
        });
    }

    private void getRouteProposalData(View v) {
        db.collection("RoutesProposals").document(routeProposalCode).get().addOnSuccessListener(documentSnapshot -> {
            RouteProposal routeProposalAux = documentSnapshot.toObject(RouteProposal.class);
            TextView dateOfRoute = v.findViewById(R.id.date_of_route_senderist_routes);
            TextView guideNameOfRoute = v.findViewById(R.id.guide_information_senderist_routes_text);
            TextView commentOfRoute = v.findViewById(R.id.comment_senderist_routes_text);
            ImageView guideImage = v.findViewById(R.id.guide_image_route_senderist_card);
            String dateOfRouteString = routeProposalAux.getWhichDay();
            dateOfRoute.setText(dateOfRouteString);
            commentOfRoute.setText(routeProposalAux.getComments());

            db.collection("Users").document(routeProposalAux.getIdGuide()).get().addOnSuccessListener(documentSnapshot1 -> {
                User guide = documentSnapshot1.toObject(User.class);
                guideNameOfRoute.setText(guide.getName());
                if(guide.getImageURL() != null)
                    Picasso.get().load(guide.getImageURL()).into(guideImage);
                else
                    Picasso.get().load(R.drawable.default_profile_pic_man).into(guideImage);
            });
        });

        db.collection("Routes").document(routeCode).get().addOnSuccessListener(documentSnapshot -> {
            Route routeAux = documentSnapshot.toObject(Route.class);
            TextView nameOfRoute = v.findViewById(R.id.name_of_route_inside_card_senderist);
            TextView locationOfRoute = v.findViewById(R.id.location_information_senderist_routes_text);
            ImageView imageRoute = v.findViewById(R.id.image_of_route_inside_card_senderist);
            ImageView difficultyRoute = v.findViewById(R.id.difficulty_icon_route_senderist_card);
            TextView durationRoute = v.findViewById(R.id.duration_of_route_senderist_routes);
            durationRoute.setText(routeAux.getDurationInHours() + " h");
            nameOfRoute.setText(routeAux.getName());
            locationOfRoute.setText(routeAux.getLocation());
            addDifficultyToTheList(difficultyRoute, String.valueOf(routeAux.getDifficulty()));
            if(routeAux.getImageURL() != null)
                Picasso.get().load(routeAux.getImageURL()).into(imageRoute);
            else
                Picasso.get().load(R.drawable.mountain).into(imageRoute);
        });
    }

    private void addDifficultyToTheList(ImageView difficultyRoute, String difficulty) {
        switch (difficulty) {
            case "EASY":
                difficultyRoute.setImageResource(R.drawable.difficulty_easy);
                break;
            case "MEDIUM":
                difficultyRoute.setImageResource(R.drawable.difficulty_moderate);
                break;
            case "HARD":
                difficultyRoute.setImageResource(R.drawable.difficulty_hard);
                break;
            case "EXPERIENCED":
                difficultyRoute.setImageResource(R.drawable.difficulty_expert);
                break;
        }
    }
}