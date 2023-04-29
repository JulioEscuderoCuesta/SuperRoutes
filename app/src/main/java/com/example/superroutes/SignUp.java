package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private static final String SIGN_UP_EMAIL_INCORRECT = "Email format was incorrect";
    private static final String SIGN_UP_NAME_SURNAME_EMPTY = "You need to provide a name and a surname";
    private static final String PASSWORD_EQUALS_CONFIRM_PASSWORD = "Passwords must match";
    private static final String SIGN_UP_PASSWORD_INCORRECT = "Invalid password. You need at least " +
            "8 characters and at least 1 uppercase letter, 1 number and 1 special character.";

    private static final String SIGN_UP_SUCCESSFUL = "Sign up successfull";
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");

    }

    public void signUp(View view) {
        EditText email = findViewById(R.id.email_edit_text);
        EditText name = findViewById(R.id.name_edit_text);
        EditText surname = findViewById(R.id.surname_edit_text);
        EditText password = findViewById(R.id.password_edit_text);
        EditText confirmPassword = findViewById(R.id.confirm_password_edit_text);
        String emailString = email.getText().toString();
        String nameString = name.getText().toString();
        String surnameString = surname.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        if(!ValidateEmail(emailString)) Toast.makeText(this, SIGN_UP_EMAIL_INCORRECT, Toast.LENGTH_LONG).show();
        else if(!ValidateNameAndSurname(nameString, surnameString)) Toast.makeText(this, SIGN_UP_NAME_SURNAME_EMPTY, Toast.LENGTH_LONG).show();
        else if(!ValidatePasswordEqualsConfirmPassword(passwordString, confirmPasswordString)) Toast.makeText(this, PASSWORD_EQUALS_CONFIRM_PASSWORD, Toast.LENGTH_LONG).show();
        else if(!ValidatePassword(passwordString)) Toast.makeText(this, SIGN_UP_PASSWORD_INCORRECT, Toast.LENGTH_LONG).show();
        else {
            mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        User user = new User(nameString + " " + surnameString, emailString, currentUser.getPhoneNumber());
                        database.getReference().child("Users").child(currentUser.getUid()).setValue(user);
                        Toast.makeText(SignUp.this, SIGN_UP_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                    }
                    else {
                        Log.w("ERROR", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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