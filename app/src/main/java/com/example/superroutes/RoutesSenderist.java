package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import com.example.superroutes.custom_classes.MyListAdapter;

public class RoutesSenderist extends AppCompatActivity {

    public static final String ROUTE_SELECTED = "";
    private ListView list;
    private String routeSelected;
    private ArrayList<String> routes;
    private ArrayList<Integer> guidesIcons;
    private ArrayList<Integer> weatherIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_routes);

        routes = new ArrayList<>();
        guidesIcons = new ArrayList<>();
        weatherIcons = new ArrayList<>();

        MyListAdapter adapter=new MyListAdapter(this, routes, guidesIcons, weatherIcons);
        list=findViewById(R.id.list);
        list.setAdapter(adapter);

        //Capture the layout's TextView and set the string as its text
        TextView welcome_text = findViewById(R.id.title_text);
        //welcome_text.setText(welcome_text.getText() + user);

        //Make the button disabled. No route has been selected yet
        Button selectRouteButton = findViewById(R.id.select_route_button);
        selectRouteButton.setClickable(false);

        routes.add("Ruta 1");
        routes.add("Ruta 2");
        routes.add("Ruta 3");
        routes.add("Ruta 4");
        routes.add("Ruta 5");
        guidesIcons.add(R.drawable.icons8_tour_guide_48);
        guidesIcons.add(R.drawable.icons8_tour_guide_48);
        guidesIcons.add(R.drawable.icons8_tour_guide_48);
        guidesIcons.add(R.drawable.icons8_tourist_guide_50);
        guidesIcons.add(R.drawable.icons8_tourist_guide_50);
        weatherIcons.add(R.drawable.mountain);
        weatherIcons.add(R.drawable.mountain);
        weatherIcons.add(R.drawable.mountain);
        weatherIcons.add(R.drawable.mountains);
        weatherIcons.add(R.drawable.mountains);

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