package com.classroom.applicationactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by NAPOLEON on 4/27/2015.
 */
public class NavDrawerItem implements NavDrawerObject {

    private String label;
    private int id;

    public NavDrawerItem(String label){
        this.label = label;
    }


    @Override
    public String getName(){
        return label;
    }

    @Override
    public int getViewType(){
        return NavDrawerAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView){
        View view;
        if(convertView == null){
            view = inflater.inflate(R.layout.nav_drawer_item, null);
        } else {
            view = convertView;
        }
        ImageView icon = (ImageView) view.findViewById(R.id.navmenuitem_icon);
        if(label.equalsIgnoreCase("Camera shot")){
            icon.setImageResource(R.drawable.camera);
        } else if (label.equalsIgnoreCase("Group chat")) {
            icon.setImageResource(R.drawable.chat);
        } else if (label.equalsIgnoreCase("New File...")){
            icon.setImageResource(R.drawable.new_file);
        } else {
            icon.setImageResource(R.drawable.folder);
        }
        TextView text = (TextView)view.findViewById(R.id.navmenuitem_label);
        text.setText(label);
        return view;
    }


}
