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

public class ListAdapterRoutesGuide extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> routes;
    private ArrayList<String> numberOfSenderist;
    private ArrayList<Integer> weatherOfRoutes;

    public ListAdapterRoutesGuide(Activity context, ArrayList<String> routes, ArrayList<String> numberOfSenderist, ArrayList<Integer> weatherOfRoutes) {
        super(context, R.layout.listview_routes_guide, routes);

        this.context = context;
        this.routes = routes;
        this.numberOfSenderist = numberOfSenderist;
        this.weatherOfRoutes = weatherOfRoutes;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_routes_guide, null, true);

        TextView titleText = rowView.findViewById(R.id.title);
        TextView numberOfSenderistTextView = rowView.findViewById(R.id.number_of_senderist);
        ImageView weatherIcon = rowView.findViewById(R.id.icon_weather);

        titleText.setText(routes.get(position));
        numberOfSenderistTextView.setText(numberOfSenderist.get(position));
        weatherIcon.setImageResource(weatherOfRoutes.get(position));

        return rowView;

    }
}
