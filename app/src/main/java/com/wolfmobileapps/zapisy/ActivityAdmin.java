package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;


public class ActivityAdmin extends AppCompatActivity {
    private static final String TAG = "ActivityAdmin";


    //views
    ProgressBar progressBarWaiForFirebaseAdmin;

    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

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

        // do Shared Prefereneces instancja
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        //wyświetlenie w listView z Firebas
        listMain = new ArrayList<>();
        adapter = new WydarzeniaArrayAdapter(this, 0, listMain);
        listViewWydarzeniaAdmin.setAdapter(adapter);

        //onClick listener na listMain View
        listViewWydarzeniaAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Do otwarcia next activity z list view - pobranie danych z danego itema wydarzenia i zapisanie danych  o wydarzeniu do shared pref
                Wydarzenie wydarzenie = new Wydarzenie();
                wydarzenie.saveDataInSharedPref(ActivityAdmin.this, listMain, position);

                // otwarcie tego activity i wskazanie że to edycja a nie nowy
                Intent intent = new Intent(ActivityAdmin.this, ActivityAdminAddEditWydarzenie.class);
                intent.putExtra("edycja", true);
                startActivity(intent);
            }
        });

    }

    // metoda dodaje listenera i słucha wszystkiego cosię dzieje wdanym folderze "Wydarzenia"
    private void addListenerToFirebaseWydarzenia () {
        adapter.clear(); // wyczyszczenie adaptera przed dodaniem od nowa listenera

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
                            String wydarzenieNazwaCollection = (String) dc.getDocument().getData().get("wydarzenieNazwaCollection");
                            String wydarzenieTytul = (String) dc.getDocument().getData().get("wydarzenieTytul");
                            String wydarzenieData = (String) dc.getDocument().getData().get("wydarzenieData");
                            float wydarzenieCena = Float.parseFloat("" + dc.getDocument().getData().get("wydarzenieCena"));
                            String wydarzenieOpis = (String) dc.getDocument().getData().get("wydarzenieOpis");
                            String wydarzenieRegulamin = (String) dc.getDocument().getData().get("wydarzenieRegulamin");
                            float wydarzenieDystans = Float.parseFloat("" + dc.getDocument().getData().get("wydarzenieDystans"));
                            float wydarzenieUczestnicyIlosc = Float.parseFloat("" + dc.getDocument().getData().get("wydarzenieUczestnicyIlosc"));
                            boolean wydarzenieHistoria = Boolean.parseBoolean("" + dc.getDocument().getData().get("wydarzenieHistoria"));

                            Wydarzenie wydarzenie = new Wydarzenie(wydarzenieNazwaCollection, wydarzenieTytul, wydarzenieData, wydarzenieCena, wydarzenieOpis, wydarzenieRegulamin, wydarzenieDystans, wydarzenieUczestnicyIlosc, wydarzenieHistoria);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        //metoda dodaje listenera i słucha wszystkiego cosię dzieje wdanym folderze "Wydarzenia"
        addListenerToFirebaseWydarzenia();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // wyłaczenie listenera
        registration.remove();
    }

    // do górnego menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dodaj:
                Intent intent = new Intent(ActivityAdmin.this, ActivityAdminAddEditWydarzenie.class);
                intent.putExtra("nowy", true);
                startActivity(intent);
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
