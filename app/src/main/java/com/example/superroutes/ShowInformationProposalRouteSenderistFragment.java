package com.example.superroutes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.custom_classes.ListOfParticipantsCardAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class ShowInformationProposalRouteSenderistFragment extends DialogFragment {

    private static final String NO_MORE_PARTICIPANTS = "This proposal is full. You can't join";
    private static final String JOINED = "You joined this proposal!";
    private String routeCode;
    private String routeProposalCode;
    private int numberOfMaxParticipants;

    private FirebaseDatabase database;
    private DatabaseReference routeProposal;
    private DatabaseReference route;
    private DatabaseReference participants;
    private FirebaseUser user;

    public ShowInformationProposalRouteSenderistFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        user = FirebaseAuth.getInstance().getCurrentUser();
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

        Button startRouteButton = v.findViewById(R.id.join_route_senderist_button);
        Button deleteProposalButton = v.findViewById(R.id.close_proposal_button);
        startRouteButton.setOnClickListener(view -> {
            joinRouteProposal();
            dismiss();
        });
        deleteProposalButton.setOnClickListener(view -> {
            dismiss();
        });

        getRouteProposalData(v);

        return builder.create();
    }

    private void joinRouteProposal() {
        participants = database.getReference().child("Participants").child(routeProposalCode);
        participants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((int) snapshot.getChildrenCount() < numberOfMaxParticipants) {
                    participants.child("userId").setValue(user.getUid());
                    Toast.makeText(getContext(), JOINED, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(), NO_MORE_PARTICIPANTS, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRouteProposalData(View v) {
        routeProposal = database.getReference().child("RoutesProposals").child(routeProposalCode);
        routeProposal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RouteProposal routeProposalAux = snapshot.getValue(RouteProposal.class);
                numberOfMaxParticipants = routeProposalAux.getMaxParticipants();
                TextView dateOfRoute = v.findViewById(R.id.date_of_route_senderist_routes);
                TextView guideNameOfRoute = v.findViewById(R.id.guide_information_senderist_routes_text);
                TextView commentOfRoute = v.findViewById(R.id.comment_senderist_routes_text);
                String dateOfRouteString = routeProposalAux.getWhichDay().toString().replace('-', '/');
                dateOfRoute.setText(dateOfRouteString);
                guideNameOfRoute.setText(routeProposalAux.getGuide().getNombre());
                commentOfRoute.setText(routeProposalAux.getComments());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        route = database.getReference().child("Routes").child(routeCode);
        route.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Route routeAux = snapshot.getValue(Route.class);
                TextView nameOfRoute = v.findViewById(R.id.name_of_route_inside_card_senderist);
                TextView locationOfRoute = v.findViewById(R.id.location_information_senderist_routes_text);
                nameOfRoute.setText(routeAux.getName());
                locationOfRoute.setText(routeAux.getLocation());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}