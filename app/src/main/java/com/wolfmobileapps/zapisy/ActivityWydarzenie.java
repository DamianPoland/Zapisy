package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_CENA;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_MIEJSCE_I_CZAS;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_OPIS;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_TYTUL;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_USER_EMAIL;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_USER_NAME;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.CHANNEL_ID;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_DISTANCE;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_FULL_TIME;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_MAP_POINTS;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_SPEED;


public class ActivityWydarzenie extends AppCompatActivity{
    private static final String TAG = "ActivityWydarzenie";

    //stałe
    public static final String KEY_MAP_POINTS_FROM_FIREBASE = "key punkty na mapie z firebase";
    public static final String KEY_MAP_POINTS_TO_INTENT_OPEN_MAP = "key map points to intent open map";


    //views
    private TextView textViewActivityWydarzenieTytul;
    private TextView textViewActivityWydarzenieMiejsceICzas;
    private TextView textViewActivityWydarzenieOpis;
    private Button buttonDolacz;
    private TextView textViewTrasa;
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonMapa;
    private TextView textViewActivityWydarzenieDystans;
    private TextView textViewActivityWydarzenieCzas;
    private TextView textViewActivityWydarzeniePredkosc;
    private TextView textViewOstatniaNagranaTrasaFIREBASE;
    private Button buttonMapaFIREBASE;
    private LinearLayout linearLayoutFIREBASE;
    private TextView textViewActivityWydarzenieDystansFIREBASE;
    private TextView textViewActivityWydarzenieCzasFIREBASE;
    private TextView textViewActivityWydarzeniePredkoscFIREBASE;
    private Button buttonWyslijTraseNaServer;

    //do Firebase Database
    private DatabaseReference myRef;
    private DatabaseReference myRefThisUser;


    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //do permissions nazwy
    private String[] permissions;
    // stała do permissions
    public static final int PERMISSION_ALL = 101;

    // dane pobrane z Intent
    private String tytul;
    private String miejsceICzas;
    private String opis;
    private String userName;
    private String userEmail;
    private int cena;

    // do broadcast reciver
    private BroadcastReceiver updateUIReciver;


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
        tytul = shar.getString(TO_ACTIVITY_WYDARZENIE_TYTUL, "Tytuł");
        miejsceICzas = shar.getString(TO_ACTIVITY_WYDARZENIE_MIEJSCE_I_CZAS, "Miejsce i czas");
        opis = shar.getString(TO_ACTIVITY_WYDARZENIE_OPIS, "opis");
        cena = shar.getInt(TO_ACTIVITY_WYDARZENIE_CENA, 0);
        userName = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "User name");
        userEmail = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, "User e-mail");

        //views
        textViewActivityWydarzenieTytul = findViewById(R.id.textViewActivityWydarzenieTytul);
        textViewActivityWydarzenieMiejsceICzas = findViewById(R.id.textViewActivityWydarzenieMiejsceICzas);
        textViewActivityWydarzenieOpis = findViewById(R.id.textViewActivityWydarzenieOpis);
        buttonDolacz = findViewById(R.id.buttonDolacz);
        textViewTrasa = findViewById(R.id.textViewTrasa);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonMapa = findViewById(R.id.buttonMapa);
        textViewActivityWydarzenieDystans = findViewById(R.id.textViewActivityWydarzenieDystans);
        textViewActivityWydarzenieCzas = findViewById(R.id.textViewActivityWydarzenieCzas);
        textViewActivityWydarzeniePredkosc = findViewById(R.id.textViewActivityWydarzeniePredkosc);
        textViewOstatniaNagranaTrasaFIREBASE = findViewById(R.id.textViewOstatniaNagranaTrasaFIREBASE);
        buttonMapaFIREBASE = findViewById(R.id.buttonMapaFIREBASE);
        linearLayoutFIREBASE = findViewById(R.id.linearLayoutFIREBASE);
        textViewActivityWydarzenieDystansFIREBASE = findViewById(R.id.textViewActivityWydarzenieDystansFIREBASE);
        textViewActivityWydarzenieCzasFIREBASE = findViewById(R.id.textViewActivityWydarzenieCzasFIREBASE);
        textViewActivityWydarzeniePredkoscFIREBASE = findViewById(R.id.textViewActivityWydarzeniePredkoscFIREBASE);
        buttonWyslijTraseNaServer = findViewById(R.id.buttonWyslijTraseNaServer);

        //ustawienie tekstów
        textViewActivityWydarzenieTytul.setText(tytul);
        textViewActivityWydarzenieMiejsceICzas.setText(miejsceICzas);
        textViewActivityWydarzenieOpis.setText(opis);

        //ustawienie tekstu na przycisku dołącz
        if (cena > 0) {
            buttonDolacz.setText("Dołącz \nopłata: " + cena + " zł");
        } else {
            buttonDolacz.setText("Dołącz");
        }

        // ustawienie wyników z servisu w textViews
        setViewsFromServiseOncore();

        // do Firebase instancja Database
        myRef = FirebaseDatabase.getInstance().getReference(tytul);

        // ukrywa przycisk dołacz jak jest wysłane coś na serwer pod child user Email
        myRefThisUser = myRef.child(userEmail);
        myRefThisUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //ustawienie visibility odpowiednich pól jeśli już ktoś dołączył do danego wydarzenia
                buttonDolacz.setVisibility(View.GONE);
                buttonWyslijTraseNaServer.setVisibility(View.VISIBLE);
                buttonMapaFIREBASE.setVisibility(View.VISIBLE);
                linearLayoutFIREBASE.setVisibility(View.VISIBLE);
                textViewOstatniaNagranaTrasaFIREBASE.setVisibility(View.VISIBLE);


                getDataFromFirebaseAndShowOnTextViews(dataSnapshot);

            } //działa gdy jest child dodany a za pierwszym razem dla każdego child czyli sam robi pentlę

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                getDataFromFirebaseAndShowOnTextViews(dataSnapshot);

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



        // przycisk dołącz
        buttonDolacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ustawienie zerowych danych
                float distance = 0;
                String fullTime = "00:00:00";
                float speed = 0;
                String mapPoints = "";
                //wysłanie uczestnictwa naserwer
                sentDataToFirebase(distance, fullTime, speed, mapPoints);

                Toast.makeText(ActivityWydarzenie.this, "Dziękujemy za dołączenie do wydarzenia", Toast.LENGTH_LONG).show();
            }
        });

        // przycisk start
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //zapytanie o permissions do GPS
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                if (!hasPermissions(ActivityWydarzenie.this, permissions)) {
                    ActivityCompat.requestPermissions(ActivityWydarzenie.this, permissions, PERMISSION_ALL);
                    return;
                }

                Toast.makeText(ActivityWydarzenie.this, "Start", Toast.LENGTH_SHORT).show();

                //włączenie servisu
                Intent intent = new Intent(ActivityWydarzenie.this, ServiceWydarzenie.class);
                startService(intent);
            }
        });

        // przycisk stop
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityWydarzenie.this, "Stop", Toast.LENGTH_SHORT).show();

                // wyłączenie srvisu
                Intent intent = new Intent(ActivityWydarzenie.this, ServiceWydarzenie.class);
                stopService(intent);

            }
        });

        // przycisk do wysyłania na server
        buttonWyslijTraseNaServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pobranie danych z shar Pref
                float distance = shar.getFloat(KEY_DISTANCE, 0);
                String fullTime = shar.getString(KEY_FULL_TIME, "00:00:00");
                float speed = shar.getFloat(KEY_SPEED, 0);
                String mapPoints = shar.getString(KEY_MAP_POINTS, "");

                //wysłanie danych naserwer
                sentDataToFirebase(distance, fullTime, speed, mapPoints);
            }
        });

        // przycisk mapa ostatniej trasy
        buttonMapa.setOnClickListener(new View.OnClickListener() {
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
        buttonMapaFIREBASE.setOnClickListener(new View.OnClickListener() {
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


        // broadcast reciver od servisu żeby upgradował text Views
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.wolfmobileapps.zapisy");
        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                // pobranie danych z servisu za pomocą→ broadcast recivera
                float distance = intent.getFloatExtra(KEY_DISTANCE, 0);
                String fullTime = intent.getStringExtra(KEY_FULL_TIME);
                float speed = intent.getFloatExtra(KEY_SPEED, 0);
                // ustawienie danych w textViews
                setDataInViews(distance, fullTime, speed);
            }
        };
        registerReceiver(updateUIReciver,filter);


    } //koniec onCreate_____________________________________________________________________


    // pobranie danych o zapisanej trasie z Firebase
    private void getDataFromFirebaseAndShowOnTextViews(DataSnapshot dataSnapshot){
        // pobranie danych z Firebase
        DaneTrasy daneTrasyFirebase = dataSnapshot.getValue(DaneTrasy.class);
        float distanceFIREBASE = daneTrasyFirebase.getDistance();
        String fullTimeFIREBASE = daneTrasyFirebase.getFullTime();
        float speedFIREBASE = daneTrasyFirebase.getSpeed();
        String maPointsFIREBASE = daneTrasyFirebase.getMapPoints();

        //zapisanie do shar  Pref stringa z punktami żeby potem można było otworzyć mapę z tymi punktami
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putString(KEY_MAP_POINTS_FROM_FIREBASE, maPointsFIREBASE);
        editor.apply(); // musi być na końcu aby zapisać zmiany w shar

        //ustawienie danych w textView z Firebase
        setDataInViewsFIREBASE(distanceFIREBASE, fullTimeFIREBASE, speedFIREBASE);
    }


    //wysłanie uczestnictwa lub danych naserwer
    private void sentDataToFirebase(float distance, String fullTime, float speed, String mapPoints){

        //utworzenie obiektu daneTrasy aby wysłąć do Firebase
        DaneTrasy daneTrasy = new DaneTrasy(distance, fullTime,speed,mapPoints);

        //wpisanie do Firebase uczestnictwa i odrazu trasy z zerowymi danymi lub ostatnimi
        myRefThisUser.child("road").setValue(daneTrasy);


        //jeśli będzie zero to znaczy że było tylko dołączenie a nie dodanie trasy więc nie wyświetli komunikatu
        if (distance!=0){
            Toast.makeText(ActivityWydarzenie.this, "Dziękujemy za dodanie trasy", Toast.LENGTH_LONG).show();
        }
    }

    // ustawienie wyników z servisu w textViews
    private void setViewsFromServiseOncore() {
        //pobranie czsu, predkosci i odległości z shared co się zapisało w servisie
        float distance = shar.getFloat(KEY_DISTANCE, 0);
        String fullTime = shar.getString(KEY_FULL_TIME, "00:00:00");
        float speed = shar.getFloat(KEY_SPEED, 0);

        // ustawienie danych w textViews
        setDataInViews(distance, fullTime, speed);
    }

    // ustawienie danych w textViews
    private void setDataInViews(float distance, String fullTime, float speed){
        textViewActivityWydarzenieDystans.setText("Dystans: " + distance + " m");
        textViewActivityWydarzenieCzas.setText("Czas: " + fullTime + " s");
        textViewActivityWydarzeniePredkosc.setText("Prędkość: " + speed + " km/h");
    }

    // ustawienie danych pobranych z FIREBASE w textViews FIREBASE
    private void setDataInViewsFIREBASE(float distanceFIREBASE, String fullTimeFIREBASE, float speedFIREBASE){
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
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    //musi być bo do tego activity wchodzi po logowaniu i jak sie wcika bez tego to wariuje bo poprzednia strona jest tak naprawde logowania a nie MainActivity
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityWydarzenie.this, MainActivity.class));
    }

    // odłączenie register recivera

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateUIReciver);
    }
}

