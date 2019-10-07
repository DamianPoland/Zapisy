package com.wolfmobileapps.zapisy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WydarzeniaArrayAdapter extends ArrayAdapter<Wydarzenie> {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Wydarzenie currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_adapter_main,parent,false);
        }
        TextView text1 = convertView.findViewById(R.id.textViewTytul);
        TextView text2 = convertView.findViewById(R.id.textViewMiejsceICzas);
        text1.setText(currentItem.getTytul());
        text2.setText(currentItem.getMiejsceICzas());
        return convertView;
    }
    public WydarzeniaArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

}
