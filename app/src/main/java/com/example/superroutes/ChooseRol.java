package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superroutes.model.Rol;
import com.firebase.ui.auth.AuthUI;

public class ChooseRol extends AppCompatActivity {

    private static final String NO_ROOL_SELECTED = "You have to choose a rol";
    private RadioGroup radioGroup;
    private RadioButton guideOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_rol);
        radioGroup = findViewById(R.id.senderist_guide_radio_buttons);
        guideOption = findViewById(R.id.rol_guide);

        ImageView blueQuestionIcon = findViewById(R.id.blue_question_icon);
        TextView explanation = findViewById(R.id.text_after_clicking_blue_question);
        TextView information = findViewById(R.id.choose_rol_information);
        blueQuestionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(explanation.getVisibility() == View.INVISIBLE) {
                    blueQuestionIcon.setAlpha(0.5f);
                    explanation.bringToFront();
                    explanation.setVisibility(View.VISIBLE);
                }
                else if(explanation.getVisibility() == View.VISIBLE) {
                    blueQuestionIcon.setAlpha(1.0f);
                    information.bringToFront();
                    explanation.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void onClickGo(View view) {
        Intent intent = new Intent();
        if(radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, NO_ROOL_SELECTED, Toast.LENGTH_SHORT).show();
        }
        else {
            if(radioGroup.getCheckedRadioButtonId() == guideOption.getId()) {
                intent.putExtra("ROL", Rol.GUIDE);
                startActivity(new Intent(this, MainMenuGuide.class));
            }
            else {
                intent.putExtra("ROL", Rol.SENDERIST);
                startActivity(new Intent(this, MainMenuSenderist.class));
            }
        }

    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
                    AuthUI.getInstance().signOut(getApplicationContext());
                    startActivity(new Intent(ChooseRol.this, MainActivity.class));
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}