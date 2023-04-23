package com.example.superroutes.custom_classes;

import android.app.Activity;
import android.util.Log;
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
    private ArrayList<String> routesNames;
    private ArrayList<String> numberOfParticipantsSlashTotal;
    private ArrayList<Integer> mainImageOfRoute;
    private ArrayList<String> datesOfRoutes;

    public ListAdapterRoutesGuide(Activity context, ArrayList<String> routesNames, ArrayList<String> numberOfParticipantsSlashTotal, ArrayList<Integer> mainImageOfRoute, ArrayList<String> datesOfRoutes) {
        super(context, R.layout.listview_routes_guide, routesNames);

        this.context = context;
        this.routesNames = routesNames;
        this.numberOfParticipantsSlashTotal = numberOfParticipantsSlashTotal;
        this.mainImageOfRoute = mainImageOfRoute;
        this.datesOfRoutes = datesOfRoutes;
        Log.d("en el constructor guide", "hola");

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_routes_guide, null, true);

        TextView nameOfRouteTextView = rowView.findViewById(R.id.name_of_route_guide_routes);
        TextView numberOfParticipantsTextView = rowView.findViewById(R.id.number_of_participants_slash_total);
        ImageView mainImageOfRouteImage = rowView.findViewById(R.id.main_image_of_route);
        TextView datesOfRouteTextView = rowView.findViewById(R.id.date_of_route_guide_routes);

        nameOfRouteTextView.setText(routesNames.get(position));
        numberOfParticipantsTextView.setText(numberOfParticipantsSlashTotal.get(position));
        mainImageOfRouteImage.setImageResource(mainImageOfRoute.get(position));
        datesOfRouteTextView.setText(datesOfRoutes.get(position));

        return rowView;

    }
}
