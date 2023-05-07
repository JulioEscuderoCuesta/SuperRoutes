package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.model.Group;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateNewGroup extends AppCompatActivity {

    private static final String SIGN_UP_EMAIL_INCORRECT = "Email format was incorrect";
    private static final String NO_MEMBERS_INVITED = "Email format was incorrect";
    private static final String GROUP_CREATED_SUCCESSFULLY = "Group created successfully";
    private FirebaseUser currentFireBaseUser;

    private FirebaseFirestore db;
    private EditText addNewParticipant;
    private List<String> listOfMembers;
    private TextView listOfMembersTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        db = FirebaseFirestore.getInstance();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        addNewParticipant = findViewById(R.id.list_invitation_new_members);
        listOfMembersTextView = findViewById(R.id.list_of_new_members);

    }

    public void confirmNewGroup(View view) {
        EditText nameOfGroup = findViewById(R.id.name_of_new_group_edittext);
        String nameOfGroupString = nameOfGroup.getText().toString();
        listOfMembers.add(currentFireBaseUser.getUid());
        if(listOfMembersTextView.getText().toString().isEmpty())
            dialogNoMembersInvited();
        else {
            Group newGroup = new Group(nameOfGroupString, listOfMembers, reverseFormat(LocalDate.now()));
            db.collection("Groups").add(newGroup).addOnSuccessListener(documentReference -> {
                Toast.makeText(CreateNewGroup.this, GROUP_CREATED_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateNewGroup.this, MyGroupsSenderist.class));
            });
        }
    }

    public void addNewMember(View view) {
        String emailNewMember = addNewParticipant.getText().toString();
        if(!ValidateEmail(emailNewMember))
            Toast.makeText(this, SIGN_UP_EMAIL_INCORRECT, Toast.LENGTH_SHORT).show();
        else
            listOfMembersTextView.setText(emailNewMember+"\n");
    }

    private boolean ValidateEmail(String email) {
        if(email=="" || email==null) return false;
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches())
            return false;
        return true;
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

    private void dialogNoMembersInvited() {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("There is still room for more people ")
                .setMessage("Do you still want to start?")
                .setNegativeButton("I'll wait", ((dialogInterface, i) -> builder.di)
                .setPositiveButton("Go!", (dialogInterface, i) -> startProposal());
        builder.show();*/
    }

}