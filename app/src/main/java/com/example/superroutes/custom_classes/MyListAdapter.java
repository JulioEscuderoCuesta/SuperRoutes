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

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> routes;
    private ArrayList<Integer> images;

    public MyListAdapter(Activity context, ArrayList<String> routes, ArrayList<Integer> images) {
        super(context, R.layout.mylistview_routes, routes);

        this.context=context;
        this.routes=routes;
        this.images=images;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylistview_routes, null,true);

        TextView titleText = rowView.findViewById(R.id.title);
        ImageView imageView = rowView.findViewById(R.id.icon);

        titleText.setText(routes.get(position));
        imageView.setImageResource(images.get(position));

        return rowView;

    };
}
