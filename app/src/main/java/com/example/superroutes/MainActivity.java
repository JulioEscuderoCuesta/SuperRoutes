package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String USER = "user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, Routes.class);
        EditText user = findViewById(R.id.user_edit_text);
        String user_string = user.getText().toString();
        intent.putExtra(USER, user_string);
        startActivity(intent);
    }
}
