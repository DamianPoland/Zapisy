package com.wolfmobileapps.zapisy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListUserArrayAdapter extends ArrayAdapter<UserToAddToFirebase> {


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserToAddToFirebase currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_adapter_list_of_users,parent,false);
        }

        TextView text1 = convertView.findViewById(R.id.textViewListUserEmail);
        TextView text2 = convertView.findViewById(R.id.textViewListUserNameAndSurnameFromGoogle);
        TextView text3 = convertView.findViewById(R.id.textViewListUserNameAndSurname);
        TextView text4 = convertView.findViewById(R.id.textViewListUserAdres);
        TextView text5 = convertView.findViewById(R.id.textViewListUserMiasto);
        TextView text6 = convertView.findViewById(R.id.textViewListUserKodPocztowy);
        TextView text7 = convertView.findViewById(R.id.textViewListUserNrTelefonu);

        text1.setText("Email: " + currentItem.getEmailUser());
        text2.setText("Imię i Nazwisko z konta Google: " + currentItem.getNameUserFromGoogle());
        text3.setText("Imię i Nazwisko: " + currentItem.getNameAndSurnameUser());
        text4.setText("Adres: " + currentItem.getAdressUser());
        text5.setText("Miasto: " + currentItem.getCityUser());
        text6.setText("Kod pocztowy: " + currentItem.getPostCodeUser());
        text7.setText("Nr telefonu: " + currentItem.getTelephoneUser());

        return convertView;
    }

    public ListUserArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}



