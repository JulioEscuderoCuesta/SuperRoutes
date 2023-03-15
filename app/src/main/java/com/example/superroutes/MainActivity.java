package com.example.superroutes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.model.Rol;
import com.example.superroutes.model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String USER ="Julio Escudero";
    private static final String PASSWORD ="1234";
    private static final String LOGIN_ERROR = "User or password incorrect";
    private static final String TAG = "FirebaseAuthActivity";
    private static final String ERROR_LOG_IN = "Error login";
    private FirebaseDatabase database;
    private DatabaseReference usuarios;

    private FirebaseAuth mAuth;
    private TextView signUpText;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        signUpText = findViewById(R.id.sign_up_text);

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });
    }

    /**
     * Launches the event to log in using google.
     * @param view Just the button.
     */
    public void onClickLogInUsingGoogle(View view){
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
        //                .setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
        //                .setLogo(R.mipmap.room_choice)
    }

    /*
        Verify login was succesful. Otherwise, returns error
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Toast.makeText(this, "Log in succesful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ChooseRol.class));
        } else {
            if(result.getResultCode() == RESULT_CANCELED) {
                Log.d(TAG, ERROR_LOG_IN);
                Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }
    }

    public void logIn(View view) {
        final Intent[] intent = new Intent[1];
        EditText email = findViewById(R.id.email_edit_text);
        EditText password = findViewById(R.id.password_edit_text);
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    intent[0] = new Intent(MainActivity.this, ChooseRol.class);
                    startActivity(intent[0]);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, LOGIN_ERROR,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
