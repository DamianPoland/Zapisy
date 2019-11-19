package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_STARTY;
import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_USERS;
import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_WYDARZENIE;
import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_USER_EMAIL;
import static com.wolfmobileapps.zapisy.MainActivity.TO_ACTIVITY_WYDARZENIE_USER_NAME;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    //views
    TextView textViewActivityUserOpis;
    TextView textViewUserEmail;
    TextView textViewUserNameAndSurname;
    TextView textViewUserAdres;
    TextView textViewUserMiasto;
    TextView textViewUserKodPocztowy;
    TextView textViewUserNrTelefonu;
    Button buttonUserZmienDane;

    EditText editTextUserNameAndSurname;
    EditText editTextUserAdres;
    EditText editTextUserMiasto;
    EditText editTextUserKodPocztowy;
    EditText editTextUserNrTelefonu;
    Button buttonUserZapiszDane;
    LinearLayout linLayUserEdycjaDanychUzytkownika;

    private ProgressBar progressBarWaiForFirebaseUser;
    private ListView listViewWydarzeniaUser;


    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //do Firebase Database
    FirebaseFirestore db;
    String emailUser;
    ListenerRegistration registration;

    // listMainUser view arraylist i adapter
    private ArrayList<Wydarzenie> listMainUser;
    private WydarzeniaArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // do Shared Prefereneces instancja
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        //views
        textViewActivityUserOpis = findViewById(R.id.textViewActivityUserOpis);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserNameAndSurname = findViewById(R.id.textViewUserNameAndSurname);
        textViewUserAdres = findViewById(R.id.textViewUserAdres);
        textViewUserMiasto = findViewById(R.id.textViewUserMiasto);
        textViewUserKodPocztowy = findViewById(R.id.textViewUserKodPocztowy);
        textViewUserNrTelefonu = findViewById(R.id.textViewUserNrTelefonu);
        buttonUserZmienDane = findViewById(R.id.buttonUserZmienDane);

        editTextUserNameAndSurname = findViewById(R.id.editTextUserNameAndSurname);
        editTextUserAdres = findViewById(R.id.editTextUserAdres);
        editTextUserMiasto = findViewById(R.id.editTextUserMiasto);
        editTextUserKodPocztowy = findViewById(R.id.editTextUserKodPocztowy);
        editTextUserNrTelefonu = findViewById(R.id.editTextUserNrTelefonu);
        buttonUserZapiszDane = findViewById(R.id.buttonUserZapiszDane);
        linLayUserEdycjaDanychUzytkownika = findViewById(R.id.linLayUserEdycjaDanychUzytkownika);
        listViewWydarzeniaUser = findViewById(R.id.listViewWydarzeniaUser);
        progressBarWaiForFirebaseUser = findViewById(R.id.progressBarWaiForFirebaseUser);

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        // pobranie umaila Usera jako key do nazwania dokumentu
        emailUser = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, "");

        //pobranie danych danego usera (czyli tylko maila) i wysłanie do firebase jeśli jest pierwsze uruchomienie
        if (shar.getBoolean("booleanOnlyForOneTimeToStartMethod", true)){
            userZapiszDane();
            editor = shar.edit();
            editor.putBoolean("booleanOnlyForOneTimeToStartMethod", false);
            editor.apply();
        }

        //wyświetlenie w listView z Firebas
        listMainUser = new ArrayList<>();
        adapter = new WydarzeniaArrayAdapter(this, 0, listMainUser);
        listViewWydarzeniaUser.setAdapter(adapter);


        //onClick listener na listMainUser View
        listViewWydarzeniaUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Do otwarcia next activity z list view - pobranie danych z danego itema wydarzenia i zapisanie danych  o wydarzeniu do shared pref
                Wydarzenie wydarzenie = new Wydarzenie();
                wydarzenie.saveDataInSharedPref(UserActivity.this, listMainUser, position);

                // otwarcie tego activity
                Intent intent = new Intent(UserActivity.this, ActivityWydarzenie.class);
                startActivity(intent);
            }
        });


        // WYDARZENIA W KTÓRYCH WZIĘTO UDZIAŁ- czyta tylko raz (nie jest to ciągły listener i nie trzeba go wyłączać potem)
        db.collection(COLLECTION_NAME_USERS).document(emailUser).collection(COLLECTION_NAME_STARTY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // pobiera ID danego wydarzenia do którego user dołączył
                                String idWydarzenia = document.getId();
                                getUserWydarzenie(idWydarzenia);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //button do zmiany danych
        buttonUserZmienDane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // włączenie pól edycji do zmian danych i wyłączenie guzika zmień dane
                linLayUserEdycjaDanychUzytkownika.setVisibility(View.VISIBLE);
                buttonUserZmienDane.setVisibility(View.INVISIBLE);

            }
        });

        //button do zapisanie danych
        buttonUserZapiszDane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pobranie danych danego usera i wysłanie do firebase
                userZapiszDane();
            }
        });
    }

    //pobranie danych danego usera i wysłanie do firebase
    private void userZapiszDane (){

        //String emailUser - pobrany wcześniej
        String nameUser = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "");
        String nameAndSurnameUser = editTextUserNameAndSurname.getText().toString();
        String adressUser = editTextUserAdres.getText().toString();
        String cityUser = editTextUserMiasto.getText().toString();
        String postCodeUser = editTextUserKodPocztowy.getText().toString();
        String telephoneUser = editTextUserNrTelefonu.getText().toString();

        //dodanie danych użytkownika do firestore users
        UserToAddToFirebase userToAddToFirebase = new UserToAddToFirebase(emailUser, nameUser, nameAndSurnameUser, adressUser, cityUser, postCodeUser, telephoneUser);
        userToAddToFirebase.addUserToFirestore(UserActivity.this, COLLECTION_NAME_USERS, emailUser, userToAddToFirebase);

        //wyłaczenie pól edycji i włączenie guzika zmien dane
        linLayUserEdycjaDanychUzytkownika.setVisibility(View.GONE);
        buttonUserZmienDane.setVisibility(View.VISIBLE);

    }

    // ZMIANA DANYCH - słucha zmian na serwerze - musi być wyłaczony po wyjsciu z activity
    private void getUserDaneAndPutToViews () {

        final DocumentReference docRef = db.collection(COLLECTION_NAME_USERS).document(emailUser);
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

                    // pobranie danych z serwera po zmianie
                    String emailUserAndDescription = "Email: " + emailUser;
                    String nameAndSurnameUser = "Imię i Nazwisko: " + (String) snapshot.getData().get("nameAndSurnameUser");
                    String adressUser = "Adres: " + (String) snapshot.getData().get("adressUser");
                    String cityUser = "Miasto: " + (String) snapshot.getData().get("cityUser");
                    String postCodeUser = "Kod pocztowy: " + (String) snapshot.getData().get("postCodeUser");
                    String telephoneUser = "Nr telefonu: " + (String) snapshot.getData().get("telephoneUser");

                    // dodanie danych do textViews z danymi uzytkownika
                    textViewUserEmail.setText("Email: " + emailUser);
                    textViewUserNameAndSurname.setText(nameAndSurnameUser);
                    textViewUserAdres.setText(adressUser);
                    textViewUserMiasto.setText(cityUser);
                    textViewUserKodPocztowy.setText(postCodeUser);
                    textViewUserNrTelefonu.setText(telephoneUser);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    // wczytuje dane wydarzenie do którego user dołaczył i wrzuca do listy - czyta tylko raz (nie jest to ciągły listener i nie trzeba go wyłączać potem)
    private void getUserWydarzenie(String idWydarzenia) {

        DocumentReference docRefWydarzenia = db.collection(COLLECTION_NAME_WYDARZENIE).document(idWydarzenia);
        docRefWydarzenia.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        //wyłączenie progress bara
                        progressBarWaiForFirebaseUser.setVisibility(View.GONE);

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

                        // wydarzenie zostanie dodane do adaptera
                        Wydarzenie wydarzenie = new Wydarzenie(wydarzenieNazwaCollection, wydarzenieTytul, wydarzenieData, wydarzenieCena, wydarzenieOpis, wydarzenieRegulamin, wydarzenieDystans, wydarzenieUczestnicyIlosc, wydarzenieHistoria);
                        adapter.add(wydarzenie);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        //metoda dodaje listenera i słucha wszystkiego cosię dzieje wdanym folderze "Wydarzenia"
        getUserDaneAndPutToViews();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // wyłaczenie listenera
        registration.remove();
    }


    //    @Override
    public void onBackPressed() {
        //musi być bo do tego activity wchodzi po logowaniu i jak sie wcika bez tego to wariuje bo poprzednia strona jest tak naprawde logowania a nie MainActivity
        startActivity(new Intent(UserActivity.this, MainActivity.class));
    }
}
