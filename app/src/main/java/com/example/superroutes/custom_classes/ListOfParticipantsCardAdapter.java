package com.example.superroutes.custom_classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.superroutes.R;

import java.util.List;

public class ListOfParticipantsCardAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mItems;

    public ListOfParticipantsCardAdapter(List<String> items, Context context) {
        mItems = items;
        mContext = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_show_information_route_guide, parent, false);
        }

        String item = (String) getItem(position);
        Log.d("item", item);
        TextView textView = (TextView) convertView;
        textView.setText(item);

        return convertView;
    }
}