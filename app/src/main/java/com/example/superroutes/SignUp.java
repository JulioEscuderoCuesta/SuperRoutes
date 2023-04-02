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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private static final String SIGN_UP_EMAIL_INCORRECT = "Email format was incorrect";
    private static final String SIGN_UP_PASSWORD_INCORRECT = "Invalid password. You need at least " +
            "8 characters and at least 1 uppercase letter, 1 number and 1 special character.";
    private static final String SIGN_UP_SUCCESSFUL = "Sign up successfull";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {
        EditText email = findViewById(R.id.email_edit_text);
        EditText password = findViewById(R.id.password_edit_text);
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        if(!ValidateEmail(emailString)) Toast.makeText(this, SIGN_UP_EMAIL_INCORRECT, Toast.LENGTH_LONG).show();
        else if(!ValidatePassword(passwordString)) Toast.makeText(this, SIGN_UP_PASSWORD_INCORRECT, Toast.LENGTH_LONG).show();
        else {
            mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(SignUp.this, SIGN_UP_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, ChooseRol.class));
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

    private boolean ValidatePassword(String password) {
        if(password=="" || password==null) return false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if(!matcher.matches())
            return false;
        return true;
    }
}