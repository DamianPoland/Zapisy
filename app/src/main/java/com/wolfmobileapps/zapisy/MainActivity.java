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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //stałe do przeniesienia do activity wydarzenie
    public static final String TO_ACTIVITY_WYDARZENIE_NAZWA_COLLECTION = "to activity wydarzenie nazwa collection";
    public static final String TO_ACTIVITY_WYDARZENIE_TYTUL = "to activity wydarzenie tytul";
    public static final String TO_ACTIVITY_WYDARZENIE_DATA = "to activity wydarzenie miejsceICzas";
    public static final String TO_ACTIVITY_WYDARZENIE_CENA = "to activity wydarzenie cena";
    public static final String TO_ACTIVITY_WYDARZENIE_OPIS = "to activity wydarzenie opis";
    public static final String TO_ACTIVITY_WYDARZENIE_REGULAMIN = "to activity wydarzenie regulamin";
    public static final String TO_ACTIVITY_WYDARZENIE_DYSTANS = "to activity wydarzenie dystans";
    public static final String TO_ACTIVITY_WYDARZENIE_UCZESTNICY_ILOSC = "to activity wydarzenie uczestnicy ilosc";
    public static final String TO_ACTIVITY_WYDARZENIE_HISTORIA = "to activity wydarzenie historia";
    public static final String TO_ACTIVITY_WYDARZENIE_USER_NAME = "to activity wydarzenie user name";
    public static final String TO_ACTIVITY_WYDARZENIE_USER_EMAIL = "to activity wydarzenie user email";

    // stałe do firebase
    public static final String COLLECTION_NAME_WYDARZENIE = "Wydarzenia";
    public static final String COLLECTION_NAME_USERS = "Users";
    public static final String COLLECTION_NAME_STARTY = "Starty";
    public static final String COLLECTION_NAME_UCZESTNICY = "Uczestnicy";

    //stałe inne
    public static final String SHARED_PREFERENCES_NAME = "zapisy shared preferences";
    public static final String KEY_MAP_POINTS_TO_INTENT_OPEN_MAP_LIST = "key map points to intent open map LISTA";
    public static final String KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_MAP_POINTS = "key to intent open map life data string WITH MAP POINTS";
    public static final String KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_DISTANCE = "key to intent open map life data string WITH DISTANCE";
    public static final String KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_TIME = "key to intent open map life data string WITH TIME";



    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //Views
    private TextView textViewWitaj;
    private TextView textViewOpisOgolny;
    private TextView textViewWydarzenia;
    private ProgressBar progressBarWaiForFirebase;

    //do Firebase Database
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    // do Firebase Authetication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;

    // listMain view arraylist i adapter
    private ListView listViewWydarzenia;
    private ArrayList<Wydarzenie> listMain;
    private WydarzeniaArrayAdapter adapter;

    public static final String[] TABLE_OF_ADMINS = {"damianwilczynskipl@gmailcom"}; // admin z dostępem do panelu admina - musi być 1!



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
        progressBarWaiForFirebase = findViewById(R.id.progressBarWaiForFirebase);

        // ustawienie textu na textView Witaj, jeśli użytkownik ma zapisane imię to się wyświetli
        textViewWitaj.setText("Hi!");
        if (!shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "none").equals("none")) {
            textViewWitaj.setText("Hi " + shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "none") + " !");
        }

        // ustawienie textu na textView OpisOgólny
        textViewOpisOgolny.setText("Here you will find current events from around the world. Click an event to find more information.");

        // ustawienie textu na textView Wydarzenia
        textViewWydarzenia.setText("Events:");


//        //ustawienie action bara
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setTitle(getResources().getString(R.string.app_name));

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        //wyświetlenie w listView z Firebas
        listMain = new ArrayList<>();
        adapter = new WydarzeniaArrayAdapter(this, 0, listMain);
        listViewWydarzenia.setAdapter(adapter);

        //onClick listener na listMain View
        listViewWydarzenia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // metoda do logowania
                logOrRegisterToFirebase(position);
            }
        });

    } //koniec onCreate_____________________________________________________________________


    // metoda dodaje listenera i słucha wszystkiego cosię dzieje wdanym folderze "Wydarzenia"
    private void addListenerToFirebaseWydarzenia (){

        // wyczyszczenie adaptera przed dodaniem od nowego listenera
        adapter.clear();

        // wyłaczenie listenera jeśli już jest odpalony - inaczej działają dwa na raaz i sei d ublują
        if (registration != null){
            registration.remove();
        }

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
                            //wyłączenie progress bara
                            progressBarWaiForFirebase.setVisibility(View.GONE);

                            //dodanie nowego wydarzenia do adaptera
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

                            // wydarzenie zostanie dodane do adaptera tylko jeśli NIE będzie w historii
                            if (!wydarzenieHistoria){
                                adapter.add(wydarzenie);

                            }

                            Log.d(TAG, "onEvent: ________" + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified city: " + dc.getDocument().getData());

                            // jeśli jest zrobiona modyfikacja jakiegos wydarzenia to przeładuje całość - inaczej wydarzenia się dodają a nie zmienaiją
                            addListenerToFirebaseWydarzenia();
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                            break;
                    }
                }

            }
        });
    }

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

                    // zapisanie imienia i emailu do shar pref
                    editor = shar.edit(); //wywołany edytor do zmian
                    editor.putString(TO_ACTIVITY_WYDARZENIE_USER_NAME, userName);
                    editor.putString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, userEmail);
                    editor.apply(); // musi być na końcu aby zapisać zmiany w shar

                    // przejście do profilu uczestnika______________________________________________________________________________________________________________________________________
                    if (position == -1) {

                        //włączenie activity z profilem użytkownika
                        startActivity(new Intent(MainActivity.this, UserActivity.class));
                        return;
                    }
                    //______________________________________________________________________________________________________________________________________________________________________

                    // Do otwarcia next activity z list view - pobranie danych z danego itema wydarzenia i zapisanie danych  o wydarzeniu do shared pref
                    Wydarzenie wydarzenie = new Wydarzenie();
                    wydarzenie.saveDataInSharedPref(MainActivity.this, listMain, position);

                    // otwarcie tego activity
                    Intent intent = new Intent(MainActivity.this, ActivityWydarzenie.class);
                    startActivity(intent);

                } else {

                    // musi byc alert dialog bo inaczej jak ktoś kliknął onBackPressed to nie wracało do głównego activity a traz wraca
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Log in");
                    builder.setMessage("To proceed, you must be logged in via a Google account.");
                    builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //odpalenie logowania
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
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do something when click cancel
                        }
                    }).create();
                    builder.show();
                }
            }
        };
        //włącza listenera
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //metoda dodaje listenera i słucha wszystkiego cosię dzieje wdanym folderze "Wydarzenia"
        addListenerToFirebaseWydarzenia();

        // do właczenia listenera logowania
        if (mFirebaseAuth != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // wyłaczenie listenera
        registration.remove();

        // do wyłaczenia listenera logowania
        if (mFirebaseAuth != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }





    // do górnego menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                startActivity(new Intent(MainActivity.this, ActivityInfo.class));
                break;
            case R.id.menu_user_profile:
                logOrRegisterToFirebase(-1);
                break;
            // otwiera panel administratora, przycisk jest wyłaczony chyba że bedzie odpowiedni email - warynek jest w metodzie onCreateOptionsMenu
            case R.id.menu_admin:
                Intent intent = new Intent(MainActivity.this, ActivityAdmin.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // do górnego menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_layout, menu);

        //ustawienie widoczności guzika dla admina
        String emailAdmin = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, "");
        if (emailAdmin.equals(TABLE_OF_ADMINS[0])) {
            MenuItem item = menu.findItem((R.id.menu_admin));
            item.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //musi być bo nie dziłało inaczej. Przenosiło do aplikacji poprzedniej a tera zamyka apke dobrze
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}