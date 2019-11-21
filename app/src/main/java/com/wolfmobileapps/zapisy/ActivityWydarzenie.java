package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_STARTY;
import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_UCZESTNICY;
import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_USERS;
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
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.CHANNEL_ID;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_DISTANCE;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_FULL_TIME;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_MAP_POINTS;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_SPEED;


public class ActivityWydarzenie extends AppCompatActivity {
    private static final String TAG = "ActivityWydarzenie";

    //stałe
    public static final String KEY_MAP_POINTS_FROM_FIREBASE = "key punkty na mapie z firebase";
    public static final String KEY_MAP_POINTS_TO_INTENT_OPEN_MAP = "key map points to intent open map";
    public static final String KEY_TO_START_STOP = "key to star stop";
    public static final String KEY_TO_VISIBILITY_LIN_LAY_NAGRANA_TRASA = "key to visibility linLay nagrana trasa";
    public static final String DEF_VALUE_TO_CHILD_OF_MY_REF_THIS_USER = "road";
    public static final String DEF_VALUE_TO_SET_FULL_TIME = "00:00:00";
    public static final String DEF_VALUE_TO_VIEW_NAGRYWANIE_TRASY = "Nagraj trasę";
    public static final String DEF_VALUE_TO_VIEW_NAGRYWANIE_TRASY_TRWA_NAGRYWANIE = "Trwa nagrywanie trasy...\n(automatycznie wyłączy się po ";

    //views
    private TextView textViewActivityWydarzenieTytul;
    private TextView textViewActivityWydarzenieData;
    private TextView textViewActivityWydarzenieDystansOgolny;
    private TextView textViewActivityWydarzenieUczestnicy;
    private TextView textViewActivityWydarzenieOpis;
    private Button buttonDolacz;
    private TextView textViewNagrajTrase;
    private ImageView imageViewStartStop;
    private ImageView imageViewMapa;
    private TextView textViewActivityWydarzenieDystans;
    private TextView textViewActivityWydarzenieCzas;
    private TextView textViewActivityWydarzeniePredkosc;
    private TextView textViewOstatniaNagranaTrasaFIREBASE;
    private ImageView imageViewMapaFIREBASE;
    private LinearLayout linearLayoutFIREBASE;
    private TextView textViewActivityWydarzenieDystansFIREBASE;
    private TextView textViewActivityWydarzenieCzasFIREBASE;
    private TextView textViewActivityWydarzeniePredkoscFIREBASE;
    private Button buttonWyslijTraseNaServer;
    private Button buttonSkasujTrase;
    private View imageViewgooglePay;
    private LinearLayout linLayZgloszonaTrasa;
    private LinearLayout linLayNagrajTrasę;
    private LinearLayout linLayNagranaTrasa;
    private Button buttonWyniki;
    private ImageView imageViewMapaLifeData;

    //do Firebase Database
    private FirebaseFirestore db;
    private ListenerRegistration registration;


    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //do permissions nazwy
    private String[] permissions;
    // stała do permissions
    public static final int PERMISSION_ALL = 101;

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

    // do broadcast reciver
    private BroadcastReceiver updateUIReciver;

    // do animation
    private Animation animationDown;
    private Animation animationUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wydarzenie);

        //ustawia up button
        getSupportActionBar().setTitle("Powrót"); //ustawia nazwę na górze
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ustawia strzałkę

        // metoda tworząca notificationChanel
        createNotificationChannel();

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

        //views
        textViewActivityWydarzenieTytul = findViewById(R.id.textViewActivityWydarzenieTytul);
        textViewActivityWydarzenieData = findViewById(R.id.textViewActivityWydarzenieData);
        textViewActivityWydarzenieDystansOgolny = findViewById(R.id.textViewActivityWydarzenieDystansOgolny);
        textViewActivityWydarzenieUczestnicy = findViewById(R.id.textViewActivityWydarzenieUczestnicy);
        textViewActivityWydarzenieOpis = findViewById(R.id.textViewActivityWydarzenieOpis);
        buttonDolacz = findViewById(R.id.buttonDolacz);
        textViewNagrajTrase = findViewById(R.id.textViewNagrajTrase);
        imageViewStartStop = findViewById(R.id.imageViewStartStop);
        imageViewMapa = findViewById(R.id.imageViewMapa);
        textViewActivityWydarzenieDystans = findViewById(R.id.textViewActivityWydarzenieDystans);
        textViewActivityWydarzenieCzas = findViewById(R.id.textViewActivityWydarzenieCzas);
        textViewActivityWydarzeniePredkosc = findViewById(R.id.textViewActivityWydarzeniePredkosc);
        textViewOstatniaNagranaTrasaFIREBASE = findViewById(R.id.textViewOstatniaNagranaTrasaFIREBASE);
        imageViewMapaFIREBASE = findViewById(R.id.imageViewMapaFIREBASE);
        linearLayoutFIREBASE = findViewById(R.id.linearLayoutFIREBASE);
        textViewActivityWydarzenieDystansFIREBASE = findViewById(R.id.textViewActivityWydarzenieDystansFIREBASE);
        textViewActivityWydarzenieCzasFIREBASE = findViewById(R.id.textViewActivityWydarzenieCzasFIREBASE);
        textViewActivityWydarzeniePredkoscFIREBASE = findViewById(R.id.textViewActivityWydarzeniePredkoscFIREBASE);
        buttonWyslijTraseNaServer = findViewById(R.id.buttonWyslijTraseNaServer);
        buttonSkasujTrase = findViewById(R.id.buttonSkasujTrase);
        imageViewgooglePay = findViewById(R.id.imageViewgooglePay);
        linLayZgloszonaTrasa = findViewById(R.id.linLayZgloszonaTrasa);
        linLayNagrajTrasę = findViewById(R.id.linLayNagrajTrasę);
        linLayNagranaTrasa = findViewById(R.id.linLayNagranaTrasa);
        buttonWyniki = findViewById(R.id.buttonWyniki);
        imageViewMapaLifeData = findViewById(R.id.imageViewMapaLifeData);

        //ustawienie tekstów
        textViewActivityWydarzenieTytul.setText(tytul);
        textViewActivityWydarzenieData.setText("Data: " + data);
        textViewActivityWydarzenieDystansOgolny.setText("Dystans: " + dystans + " km");
        textViewActivityWydarzenieUczestnicy.setText("Uczestnicy: " + Math.round(uczestnicyIlosc));
        textViewActivityWydarzenieOpis.setText(opis);
        textViewNagrajTrase.setText(DEF_VALUE_TO_VIEW_NAGRYWANIE_TRASY);

        //ustawienie tekstu dołącz i przycisku googlePay
        if (cena == 0) {
            buttonDolacz.setText("Dołącz");
            imageViewgooglePay.setVisibility(View.GONE);
        } else {
            buttonDolacz.setText("Dołącz (" + cena + " zł)");
        }

        // jeśli wydarzenie jest w historii to ukryje przycisk nagraj i opis i pokaże przycisk wyniki
        if (historia) {
            linLayNagrajTrasę.setVisibility(View.VISIBLE);
            buttonWyniki.setVisibility(View.VISIBLE);
            imageViewStartStop.setVisibility(View.GONE);
            textViewNagrajTrase.setVisibility(View.GONE);
            imageViewMapaLifeData.setVisibility(View.GONE);

        }

        // ustawienie wyników z servisu w textViews
        setViewsFromServiseOncore();

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        //ustawienie animacji
        animationDown = AnimationUtils.loadAnimation(ActivityWydarzenie.this, R.anim.anim_rotation_down);
        animationDown.setDuration(1000);
        animationUp = AnimationUtils.loadAnimation(ActivityWydarzenie.this, R.anim.anim_rotation_up);
        animationUp.setDuration(1000);

        //ustawienie przycisku start stop
        if (shar.getBoolean(KEY_TO_START_STOP, true)) {

            //ustawienie ikonki na start i nazwy text View Nagraj Trasę
            setImageAndTextWhenStart();

        } else {

            //ustawienie ikonki na stop i nazwy text View Nagraj Trasę
            setImageAndTextWhenStop();
        }

        // broadcast reciver od servisu żeby upgradował text Views
        IntentFilter filter = new IntentFilter();
        filter.addAction(getApplicationContext().getPackageName());
        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                // trzeba zrobić ifa bo servis wysyła tez broadcast do map i żeby go nie łapał
                if (intent.hasExtra(KEY_DISTANCE)) {
                    // pobranie danych z servisu za pomocą→ broadcast recivera
                    float distance = intent.getFloatExtra(KEY_DISTANCE, 0);
                    String fullTime = intent.getStringExtra(KEY_FULL_TIME);
                    float speed = intent.getFloatExtra(KEY_SPEED, 0);

                    // ustawienie danych w textViews
                    setDataInViews(distance, fullTime, speed);

                    //ustawia wszystkie views po zatrzymaniu servisu
                    changeViewsAfterServiceStop();
                }
            }
        };
        registerReceiver(updateUIReciver, filter);


        // przycisk dołącz
        buttonDolacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ustawienie tekstu na przycisku dołącz
                if (cena > 0) { // płatnośc google pay

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWydarzenie.this);
                    builder.setTitle("Uwaga!");
                    builder.setMessage("Udział w tym wydarzeniu jest płatny. \nKoszt to: " + cena + " zł. \nAby dołączyć najpierw dokonaj płatności przez GooglePay");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do something when click OK
                        }
                    }).create();
                    builder.show();

                } else {
                    // zgłoszeni udziału bezpłatnego
                    joinToEvent();
                }
            }
        });

        // przycisk startStop
        imageViewStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shar.getBoolean(KEY_TO_START_STOP, true)) {

                    //zapytanie o permissions do GPS
                    permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                    if (!hasPermissions(ActivityWydarzenie.this, permissions)) {
                        ActivityCompat.requestPermissions(ActivityWydarzenie.this, permissions, PERMISSION_ALL);
                        return;
                    }

                    //sprawdzeni czy GPS jest włączony
                    if (!isLocationEnabled(ActivityWydarzenie.this)) {
                        Toast.makeText(ActivityWydarzenie.this, "GPS jest wyłączony!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //włączenie servisu
                    Intent intent = new Intent(ActivityWydarzenie.this, ServiceWydarzenie.class);
                    startService(intent);

                    //zmienia widoki i zapisuje da shar jeśli servis się uruchomi
                    changeViewsAfterServiceStart();

                } else { // gdy jest przycisk stop

                    // wyłączenie srwisu
                    Intent intent = new Intent(ActivityWydarzenie.this, ServiceWydarzenie.class);
                    stopService(intent);

                    //zmienia widoki i zapisuje da shar jeśli servis się zatrzyma
                    changeViewsAfterServiceStop();
                }

            }
        });

        // przycisk do wysyłania na server
        buttonWyslijTraseNaServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pobranie danych z shar Pref
                // userEmail pobrane wcześniej
                float distance = shar.getFloat(KEY_DISTANCE, 0);
                String fullTime = shar.getString(KEY_FULL_TIME, DEF_VALUE_TO_SET_FULL_TIME);
                float speed = shar.getFloat(KEY_SPEED, 0);
                String mapPoints = shar.getString(KEY_MAP_POINTS, "");

                // zabezpieczenie przed wysłąniem trasy z zerowym wynikiem
                if (fullTime.equals(DEF_VALUE_TO_SET_FULL_TIME)) {
                    Toast.makeText(ActivityWydarzenie.this, "Trasa jest zerowa!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // zabezpieczenie przed wysłanem z za krótkim dystansem
                if (distance < dystans * 1000) { // porównanie w metrach nie w km
                    Toast.makeText(ActivityWydarzenie.this, "Trasa jest za krótka", Toast.LENGTH_SHORT).show();
                    return;
                }

                //wysłanie danych na serwer
                DaneTrasy daneTrasy = new DaneTrasy(userEmail, distance, fullTime, speed, mapPoints); //utworzenie obiektu daneTrasy aby wysłąć do Firebase
                daneTrasy.sentDataToFirebase(ActivityWydarzenie.this, daneTrasy, COLLECTION_NAME_WYDARZENIE, dbNameCollection, COLLECTION_NAME_UCZESTNICY, userEmail); // wysłanie do collection Wydarzenia
                daneTrasy.sentDataToFirebase(ActivityWydarzenie.this, daneTrasy, COLLECTION_NAME_USERS, userEmail, COLLECTION_NAME_STARTY, dbNameCollection); // wysłąnie do collection Users

                // ustawienie wisibility linLay do nagranej trasy
                editor = shar.edit(); //wywołany edytor do zmian
                editor.putBoolean(KEY_TO_VISIBILITY_LIN_LAY_NAGRANA_TRASA, false);
                editor.apply(); // musi być na końcu aby zapisać zmiany w shar

                //ustawienie visibility linLay do nagranej trasy
                changeViewsVisibilityNagranaTrasa();

                //ustawienie danych zerowych w text views
                setViewsFromServiseOncore();

                //jeśli będzie zero to znaczy że było tylko dołączenie a nie dodanie trasy więc nie wyświetli komunikatu
                if (!fullTime.equals(DEF_VALUE_TO_SET_FULL_TIME)) {
                    Toast.makeText(ActivityWydarzenie.this, "Dziękujemy za dodanie trasy", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // przycisk do kasowania trasy
        buttonSkasujTrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // zapisanie czasu i punktów na mapie w postaci stringa dp shared pref
                editor = shar.edit(); //wywołany edytor do zmian
                editor.putFloat(KEY_DISTANCE, 0); // do Activity Wydarzenie
                editor.putString(KEY_FULL_TIME, DEF_VALUE_TO_SET_FULL_TIME); // do Activity Wydarzenie
                editor.putFloat(KEY_SPEED, 0); // do Activity Wydarzenie
                editor.putString(KEY_MAP_POINTS, ""); // do activity z Mapą
                editor.apply(); // musi być na końcu aby zapisać zmiany w shar

                //ustawienie danych zerowych w text views
                setViewsFromServiseOncore();

                // ustawienie wisibility linLay do nagranej trasy
                editor = shar.edit(); //wywołany edytor do zmian
                editor.putBoolean(KEY_TO_VISIBILITY_LIN_LAY_NAGRANA_TRASA, false);
                editor.apply(); // musi być na końcu aby zapisać zmiany w shar
                //ustawienie visibility linLay do nagranej trasy
                changeViewsVisibilityNagranaTrasa();
            }
        });

        // przycisk mapa ostatniej trasy
        imageViewMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //zabezpieczenie przed tym żeby mapa się nie otworzyła jak jest pusta lista punktów na mapie
                String listLatLngString = shar.getString(KEY_MAP_POINTS, "");
                if (listLatLngString.equals("")) {
                    Toast.makeText(ActivityWydarzenie.this, "Jeszcze nie zapisałeś żadnej trasy", Toast.LENGTH_SHORT).show();
                    return;
                }

                //otiwiera mapę
                Intent intentMap = new Intent(ActivityWydarzenie.this, ActivityMaps.class);
                intentMap.putExtra(KEY_MAP_POINTS_TO_INTENT_OPEN_MAP, listLatLngString);
                startActivity(intentMap);
            }
        });

        // przycisk mapa trasy z Firebase
        imageViewMapaFIREBASE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //zabezpieczenie przed tym żeby mapa się nie otworzyła jak jest pusta lista punktów na mapie
                String listLatLngString = shar.getString(KEY_MAP_POINTS_FROM_FIREBASE, "");
                if (listLatLngString.equals("")) {
                    Toast.makeText(ActivityWydarzenie.this, "Jeszcze nie zapisałeś żadnej trasy", Toast.LENGTH_SHORT).show();
                    return;
                }

                //otiwiera mapę
                Intent intentMap = new Intent(ActivityWydarzenie.this, ActivityMaps.class);
                intentMap.putExtra(KEY_MAP_POINTS_TO_INTENT_OPEN_MAP, listLatLngString);
                startActivity(intentMap);
            }
        });

        imageViewMapaLifeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //otiwiera mapę pustą do updatowania life data podczas biegnięcia
                Intent intentMap = new Intent(ActivityWydarzenie.this, ActivityMaps.class);
                startActivity(intentMap);
            }
        });

        // przycisk do wyników
        buttonWyniki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // przenosi do wyników danego wydarzenia - wszystkie dane są już zapisane w shar pref przy wcześniejszym przejsciu z listView do tego activity
                Intent intentWydarzenie = new Intent(ActivityWydarzenie.this, ActivityWyniki.class);
                startActivity(intentWydarzenie);
            }
        });


        // do płatności GooglePay - ustawia możliwość płatności jeśli cena jest większa niż zero
        if (cena > 0) {
            mPaymentsClient =
                    Wallet.getPaymentsClient(
                            this,
                            new Wallet.WalletOptions.Builder()
                                    .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                    .build());
            possiblyShowGooglePayButton(); //startuje z tej metody i sprawdza czy są google pay aktywne i pokazuje button
        }


    } //koniec onCreate_____________________________________________________________________


    //zmienia widoki i zapisuje da shar jeśli servis się uruchomi
    private void changeViewsAfterServiceStart() {

        //ustawienie ikonki na stop i nazwy text View Nagraj Trasę
        setImageAndTextWhenStop();

        //zapisanie do shar  że ma być stop i nazwy text View Nagraj Trasę
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putBoolean(KEY_TO_START_STOP, false);
        editor.apply(); // musi być na końcu aby zapisać zmiany w shar
    }

    //ustawienie ikonki na stop i nazwy text View Nagraj Trasę
    private void setImageAndTextWhenStop() {
        imageViewStartStop.setImageDrawable(getDrawable(R.drawable.stop_button_transparent));
        textViewNagrajTrase.setText(DEF_VALUE_TO_VIEW_NAGRYWANIE_TRASY_TRWA_NAGRYWANIE + dystans + " km)");
    }

    //zmienia widoki i zapisuje da shar jeśli servis się zatrzyma
    private void changeViewsAfterServiceStop() {

        //ustawienie ikonki na start i nazwy text View Nagraj Trasę
        setImageAndTextWhenStart();

        //zapisanie do shar  że ma być start i nazwy text View Nagraj Trasę
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putBoolean(KEY_TO_START_STOP, true);
        editor.apply(); // musi być na końcu aby zapisać zmiany w shar

        // ustawienie wisibility linLay do nagranej trasy
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putBoolean(KEY_TO_VISIBILITY_LIN_LAY_NAGRANA_TRASA, true);
        editor.apply(); // musi być na końcu aby zapisać zmiany w shar

        //ustawienie visibility linLay do nagranej trasy
        changeViewsVisibilityNagranaTrasa();
    }

    //ustawienie ikonki na start i nazwy text View Nagraj Trasę
    private void setImageAndTextWhenStart() {
        imageViewStartStop.setImageDrawable(getDrawable(R.drawable.start_button_transparent));
        textViewNagrajTrase.setText(DEF_VALUE_TO_VIEW_NAGRYWANIE_TRASY);
    }

    // metoda do zmiększenia o jeden liczby uczestników wyświetlanych w bazie
    private void addOneNumberOfUsersToDb() {

        //Read only once Document - nie trzeba go potem wyłączać  (onCompleateListener):
        DocumentReference docRefWydarzenia = db.collection(COLLECTION_NAME_WYDARZENIE).document(dbNameCollection);
        docRefWydarzenia.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        //pobranie danych

                        //dodanie kolejnych wydarzeń
                        String wydarzenieNazwaCollection = (String) document.getData().get("wydarzenieNazwaCollection");
                        String wydarzenieTytul = (String) document.getData().get("wydarzenieTytul");
                        String wydarzenieData = (String) document.getData().get("wydarzenieData");
                        float wydarzenieCena = Float.parseFloat("" + document.getData().get("wydarzenieCena"));
                        String wydarzenieOpis = (String) document.getData().get("wydarzenieOpis");
                        String wydarzenieRegulamin = (String) document.getData().get("wydarzenieRegulamin");
                        float wydarzenieDystans = Float.parseFloat("" + document.getData().get("wydarzenieDystans"));
                        float wydarzenieUczestnicyIlosc = Float.parseFloat("" + document.getData().get("wydarzenieUczestnicyIlosc"));
                        boolean wydarzenieHistoria = Boolean.parseBoolean("" + document.getData().get("wydarzenieHistoria"));

                        // dodanie jednego uczestnika
                        wydarzenieUczestnicyIlosc += 1;

                        // utworzenie wydarzenia z jednym uczestnikiem wiecej
                        Wydarzenie wydarzenie = new Wydarzenie(wydarzenieNazwaCollection, wydarzenieTytul, wydarzenieData, wydarzenieCena, wydarzenieOpis, wydarzenieRegulamin, wydarzenieDystans, wydarzenieUczestnicyIlosc, wydarzenieHistoria);

                        // wysłąnie danych do firebase
                        wydarzenie.addDataToFirestore(ActivityWydarzenie.this, COLLECTION_NAME_WYDARZENIE, wydarzenieNazwaCollection, wydarzenie);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // metoda dodaje listenera i słucha wszystkiego cosię dzieje w zapisie trasy w Users a nie w wydarzenia czyliss łuchaw tej ścieżce: (COLLECTION_NAME_USERS,userEmail,COLLECTION_NAME_STARTY,dbNameCollection), żeby słychał w wynikach to trzeba podać tą ściężkę: (COLLECTION_NAME_WYDARZENIE,dbNameCollection,COLLECTION_NAME_UCZESTNICY,userEmail)
    private void addListenerToFirebaseWydarzeniaDanaTrasa() {
        // słucha zmian na serwerze - ukrywa przycisk dołacz jak jest wysłane coś na serwer pod child user Email
        final DocumentReference docRef = db.collection(COLLECTION_NAME_USERS).document(userEmail).collection(COLLECTION_NAME_STARTY).document(dbNameCollection);
        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) { // włącza za każdym razem jak zrobi nowy lub zaktualizuje
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    // pokazanie lub wyłączenie widoków gdy się dodaje lub zmienia w firebase child
                    changeViewsVisibilityAfterAddOrChangeChild(snapshot);

                    // pobranie danych o zapisanej trasie z Firebase i wstawienie do textViewsFirebase
                    getDataFromFirebaseAndShowOnTextViews(snapshot);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    // pokazanie lub wyłączenie widoków gdy się dodaje lub zmienia w firebase child
    private void changeViewsVisibilityAfterAddOrChangeChild(DocumentSnapshot snapshot) {

        // wyłączenie przycisków dołacz i do płątności
        buttonDolacz.setVisibility(View.GONE);
        imageViewgooglePay.setVisibility(View.GONE);

        // włączenie linLay do nagrywania
        linLayNagrajTrasę.setVisibility(View.VISIBLE);
        linLayNagrajTrasę.startAnimation(animationDown);

        // włączenie liLay z trasą z firebase

        String fullTimeTakenFIREBASE = (String) snapshot.getData().get("fullTime");

//        DaneTrasy daneTrasyFirebase = dataSnapshot.getValue(DaneTrasy.class);
//        String fullTimeTakenFIREBASE = daneTrasyFirebase.getFullTime();
        if (!fullTimeTakenFIREBASE.equals(DEF_VALUE_TO_SET_FULL_TIME)) {
            linLayZgloszonaTrasa.setVisibility(View.VISIBLE);
            linLayZgloszonaTrasa.startAnimation(animationDown);
        }

        //ustawienie visibility linLay do nagranej trasy
        changeViewsVisibilityNagranaTrasa();
    }

    //ustawienie visibility linLay do nagranej trasy
    private void changeViewsVisibilityNagranaTrasa() {

        // nie pokaże linLayNagranaTrasa jeśli wydarzenie jest w historii
        if (historia) {
            return;
        }

        //ustawienie visibility linLay do nagranej trasy
        if (shar.getBoolean(KEY_TO_VISIBILITY_LIN_LAY_NAGRANA_TRASA, false)) {
            linLayNagranaTrasa.setVisibility(View.VISIBLE);
            linLayNagranaTrasa.startAnimation(animationDown);
        } else {
            if (linLayNagranaTrasa.getVisibility() == View.VISIBLE) {

                linLayNagranaTrasa.startAnimation(animationUp);
                // ustawienie animation listenera żeby po skończeniu animacji wyłączył widok
                animationUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        linLayNagranaTrasa.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        }
    }

    // zgłoszenie uczestnictwa w wydarzeniu
    private void joinToEvent() {

        //ustawienie zerowych danych
        String userName = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "User");
        float distance = 0;
        String fullTime = DEF_VALUE_TO_SET_FULL_TIME;
        float speed = 0;
        String mapPoints = "";

        //wysłanie uczestnictwa naserwer
        DaneTrasy daneTrasy = new DaneTrasy(userEmail, distance, fullTime, speed, mapPoints); //utworzenie obiektu daneTrasy aby wysłąć do Firebase
        daneTrasy.sentDataToFirebase(ActivityWydarzenie.this, daneTrasy, COLLECTION_NAME_WYDARZENIE, dbNameCollection, COLLECTION_NAME_UCZESTNICY, userEmail); // wysłanie do collection Wydarzenia
        daneTrasy.sentDataToFirebase(ActivityWydarzenie.this, daneTrasy, COLLECTION_NAME_USERS, userEmail, COLLECTION_NAME_STARTY, dbNameCollection); // wysłąnie do collection Users
        Toast.makeText(ActivityWydarzenie.this, "Dziękujemy za dołączenie do wydarzenia", Toast.LENGTH_SHORT).show();

        // zaktualizowanie danych dotyczących ilości uczestników
        addOneNumberOfUsersToDb();
    }

    // pobranie danych o zapisanej trasie z Firebase
    private void getDataFromFirebaseAndShowOnTextViews(DocumentSnapshot snapshot) {

        // pobranie danych z Firebase
        float distanceFIREBASE = Float.parseFloat("" + snapshot.getData().get("distance"));
        String fullTimeFIREBASE = (String) snapshot.getData().get("fullTime");
        float speedFIREBASE = Float.parseFloat("" + snapshot.getData().get("speed"));
        String maPointsFIREBASE = (String) snapshot.getData().get("mapPoints");

        //zapisanie do shar  Pref stringa z punktami żeby potem można było otworzyć mapę z tymi punktami
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putString(KEY_MAP_POINTS_FROM_FIREBASE, maPointsFIREBASE);
        editor.apply(); // musi być na końcu aby zapisać zmiany w shar

        //ustawienie danych w textView z Firebase
        setDataInViewsFIREBASE(distanceFIREBASE, fullTimeFIREBASE, speedFIREBASE);
    }

    // ustawienie wyników z servisu w textViews
    private void setViewsFromServiseOncore() {
        //pobranie czsu, predkosci i odległości z shared co się zapisało w servisie
        float distance = shar.getFloat(KEY_DISTANCE, 0);
        String fullTime = shar.getString(KEY_FULL_TIME, DEF_VALUE_TO_SET_FULL_TIME);
        float speed = shar.getFloat(KEY_SPEED, 0);

        // ustawienie danych w textViews
        setDataInViews(distance, fullTime, speed);
    }

    // ustawienie danych w textViews
    private void setDataInViews(float distance, String fullTime, float speed) {
        textViewActivityWydarzenieDystans.setText("Dystans: " + distance + " m");
        textViewActivityWydarzenieCzas.setText("Czas: " + fullTime + " s");
        textViewActivityWydarzeniePredkosc.setText("Prędkość: " + speed + " km/h");
    }

    // ustawienie danych pobranych z FIREBASE w textViews FIREBASE
    private void setDataInViewsFIREBASE(float distanceFIREBASE, String fullTimeFIREBASE, float speedFIREBASE) {
        textViewActivityWydarzenieDystansFIREBASE.setText("Dystans: " + distanceFIREBASE + " m");
        textViewActivityWydarzenieCzasFIREBASE.setText("Czas: " + fullTimeFIREBASE + " s");
        textViewActivityWydarzeniePredkoscFIREBASE.setText("Prędkość: " + speedFIREBASE + " km/h");
    }

    //metoda do sprawdzenia czy są nadane permisssions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // notification chanel do servisu musi być
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name); //chanel name
            String description = getString(R.string.app_name); //chanel description
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //    @Override
    public void onBackPressed() {
        //musi być bo do tego activity wchodzi po logowaniu i jak sie wcika bez tego to wariuje bo poprzednia strona jest tak naprawde logowania a nie MainActivity
        startActivity(new Intent(ActivityWydarzenie.this, MainActivity.class));
    }

    //sprawdzenie czy GPS jest włączony
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    // odłączenie register recivera
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateUIReciver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //metoda dodaje listenera i słucha wszystkiego cosię dzieje wdanym folderze "Wydarzenia"
        addListenerToFirebaseWydarzeniaDanaTrasa();
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
            case R.id.menu_regulamin:

                // alert dialog z regulaminem
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWydarzenie.this);
                builder.setTitle("Regulamin");
                builder.setMessage(regulamin);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do something when click OK
                    }
                }).create();
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // do górnego menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_wudarzenie, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //-------------------------------------------------- Do goole pay----------------------------------------------------------------


    private PaymentsClient mPaymentsClient;

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;


    //startuje z tej metody i sprawdza czy są google pay aktywne i jesli tak to pokazuje button
    private void possiblyShowGooglePayButton() {
        if (GooglePay.getIsReadyToPayRequest() == null) {
            Toast.makeText(this, "Google PAY są niedostępne", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
        if (request == null) {
            Toast.makeText(this, "Google PAY nie jest gotowe do wykonania płatności.", Toast.LENGTH_LONG).show();
            return;
        }
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if (result) {
                                // show Google as a payment option
                                imageViewgooglePay.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                // po kliknięciu na button uruchamia tą metodę
                                                requestPayment(view);
                                            }
                                        });
                                imageViewgooglePay.setVisibility(View.VISIBLE);
                            }
                        } catch (ApiException exception) {
                            // handle developer errors
                        }
                    }
                });
    }


    // po kliknięciu na button uruchamia tą metodę
    public void requestPayment(View view) {
        if (GooglePay.getPaymentDataRequest(cena)==null){
            return;
        }
        JSONObject paymentDataRequestJson = GooglePay.getPaymentDataRequest(cena);
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet
     *
     * @param requestCode the request code originally supplied to AutoResolveHelper in
     *                    requestPayment()
     * @param resultCode  the result code returned by the Google Pay API
     * @param data        an Intent from the Google Pay API containing payment or error data
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     * from an Activity</a>
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String json = paymentData.toJson();
                        // if using gateway tokenization, pass this token without modification
                        JSONObject paymentMethodData = null;
                        try {
                            paymentMethodData = new JSONObject(json).getJSONObject("paymentMethodData");
                            // Alert dialog po to że jeśli - If the gateway is set to "example", no payment information is returned - instead, the token will only consist of "examplePaymentMethodToken".
                            if (paymentMethodData
                                    .getJSONObject("tokenizationData")
                                    .getString("type")
                                    .equals("PAYMENT_GATEWAY")
                                    && paymentMethodData
                                    .getJSONObject("tokenizationData")
                                    .getString("token")
                                    .equals("examplePaymentMethodToken")) {
                                AlertDialog alertDialog =
                                        new AlertDialog.Builder(this)
                                                .setTitle("Warning")
                                                .setMessage("Gateway name set to \"example\" - please modify " + "Constants.java and replace it with your own gateway.")
                                                .setPositiveButton("OK", null)
                                                .create();
                                alertDialog.show();
                            }

                            //log do tokena
                            Log.d(TAG, "onActivityResult: paymentToken: " + paymentMethodData.getJSONObject("tokenizationData").getString("token"));

                            //gdy się powiedzie opłata
                            String billingName =
                                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
                            Log.d("BillingName", billingName);
                            Toast.makeText(this, "Płatność została dokonana. \nDziękujemy.", Toast.LENGTH_LONG).show();

                            // włączyć metode z dołącz gdy zostało opłacone
                            joinToEvent();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Toast.makeText(this, "ERROR. Nie dokonano płatności.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onActivityResult: RESULT_ERROR: " + status.getStatusMessage());
                        break;
                    default:
                        // Do nothing.
                }
                break;
            default:
                // Do nothing.
        }
    }

}


