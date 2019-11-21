package com.wolfmobileapps.zapisy;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
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


public class Wydarzenie {


    public Wydarzenie(String wydarzenieNazwaCollection, String wydarzenieTytul, String wydarzenieData, float wydarzenieCena, String wydarzenieOpis, String wydarzenieRegulamin, float wydarzenieDystans, float wydarzenieUczestnicyIlosc, boolean wydarzenieHistoria) {
        this.wydarzenieNazwaCollection = wydarzenieNazwaCollection;
        this.wydarzenieTytul = wydarzenieTytul;
        this.wydarzenieData = wydarzenieData;
        this.wydarzenieCena = wydarzenieCena;
        this.wydarzenieOpis = wydarzenieOpis;
        this.wydarzenieRegulamin = wydarzenieRegulamin;
        this.wydarzenieDystans = wydarzenieDystans;
        this.wydarzenieUczestnicyIlosc = wydarzenieUczestnicyIlosc;
        this.wydarzenieHistoria = wydarzenieHistoria;
    }

    public String getWydarzenieNazwaCollection() {
        return wydarzenieNazwaCollection;
    }

    public void setWydarzenieNazwaCollection(String wydarzenieNazwaCollection) {
        this.wydarzenieNazwaCollection = wydarzenieNazwaCollection;
    }

    public String getWydarzenieTytul() {
        return wydarzenieTytul;
    }

    public void setWydarzenieTytul(String wydarzenieTytul) {
        this.wydarzenieTytul = wydarzenieTytul;
    }

    public String getWydarzenieData() {
        return wydarzenieData;
    }

    public void setWydarzenieData(String wydarzenieData) {
        this.wydarzenieData = wydarzenieData;
    }

    public float getWydarzenieCena() {
        return wydarzenieCena;
    }

    public void setWydarzenieCena(float wydarzenieCena) {
        this.wydarzenieCena = wydarzenieCena;
    }

    public String getWydarzenieOpis() {
        return wydarzenieOpis;
    }

    public void setWydarzenieOpis(String wydarzenieOpis) {
        this.wydarzenieOpis = wydarzenieOpis;
    }

    public String getWydarzenieRegulamin() {
        return wydarzenieRegulamin;
    }

    public void setWydarzenieRegulamin(String wydarzenieRegulamin) {
        this.wydarzenieRegulamin = wydarzenieRegulamin;
    }

    public float getWydarzenieDystans() {
        return wydarzenieDystans;
    }

    public void setWydarzenieDystans(float wydarzenieDystans) {
        this.wydarzenieDystans = wydarzenieDystans;
    }

    public float getWydarzenieUczestnicyIlosc() {
        return wydarzenieUczestnicyIlosc;
    }

    public void setWydarzenieUczestnicyIlosc(float wydarzenieUczestnicyIlosc) {
        this.wydarzenieUczestnicyIlosc = wydarzenieUczestnicyIlosc;
    }

    public boolean getWydarzenieHistoria() {
        return wydarzenieHistoria;
    }

    public void setWydarzenieHistoria(boolean wydarzenieHistoria) {
        this.wydarzenieHistoria = wydarzenieHistoria;
    }

    //zmienne
    private String wydarzenieNazwaCollection;
    private String wydarzenieTytul;
    private String wydarzenieData;
    private float wydarzenieCena;
    private String wydarzenieOpis;
    private String wydarzenieRegulamin;
    private float wydarzenieDystans;
    private float wydarzenieUczestnicyIlosc;
    private boolean wydarzenieHistoria;

    // pusty kostruktor który musi byż żeby firebase działąło
    public Wydarzenie() {
    }

    // dodawanie wydarzenia do firestore
    public void addDataToFirestore(final Context context, String nameWydarzenieCollection, String documentKey, final Wydarzenie objectWyd) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection(nameWydarzenieCollection).document(documentKey) // key bedzie exampleKey
                .set(objectWyd) // wyd1 to obiekt który ma być dodany
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (objectWyd.getWydarzenieHistoria() == true){
                            Toast.makeText(context, "Wydarzenie przeniesione do historii", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // usuwanie wydarzenia z firestore
    public void deleteDataFromFirestore(final Context context, String nameWydarzenieCollection, String documentKey) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection(nameWydarzenieCollection).document(documentKey) // key bedzie exampleKey
                .delete() // usuwa
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Wydarzenie usunięte", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error - wydarzenie nie usunięte", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Do otwarcia next activity z list view w MainActivity, UserActivity i ActivityAdmin - pobranie danych z danego itema wydarzenia i zapisanie danych  o wydarzeniu do shared pref
    public void saveDataInSharedPref (Context context, ArrayList<Wydarzenie> listMain , int position){

        // instancja shar pref
        SharedPreferences shar;
        SharedPreferences.Editor editor;
        shar = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        //przejście do wydarzenia danego - pobranie danych z danego itema wydarzenia przekazane jest w metodzie z listViewWydarzenia.setOnItemClickListener
        Wydarzenie currentWydarzenie = listMain.get(position);
        String wydarzenieNazwaCollection = currentWydarzenie.getWydarzenieNazwaCollection();
        String wydarzenieTytul = currentWydarzenie.getWydarzenieTytul();
        String wydarzenieData = currentWydarzenie.getWydarzenieData();
        float wydarzenieCena = currentWydarzenie.getWydarzenieCena();
        String wydarzenieOpis = currentWydarzenie.getWydarzenieOpis();
        String wydarzenieRegulamin = currentWydarzenie.getWydarzenieRegulamin();
        float wydarzenieDystans = currentWydarzenie.getWydarzenieDystans();
        float wydarzenieUczestnicyIlosc = currentWydarzenie.getWydarzenieUczestnicyIlosc();
        boolean wydarzenieHistoria = currentWydarzenie.getWydarzenieHistoria();

        //zapisanie danych  o wydarzeniu do shared pref
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putString(TO_ACTIVITY_WYDARZENIE_NAZWA_COLLECTION, wydarzenieNazwaCollection);
        editor.putString(TO_ACTIVITY_WYDARZENIE_TYTUL, wydarzenieTytul);
        editor.putString(TO_ACTIVITY_WYDARZENIE_DATA, wydarzenieData);
        editor.putFloat(TO_ACTIVITY_WYDARZENIE_CENA, wydarzenieCena);
        editor.putString(TO_ACTIVITY_WYDARZENIE_OPIS, wydarzenieOpis);
        editor.putString(TO_ACTIVITY_WYDARZENIE_REGULAMIN, wydarzenieRegulamin);
        editor.putFloat(TO_ACTIVITY_WYDARZENIE_DYSTANS, wydarzenieDystans);
        editor.putFloat(TO_ACTIVITY_WYDARZENIE_UCZESTNICY_ILOSC, wydarzenieUczestnicyIlosc);
        editor.putBoolean(TO_ACTIVITY_WYDARZENIE_HISTORIA, wydarzenieHistoria);
        editor.apply();

    }

}


