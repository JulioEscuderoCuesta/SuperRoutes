package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.superroutes.model.Rol;

public class ChooseRol extends AppCompatActivity {

    private static final String NO_ROOL_SELECTED = "You have to choose a rol";
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_rol);
        radioGroup = findViewById(R.id.senderist_guide_radio_buttons);
    }

    public void onClickGo(View view) {
        Intent intent = new Intent();
        if(radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, NO_ROOL_SELECTED, Toast.LENGTH_SHORT).show();
        }
        else {
            if(radioGroup.getCheckedRadioButtonId() == 2)
                intent.putExtra("ROL", Rol.GUIDE);
            else
                intent.putExtra("ROL", Rol.SENDERIST);
            startActivity(new Intent(this, Routes.class));
        }

    }
}