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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.custom_classes.ListOfParticipantsCardAdapter;
import com.example.superroutes.model.Rol;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
import com.example.superroutes.model.RouteProposalState;
import com.example.superroutes.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowInformationProposalRouteGuideFragment extends DialogFragment {

    private static final String PROPOSAL_DELETED = "Proposal deleted";

    private int numberOfParticipants;
    private int maxNumberOfParticipants;
    private TextView listOfParticipantsNames;
    private String routeProposalCode;
    private String routeCode;
    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;

    public ShowInformationProposalRouteGuideFragment() {
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
        return inflater.inflate(R.layout.fragment_show_information_route_guide, container, false);
    }


    private Dialog showCardInformationProposal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_show_information_route_guide, null);
        builder.setView(v);

        Button startRouteButton = v.findViewById(R.id.start_route_guide_button);
        Button deleteProposalButton = v.findViewById(R.id.delete_proposal_button);
        listOfParticipantsNames = v.findViewById(R.id.list_of_participants_names_route_guide);
        startRouteButton.setOnClickListener(view -> {
            if(numberOfParticipants == 0) {
                dialogNumberParticipantsIsZero();
            }
            else if (numberOfParticipants < maxNumberOfParticipants)
                dialogNumberParticipantsLessThanMaxParticipants();
            else
                confirmStartProposalDialog();
        });
        deleteProposalButton.setOnClickListener(view -> {
            confirmDeleteProposalDialog();
            //deleteProposal();
        });

        getRouteProposalData(v);

        return builder.create();
    }

    private void getRouteProposalData(View v) {
        db.collection("RoutesProposals").document(routeProposalCode).get().addOnSuccessListener(documentSnapshot -> {
            RouteProposal routeProposalAux = documentSnapshot.toObject(RouteProposal.class);
            numberOfParticipants = routeProposalAux.getParticipantsIds().size();
            maxNumberOfParticipants = routeProposalAux.getMaxParticipants();
            TextView dateOfRoute = v.findViewById(R.id.date_of_route_guide_routes);
            String dateOfRouteString = routeProposalAux.getWhichDay();
            dateOfRoute.setText(dateOfRouteString);

            List<String> participantsIds = routeProposalAux.getParticipantsIds();
            StringBuilder sb = new StringBuilder();
            db.collection("Users").get().addOnCompleteListener(task -> {
                User userAux;
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        for(String id : participantsIds) {
                            if(document.getId().equals(id)) {
                                userAux = document.toObject(User.class);
                                sb.append(userAux.getName()).append("\n");
                            }
                        }
                    }
                    listOfParticipantsNames.setText(sb.toString());
                }
            });
        });

        db.collection("Routes").document(routeCode).get().addOnSuccessListener(documentSnapshot -> {
            Route routeAux = documentSnapshot.toObject(Route.class);
            TextView nameOfRoute = v.findViewById(R.id.name_of_route_inside_card_guide);
            nameOfRoute.setText(routeAux.getName());
        });
    }

    private void dialogNumberParticipantsIsZero() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("There are no participants in this proposal")
                .setMessage("You can't start a proposal with 0 participants")
                .setPositiveButton("Ok", (dialogInterface, i) -> dismiss());
        builder.show();
    }

    private void dialogNumberParticipantsLessThanMaxParticipants() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("There is still room for more people ")
                .setMessage("Do you still want to start?")
                .setNegativeButton("I'll wait", ((dialogInterface, i) -> dismiss()))
                .setPositiveButton("Go!", (dialogInterface, i) -> startProposal());
        builder.show();

    }

    private void confirmStartProposalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?")
                .setMessage("Once you select this option, the route will start.")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dismiss())
                .setPositiveButton("Go!", (dialogInterface, i) -> startProposal());
        builder.show();

    }

    private void startProposal() {
        db.collection("RoutesProposals").document(routeProposalCode)
                .update("routeProposalState", String.valueOf(RouteProposalState.WAITING)).addOnSuccessListener(unused -> {
                    Intent intent = new Intent(getContext(), RouteStarted.class);
                    intent.putExtra("route_proposal_code", routeProposalCode);
                    intent.putExtra("rol", "GUIDE");
                    startActivity(intent);
                });
    }

    private void confirmDeleteProposalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?")
                .setMessage("Once you delete the proposal, it can't be undone")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dismiss())
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    deleteProposal();
                    dismiss();
                });
        builder.show();
    }

    private void deleteProposal() {
        db.collection("RoutesProposals").document(routeProposalCode)
                .delete().addOnSuccessListener(unused -> {
                    //Toast.makeText(getContext(), PROPOSAL_DELETED, Toast.LENGTH_SHORT).show();
                });
    }
}