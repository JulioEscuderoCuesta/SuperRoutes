package com.example.superroutes;

import androidx.activity.result.ActivityResultLauncher;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.model.Rol;
import com.example.superroutes.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private static final String SOME_EMPTY_FIELD = "You need to fill in all the fields";
    private static final String ERROR_UPLOAD_IMAGE = "Error uploading image";
    private static final String ACCOUNT_ALREADY_EXIST = "There is already an account with the email provided";
    private static final String SIGN_UP_EMAIL_INCORRECT = "Email format incorrect";
    private static final String SIGN_UP_NAME_SURNAME_EMPTY = "You need to provide a name and a surname";
    private static final String SIGN_UP_TELEPHONE_INCORRECT = "Telephone number must have a right format";
    private static final String PASSWORD_EQUALS_CONFIRM_PASSWORD = "Passwords must match";
    private static final String SIGN_UP_PASSWORD_INCORRECT = "Invalid password. You need at least " +
            "8 characters and at least 1 uppercase letter, 1 number and 1 special character.";
    private static final String SIGN_UP_SUCCESSFUL = "Sign up successfull";
    private FirebaseAuth mAuth;
    private FirebaseUser currentFireBaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore db;

    private Uri imageUri;
    private ImageView imageView;
    private Bitmap bitmap;
    private String imageURL;
    private TextView logInText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        db = FirebaseFirestore.getInstance();
        imageView = findViewById(R.id.group_image);
        logInText = findViewById(R.id.log_in_text);
        signUpButton = findViewById(R.id.button_sign_in);

        logInText.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        signUpButton.setOnClickListener(view -> {
            if(imageUri == null)
                alertNoProfilePicture();
            else
                UploadNewUserData();
        });

    }

    public void selectNewProfilePic(View view) {
        if (CheckStoragePermission()) {
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

    public void signUp(View view) {
        UploadNewUserData();
    }

    //Upload new user data if everything is correct
    private void UploadNewUserData() {
        EditText email = findViewById(R.id.email_edit_text);
        EditText name = findViewById(R.id.name_edit_text);
        EditText surname = findViewById(R.id.surname_edit_text);
        EditText telephone = findViewById(R.id.telephone_edit_text);
        EditText password = findViewById(R.id.password_edit_text);
        EditText confirmPassword = findViewById(R.id.confirm_password_edit_text);
        String emailString = email.getText().toString();
        String nameString = name.getText().toString();
        String surnameString = surname.getText().toString();
        String telephoneString = telephone.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        //Remove white spaces (just in case)
        nameString.replace(" ", "");
        surnameString.replace(" ", "");

        if(emailString.isEmpty()||nameString.isEmpty()||surnameString.isEmpty()||telephoneString.isEmpty()||passwordString.isEmpty()||confirmPasswordString.isEmpty())
            Toast.makeText(this, SOME_EMPTY_FIELD, Toast.LENGTH_SHORT).show();
        if(!ValidateEmail(emailString)) Toast.makeText(this, SIGN_UP_EMAIL_INCORRECT, Toast.LENGTH_LONG).show();
        else if(!ValidateNameAndSurname(nameString, surnameString)) Toast.makeText(this, SIGN_UP_NAME_SURNAME_EMPTY, Toast.LENGTH_LONG).show();
        else if(!ValidateTelephone(telephoneString)) Toast.makeText(this, SIGN_UP_TELEPHONE_INCORRECT, Toast.LENGTH_LONG).show();
        else if(!ValidatePasswordEqualsConfirmPassword(passwordString, confirmPasswordString)) Toast.makeText(this, PASSWORD_EQUALS_CONFIRM_PASSWORD, Toast.LENGTH_LONG).show();
        else if(!ValidatePassword(passwordString)) Toast.makeText(this, SIGN_UP_PASSWORD_INCORRECT, Toast.LENGTH_LONG).show();
        else {
            mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    currentFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    //The image needs to be stored in Storage before being seing in Firestore
                    User currentUser= new User(nameString, surnameString, emailString, telephoneString, null);
                    db.collection("Users").document(currentFireBaseUser.getUid()).set(currentUser);
                    Toast.makeText(SignUp.this, SIGN_UP_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                    //Now that the user is registered, the image can be uploaded
                    UploadImage();
                }
                else {
                    Log.w("ERROR", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUp.this, ACCOUNT_ALREADY_EXIST,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void UploadImage() {
        DocumentReference user = db.collection("Users").document(currentFireBaseUser.getUid());
        if(imageUri != null) {
            StorageReference myReference = storageReference.child("photo/" + imageUri.getLastPathSegment());
            myReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> myReference.getDownloadUrl().addOnSuccessListener(uri -> {
                if(uri != null) {
                    imageURL = uri.toString();
                    //Update profile pic
                    user.update("imageURL", imageURL);
                }
            }).addOnFailureListener(e -> {
            })).addOnFailureListener(e -> Toast.makeText(this, ERROR_UPLOAD_IMAGE, Toast.LENGTH_SHORT).show());
        }
        //If no profile picture was selected, just go back to main interface
        startActivity(new Intent(SignUp.this, MainActivity.class));

    }

    private void alertNoProfilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No profile picture was selected?")
                .setMessage("A default picture will be assinged to you, do you want to continue?")
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Yes", (dialogInterface, i) -> { UploadNewUserData();
                });
        builder.show();
    }

    private boolean CheckStoragePermission() {
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

    private boolean ValidateNameAndSurname(String name, String surname) {
        if(name.isEmpty() || surname.isEmpty())
            return false;
        return true;
    }

    private boolean ValidateTelephone(String telephone) {
        if(telephone=="" || telephone==null) return false;
        String regex = "^[0-9]{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(telephone);
        if(!matcher.matches())
            return false;
        return true;
    }
    private boolean ValidatePasswordEqualsConfirmPassword(String password, String confirmPassword) {
        if(!password.equals(confirmPassword))
            return false;
        return true;
    }

    private boolean ValidatePassword(String password) {
        if(password=="" || password==null) return false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!/])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if(!matcher.matches())
            return false;
        return true;
    }
}