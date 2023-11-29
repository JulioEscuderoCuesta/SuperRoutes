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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapterRoutesGuide extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> routesNames;
    private ArrayList<String> locationsOfRoutes;
    private ArrayList<String> numberOfParticipantsSlashTotal;
    private ArrayList<String> mainImageOfRoute;
    private ArrayList<String> datesOfRoutes;

    public ListAdapterRoutesGuide(Activity context, ArrayList<String> routesNames, ArrayList<String> locationsOfRoutes, ArrayList<String> numberOfParticipantsSlashTotal, ArrayList<String> mainImageOfRoute, ArrayList<String> datesOfRoutes) {
        super(context, R.layout.listview_routes_guide, routesNames);

        this.context = context;
        this.routesNames = routesNames;
        this.locationsOfRoutes = locationsOfRoutes;
        this.numberOfParticipantsSlashTotal = numberOfParticipantsSlashTotal;
        this.mainImageOfRoute = mainImageOfRoute;
        this.datesOfRoutes = datesOfRoutes;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_routes_guide, null, true);

        TextView nameOfRouteTextView = rowView.findViewById(R.id.name_of_route_guide_routes);
        TextView numberOfParticipantsTextView = rowView.findViewById(R.id.number_of_participants_slash_total);
        TextView locationOfRouteTextView = rowView.findViewById(R.id.location_of_route_guide_routes);
        ImageView mainImageOfRouteImage = rowView.findViewById(R.id.main_image_of_route);
        TextView datesOfRouteTextView = rowView.findViewById(R.id.date_of_route_guide_routes);

        nameOfRouteTextView.setText(routesNames.get(position));
        locationOfRouteTextView.setText(locationsOfRoutes.get(position));
        numberOfParticipantsTextView.setText(numberOfParticipantsSlashTotal.get(position));
        String imageURL = mainImageOfRoute.get(position);
        if(imageURL == null)
            Picasso.get().load(R.drawable.mountain).into(mainImageOfRouteImage);
        else
            Picasso.get().load(imageURL).into(mainImageOfRouteImage);
        datesOfRouteTextView.setText(datesOfRoutes.get(position));

        return rowView;

    }
}
