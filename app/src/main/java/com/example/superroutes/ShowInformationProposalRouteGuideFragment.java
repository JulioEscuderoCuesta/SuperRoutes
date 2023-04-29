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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowInformationProposalRouteGuideFragment extends DialogFragment {

    private ListView listViewOfParticipantsCard;
    private List<String> listOfParticipantsCard;

    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference routeProposal;
    private DatabaseReference route;
    private DatabaseReference participants;
    public ShowInformationProposalRouteGuideFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle args = getArguments();
        String routeCode = args.getString("route_code");
        String routeProposalCode = args.getString("route_proposal_code");
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
            routeProposal.child("routeProposalState").setValue(RouteProposalState.WAITING);
            Intent intent = new Intent(getContext(), RouteStarted.class);
            intent.putExtra("route_proposal_code", routeProposalCode);
            intent.putExtra("rol", "GUIDE");
            startActivity(intent);
        });
        deleteProposalButton.setOnClickListener(view -> {
            confirmDeleteProposalDialog();
        });
        getRouteProposalData(v, routeProposalCode);
        getRouteName(v, routeCode);

        listOfParticipantsCard = new ArrayList<>();
        getParticipantsFromDatabase(routeProposalCode);
        listViewOfParticipantsCard = v.findViewById(R.id.list_of_participants_card);
        ListOfParticipantsCardAdapter adapter = new ListOfParticipantsCardAdapter(listOfParticipantsCard, getContext());
        listViewOfParticipantsCard.setAdapter(adapter);

        return builder.create();
    }

    private void getRouteName(View v, String routeCode) {
        route = database.getReference().child("Routes").child(routeCode);
        route.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Route routeAux = snapshot.getValue(Route.class);
                TextView nameOfRoute = v.findViewById(R.id.name_of_route_inside_card_guide);
                nameOfRoute.setText(routeAux.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRouteProposalData(View v, String routeProposalCode) {
        routeProposal = database.getReference().child("RoutesProposals").child(routeProposalCode);
        routeProposal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RouteProposal routeProposalAux = snapshot.getValue(RouteProposal.class);
                TextView dateOfRoute = v.findViewById(R.id.date_of_route_guide_routes);
                String dateOfRouteString = routeProposalAux.getWhichDay().toString().replace('-', '/');
                dateOfRoute.setText(dateOfRouteString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getParticipantsFromDatabase(String routeProposalCode) {
        participants = database.getReference().child("ParticipantsInProposals").child(routeProposalCode);
        participants.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot participantSnapshot: snapshot.getChildren()) {
                    User participant = participantSnapshot.getValue(User.class);
                    listOfParticipantsCard.add(participant.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private AlertDialog confirmDeleteProposalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?")
                .setMessage("Once you delete the proposal, it can't be undone")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Proposal deleted", Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }
}