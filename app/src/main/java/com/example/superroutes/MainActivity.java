package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.custom_classes.MyActivity;

public class MainActivity extends AppCompatActivity {
    private static final String USER ="Julio Escudero";
    private static final String PASSWORD ="1234";
    private static final String LOGIN_ERROR = "User or password incorrect";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent;
        EditText user = findViewById(R.id.user_edit_text);
        EditText password = findViewById(R.id.password_edit_text);
        String user_string = user.getText().toString();
        String password_string = password.getText().toString();
        if(user_string.equals(USER) && password_string.equals(PASSWORD)) {
            intent = new Intent(this, MyActivity.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, LOGIN_ERROR, Toast.LENGTH_SHORT).show();
    }
}
