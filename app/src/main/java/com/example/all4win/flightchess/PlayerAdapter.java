package com.example.all4win.flightchess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by All4win on 5/9/16.
 */
public class PlayerAdapter extends ArrayAdapter<Player> {

    private int resource;
    public PlayerAdapter(Context context, int ResourceId, List<Player> objects) {
        super(context, ResourceId, objects);
        resource = ResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout li;
        final Player item = getItem(position);
        if(convertView == null) {
            li = new LinearLayout(getContext());
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(resource, li, true);
        } else {
            li = (LinearLayout)convertView;
        }
        TextView player_name = (TextView)li.findViewById(R.id.player_name);
        ImageView player_img = (ImageView)li.findViewById(R.id.player_img);
        TextView player_host = (TextView)li.findViewById(R.id.player_host);
        player_name.setText(item.getUser_name());
        player_img.setImageResource(item.getImg_id());
        player_host.setText(item.getHost());
        return li;
    }
}
