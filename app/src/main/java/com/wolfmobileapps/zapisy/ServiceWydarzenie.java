package com.wolfmobileapps.zapisy;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;

public class ServiceWydarzenie extends Service implements LocationListener {

    private static final String TAG = "ServiceWydarzenie";

    //stałe do zapisania w shared pref
    public static final String KEY_START_COUNTING_TIME = "key START counting time";
    public static final String KEY_FULL_TIME = "key FULL counting time";
    public static final String KEY_MAP_POINTS = "key punkty na mapie";
    public static final String KEY_SPEED = "key speed";
    public static final String KEY_DISTANCE = "key dystans";

    // do notyfication
    public static final int ID_OF_NOTIFICATION = 161;
    private Notification notification;
    public static final String CHANNEL_ID = "Service Wydarzenie counter";

    // do locationManagera
    private LocationManager locationManager;

    // do shar pref
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // lista do punktów na mapie
    private List<LatLng> listLatLng;

    // konstruktor
    public ServiceWydarzenie() {
    }

    // musi być i nic nie robi
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }




    // właczenie servisu
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // ustawienie notification
        setTheNotificationWithTapAction();

        // wystartowanie service
        startForeground(ID_OF_NOTIFICATION, notification); //nadać unikalne Id

        // instancja locationManagera
        instantionOfLocationManager(this);

        // do Shared Prefereneces instancja
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        //zapisanie STARTU czasu
        long startTime = System.currentTimeMillis();
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putLong(KEY_START_COUNTING_TIME, startTime);
        editor.apply(); // musi być na końcu aby zapisać zmiany w shar

        //instancja listy punktów na mapie
        listLatLng = new ArrayList<>();

        return START_STICKY;
    }



    private void instantionOfLocationManager(Context context) {

        // instancja locationManagera
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        // sprawdzenie czy są nadame permissions - musi byc bo błąd pokazuje
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // ustawienie locationMangera żeby słuchał
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, this); // minTime w ms (np 10000), minDistance w m np(10) - pozwolenie fine location otyczy GPS i NETWORK providers
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000,10,this); // dodane location przez sieć - pozwolenie coarse location
    }

    //przy każdej zmianie czasu lub/i dystansu z locationManagera wywołuje tą metodę

    @Override
    public void onLocationChanged(Location location) {

        //pobranie lat i lng danej lokalizacji i dodanie do listy listLatLng
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLngItem = new LatLng(lat,lng);
        listLatLng.add(latLngItem);
        Log.d(TAG, "onLocationChanged: lat: " + lat + ", lng: " + lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop of locationManager and updates
        locationManager.removeUpdates(this);

        // obliczenie długości trasy
        float distance = (float) SphericalUtil.computeLength(listLatLng); // zwraca dystans w metrach
        float distanceTimes10 = distance * 10;
        float distanceTimes10Rounded = Math.round(distanceTimes10);
        float distanceRounded = distanceTimes10Rounded/10;

        //pobranie czasu całego biegu
        long stopTime = System.currentTimeMillis();
        long fullTimeInMiliSec = stopTime - shar.getLong(KEY_START_COUNTING_TIME,0);
        long fullTimeInSec = fullTimeInMiliSec/1000;


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String fullTime = sdf.format(new Date(fullTimeInMiliSec));

        //String fullTime = new SimpleDateFormat("HH:mm:ss").format(fullTimeInMiliSec);

        // obliczenie prędkości
        float speed;
        if (fullTimeInSec!=0){
            double speedInKmPerH = (distance*3.6)/fullTimeInSec; //wynik w km na H
            double speedInKmPerHTimes100 = speedInKmPerH*100;
            double speedInKmPerHTimes10Rounded = Math.round(speedInKmPerHTimes100);
            speed = (float) (speedInKmPerHTimes10Rounded/100);

        }else{
            speed = 0;
        }

        // pobranie całej listy punktów na mapie i zapisanie do stringa
        Gson gson = new Gson();
        String listOfMapPoints = gson.toJson(listLatLng);

        // zapisanie czasu i punktów na mapie w postaci stringa dp shared pref
        editor = shar.edit(); //wywołany edytor do zmian
        editor.putFloat(KEY_DISTANCE,distanceRounded); // do Activity Wydarzenie
        editor.putString(KEY_FULL_TIME, fullTime); // do Activity Wydarzenie
        editor.putFloat(KEY_SPEED,speed); // do Activity Wydarzenie
        editor.putString(KEY_MAP_POINTS, listOfMapPoints); // do activity z Mapą
        editor.apply(); // musi być na końcu aby zapisać zmiany w shar

        // wysłanie do broadcast reciver danych
        Intent serviceIntent = new Intent();
        serviceIntent.putExtra(KEY_DISTANCE, distanceRounded);
        serviceIntent.putExtra(KEY_FULL_TIME, fullTime);
        serviceIntent.putExtra(KEY_SPEED,speed);
        serviceIntent.setAction("com.wolfmobileapps.zapisy");
        this.sendBroadcast(serviceIntent);
    }

    //notyfication do foreground service
    public void setTheNotificationWithTapAction() {
        // ustawienie pending intent które będzie otwierać MainActivity
        Intent intent = new Intent(this, ActivityWydarzenie.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Trwa rejestowanie Twojej trasy...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notification = builder.build();
    }
}
