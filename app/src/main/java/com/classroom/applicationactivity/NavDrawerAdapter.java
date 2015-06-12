package com.classroom.applicationactivity;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by NAPOLEON on 4/27/2015.
 */
public class NavDrawerAdapter  extends ArrayAdapter<NavDrawerObject> {

    private LayoutInflater inflater;
    private String[] s = {""};

    public enum RowType{
        LIST_ITEM, HEADER_ITEM;
    }

    public NavDrawerAdapter (Context context, int resource, List<NavDrawerObject> items){
        super(context, resource, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount(){
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position){
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        return getItem(position).getView(inflater, convertView);
    }



}
