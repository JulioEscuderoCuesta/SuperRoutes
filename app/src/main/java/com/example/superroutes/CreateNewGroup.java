package com.example.superroutes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.model.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateNewGroup extends AppCompatActivity {

    private static final String NO_IMAGE_SELECTED = "The group will not have an image";
    private static final String SIGN_UP_EMAIL_INCORRECT = "Email format was incorrect";
    private static final String NO_MEMBERS_INVITED = "You did not invite anyone";
    private static final String ERROR_UPLOAD_IMAGE = "Error uploading image";
    private static final String GROUP_CREATED_SUCCESSFULLY = "Group created successfully";
    private FirebaseUser currentFireBaseUser;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private EditText addNewParticipant;
    private List<String> listOfMembers;
    private TextView listOfMembersTextView;
    private Uri imageUri;
    private ImageView imageView;
    private Bitmap bitmap;
    private String imageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        imageView = findViewById(R.id.group_image);
        addNewParticipant = findViewById(R.id.list_invitation_new_members);
        listOfMembersTextView = findViewById(R.id.list_of_members_invited);
        listOfMembers = new ArrayList<>();
    }

    public void confirmNewGroup(View view) {
        checkGroupHasImage();
    }

    public void selectNewImage(View view) {
        if (checkStoragePermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            launcher.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                //Convert image to Bitmap
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), imageUri
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (imageUri != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    });


    //Upload image to Firebase Storage
    private void checkGroupHasImage() {
        if(imageUri != null)
            uploadImage();
        else {
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("The group will not have an image ")
                .setMessage("Do you still want to continue?")
                .setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("Yes", (dialogInterface, i) -> uploadGroupData());
                builder.show();
        }
    }

    private void uploadImage() {
        StorageReference myReference = storageReference.child("photo/" + imageUri.getLastPathSegment());
        myReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> myReference.getDownloadUrl().addOnSuccessListener(uri -> {
            if(uri != null) {
                imageURL = uri.toString();
                uploadGroupData();
            }
        }).addOnFailureListener(e -> {
        })).addOnFailureListener(e -> Toast.makeText(this, ERROR_UPLOAD_IMAGE, Toast.LENGTH_SHORT).show());
    }

    private void uploadGroupData() {
        EditText nameOfGroup = findViewById(R.id.name_of_new_group_edittext);
        String nameOfGroupString = nameOfGroup.getText().toString();
        listOfMembers.add(currentFireBaseUser.getUid());
        if(listOfMembersTextView.getText().toString().isEmpty())
            dialogNoMembersInvited();
        else {
            Group newGroup = new Group(nameOfGroupString, listOfMembers, reverseFormat(LocalDate.now()), imageURL);
            db.collection("Groups").add(newGroup).addOnSuccessListener(documentReference -> {
                Toast.makeText(CreateNewGroup.this, GROUP_CREATED_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateNewGroup.this, MyGroupsSenderist.class));
            });
        }
    }

    public void addNewMember(View view) {
        String emailNewMember = addNewParticipant.getText().toString();
        if(!validateEmail(emailNewMember))
            Toast.makeText(this, SIGN_UP_EMAIL_INCORRECT, Toast.LENGTH_SHORT).show();
        else
            listOfMembersTextView.append(emailNewMember+"\n");
        addNewParticipant.setText("");
        addNewParticipant.clearFocus();
    }

    private boolean checkStoragePermission() {
        boolean permissionOk = true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else
                permissionOk = false;
        }
        return permissionOk;
    }

    private boolean validateEmail(String email) {
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
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("There is still room for more people ")
                .setMessage("Do you still want to start?")
                .setNegativeButton("I'll wait", ((dialogInterface, i) -> builder.di)
                .setPositiveButton("Go!", (dialogInterface, i) -> startProposal());
        builder.show();*/
    }

}