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
import android.widget.TextView;

import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_USERS;
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


    // do Shared Preferences
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    //do Firebase Database
    FirebaseFirestore db;
    String emailUser;

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

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        // pobranie umaila Usera jako key do nazwania dokumentu
        emailUser = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_EMAIL, "");

        // słucha zmian na serwerze - ukrywa przycisk dołacz jak jest wysłane coś na serwer pod child user Email
        final DocumentReference docRef = db.collection(COLLECTION_NAME_USERS).document(emailUser);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                    String cityUser = "Miasto " + (String) snapshot.getData().get("cityUser");
                    String postCodeUser = "Kod pocztowy: " + (String) snapshot.getData().get("postCodeUser");
                    String telephoneUser = "Nr telefony: " + (String) snapshot.getData().get("telephoneUser");

                    // dodanie danych do textViews z danymi uzytkownika
                    textViewUserEmail.setText(emailUserAndDescription);
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

                //pobranie danych danego usera
                //String emailUser - pobrany wcześniej bo tak samo się nazywa dokument czyli key
                String nameUser = shar.getString(TO_ACTIVITY_WYDARZENIE_USER_NAME, "");
                String nameAndSurnameUser = editTextUserNameAndSurname.getText().toString();
                String adressUser = editTextUserAdres.getText().toString();
                String cityUser = editTextUserMiasto.getText().toString();
                String postCodeUser = editTextUserKodPocztowy.getText().toString();
                String telephoneUser = editTextUserNrTelefonu.getText().toString();

                //dodanie danych użytkownika do firestore users
                UserToAddToFirebase userToAddToFirebase = new UserToAddToFirebase(emailUser,nameUser,nameAndSurnameUser,adressUser,cityUser,postCodeUser,telephoneUser);
                userToAddToFirebase.addUserToFirestore(UserActivity.this, COLLECTION_NAME_USERS, emailUser, userToAddToFirebase);

                //wyłaczenie pól edycji i włączenie guzika zmien dane
                linLayUserEdycjaDanychUzytkownika.setVisibility(View.GONE);
                buttonUserZmienDane.setVisibility(View.VISIBLE);

            }
        });
    }






    //    @Override
    public void onBackPressed() {
        //musi być bo do tego activity wchodzi po logowaniu i jak sie wcika bez tego to wariuje bo poprzednia strona jest tak naprawde logowania a nie MainActivity
        startActivity(new Intent(UserActivity.this, MainActivity.class));
    }
}
