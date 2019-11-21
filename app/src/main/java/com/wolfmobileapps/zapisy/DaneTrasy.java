package com.wolfmobileapps.zapisy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.wolfmobileapps.zapisy.ActivityWydarzenie.DEF_VALUE_TO_SET_FULL_TIME;

public class DaneTrasy {

    private static final String TAG = "DaneTrasy";


    private String userEmail;
    private float distance;
    private String fullTime;
    private float speed;
    private String mapPoints;

    public DaneTrasy(String userEmail, float distance, String fullTime, float speed, String mapPoints) {
        this.userEmail = userEmail;
        this.distance = distance;
        this.fullTime = fullTime;
        this.speed = speed;
        this.mapPoints = mapPoints;
    }

    //wysłanie uczestnictwa lub danych naserwer
    public void sentDataToFirebase(Context context, DaneTrasy daneTrasy, String collectionMain, String documentMain, String collectionUser, String documentUser) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        //wpisanie do Firebase uczestnictwa i odrazu trasy z zerowymi danymi lub ostatnimi
        db.collection(collectionMain).document(documentMain).collection(collectionUser).document(documentUser) // key bedzie exampleKey
                .set(daneTrasy) // wyd1 to obiekt który ma być dodany
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    // usuwanie wyniku z bazy przez admina gdy ktoś oszukiwał
    public void deleteDataDataFirebase(Context context, String collectionMain, String documentMain, String collectionUser, String documentUser) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        //wpisanie do Firebase uczestnictwa i odrazu trasy z zerowymi danymi lub ostatnimi
        db.collection(collectionMain).document(documentMain).collection(collectionUser).document(documentUser)
                .delete() // usuwa document
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public DaneTrasy() {

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userName) {
        this.userEmail = userEmail;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getFullTime() {
        return fullTime;
    }

    public void setFullTime(String fullTime) {
        this.fullTime = fullTime;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getMapPoints() {
        return mapPoints;
    }

    public void setMapPoints(String mapPoints) {
        this.mapPoints = mapPoints;
    }
}
