package com.example.all4win.flightchess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by All4win on 3/31/16.
 */
public class RoomAdapter extends ArrayAdapter<pRoom> {
    private int resource;
    public RoomAdapter(Context context, int ResourceId, List<pRoom> objects) {
        super(context, ResourceId, objects);
        resource = ResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout li;
        final pRoom pR = getItem(position);
        if(convertView == null) {
            li = new LinearLayout(getContext());
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(resource, li, true);
        } else {
            li = (LinearLayout)convertView;
        }
        TextView roomNum = (TextView)li.findViewById(R.id.room_num);
        TextView roomState = (TextView)li.findViewById(R.id.room_state);
        roomNum.setText(pR.getRoomName());
        roomState.setText(pR.getState());
        return li;
    }


}
