package com.example.superroutes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superroutes.model.Message;
import com.example.superroutes.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter{
    private Context context;
    private ArrayList<Message> messagesAdapterArrayList;

    int ITEM_SEND=1;
    int ITEM_RECIVE=2;
    public MessagesAdapter(Context context, ArrayList<Message> messagesAdapterArrayList) {
        this.context = context;
        this.messagesAdapterArrayList = messagesAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messagesAdapterArrayList.get(position);
        FirebaseFirestore.getInstance().collection("Users").document(message.getUser()).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (holder.getClass()==senderViewHolder.class){
                senderViewHolder viewHolder = (senderViewHolder) holder;
                viewHolder.messageText.setText(message.getText());
                Picasso.get().load(R.drawable.default_profile_pic_man).into(viewHolder.circleImageView);
                if(user.getImageURL() != null)
                    Picasso.get().load(user.getImageURL()).into(viewHolder.circleImageView);
            }else { receiverViewHolder viewHolder = (receiverViewHolder) holder;
                viewHolder.messageText.setText(message.getText());
                Picasso.get().load(R.drawable.default_profile_pic_man).into(viewHolder.circleImageView);
                if(user.getImageURL() != null)
                    Picasso.get().load(user.getImageURL()).into(viewHolder.circleImageView);
            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        Message message = messagesAdapterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getUser())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdapterArrayList.size();
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImageView;
        TextView messageText;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.picture_sender);
            messageText = itemView.findViewById(R.id.message_sender);

        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImageView;
        TextView messageText;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.picture_receiver);
            messageText = itemView.findViewById(R.id.message_receiver);
        }
    }
}
