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

import java.util.ArrayList;
import java.util.List;

public class ShowInformationProposalRouteGuideFragment extends DialogFragment {

    private static final String PROPOSAL_DELETED = "Proposal deleted";
    private ListView listViewOfParticipantsCard;
    private List<String> listOfParticipantsCard;
    private String routeProposalCode;
    private String routeCode;
    private RouteProposal routeProposal;
    private FirebaseDatabase database;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference routeProposalReference;
    private FirebaseFirestore db;
    private DatabaseReference route;
    private DatabaseReference participants;
    public ShowInformationProposalRouteGuideFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Bundle args = getArguments();
        routeCode = args.getString("route_code");
        routeProposalCode = args.getString("route_proposal_code");
        Log.d("routePRoposalCode", routeProposalCode);
        return showCardInformationProposal(routeCode, routeProposalCode);
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


    private Dialog showCardInformationProposal(String routeCode, String routeProposalCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_show_information_route_guide, null);
        builder.setView(v);

        Button startRouteButton = v.findViewById(R.id.start_route_guide_button);
        Button deleteProposalButton = v.findViewById(R.id.delete_proposal_button);
        startRouteButton.setOnClickListener(view -> {
            //confirmStartProposalDialog();
            startProposal();
        });
        deleteProposalButton.setOnClickListener(view -> {
            //confirmDeleteProposalDialog();
            deleteProposal();
            dismiss();
        });
        getRouteProposalData(v);

        listOfParticipantsCard = new ArrayList<>();
        listViewOfParticipantsCard = v.findViewById(R.id.list_of_participants_card);
        ListOfParticipantsCardAdapter adapter = new ListOfParticipantsCardAdapter(listOfParticipantsCard, getContext());
        listViewOfParticipantsCard.setAdapter(adapter);

        return builder.create();
    }

    private void getRouteProposalData(View v) {
        db.collection("RoutesProposals").document(routeProposalCode).get().addOnSuccessListener(documentSnapshot -> {
            RouteProposal routeProposalAux = documentSnapshot.toObject(RouteProposal.class);

            TextView dateOfRoute = v.findViewById(R.id.date_of_route_guide_routes);
            String dateOfRouteString = routeProposalAux.getWhichDay();
            dateOfRoute.setText(dateOfRouteString);

            List<String> participantsIds = routeProposalAux.getParticipantsIds();
            for(String id: participantsIds) {
                db.collection("Users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User userAux = documentSnapshot.toObject(User.class);
                        listOfParticipantsCard.add(userAux.getName());
                    }
                });
            }
        });

        db.collection("Routes").document(routeCode).get().addOnSuccessListener(documentSnapshot -> {
            Route routeAux = documentSnapshot.toObject(Route.class);
            TextView nameOfRoute = v.findViewById(R.id.name_of_route_inside_card_guide);
            nameOfRoute.setText(routeAux.getName());
        });
    }

    private AlertDialog confirmStartProposalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?")
                .setMessage("Once you select this option, the route will start.")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dismiss())
                .setPositiveButton("Go!", (dialogInterface, i) -> startProposal());
        return builder.create();
    }

    private void startProposal() {
        db.collection("RoutesProposals").document(routeProposalCode)
                .update("routeProposalState", String.valueOf(RouteProposalState.WAITING)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(getContext(), RouteStarted.class);
                        intent.putExtra("route_proposal_code", routeProposalCode);
                        intent.putExtra("rol", "GUIDE");
                        startActivity(intent);
                    }
                });
    }

    private AlertDialog confirmDeleteProposalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?")
                .setMessage("Once you delete the proposal, it can't be undone")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dismiss())
                .setPositiveButton("Delete", (dialogInterface, i) -> deleteProposal());
        return builder.create();
    }

    private void deleteProposal() {
        db.collection("RoutesProposals").document(routeProposalCode)
                .delete().addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), PROPOSAL_DELETED, Toast.LENGTH_SHORT).show();
                });
    }
}