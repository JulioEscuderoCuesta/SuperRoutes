package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.databinding.ActivityGroupChatBinding;
import com.example.superroutes.model.Group;
import com.example.superroutes.model.Message;
import com.example.superroutes.model.Rol;
import com.example.superroutes.model.Route;
import com.example.superroutes.model.RouteProposal;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupChat extends AppCompatActivity {

    private static final String EMPTY_MESSAGE = "The message is empty";
    private FirebaseFirestore db;
    private FirebaseUser currentFireBaseUser;
    private FirebaseDatabase fbDatabase;

    private ActivityGroupChatBinding binding;
    private String routeProposalCode;
    private RouteProposal routeProposal;
    private Rol rolOfUser;
    private String groupId;
    private ImageView groupImage;
    private TextView groupName;
    private ImageView newRouteProposalImage;
    private TextView newRouteProposalName;
    private TextView newRouteProposalLocation;
    private TextView newRouteProposalDate;
    private String newRouteProposalId;
    private Button buttonNewProposal;
    private TextView noProposalYet;
    private Button agreeButton;
    private Button disagreeButton;
    private TextView activityConfirmed;
    private RecyclerView recyclerView;
    private MessagesAdapter messagesAdapter;
    ArrayList<Message> messagesArrayList;
    private EditText textMessage;
    private CardView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();
        fbDatabase = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        groupId = getIntent().getStringExtra("group_id");
        routeProposal = (RouteProposal) getIntent().getSerializableExtra("route_proposal");
        String rolOfUserString = getIntent().getStringExtra("rol");
        if(rolOfUserString != null)
            rolOfUser = Rol.valueOf(rolOfUserString);
        routeProposalCode = getIntent().getStringExtra("route_proposal_code");

        setContentView(R.layout.activity_group_chat);

        //Commom data in every chat
        newRouteProposalImage = findViewById(R.id.image_new_route_proposal_group);
        newRouteProposalName = findViewById(R.id.name_route_proposal_group);
        newRouteProposalLocation = findViewById(R.id.location_route_proposal_group);
        newRouteProposalDate = findViewById(R.id.date_route_proposal_group);

        //Get views
        recyclerView = findViewById(R.id.message_adapter);
        messagesArrayList = new ArrayList<>();
        textMessage = findViewById(R.id.text_message);
        sendButton = findViewById(R.id.send_button);
        groupImage = findViewById(R.id.group_image);
        groupName = findViewById(R.id.group_name);
        agreeButton = findViewById(R.id.agree_activity_button);
        disagreeButton = findViewById(R.id.disagree_activity_button);
        activityConfirmed = findViewById(R.id.text_activity_confirmed);
        buttonNewProposal = findViewById(R.id.button_new_proposal_group);
        noProposalYet = findViewById(R.id.no_proposals_for_group_yet);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(this, messagesArrayList);
        recyclerView.setAdapter(messagesAdapter);


        DatabaseReference receiverReference;
        //If chat of no group, some views should never appear
        if(groupId == null) {
            groupImage.setVisibility(View.GONE);
            groupName.setVisibility(View.GONE);
            agreeButton.setVisibility(View.GONE);
            disagreeButton.setVisibility(View.GONE);
            buttonNewProposal.setVisibility(View.GONE);
            noProposalYet.setVisibility(View.GONE);
            activityConfirmed.setVisibility(View.GONE);

            getNoGroupInformation();
            receiverReference = fbDatabase.getReference().child("NoGroups").child(routeProposalCode).child("Messages");
        }
        else {
            getGroupInformation();
            receiverReference = fbDatabase.getReference().child("Groups").child(groupId).child("Messages");
        }

        //Listen for new messages in the group chat
        receiverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messagesArrayList.add(message);
                }
                messagesAdapter.notifyDataSetChanged();
                new Handler().postDelayed(() -> {
                    recyclerView.scrollToPosition(messagesArrayList.size() - 1);
                }, 500);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendButton.setOnClickListener(view -> {
            String message = textMessage.getText().toString();
            if(message.isEmpty())
                Toast.makeText(this, EMPTY_MESSAGE, Toast.LENGTH_SHORT).show();
            else {
                textMessage.setText("");
                Date date = new Date();
                Message newMessage = new Message(message, date.getTime(), currentFireBaseUser.getUid());
                receiverReference.push().setValue(newMessage);
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(groupId == null)
                    db.collection("NoGroups").document(routeProposalCode).update("dateLastMessage", reverseFormat(localDate));
                else
                    db.collection("Groups").document(groupId).update("dateLastMessage", reverseFormat(localDate));
            }
        });
    }

    public void onClickCreateNewActivity(View view) {
        Intent intent = new Intent(this, CreateNewRouteProposal.class);
        intent.putExtra("group_id", groupId);
        startActivity(intent);
    }

    //Load only information about the route
    private void getNoGroupInformation() {
        db.collection("Routes").document(routeProposal.getRouteId()).get().addOnSuccessListener(documentSnapshot -> {
            Route route = documentSnapshot.toObject(Route.class);
            newRouteProposalName.setText(route.getName());
            newRouteProposalLocation.setText(route.getLocation());
            if(route.getImageURL() == null)
                Picasso.get().load(R.drawable.mountain).into(newRouteProposalImage);
            else
                Picasso.get().load(route.getImageURL()).into(newRouteProposalImage);
        });

        db.collection("RoutesProposals").document(routeProposalCode).get().addOnSuccessListener(documentSnapshot -> {
            RouteProposal routeProposal1 = documentSnapshot.toObject(RouteProposal.class);
            newRouteProposalDate.setText(routeProposal1.getWhichDay());
        });
    }

    //Load information about the route and the group
    private void getGroupInformation() {
        db.collection("Groups").document(groupId).addSnapshotListener((value, error) -> {
            Group group = value.toObject(Group.class);
            groupName.setText(group.getName());
            if(group.getImageURL() == null)
                Picasso.get().load(R.drawable.mountain).into(groupImage);
            else
                Picasso.get().load(group.getImageURL()).into(groupImage);

            //If there are proposals in the group, get data of the last one
            if(group.getListOfIdsRoutesProposals().size() != 0) {

                // Remove views of propose new activity (for the time being)
                buttonNewProposal.setVisibility(View.GONE);
                noProposalYet.setVisibility(View.GONE);
                activityConfirmed.setVisibility(View.GONE);

                db.collection("RoutesProposals").document(group.getListOfIdsRoutesProposals().get(group.getListOfIdsRoutesProposals().size() - 1)).get().addOnSuccessListener(documentSnapshot12 -> {
                    Log.d("cambio", "cambio2)");
                    RouteProposal routeProposal = documentSnapshot12.toObject(RouteProposal.class);
                    newRouteProposalDate.setText(routeProposal.getWhichDay());
                    newRouteProposalId = documentSnapshot12.getId();

                    //Get the data of the route proposed
                    db.collection("Routes").document(routeProposal.getRouteId()).get().addOnSuccessListener(documentSnapshot1 -> {
                        Route route = documentSnapshot1.toObject(Route.class);
                        newRouteProposalName.setText(route.getName());
                        newRouteProposalLocation.setText(route.getLocation());
                        if (route.getImageURL() == null)
                            Picasso.get().load(R.drawable.mountain).into(newRouteProposalImage);
                        else
                            Picasso.get().load(route.getImageURL()).into(newRouteProposalImage);
                    });

                    //If you already voted, you can not vote or change your vote (for the time being)
                    HashMap<String, Boolean> votes = (HashMap<String, Boolean>) routeProposal.getVotes();
                    Log.d("votes", votes.toString());
                    for(String id: votes.keySet()) {
                        if(id.equals(currentFireBaseUser.getUid())) {
                            Log.d("votes", id);
                            agreeButton.setVisibility(View.INVISIBLE);
                            disagreeButton.setVisibility(View.INVISIBLE);
                            activityConfirmed.setVisibility(View.VISIBLE);
                            activityConfirmed.setText(R.string.text_activity_voted);
                        }
                    }

                    //Listen to any change in the votes
                    DocumentReference reference = db.collection("RoutesProposals").document(newRouteProposalId);
                    reference.addSnapshotListener((value2, error2) -> {
                       if(value2.exists()) {
                           Map<String, Boolean> votes2 = (Map<String, Boolean>) value2.getData().get("votes");

                           //If you already voted, you can not vote again
                           if(votes2.keySet().contains(currentFireBaseUser.getUid())) {
                               agreeButton.setVisibility(View.INVISIBLE);
                               disagreeButton.setVisibility(View.INVISIBLE);
                               activityConfirmed.setVisibility(View.VISIBLE);
                               activityConfirmed.setText(R.string.text_activity_voted);
                           }

                           //If everybody voted, then let's count the votes
                           if(votes2.size() == group.getListOfMembers().size()) {
                               int agree = 0;
                               for(boolean vote: votes2.values()) {
                                   if(vote == true)
                                       agree++;
                               }
                               //If the majority agreed, the activity is confirmed
                               if(agree > votes2.size() / 2) {
                                   agreeButton.setVisibility(View.INVISIBLE);
                                   disagreeButton.setVisibility(View.INVISIBLE);
                                   activityConfirmed.setVisibility(View.VISIBLE);
                                   activityConfirmed.setText(R.string.text_activity_confirmed);
                               }
                               //Otherwise let members propose a new activity
                               else {
                                   newRouteProposalDate.setVisibility(View.GONE);
                                   newRouteProposalLocation.setVisibility(View.GONE);
                                   newRouteProposalName.setVisibility(View.GONE);
                                   newRouteProposalImage.setVisibility(View.GONE);
                                   noProposalYet.setVisibility(View.VISIBLE);
                                   buttonNewProposal.setVisibility(View.VISIBLE);
                                   activityConfirmed.setVisibility(View.VISIBLE);
                                   activityConfirmed.setText(R.string.text_activity_declined);
                               }
                           }

                       }
                    });

                    //If member can vote, record it
                    if(agreeButton.getVisibility() == View.VISIBLE) {
                        reference.get().addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()) {
                                Map<String, Boolean> existingVotes = (Map<String, Boolean>) documentSnapshot.get("votes");
                                agreeButton.setOnClickListener(view -> {
                                    existingVotes.put(currentFireBaseUser.getUid(), true);
                                    db.collection("RoutesProposals").document(newRouteProposalId).update("votes", existingVotes);
                                    agreeButton.setVisibility(View.GONE);
                                    disagreeButton.setVisibility(View.GONE);
                                    activityConfirmed.setVisibility(View.VISIBLE);
                                    activityConfirmed.setText(R.string.text_activity_voted);
                                });
                                disagreeButton.setOnClickListener(view -> {
                                    existingVotes.put(currentFireBaseUser.getUid(), false);
                                    db.collection("RoutesProposals").document(newRouteProposalId).update("votes", existingVotes);
                                    agreeButton.setVisibility(View.GONE);
                                    disagreeButton.setVisibility(View.GONE);
                                    activityConfirmed.setVisibility(View.VISIBLE);
                                    activityConfirmed.setText(R.string.text_activity_voted);
                                });
                            }
                        });

                    }
                });

            }
            else {
                newRouteProposalName.setVisibility(View.INVISIBLE);
                newRouteProposalImage.setVisibility(View.INVISIBLE);
                newRouteProposalDate.setVisibility(View.INVISIBLE);
                newRouteProposalLocation.setVisibility(View.INVISIBLE);
                agreeButton.setVisibility(View.INVISIBLE);
                disagreeButton.setVisibility(View.INVISIBLE);
                activityConfirmed.setVisibility(View.INVISIBLE);
                noProposalYet.setVisibility(View.VISIBLE);
            }
        });
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
}