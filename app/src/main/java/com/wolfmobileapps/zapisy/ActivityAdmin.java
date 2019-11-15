package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_WYDARZENIE;

public class ActivityAdmin extends AppCompatActivity {
    private static final String TAG = "ActivityAdmin";


    //views
    ProgressBar progressBarWaiForFirebaseAdmin;


    //do Firebase Database
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    // listMain view arraylist i adapter
    private ListView listViewWydarzeniaAdmin;
    private ArrayList<Wydarzenie> listMain;
    private WydarzeniaArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // views
        progressBarWaiForFirebaseAdmin= findViewById(R.id.progressBarWaiForFirebaseAdmin);
        listViewWydarzeniaAdmin = findViewById(R.id.listViewWydarzeniaAdmin);

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();


        // przykładowe wydarzenie
//        Wydarzenie wyd1 = new Wydarzenie("Czwartki Bojanie - jesień 2019, konkurs 5", "2019-10-12", 0, "Zawody w 3 kategoriach wiekowych:\n11 lat i młodsi (rocznik 2009), 12 lat (rocznik 2008), 13 lat (rocznik 2007)", "Przyklądowy regulamin", 12, 180, false);
//        Wydarzenie wyd2 = new Wydarzenie("Przyjaźń - Małe Kaszuby Biegają 2019", "2019-09-12", 0, "Zapraszamy do udziału w II Paradzie Rowerowej w ramach VI Festiwalu Zdrowia! To krótki, 10-kilometrowy przejazd dla całych rodzin! Trasa wiedzie przez Przyjaźń 2007)", "Przyklądowy regulamin", 50, 810, false);
//        Wydarzenie wyd3 = new Wydarzenie("II Kaszubska Prada Rowerowa\", \"2019-10-12 10:50 Przyjaźń ", "2019-10-11", 0, "Opłać swój udział i wystartuj w całym cyklu Kaszuby Biegają 2019", "Przyklądowy regulamin", 56.5f, 80, false);
//        Wydarzenie wyd4 = new Wydarzenie("VII Bieg Przyjaźni\", \"2019-10-12 11:00 Przyjaźń k. Żukowa", "2018-10-12", 1, "15 km, 10 km, 5 km, 21 km i 1,6 km na zakończenie. Weź udział w Biegowej Imprezie Marzeń! Zwolnij i odpocznij we Wdzydzach na Kaszubach od 12 ", "Przyklądowy regulamin", 15, 90, false);
//        Wydarzenie wyd5 = new Wydarzenie("Marsz Nordic Walking - Festiwal Zdrowia", "2019-01-12", 1.50f, "Zawody w 3 kategoriach wiekowych:\n11 lat i młodsi (rocznik 2009), 12 lat (rocznik 2008), 13 lat (rocznik 2007)", "Przyklądowy regulamin", 22, 550, false);
//        Wydarzenie wyd6 = new Wydarzenie("Hard Runner - IV Festiwal Biegów Polski Północnej", "2019-10-14", 0, "awodów, szatnie: Przyjaźń, ul. Szkolna 2, czynne od godz. 08:30\nStart i Meta: Przyjaźń, ul. Szkolna 2", "Przyklądowy regulamin", 7, 280, false);

        //dodanie wydarzenia do firestore
//        addDataToFirestore(COLLECTION_NAME_WYDARZENIE, wyd1.getWydarzenieTytul(), wyd1);
//        addDataToFirestore(COLLECTION_NAME_WYDARZENIE, wyd2.getWydarzenieTytul(), wyd2);
//        addDataToFirestore(COLLECTION_NAME_WYDARZENIE, wyd3.getWydarzenieTytul(), wyd3);
//        addDataToFirestore(COLLECTION_NAME_WYDARZENIE, wyd4.getWydarzenieTytul(), wyd4);
//        addDataToFirestore(COLLECTION_NAME_WYDARZENIE, wyd5.getWydarzenieTytul(), wyd5);
//        addDataToFirestore(COLLECTION_NAME_WYDARZENIE, wyd6.getWydarzenieTytul(), wyd6);

        //wyświetlenie w listView z Firebas
        listMain = new ArrayList<>();
        adapter = new WydarzeniaArrayAdapter(this, 0, listMain);
        listViewWydarzeniaAdmin.setAdapter(adapter);

        //słucha wszystkiego cosię dzieje wdanym folderze tu "Wydarzenia", odpala się za każdym razem
        CollectionReference query = db.collection(COLLECTION_NAME_WYDARZENIE);
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
                            // wyłączenie progress bara
                            progressBarWaiForFirebaseAdmin.setVisibility(View.GONE);

                            //dodanie kolejnych wydarzeń
                            String wydarzenieTytul = (String) dc.getDocument().getData().get("wydarzenieTytul");
                            String wydarzenieData = (String) dc.getDocument().getData().get("wydarzenieData");
                            float wydarzenieCena = Float.parseFloat("" + dc.getDocument().getData().get("wydarzenieCena"));
                            String wydarzenieOpis = (String) dc.getDocument().getData().get("wydarzenieOpis");
                            String wydarzenieRegulamin = (String) dc.getDocument().getData().get("wydarzenieRegulamin");
                            float wydarzenieDystans = Float.parseFloat("" + dc.getDocument().getData().get("wydarzenieDystans"));
                            float wydarzenieUczestnicyIlosc = Float.parseFloat("" + dc.getDocument().getData().get("wydarzenieUczestnicyIlosc"));
                            boolean wydarzenieHistoria = Boolean.parseBoolean("" + dc.getDocument().getData().get("wydarzenieHistoria"));

                            Wydarzenie wydarzenie = new Wydarzenie(wydarzenieTytul, wydarzenieData, wydarzenieCena, wydarzenieOpis, wydarzenieRegulamin, wydarzenieDystans, wydarzenieUczestnicyIlosc, wydarzenieHistoria);
                            adapter.add(wydarzenie);


                            Log.d(TAG, "onEvent: ________" + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                            break;
                    }
                }

            }
        });

        //onClick listener na listMain View
        listViewWydarzeniaAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ActivityAdmin.this, ActivityAdminAddEditWydarzenie.class);
                startActivity(intent);
            }
        });

    }


    // dodawanie wydarzenia do firestore
    public void addDataToFirestore(String nameWydarzenieCollection, String documentKey, Wydarzenie objectWyd) {
        // dodawanie wydarzenia
        db.collection(nameWydarzenieCollection).document(documentKey) // key bedzie exampleKey
                .set(objectWyd) // wyd1 to obiekt który ma być dodany
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ActivityAdmin.this, "Wydarzenie dodane", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityAdmin.this, "Error - wydarzenie nie dodane", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // do górnego menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dodaj:
                startActivity(new Intent(ActivityAdmin.this, ActivityAdminAddEditWydarzenie.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // do górnego menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
