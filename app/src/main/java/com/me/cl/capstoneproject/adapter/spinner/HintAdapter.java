package com.me.cl.capstoneproject.adapter.spinner;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CL on 11/20/17.
 */

public class HintAdapter extends ArrayAdapter {
    public HintAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount()>0?super.getCount()-1:0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 修改Spinner选择后结果的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setText((CharSequence) getItem(position));
        if (position == getCount()) {
            tv.setTextColor(Color.rgb(160, 160, 160));
        } else {
            tv.setTextColor(Color.BLACK);
        }

        return convertView;
    }

}
