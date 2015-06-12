package com.classroom.applicationactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by NAPOLEON on 4/27/2015.
 */
public class NavDrawerHeader implements NavDrawerObject {

    private String label;
    private int id;

    public NavDrawerHeader(String label){
        this.label = label;
    }


    @Override
    public String getName(){
        return label;
    }

    @Override
    public int getViewType(){
        return NavDrawerAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView){
        View view;
        if(convertView == null){
            view = inflater.inflate(R.layout.nav_drawer_header, null);
        } else {
            view = convertView;
        }
        TextView text = (TextView)view.findViewById(R.id.navmenu_header);
        text.setText(label);
        return view;
    }

}
