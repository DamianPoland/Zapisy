package com.wolfmobileapps.zapisy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WynikiArrayAdapter extends ArrayAdapter<DaneTrasy> {


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DaneTrasy currentItem = getItem(position);

        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_adapter_wyniki,parent,false);
        }
        TextView text1 = convertView.findViewById(R.id.textViewWynikiMiejsce);
        TextView text2 = convertView.findViewById(R.id.textViewWynikiUserNameOrEmailAdres);
        TextView text3 = convertView.findViewById(R.id.textViewWynikiUserCzas);


        text1.setText("" + (position +1) );
        String imie = currentItem.getUserEmail();
        String[] parts = imie.split("@");
        String imiePart1 = parts[0];
        text2.setText("Imię: " + imiePart1);
        text3.setText("Czas: " + currentItem.getFullTime() + " s");

        return convertView;
    }

    public WynikiArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }


}
