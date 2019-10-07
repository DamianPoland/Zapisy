package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //stałe do przeniesienia do activity wydarzenie
    public static final String TO_ACTIVITY_WYDARZENIE_TYTUL = "to activity wydarzenie tytul";
    public static final String TO_ACTIVITY_WYDARZENIE_MIEJSCE_I_CZAS = "to activity wydarzenie miejsceICzas";
    public static final String TO_ACTIVITY_WYDARZENIE_OPIS = "to activity wydarzenie opis";
    public static final String TO_ACTIVITY_WYDARZENIE_CENA = "to activity wydarzenie cena";
    public static final String TO_ACTIVITY_WYDARZENIE_USER_NAME = "to activity wydarzenie user name";
    public static final String TO_ACTIVITY_WYDARZENIE_USER_EMAIL = "to activity wydarzenie user email";

    //stałe inne
    public static final String SHARED_PREFERENCES_NAME = "zapisy shared preferences";

    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //Views
    private TextView textViewWitaj;
    private TextView textViewOpisOgolny;
    private TextView textViewWydarzenia;

    //do Firebase Database
    private DatabaseReference myRef;

    // do Firebase Authetication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;

    // listMain view arraylist i adapter
    private ListView listViewWydarzenia;
    private ArrayList<Wydarzenie> listMain;
    private WydarzeniaArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // do Shared Prefereneces instancja
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        //views
        textViewWitaj = findViewById(R.id.textViewWitaj);
        textViewOpisOgolny = findViewById(R.id.textViewOpisOgolny);
        textViewWydarzenia = findViewById(R.id.textViewWydarzenia);
        listViewWydarzenia = findViewById(R.id.listViewWydarzenia);

        // ustawienie textu na textView Witaj, jeśli użytkownik ma zapisane imię to się wyświetli
        textViewWitaj.setText("Witaj!");
        if (!shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "none").equals("none")) {
            textViewWitaj.setText("Witaj " + shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "none") + " !");
        }

        // ustawienie textu na textView OpisOgólny
        textViewOpisOgolny.setText("Zapisy to aplikacja do zapisów online. \nZnajdziesz tu aktualne wydarzenia z całej Polski. \nAby się zapisać lud dowiedzieć więcej kliknij w wydarzenie.");

        // ustawienie textu na textView Wydarzenia
        textViewWydarzenia.setText("Wydarzenia:");

        // do Firebase instancja Database
        myRef = FirebaseDatabase.getInstance().getReference("Wydarzenia");

        //zapis w firebase
//        Wydarzenie wyd1 = new Wydarzenie("Czwartki lekkoatletyczne w Bojanie - jesień 2019, konkurs 4", "Bojano 2019-10-03 17:00", "opis: cośtam1", 55);
//        Wydarzenie wyd2 = new Wydarzenie("II Kaszubska Prada Rowerowa", "Przyjaźń 2019-10-12 10:50", "opis: cośtam2", 2);
//        Wydarzenie wyd3 = new Wydarzenie("Przyjaźń - Małe Kaszuby Biegają 2019", "Przyjaźń k. Żukowa 2019-10-12 09:30", "opis: cośtam3", 47);
//        Wydarzenie wyd4 = new Wydarzenie("Marsz Nordic Walking - Festiwal Zdrowia", "Przyjaźń 2019-10-12 11:07", "opis: cośtam4", 0);
//        Wydarzenie wyd5 = new Wydarzenie("Hard Runner - IV Festiwal Biegów Polski Północnej", "Wdzydze k. Kościerzyny 2020-06-12 19:30", "opis: cośtam5", 128);
//        myRef.child("wyd1").setValue(wyd1);
//        myRef.child("wyd2").setValue(wyd2);
//        myRef.child("wyd3").setValue(wyd3);
//        myRef.child("wyd4").setValue(wyd4);
//        myRef.child("wyd5").setValue(wyd5);


        //wyświetlenie w listView z Firebas
        listMain = new ArrayList<>();
        adapter = new WydarzeniaArrayAdapter(this, 0, listMain);
        listViewWydarzenia.setAdapter(adapter);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Wydarzenie wydarzenie = dataSnapshot.getValue(Wydarzenie.class);
                adapter.add(wydarzenie);
            } //działa gdy jest child dodany a za pierwszym razem dla każdego child czyli sam robi pentlę

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            } //działa gdy jest child zmieniony

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }  //działa gdy jest child usunięty

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }  //działa gdy jest child przesuniety

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }  //działa gdy coś poszło nie tak np brak dostępu do db a chce sie coś zmienić
        });


        //onClick listener na listMain View
        listViewWydarzenia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // metoda do logowania
                logOrRegisterToFirebase(position);
            }
        });


    } //koniec onCreate_____________________________________________________________________


    // metoda sprawdzająca czy ktoś jest zalogowany i jak jest to jego imię zapisuje do dhared pref a jak nie to przenosi na stronę logowania
    private void logOrRegisterToFirebase(final int position) {

        //do Firebase instancja Authentication czyli logowanie
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    // pobranie imienia i emailu użytkownika po zarejestrowaniu
                    String userName = user.getDisplayName();
                    String userEmail = user.getEmail();
                    userEmail = userEmail.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("]", ""); //nazwa w firebse nie moze zawierać takiego znaku

                    //pobranie danych z danego itema wydarzenia przekazane jest w metodzie z listViewWydarzenia.setOnItemClickListener
                    Wydarzenie currentWydarzenie = listMain.get(position);
                    String tytul = currentWydarzenie.getTytul();
                    String miejsceICzas = currentWydarzenie.getMiejsceICzas();
                    String opis = currentWydarzenie.getOpis();
                    int cena = currentWydarzenie.getCena();

                    //zapisanie wszystkich danych do shared pref
                    editor = shar.edit(); //wywołany edytor do zmian
                    editor.putString(TO_ACTIVITY_WYDARZENIE_TYTUL, tytul);
                    editor.putString(TO_ACTIVITY_WYDARZENIE_MIEJSCE_I_CZAS, miejsceICzas);
                    editor.putString(TO_ACTIVITY_WYDARZENIE_OPIS, opis);
                    editor.putInt(TO_ACTIVITY_WYDARZENIE_CENA, cena);
                    editor.putString(TO_ACTIVITY_WYDARZENIE_USER_NAME, userName);
                    editor.putString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, userEmail);
                    editor.apply(); // musi być na końcu aby zapisać zmiany w shar

                    // otwarcie tego activity
                    Intent intent = new Intent(MainActivity.this, ActivityWydarzenie.class);
                    startActivity(intent);

                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
//                                            new AuthUI.IdpConfig.AnonymousBuilder().build(),
//                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
//                                            new AuthUI.IdpConfig.TwitterBuilder().build(),
//                                            new AuthUI.IdpConfig.GitHubBuilder().build(),
//                                            new AuthUI.IdpConfig.EmailBuilder().build(),
//                                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        //włącza listenera
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // do właczenia listenera logowania
        if (mFirebaseAuth != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // do wyłaczenia listenera logowania
        if (mFirebaseAuth != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    // do górnego menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setings:
                // write what to do

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // do górnego menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
