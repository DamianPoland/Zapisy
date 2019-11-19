package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;


import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_UCZESTNICY;
import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_WYDARZENIE;
import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_CENA;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_DATA;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_DYSTANS;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_HISTORIA;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_NAZWA_COLLECTION;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_OPIS;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_REGULAMIN;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_TYTUL;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_UCZESTNICY_ILOSC;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_USER_EMAIL;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_USER_NAME;


public class ActivityWyniki extends AppCompatActivity {

    private static final String TAG = "ActivityWyniki";

    //views
    private TextView textViewTytulWyniki;
    private ImageView imageViewMapaWyniki;
    private TextView textViewActivityWydarzenieDystansWyniki;
    private ProgressBar progressBarWyniki;
    private ListView listViewWyniki;

    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;


    // dane pobrane z Intent
    private String tytul;
    private String dbNameCollection;
    private String data;
    private float cena;
    private String opis;
    private String regulamin;
    private float dystans;
    private float uczestnicyIlosc;
    private boolean historia;
    private String userName;
    private String userEmail;

    // listMain view arraylist i adapter
    private ArrayList<DaneTrasy> listMainWyniki;
    private WynikiArrayAdapter adapter;

    //do Firebase Database
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyniki);


        //views
        textViewTytulWyniki = findViewById(R.id.textViewTytulWyniki);
        imageViewMapaWyniki = findViewById(R.id.imageViewMapaWyniki);
        textViewActivityWydarzenieDystansWyniki = findViewById(R.id.textViewActivityWydarzenieDystansWyniki);
        progressBarWyniki = findViewById(R.id.progressBarWyniki);
        listViewWyniki = findViewById(R.id.listViewWyniki);

        // do Shared Prefereneces instancja
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        // pobranie danych z shared pref
        dbNameCollection = shar.getString(TO_ACTIVITY_WYDARZENIE_NAZWA_COLLECTION, "Error");
        tytul = shar.getString(TO_ACTIVITY_WYDARZENIE_TYTUL, "Tytuł");
        data = shar.getString(TO_ACTIVITY_WYDARZENIE_DATA, "Data");
        cena = shar.getFloat(TO_ACTIVITY_WYDARZENIE_CENA, 0);
        opis = shar.getString(TO_ACTIVITY_WYDARZENIE_OPIS, "opis");
        regulamin = shar.getString(TO_ACTIVITY_WYDARZENIE_REGULAMIN, "regulamin");
        dystans = shar.getFloat(TO_ACTIVITY_WYDARZENIE_DYSTANS, 0.0f);
        uczestnicyIlosc = shar.getFloat(TO_ACTIVITY_WYDARZENIE_UCZESTNICY_ILOSC, 0.0f);
        historia = shar.getBoolean(TO_ACTIVITY_WYDARZENIE_HISTORIA, false);
        userName = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "User name");
        userEmail = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, "User e-mail");

        // ustawienie textViews
        textViewTytulWyniki.setText(tytul);
        textViewActivityWydarzenieDystansWyniki.setText("Dystans: " + dystans + " km");

        //wyświetlenie w listView z Wynikami
        listMainWyniki = new ArrayList<>();
        adapter = new WynikiArrayAdapter(this, 0, listMainWyniki);
        listViewWyniki.setAdapter(adapter);

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        // Read only once Collection - nie trzeba go potem wyłączać  (onCompleateListener):
        db.collection(COLLECTION_NAME_WYDARZENIE).document(dbNameCollection).collection(COLLECTION_NAME_UCZESTNICY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                //pobranie danych z listenera
                                String userEmail = (String) document.getData().get("userEmail");
                                float distance = Float.parseFloat( "" + document.getData().get("distance"));
                                String fullTime = (String) document.getData().get("fullTime");
                                float speed = Float.parseFloat( "" +  document.getData().get("speed"));
                                String mapPoints = (String) document.getData().get("mapPoints");

                                //dodanie danych do adaptera żeby wyświetlił w list View
                                DaneTrasy daneTrasy = new DaneTrasy(userEmail, distance, fullTime, speed, mapPoints);
                                adapter.add(daneTrasy);

                                //sortowanie tableki po FullTime który jest daną klasy DanaTrasy
                                Comparator<DaneTrasy> compareByTime = (DaneTrasy o1, DaneTrasy o2) -> o1.getFullTime().compareTo( o2.getFullTime() );
                                Collections.sort(listMainWyniki, compareByTime);
                                adapter.notifyDataSetChanged();

                                //wyłaczenie  progress bara
                                progressBarWyniki.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        // buttony
        imageViewMapaWyniki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //otwarcie mapy ze wszystkimi trrasami

            }
        });

    }
}
