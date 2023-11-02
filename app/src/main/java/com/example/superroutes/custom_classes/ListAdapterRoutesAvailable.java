package com.example.superroutes.custom_classes;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superroutes.R;
import com.example.superroutes.interfaces.OnItemRecyclerViewClickListener;
import com.example.superroutes.model.RouteInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapterRoutesAvailable extends RecyclerView.Adapter<ListAdapterRoutesAvailable.MyHolder> {

    private final Activity context;
    private ArrayList<RouteInformation> arrayList;
    private LayoutInflater layoutInflater;
    private OnItemRecyclerViewClickListener clickListener;

    public ListAdapterRoutesAvailable(Activity context, ArrayList<RouteInformation> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemRecyclerViewClickListener(OnItemRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateData(ArrayList<RouteInformation> newData) {
        arrayList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_view_routes_available, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        RouteInformation routeInformation = arrayList.get(position);

        //Asign data to each element
        holder.nameOfRoute.setText(routeInformation.getName());
        holder.locationOfRoute.setText(routeInformation.getLocation());
        Picasso.get().load(routeInformation.getImageURL()).into(holder.imageURLOfRoute);
        addDifficultyToTheList(routeInformation, holder);

        //Add onClickListener to each element
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null)
                clickListener.onItemClick(routeInformation);
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        private TextView nameOfRoute, locationOfRoute;
        private ImageView imageURLOfRoute, difficultyOfRoute;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            nameOfRoute = itemView.findViewById(R.id.name_route_routes_available);
            locationOfRoute = itemView.findViewById(R.id.location_route_routes_available);
            imageURLOfRoute = itemView.findViewById(R.id.image_route_routes_available);
            difficultyOfRoute = itemView.findViewById(R.id.difficulty_route_routes_available);
        }
    }

    private void addDifficultyToTheList(RouteInformation routeInformation, MyHolder holder) {
        switch (routeInformation.getDifficulty()) {
            case "EASY":
                holder.difficultyOfRoute.setImageResource(R.drawable.difficulty_easy);
                break;
            case "MEDIUM":
                holder.difficultyOfRoute.setImageResource(R.drawable.difficulty_moderate);
                break;
            case "HARD":
                holder.difficultyOfRoute.setImageResource(R.drawable.difficulty_hard);
                break;
            case "EXPERIENCED":
                holder.difficultyOfRoute.setImageResource(R.drawable.difficulty_expert);
                break;
        }
    }
}
