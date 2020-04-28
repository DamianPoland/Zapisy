package com.wolfmobileapps.zapisy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WydarzeniaArrayAdapter extends ArrayAdapter<Wydarzenie> {

    private static final String TAG = "WydarzeniaArrayAdapter";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Wydarzenie currentItem = getItem(position);

        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_adapter_main,parent,false);
        }
        TextView text1 = convertView.findViewById(R.id.textViewTytul);
        TextView text2 = convertView.findViewById(R.id.textViewData);
        TextView text3 = convertView.findViewById(R.id.textViewDystans);
        TextView text4 = convertView.findViewById(R.id.textViewUczestnicy);


        //jeśli jest wydarzenie przeniesione do historii
        boolean historia = currentItem.getWydarzenieHistoria();
        String wydarzenieTytul = currentItem.getWydarzenieTytul();
        if (historia){
            wydarzenieTytul = "Event moved to history:\n\n" + wydarzenieTytul;
        }

        text1.setText(wydarzenieTytul);
        text2.setText("Date: " + currentItem.getWydarzenieData());
        text3.setText("Distance: " + currentItem.getWydarzenieDystans() + " km");
        text4.setText("Participants: " + Math.round(currentItem.getWydarzenieUczestnicyIlosc()));
        return convertView;
    }
    public WydarzeniaArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

}
