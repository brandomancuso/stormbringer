package com.apps.brando.stormbringer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SheetsAdapter extends ArrayAdapter<Sheet> {

    LayoutInflater inflater;

    public SheetsAdapter(Context context, ArrayList<Sheet> data){
        super(context.getApplicationContext(), R.layout.sheet_list_item, data);
        inflater = LayoutInflater.from(context);
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.sheet_list_item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.list_item_character_name);
        TextView campaign = convertView.findViewById(R.id.list_item_character_campaign);
        Sheet item = super.getItem(position);
        if(item != null){
            name.setText(item.character_name);
            campaign.setText(item.campaign);
        }
        return convertView;
    }
}
