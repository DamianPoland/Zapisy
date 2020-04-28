package com.wolfmobileapps.zapisy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityInfo extends AppCompatActivity {


    //views
    private LinearLayout version;
    private LinearLayout owner;
    private LinearLayout source;
    private LinearLayout privacy;
    private LinearLayout info;
    private TextView textViewVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        version = findViewById(R.id.version);
        owner = findViewById(R.id.faq);
        source = findViewById(R.id.source);
        privacy = findViewById(R.id.privacy);
        info = findViewById(R.id.info);

        //ustawienie górnej nazwy i strzałki do powrotu
        getSupportActionBar().setTitle("About"); //ustawia nazwę na górze
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ustawia strzałkę

        //ustavienie zazwy wersji z gradle
        textViewVersionName = findViewById(R.id.textViewVersionName);
        textViewVersionName.setText(BuildConfig.VERSION_NAME);

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = "Version:";
                String alertString = BuildConfig.VERSION_NAME;
                createAlertDialog(titule, alertString);
            }
        });

        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = "Owner:";
                String alertString = "R&S";
                createAlertDialog(titule, alertString);
            }
        });

        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = "Open source licenses";
                String alertString = getResources().getString(R.string.sourceDescription);
                createAlertDialog(titule, alertString);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = "Privacy policy";
                String alertString = getResources().getString(R.string.privacy_policy_description);
                createAlertDialog(titule, alertString);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = "Developer Info";
                String alertString = "Contact:  \nPoland \nwww.wolfmobileapps.com";
                createAlertDialog(titule, alertString);
            }
        });
    }

    // tworzy alert dialog z podanego stringa tutułu i opisu
    private void createAlertDialog(String titule, String alertString) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInfo.this);
        builder.setTitle(titule);
        if (titule.equals("Developer Info")) {
            builder.setIcon(R.drawable.wolf_icon);
            builder.setNegativeButton("Open website", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri webpage = Uri.parse("http://wolfmobileapps.com");
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        }
        builder.setMessage(alertString);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do something when click OK
            }
        }).create();
        builder.show();
    }
}
