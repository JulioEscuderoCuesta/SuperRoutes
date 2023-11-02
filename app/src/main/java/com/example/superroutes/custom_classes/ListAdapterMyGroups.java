package com.example.superroutes.custom_classes;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.superroutes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapterMyGroups extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> groups;
    private ArrayList<String> participantsInGroups;
    private ArrayList<String> imagesOfGroups;
    private ArrayList<String> datesLastMessage;



    public ListAdapterMyGroups(Activity context, ArrayList<String> groups, ArrayList<String> participantsInGroups, ArrayList<String> imagesOfGroups, ArrayList<String> datesLastMessage) {
        super(context, R.layout.listview_my_groups, groups);

        this.context = context;
        this.groups = groups;
        this.participantsInGroups = participantsInGroups;
        this.imagesOfGroups = imagesOfGroups;
        this.datesLastMessage = datesLastMessage;

    }

    public View getView(int position, View view, ViewGroup parent) {
        Log.d("position", String.valueOf(position));
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_my_groups, null, true);

        TextView groupTextView = rowView.findViewById(R.id.name_of_the_group);
        TextView listOfParticipantsTextView = rowView.findViewById(R.id.list_of_participants_group);
        TextView dateLastMessageTextView = rowView.findViewById(R.id.date_last_message);
        ImageView imageGroupImageView = rowView.findViewById(R.id.image_group);

        //
        StringBuilder stringBuilder = new StringBuilder();
        groupTextView.setText(groups.get(position));

        int index = searchOcurrence("/", position);

        for(int i = index + 1; i < participantsInGroups.size(); i++) {
            if(!participantsInGroups.get(i).equals("/")) {
                stringBuilder.append(participantsInGroups.get(i));
                stringBuilder.append(", ");
            }
            else {
                break;
            }
        }
        stringBuilder = new StringBuilder(stringBuilder.toString().trim());
        stringBuilder.deleteCharAt(stringBuilder.length()-1);

        listOfParticipantsTextView.setText(stringBuilder.toString());
        dateLastMessageTextView.setText(datesLastMessage.get(position));
        String imageURL = imagesOfGroups.get(position);
        if(imageURL == null)
            Picasso.get().load(R.drawable.mountain).into(imageGroupImageView);
        else
            Picasso.get().load(imageURL).into(imageGroupImageView);
        return rowView;

    }

    private int searchOcurrence(String searchString, int position) {
        int index = -1;
        int ocurrence = 0;

        // Search from current position until the ocurrence is found
        for (int i = 0; i < participantsInGroups.size(); i++) {
            if (participantsInGroups.get(i).equals(searchString)) {
                ocurrence++;
                if (ocurrence == position) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

}

