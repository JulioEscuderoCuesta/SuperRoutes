package com.example.superroutes.custom_classes;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.superroutes.R;

import java.util.ArrayList;

public class ListAdapterRoutesSenderist extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> routes;
    private ArrayList<Integer> routesWithGuide;
    private ArrayList<Integer> difficultyOfRoutes;

    public ListAdapterRoutesSenderist(Activity context, ArrayList<String> routes, ArrayList<Integer> routesWithGuide, ArrayList<Integer> difficultyOfRoutes) {
        super(context, R.layout.listview_routes_senderist, routes);

        this.context = context;
        this.routes = routes;
        this.routesWithGuide = routesWithGuide;
        this.difficultyOfRoutes = difficultyOfRoutes;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_routes_senderist, null, true);

        TextView titleText = rowView.findViewById(R.id.title);
        ImageView guideIcon = rowView.findViewById(R.id.icon_guide);
        ImageView weatherIcon = rowView.findViewById(R.id.icon_difficulty);

        titleText.setText(routes.get(position));
        guideIcon.setImageResource(routesWithGuide.get(position));
        weatherIcon.setImageResource(difficultyOfRoutes.get(position));

        return rowView;

    }
}
