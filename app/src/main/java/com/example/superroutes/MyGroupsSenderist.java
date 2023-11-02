package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.superroutes.custom_classes.ListAdapterMyGroups;
import com.example.superroutes.model.Group;
import com.example.superroutes.model.Rol;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyGroupsSenderist extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String NO_MESSAGES_YET = "No messages yet";
    private static final String LAST_MESSAGE_FROM_TODAY = "Today";
    private static final String LAST_MESSAGE_FROM_YESTERDAY = "Yesterday";
    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private SearchView searchBar;
    private ListAdapterMyGroups listAdapterGroups;

    private ArrayList<String> groupsIds;
    private ArrayList<String> names;
    private ArrayList<String> participantsInGroups;

    private ArrayList<String> namesParticipantsInGroups;
    private ArrayList<String> imagesOfGroups;
    private ArrayList<String> datesLastMessage;
    private ListView list;
    private TextView noGroupsTextView;
    boolean listUpdated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        searchBar = findViewById(R.id.searchview_my_groups);
        searchBar.setOnQueryTextListener(this);
        groupsIds = new ArrayList<>();
        names = new ArrayList<>();
        participantsInGroups = new ArrayList<>();
        namesParticipantsInGroups = new ArrayList<>();
        imagesOfGroups = new ArrayList<>();
        datesLastMessage = new ArrayList<>();
        list = findViewById(R.id.list_of_groups_for_senderist);
        list.setVisibility(View.GONE);
        noGroupsTextView = findViewById(R.id.no_groups_text);

        //Make the items clikeable
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, GroupChat.class);
            Bundle args = new Bundle();
            args.putString("group_id", groupsIds.get(i));
            args.putString("rol", String.valueOf(Rol.SENDERIST));
            intent.putExtras(args);
            startActivity(intent);

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        showGroups();
    }

    /**
     * Shows information about the groups the user belongs to
     */
    private void showGroups() {
        db.collection("Groups").whereArrayContains("listOfMembers", currentFirebaseUser.getUid()).addSnapshotListener((value, e) -> {
            clearList();
            if(value != null && !value.isEmpty()) {
                noGroupsTextView.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                for (QueryDocumentSnapshot snapshot : value) {
                    Group groupAux = snapshot.toObject(Group.class);
                    groupsIds.add(snapshot.getId());
                    names.add(groupAux.getName());
                    participantsInGroups.addAll(groupAux.getListOfMembers());
                    participantsInGroups.add("/");
                    imagesOfGroups.add(groupAux.getImageURL());
                    String dateLastMessage = groupAux.getDateLastMessage();
                    if (dateLastMessage == null) {
                        datesLastMessage.add(NO_MESSAGES_YET);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate lastMessageDate = LocalDate.parse(dateLastMessage, formatter);
                        LocalDate today = LocalDate.now();
                        if (lastMessageDate.isEqual(today))
                            datesLastMessage.add(LAST_MESSAGE_FROM_TODAY);
                        else if (lastMessageDate.isEqual(today.minusDays(1)))
                            datesLastMessage.add(LAST_MESSAGE_FROM_YESTERDAY);
                        else
                            datesLastMessage.add(dateLastMessage);
                    }
                }
                Log.d("participantsInGroup", participantsInGroups.toString());
                showParticipantsForEachGroup();
            }
            else {
                list.setVisibility(View.GONE);
                noGroupsTextView.setVisibility(View.VISIBLE);
            }

        });
    }

    /**
     * Collects each participant name from each group using the ids
     */
    private void showParticipantsForEachGroup() {
        db.collection("Users").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(int i = 0; i < participantsInGroups.size(); i++) {
                    if(participantsInGroups.get(i).equals("/"))
                        namesParticipantsInGroups.add("/");
                    else {
                        for(QueryDocumentSnapshot document: task.getResult()) {
                            String idOfParticipant = participantsInGroups.get(i);
                            if(idOfParticipant.equals(document.getId())) {
                                if(idOfParticipant.equals(currentFirebaseUser.getUid()))
                                    namesParticipantsInGroups.add("You");
                                else
                                    namesParticipantsInGroups.add(document.get("name").toString() + " " + document.get("surname").toString());
                            }
                        }
                    }
                }

                Log.d("participantsInGroups", namesParticipantsInGroups.toString());
                listAdapterGroups = new ListAdapterMyGroups(this, names, namesParticipantsInGroups, imagesOfGroups, datesLastMessage);
                list.setAdapter(listAdapterGroups);
            }
        });
    }

    private void clearList() {
        names.clear();
        participantsInGroups.clear();
        imagesOfGroups.clear();
        datesLastMessage.clear();
    }

    public void onClickCreateNewGroup(View view) {
        startActivity(new Intent(this, CreateNewGroup.class));
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(this, MainMenuSenderist.class));
    }
}