package com.wolfmobileapps.zapisy;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserToAddToFirebase {

    String emailUser;
    String nameUserFromGoogle;
    String nameAndSurnameUser;
    String adressUser;
    String cityUser;
    String postCodeUser;
    String telephoneUser;


    // dodawanie danych usera do firestore
    public void addUserToFirestore(final Context context, String nameUsersCollection, String documentKeyUserEmail, UserToAddToFirebase objectUser) {

        // dodawanie wydarzenia
        FirebaseFirestore db;

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        db.collection(nameUsersCollection).document(documentKeyUserEmail) // key bedzie exampleKey
                .set(objectUser) // wyd1 to obiekt który ma być dodany
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Wydarzenie dodane", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error - wydarzenie nie dodane", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public UserToAddToFirebase(String emailUser, String nameUserFromGoogle, String nameAndSurnameUser, String adressUser, String cityUser, String postCodeUser, String telephoneUser) {
        this.emailUser = emailUser;
        this.nameUserFromGoogle = nameUserFromGoogle;
        this.nameAndSurnameUser = nameAndSurnameUser;
        this.adressUser = adressUser;
        this.cityUser = cityUser;
        this.postCodeUser = postCodeUser;
        this.telephoneUser = telephoneUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getNameUserFromGoogle() {
        return nameUserFromGoogle;
    }

    public void setNameUserFromGoogle(String nameUserFromGoogle) {
        this.nameUserFromGoogle = nameUserFromGoogle;
    }

    public String getNameAndSurnameUser() {
        return nameAndSurnameUser;
    }

    public void setNameAndSurnameUser(String nameAndSurnameUser) {
        this.nameAndSurnameUser = nameAndSurnameUser;
    }

    public String getAdressUser() {
        return adressUser;
    }

    public void setAdressUser(String adressUser) {
        this.adressUser = adressUser;
    }

    public String getCityUser() {
        return cityUser;
    }

    public void setCityUser(String cityUser) {
        this.cityUser = cityUser;
    }

    public String getPostCodeUser() {
        return postCodeUser;
    }

    public void setPostCodeUser(String postCodeUser) {
        this.postCodeUser = postCodeUser;
    }

    public String getTelephoneUser() {
        return telephoneUser;
    }

    public void setTelephoneUser(String telephoneUser) {
        this.telephoneUser = telephoneUser;
    }

    public UserToAddToFirebase() {

    }
}
