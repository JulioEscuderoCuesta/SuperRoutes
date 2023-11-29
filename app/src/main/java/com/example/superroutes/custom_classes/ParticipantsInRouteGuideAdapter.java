package com.example.superroutes.custom_classes;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superroutes.Position;
import com.example.superroutes.R;
import com.example.superroutes.databinding.ParticipantsInRouteGuideAdapterBinding;
import com.example.superroutes.model.UserInRoute;
import com.squareup.picasso.Picasso;

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

        private ParticipantsInRouteGuideAdapterBinding binding;
        public ViewHolder(ParticipantsInRouteGuideAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(UserInRoute userInRoute) {
            binding.nameOfSenderistRouteGuide.setText(userInRoute.getName());
            binding.iconSenderistStateRouteGuide.setImageResource(R.drawable.walking);
            binding.iconSenderistLocationRouteGuide.setImageResource(R.drawable.map24x24);
            binding.iconSenderistStateRouteGuide.setImageResource(R.drawable.walking);
            if(userInRoute.getFall())
                binding.iconSenderistStateRouteGuide.setImageResource(R.drawable.falling);
            binding.profilePicSenderistRouteGuide.setImageResource(R.drawable.default_profile_pic_man);
            if(userInRoute.getImageURL() != null)
                Picasso.get().load(userInRoute.getImageURL()).into(binding.profilePicSenderistRouteGuide);

            binding.iconSenderistLocationRouteGuide.setOnClickListener(view -> {
                Context context = view.getContext();
                context.startActivity(new Intent(context, Position.class));
            });
            binding.iconSenderistStateRouteGuide.setOnClickListener(view -> {
                Context context = view.getContext();
                if(userInRoute.getFall()) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                    builder.setTitle(userInRoute.getName() + " may have fallen down! ")
                            .setMessage("Do you want to check their position?")
                            .setPositiveButton("Yes", (dialog, which) -> context.startActivity(new Intent(context, Position.class)))
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .show();
                }
                else {
                    Toast.makeText(context, "User is safe", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
