package com.example.superroutes.custom_classes;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superroutes.R;
import com.example.superroutes.databinding.ParticipantsInRouteGuideAdapterBinding;
import com.example.superroutes.model.UserInRoute;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ParticipantsInRouteGuideAdapter extends RecyclerView.Adapter<ParticipantsInRouteGuideAdapter.ViewHolder> {

    private List<UserInRoute> usersInRoute;

    public ParticipantsInRouteGuideAdapter(List<UserInRoute> userInRoutes) {
        this.usersInRoute = userInRoutes;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ParticipantsInRouteGuideAdapterBinding binding = ParticipantsInRouteGuideAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(usersInRoute.get(position));
    }

    @Override
    public int getItemCount() {
        return usersInRoute.size();


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private FirebaseDatabase database;
        private DatabaseReference routesInDatabase;
        private ParticipantsInRouteGuideAdapterBinding binding;
        public ViewHolder(ParticipantsInRouteGuideAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            database = FirebaseDatabase.getInstance("https://superroutes-5378d-default-rtdb.europe-west1.firebasedatabase.app/");

        }

        public void bind(UserInRoute userInRoute) {
            binding.nameOfSenderistRouteGuide.setText(userInRoute.getName());
            binding.iconSenderistLocationRouteGuide.setImageResource(R.drawable.map24x24);
            binding.iconSenderistStateRouteGuide.setImageResource(R.drawable.walking);
            binding.profilePicSenderistRouteGuide.setImageResource(R.drawable.default_profile_pic_man);
        }
    }

}
