package com.wolfmobileapps.zapisy;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;


import static com.wolfmobileapps.zapisy.ActivityWydarzenie.DEF_VALUE_TO_SET_FULL_TIME;
import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_UCZESTNICY;
import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_WYDARZENIE;
import static com.wolfmobileapps.zapisy.MainActivity.KEY_MAP_POINTS_TO_INTENT_OPEN_MAP_LIST;
import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.zapisy.MainActivity.TABLE_OF_ADMINS;
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

    // lista ze wszystkimi trasami w Stringach
    private ArrayList<String> listOfRouds;

    //do Firebase Database
    private FirebaseFirestore db;
    private  ListenerRegistration registration;




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
        textViewActivityWydarzenieDystansWyniki.setText("Distance: " + dystans + " km");

        // lista ze wszystkimi trasami w Stringach
        listOfRouds = new ArrayList<>();

        //wyświetlenie w listView z Wynikami
        listMainWyniki = new ArrayList<>();
        adapter = new WynikiArrayAdapter(this, 0, listMainWyniki);
        listViewWyniki.setAdapter(adapter);

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        listViewWyniki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //ustawienie widoczności guzika dla admina
                String emailAdmin = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, "");
                if (emailAdmin.equals(TABLE_OF_ADMINS[0])) {


                    // alert z pytaniem czy usunąć wynik
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWyniki.this);
                    builder.setTitle("Deletind");
                    builder.setMessage("Are you sure you want to delete this result from the database?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // pobiera email użytkownika do usunięcia który też jest ścieszka
                            DaneTrasy currenDaneTrasy = listMainWyniki.get(position);
                            String userEmail = currenDaneTrasy.getUserEmail();

                            // usuwa trasę użytkownika tylko z wydarzenia, w user zostaje żeby user miał do niej wgląd ale już wynikach się nie pokazuje bo tabela jest czytana zwyników
                            DaneTrasy daneTrasy = new DaneTrasy();
                            daneTrasy.deleteDataDataFirebase(ActivityWyniki.this,COLLECTION_NAME_WYDARZENIE, dbNameCollection, COLLECTION_NAME_UCZESTNICY, userEmail);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // nic nie robi
                        }
                    }).create();
                    builder.show();



                }
            }
        });

        // buttony
        imageViewMapaWyniki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //otwarcie mapy ze wszystkimi trrasami czyli z listą tras
                Intent intent = new Intent(ActivityWyniki.this, ActivityMaps.class);
                intent.putExtra(KEY_MAP_POINTS_TO_INTENT_OPEN_MAP_LIST, listOfRouds);
                startActivity(intent);
            }
        });

    }

    // włacznie listenera żeby słuchał wszystkiego co się dzieje i wczytywał do list view ( nasłuchuje cały czas wiec trzeba wyłaczyć potem)
    private void readEncoreFromDb () {

        // wyczyszczenie adaptera przed dodaniem od nowego listenera
        adapter.clear();

        // wyłaczenie listenera jeśli już jest odpalony - inaczej działają dwa na raaz i sei d ublują
        if (registration != null){
            registration.remove();
        }

        //wyczyszczenie listy tras jeśli coś było
        if (listOfRouds.size() != 0){
            listOfRouds.clear();
        }

        //słucha wszystkiego cosię dzieje wdanym folderze, odpala się za każdym razem
        CollectionReference query = db.collection(COLLECTION_NAME_WYDARZENIE).document(dbNameCollection).collection(COLLECTION_NAME_UCZESTNICY);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "city listen:error", e);
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New city: " + dc.getDocument().getData());

                            //pobranie danych z listenera
                            String userEmail = (String) dc.getDocument().getData().get("userEmail");
                            float distance = Float.parseFloat( "" + dc.getDocument().getData().get("distance"));
                            String fullTime = (String) dc.getDocument().getData().get("fullTime");
                            float speed = Float.parseFloat( "" +  dc.getDocument().getData().get("speed"));
                            String mapPoints = (String) dc.getDocument().getData().get("mapPoints");

                            // jeśli czas nie będzie zero to doda do adaptera
                            if (!fullTime.equals(DEF_VALUE_TO_SET_FULL_TIME)) {

                                //dodanie danych do adaptera żeby wyświetlił w list View
                                DaneTrasy daneTrasy = new DaneTrasy(userEmail, distance, fullTime, speed, mapPoints);
                                adapter.add(daneTrasy);

                                //sortowanie tableki po FullTime który jest daną klasy DanaTrasy
                                Comparator<DaneTrasy> compareByTime = (DaneTrasy o1, DaneTrasy o2) -> o1.getFullTime().compareTo( o2.getFullTime() );
                                Collections.sort(listMainWyniki, compareByTime);
                                adapter.notifyDataSetChanged();

                                // dodanie trasy do listy ze stringami
                                listOfRouds.add(mapPoints);
                            }

                            //wyłaczenie  progress bara
                            progressBarWyniki.setVisibility(View.GONE);

                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed city: " + dc.getDocument().getData());

                            // jak usunięto element to uruchamia cała bazę od nowa
                            readEncoreFromDb();
                            break;
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //metoda dodaje listenera i słucha wszystkiego cosię dzieje w danym folderze "Wydarzenia"
        readEncoreFromDb();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // wyłaczenie listenera
        registration.remove();

    }

}

