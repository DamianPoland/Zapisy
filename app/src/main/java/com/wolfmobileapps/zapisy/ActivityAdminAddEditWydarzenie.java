package com.wolfmobileapps.zapisy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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



public class ActivityAdminAddEditWydarzenie extends AppCompatActivity {

    private static final String TAG = "ActivityAdminAddEditWyd";

    //views:
    LinearLayout linLayAdminButtons;
    Button buttonAdminPrzeniesDoHistorii;
    Button buttonAdminUsun;
    Button buttonAdminEdytuje;
    LinearLayout linLayAdminEdycjaWydarzenia;
    EditText editTextAdminTytul;
    EditText editTextAdminData;
    EditText editTextAdminCena;
    EditText editTextAdminOpis;
    EditText editTextAdminRegulamin;
    EditText editTextAdminDystans;
    EditText editTextAdminUczestnicyIlosc;
    Button buttonAdminAktualizuj;
    Button buttonAdminWyniki;


    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_edit_wydarzenie);

        // do Shared Prefereneces instancja
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        //views
        linLayAdminButtons = findViewById(R.id.linLayAdminButtons);
        buttonAdminPrzeniesDoHistorii = findViewById(R.id.buttonAdminPrzeniesDoHistorii);
        buttonAdminUsun = findViewById(R.id.buttonAdminUsun);
        buttonAdminEdytuje = findViewById(R.id.buttonAdminEdytuje);
        linLayAdminEdycjaWydarzenia = findViewById(R.id.linLayAdminEdycjaWydarzenia);
        editTextAdminTytul = findViewById(R.id.editTextAdminTytul);
        editTextAdminData = findViewById(R.id.editTextAdminData);
        editTextAdminCena = findViewById(R.id.editTextAdminCena);
        editTextAdminOpis = findViewById(R.id.editTextAdminOpis);
        editTextAdminRegulamin = findViewById(R.id.editTextAdminRegulamin);
        editTextAdminDystans = findViewById(R.id.editTextAdminDystans);
        editTextAdminUczestnicyIlosc = findViewById(R.id.editTextAdminUczestnicyIlosc);
        buttonAdminAktualizuj = findViewById(R.id.buttonAdminAktualizuj);
        buttonAdminWyniki = findViewById(R.id.buttonAdminWyniki);

        //dodanie  przykładowych wydarzeń do firestore
//        Wydarzenie wyd1 = new Wydarzenie("" + System.currentTimeMillis(), "Czwartki Bojanie - jesień 2019, konkurs 5", "2019-10-12", 0, "Zawody w 3 kategoriach wiekowych:\n11 lat i młodsi (rocznik 2009), 12 lat (rocznik 2008), 13 lat (rocznik 2007)", "Przyklądowy regulamin", 12, 180, false);
//        Wydarzenie wyd2 = new Wydarzenie("" + System.currentTimeMillis(), "Przyjaźń - Małe Kaszuby Biegają 2019", "2019-09-12", 0, "Zapraszamy do udziału w II Paradzie Rowerowej w ramach VI Festiwalu Zdrowia! To krótki, 10-kilometrowy przejazd dla całych rodzin! Trasa wiedzie przez Przyjaźń 2007)", "Przyklądowy regulamin", 50, 810, false);
//        Wydarzenie wyd3 = new Wydarzenie("" + System.currentTimeMillis(), "II Kaszubska Prada Rowerowa\", \"2019-10-12 10:50 Przyjaźń ", "2019-10-11", 0, "Opłać swój udział i wystartuj w całym cyklu Kaszuby Biegają 2019", "Przyklądowy regulamin", 56.5f, 80, false);
//        Wydarzenie wyd4 = new Wydarzenie("" + System.currentTimeMillis(), "VII Bieg Przyjaźni\", \"2019-10-12 11:00 Przyjaźń k. Żukowa", "2018-10-12", 1, "15 km, 10 km, 5 km, 21 km i 1,6 km na zakończenie. Weź udział w Biegowej Imprezie Marzeń! Zwolnij i odpocznij we Wdzydzach na Kaszubach od 12 ", "Przyklądowy regulamin", 15, 90, false);
//        Wydarzenie wyd5 = new Wydarzenie("" + System.currentTimeMillis(), "Marsz Nordic Walking - Festiwal Zdrowia", "2019-01-12", 1.50f, "Zawody w 3 kategoriach wiekowych:\n11 lat i młodsi (rocznik 2009), 12 lat (rocznik 2008), 13 lat (rocznik 2007)", "Przyklądowy regulamin", 22, 550, false);
//        Wydarzenie wyd6 = new Wydarzenie("" + System.currentTimeMillis(), "Hard Runner - IV Festiwal Biegów Polski Północnej", "2019-10-14", 0, "awodów, szatnie: Przyjaźń, ul. Szkolna 2, czynne od godz. 08:30\nStart i Meta: Przyjaźń, ul. Szkolna 2", "Przyklądowy regulamin", 7, 280, false);
//        Wydarzenie wydarzenie = new Wydarzenie();
//        wydarzenie.addDataToFirestore(this, COLLECTION_NAME_WYDARZENIE, wyd1.getWydarzenieNazwaCollection(), wyd1);
//        wydarzenie.addDataToFirestore(this,COLLECTION_NAME_WYDARZENIE, wyd2.getWydarzenieNazwaCollection(), wyd2);
//        wydarzenie.addDataToFirestore(this,COLLECTION_NAME_WYDARZENIE, wyd3.getWydarzenieNazwaCollection(), wyd3);
//        wydarzenie.addDataToFirestore(this,COLLECTION_NAME_WYDARZENIE, wyd4.getWydarzenieNazwaCollection(), wyd4);
//        wydarzenie.addDataToFirestore(this,COLLECTION_NAME_WYDARZENIE, wyd5.getWydarzenieNazwaCollection(), wyd5);
//        wydarzenie.addDataToFirestore(this,COLLECTION_NAME_WYDARZENIE, wyd6.getWydarzenieNazwaCollection(), wyd6);

        // ustawienie jeśli jest pusty czyli ma utworzyć nowe wydarzenie
        intent = getIntent();
        if (intent.hasExtra("nowy")){

            linLayAdminButtons.setVisibility(View.GONE);
            linLayAdminEdycjaWydarzenia.setVisibility(View.VISIBLE);
            buttonAdminAktualizuj.setText("DODAJ");
        }

        // ustawienie jeśli jest nie jest pusty czyli ma być edytowane wydarzenie lub sprawdzone wyniki
        if (intent.hasExtra("edycja")){

            // ustawienie danych z intent w edit tekstach
            editTextAdminTytul.setText(shar.getString(TO_ACTIVITY_WYDARZENIE_TYTUL, "Tytuł"));
            editTextAdminData.setText(shar.getString(TO_ACTIVITY_WYDARZENIE_DATA, "Data"));
            editTextAdminCena.setText("" + shar.getFloat(TO_ACTIVITY_WYDARZENIE_CENA, 0.0f));
            editTextAdminOpis.setText(shar.getString(TO_ACTIVITY_WYDARZENIE_OPIS, "Opis"));
            editTextAdminRegulamin.setText(shar.getString(TO_ACTIVITY_WYDARZENIE_REGULAMIN, "Regulamin"));
            editTextAdminDystans.setText("" + shar.getFloat(TO_ACTIVITY_WYDARZENIE_DYSTANS, 0.0f));
            editTextAdminUczestnicyIlosc.setText("" + shar.getFloat(TO_ACTIVITY_WYDARZENIE_UCZESTNICY_ILOSC, 0.0f));


            //jeśli wydarzenie jest przeniesione do historii to ukryje guziki z przenoszeniem do historii i edycją wydarzenia, pokazuje z wynikami
            if (shar.getBoolean(TO_ACTIVITY_WYDARZENIE_HISTORIA, false)) {
                buttonAdminPrzeniesDoHistorii.setVisibility(View.GONE);
                buttonAdminEdytuje.setVisibility(View.GONE);
                buttonAdminWyniki.setVisibility(View.VISIBLE);
            }
        }

        //buttons

        //przenosi wdarzenie do historii
        buttonAdminPrzeniesDoHistorii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDataOrAddToHistoryData(true);

                //zamknięcie activity po educji
                finish();
            }
        });

        // usuwa wydarzenie
        buttonAdminUsun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wydarzenieNazwaCollection = shar.getString(TO_ACTIVITY_WYDARZENIE_NAZWA_COLLECTION, "error");
                Wydarzenie wydarzenie = new Wydarzenie();
                wydarzenie.deleteDataFromFirestore(ActivityAdminAddEditWydarzenie.this, COLLECTION_NAME_WYDARZENIE, wydarzenieNazwaCollection);

                //zamknięcie activity po educji
                finish();
            }
        });

        // pokazuje linlay z edycjami textów
        buttonAdminEdytuje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linLayAdminButtons.setVisibility(View.GONE);
                linLayAdminEdycjaWydarzenia.setVisibility(View.VISIBLE);
            }
        });

        // button aktualizacja bądz dodanie nowego wydarzenia
        buttonAdminAktualizuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editDataOrAddToHistoryData(false);

                //zamknięcie activity po educji
                finish();

            }
        });

        //przenosi do activity z wynikami danego wydarzenia
        buttonAdminWyniki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // przenosi do wyników danego wydarzenia - wszystkie dane wydarzenia dodane już wcześniej do shared pref przy przejściu z listView do tego activity
                Intent intentWydarzenie = new Intent(ActivityAdminAddEditWydarzenie.this, ActivityWyniki.class);
                startActivity(intentWydarzenie);
            }
        });
    }

    public void editDataOrAddToHistoryData(boolean dodajDoHistorii){
        String wydarzenieTytul = editTextAdminTytul.getText().toString();
        String wydarzenieData = editTextAdminData.getText().toString();
        Float wydarzenieCena = Float.parseFloat(editTextAdminCena.getText().toString());
        String wydarzenieOpis = editTextAdminOpis.getText().toString();
        String wydarzenieRegulamin = editTextAdminRegulamin.getText().toString();
        Float wydarzenieDystans = Float.parseFloat(editTextAdminDystans.getText().toString());
        Float wydarzenieUczestnicyIlosc = Float.parseFloat(editTextAdminUczestnicyIlosc.getText().toString());
        Boolean wydarzenieHistoria = dodajDoHistorii;

        //  dodanie nowego wydarzeania - jeśli jest edycja to ominie tego ifa
        if (buttonAdminAktualizuj.getText().equals("DODAJ")){

            String wydarzenieNazwaCollection = "" + System.currentTimeMillis();
            Wydarzenie wydarzenie = new Wydarzenie(wydarzenieNazwaCollection,wydarzenieTytul,wydarzenieData,wydarzenieCena,wydarzenieOpis,wydarzenieRegulamin,wydarzenieDystans,wydarzenieUczestnicyIlosc,wydarzenieHistoria);
            wydarzenie.addDataToFirestore(ActivityAdminAddEditWydarzenie.this, COLLECTION_NAME_WYDARZENIE, wydarzenieNazwaCollection, wydarzenie);

            //zamknięcie activity po zapisie
            finish();

            return;
        }

        // aktualizacja wydarzenia z collection
        String wydarzenieNazwaCollection = shar.getString(TO_ACTIVITY_WYDARZENIE_NAZWA_COLLECTION, "error");
        Wydarzenie wydarzenie = new Wydarzenie(wydarzenieNazwaCollection, wydarzenieTytul,wydarzenieData,wydarzenieCena,wydarzenieOpis,wydarzenieRegulamin,wydarzenieDystans,wydarzenieUczestnicyIlosc,wydarzenieHistoria );
        wydarzenie.addDataToFirestore(ActivityAdminAddEditWydarzenie.this, COLLECTION_NAME_WYDARZENIE, wydarzenieNazwaCollection, wydarzenie);

    }
}
