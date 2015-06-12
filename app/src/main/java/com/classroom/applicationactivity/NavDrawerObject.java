package com.classroom.applicationactivity;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by NAPOLEON on 4/27/2015.
 */
public interface NavDrawerObject {

    public String getName();
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}
