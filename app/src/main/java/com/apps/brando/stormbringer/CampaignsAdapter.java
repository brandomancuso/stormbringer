package com.apps.brando.stormbringer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CampaignsAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<Campaign> campaigns;

    public CampaignsAdapter(Context context, ArrayList<Campaign> campaigns){
        inflater = LayoutInflater.from(context);
        this.campaigns = campaigns;

    }


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return campaigns.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return campaigns.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            v = inflater.inflate(R.layout.campaign_list_item, parent, false);
        }
        Campaign campaign = (Campaign) getItem(position);
        TextView name = v.findViewById(R.id.list_item_campaign_name);
        TextView num_players = v.findViewById(R.id.list_item_campaign_numplayers);
        name.setText(campaign.campaign_name);
        num_players.setText(String.valueOf(campaign.sheets.size()));
        return v;
    }
}
