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

public class ListAdapterRoutesSenderist extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> namesOfRoutes;
    private ArrayList<String> datesOfRoutes;
    private ArrayList<String> mainImageOfRoutes;
    private ArrayList<Integer> routesWithGuide;
    private ArrayList<Integer> difficultyOfRoutes;

    public ListAdapterRoutesSenderist(Activity context, ArrayList<String> namesOfRoutes, ArrayList<String> datesOfRoutes, ArrayList<String> mainImageOfRoutes, ArrayList<Integer> routesWithGuide, ArrayList<Integer> difficultyOfRoutes) {
        super(context, R.layout.listview_routes_senderist, namesOfRoutes);

        this.context = context;
        this.namesOfRoutes = namesOfRoutes;
        this.datesOfRoutes = datesOfRoutes;
        this.mainImageOfRoutes = mainImageOfRoutes;
        this.routesWithGuide = routesWithGuide;
        this.difficultyOfRoutes = difficultyOfRoutes;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_routes_senderist, null, true);

        rowView.setBackgroundResource(R.drawable.route_card_background);
        TextView nameOfRouteText = rowView.findViewById(R.id.name_of_route_routes_senderist);
        TextView dateOfRouteText = rowView.findViewById(R.id.date_of_route_routes_senderist);
        ImageView mainImageOfRouteImage = rowView.findViewById(R.id.main_image_of_route_routes_senderist);
        ImageView routesWithGuideImage = rowView.findViewById(R.id.icon_guide_routes_senderist);
        ImageView difficultyOfRoutesImage = rowView.findViewById(R.id.icon_difficulty_routes_senderist);

        nameOfRouteText.setText(namesOfRoutes.get(position));
        String imageURL = mainImageOfRoutes.get(position);
        if(imageURL == null)
            Picasso.get().load(R.drawable.mountain).into(mainImageOfRouteImage);
        else
            Picasso.get().load(imageURL).into(mainImageOfRouteImage);
        routesWithGuideImage.setImageResource(routesWithGuide.get(position));
        difficultyOfRoutesImage.setImageResource(difficultyOfRoutes.get(position));
        dateOfRouteText.setText(datesOfRoutes.get(position));

        return rowView;

    }
}
