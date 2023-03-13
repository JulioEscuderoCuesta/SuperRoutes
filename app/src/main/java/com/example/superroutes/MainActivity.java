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
import android.widget.Toast;

import com.example.superroutes.model.Rol;
import com.example.superroutes.model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
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
    private static final String TAG = "FirebaseAuthActivity";
    private static final String ERROR_LOG_IN = "Error login";
    private FirebaseDatabase database;
    private DatabaseReference usuarios;

    private RadioGroup radioGroup;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build());
    private static final String USER ="Julio Escudero";
    private static final String PASSWORD ="1234";
    private static final String LOGIN_ERROR = "User or password incorrect";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");
        radioGroup = findViewById(R.id.senderist_guide_radio_buttons);
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
            checkNewUser();
        } else {
            if(result.getResultCode() == RESULT_CANCELED) {
                Log.d(TAG, ERROR_LOG_IN);
                Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }
    }

    //Comprueba si el usuario estaba registrado en la base de datos
    private void checkNewUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usuarios = database.getReference().child("Users");
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean existe = false;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot: snapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);
                    if(user.getEmail().equals(user.getEmail()))
                        existe = true;
                }
                if(!existe)
                    putNewUserInFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO
            }
        });
        Intent intent = new Intent();
        if(radioGroup.getCheckedRadioButtonId() == 2)
            intent.putExtra("ROL", Rol.GUIDE);
        else
            intent.putExtra("ROL", Rol.SENDERIST);
        startActivity(new Intent(this, Routes.class));
    }

    private void putNewUserInFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(radioGroup.getCheckedRadioButtonId() == -1) {
            Log.d(TAG, ERROR_LOG_IN);
            Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else {
            User user = new User(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhoneNumber());
            database.getReference().child("User/"+currentUser.getUid()).setValue(user);
        }

    }

    public void sendMessage(View view) {
        Intent intent;
        EditText user = findViewById(R.id.user_edit_text);
        EditText password = findViewById(R.id.password_edit_text);
        String user_string = user.getText().toString();
        String password_string = password.getText().toString();
        if(user_string.equals(USER) && password_string.equals(PASSWORD)) {
            intent = new Intent(this, Routes.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, LOGIN_ERROR, Toast.LENGTH_SHORT).show();
    }
}
