package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Routes extends AppCompatActivity {

    public static final String ROUTE_SELECTED = "";
    private ListView list;
    private String routeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        //Capture the layout's TextView and set the string as its text
        TextView welcome_text = findViewById(R.id.title_text);
        //welcome_text.setText(welcome_text.getText() + user);

        //Make the button disabled. No route has been selected yet
        Button selectRouteButton = findViewById(R.id.select_route_button);
        selectRouteButton.setClickable(false);

        //List with all the options
        list = findViewById(R.id.list_of_routes);
        ArrayList options = new ArrayList<>();
        options.add("Ruta 1");
        options.add("Ruta 2");
        options.add("Ruta 3");
        options.add("Ruta 4");
        options.add("Ruta 5");
        options.add("Ruta 6");
        options.add("Ruta 7");
        options.add("Ruta 8");
        options.add("Ruta 9");
        options.add("Ruta 10");
        options.add("Ruta 11");
        options.add("Ruta 12");
        options.add("Ruta 13");
        options.add("Ruta 14");

        //Show list with all the options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);

        //Make the items clikeable
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!selectRouteButton.isClickable())
                    selectRouteButton.setClickable(true);
                view.setSelected(true);
                routeSelected = list.getItemAtPosition(i).toString();
                Resources res = getResources();
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.my_selector, null);
                view.setBackground(drawable);

            }
        });
    }

    public void startRoute(View view) {
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra(ROUTE_SELECTED, routeSelected);
        startActivity(intent);
    }
}