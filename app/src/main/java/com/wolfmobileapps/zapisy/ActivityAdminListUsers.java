package com.wolfmobileapps.zapisy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.wolfmobileapps.zapisy.MainActivity.COLLECTION_NAME_USERS;

public class ActivityAdminListUsers extends AppCompatActivity {

    private static final String TAG = "ActivityAdminListUsers";

    //views
    private ListView listViewUsers;

    // listMain view arraylist i adapter
    private ArrayList<UserToAddToFirebase> listUsers;
    private ListUserArrayAdapter adapter;

    //do Firebase Database
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_users);

        //views
        listViewUsers = findViewById(R.id.listViewUsers);

        // do Firebase instancja Database
        db = FirebaseFirestore.getInstance();

        //wyświetlenie w listView
        listUsers = new ArrayList<>();
        adapter = new ListUserArrayAdapter(this, 0, listUsers);
        listViewUsers.setAdapter(adapter);

        //onClick listener na listMain View
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // do nothing
            }
        });

        //Read only once Collection - nie trzeba go potem wyłączać  (onCompleateListener):
        db.collection(COLLECTION_NAME_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                //pobranie danych z listenera
                                String emailUser = (String) document.getData().get("emailUser");
                                String nameUserFromGoogle = (String) document.getData().get("nameUserFromGoogle");
                                String nameAndSurnameUser = (String) document.getData().get("nameAndSurnameUser");
                                String adressUser = (String) document.getData().get("adressUser");
                                String cityUser = (String) document.getData().get("cityUser");
                                String postCodeUser = (String) document.getData().get("postCodeUser");
                                String telephoneUser = (String) document.getData().get("telephoneUser");

                                //dodanie danych do adaptera żeby wyświetlił w list View
                                UserToAddToFirebase userToAddToFirebase = new UserToAddToFirebase(emailUser, nameUserFromGoogle,nameAndSurnameUser,adressUser,cityUser,postCodeUser,telephoneUser);
                                adapter.add(userToAddToFirebase);

                                //sortowanie tableki po email
                                Comparator<UserToAddToFirebase> compareByTime = (UserToAddToFirebase o1, UserToAddToFirebase o2) -> o1.getEmailUser().compareTo( o2.getEmailUser() );
                                Collections.sort(listUsers, compareByTime);
                                adapter.notifyDataSetChanged();


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}
