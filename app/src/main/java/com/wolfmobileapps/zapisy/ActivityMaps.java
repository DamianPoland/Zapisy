package com.wolfmobileapps.zapisy;

import androidx.fragment.app.FragmentActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.wolfmobileapps.zapisy.ActivityWydarzenie.KEY_MAP_POINTS_TO_INTENT_OPEN_MAP;
import static com.wolfmobileapps.zapisy.MainActivity.KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_DISTANCE;
import static com.wolfmobileapps.zapisy.MainActivity.KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_MAP_POINTS;
import static com.wolfmobileapps.zapisy.MainActivity.KEY_MAP_POINTS_TO_INTENT_OPEN_MAP_LIST;
import static com.wolfmobileapps.zapisy.MainActivity.KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_TIME;
import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "ActivityMaps";

    // mapa na której jest wyświrtlane
    private GoogleMap mMap;

    // do shar pref
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // do broadcast recivera
    private BroadcastReceiver updateUIReciver;
    private IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // do Shared Prefereneces instancja
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // do wyświetlenia markera swojego większego - skopiowane z neta
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(ActivityMaps.this);
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(ActivityMaps.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(ActivityMaps.this);
                snippet.setTextColor(Color.RED);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });

        // ustawienie kamery na środku Polski
        LatLng latLngSrodekPolski = new LatLng(51.976204,19.403013);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngSrodekPolski,6)); // im większ cyfra tym większe zbliżenie

        // pobranie intent
        Intent intentMap = getIntent();

        // jeśli jest otwarta mapa zbiorcza z ActivityWyniki
        if (intentMap.hasExtra(KEY_MAP_POINTS_TO_INTENT_OPEN_MAP_LIST)){

            //pobranie listy z intent
            ArrayList<String> listOfRouts = intentMap.getStringArrayListExtra(KEY_MAP_POINTS_TO_INTENT_OPEN_MAP_LIST);

            // rozbicie na poszczególne trasy z listy
            for (int i = 0; i < listOfRouts.size(); i++) {
                String rout = listOfRouts.get(i);

                // przekształcenie pobranego stringa w listę punktów
                Gson gson = new Gson();
                Type listTypeToGson = new TypeToken<ArrayList<LatLng>>() {}.getType();
                ArrayList<LatLng> listLatLng = gson.fromJson(rout, listTypeToGson);

                //ustawienie polyline na mapie
                PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
                polylineOptions.addAll(listLatLng);
                mMap.addPolyline(polylineOptions);

                //ustawienie markera na końcu
                mMap.addMarker(new MarkerOptions().position(listLatLng.get(listLatLng.size()-1)));
            }
        }

        // jeśli jest otwarcie mapy z ActivityWydarzenie z ZAPISANĄ TRASĄ
        if (intentMap.hasExtra(KEY_MAP_POINTS_TO_INTENT_OPEN_MAP)){

            // pobranie z intent stringa z punktami na mapie
            String listLatLngString = intentMap.getStringExtra(KEY_MAP_POINTS_TO_INTENT_OPEN_MAP);
            Gson gson = new Gson();
            Type listTypeToGson = new TypeToken<ArrayList<LatLng>>() {}.getType();
            ArrayList<LatLng> listLatLng = gson.fromJson(listLatLngString, listTypeToGson);

            //zabezpieczenie przzed pustą tabelką - nie przed pustym Stringiem bo to jest wcześniej tylko gdy tabelka jest ale pusta bo gps nic nie zapisał
            if (listLatLng.size()==0){
                return;
            }

            // pobranie z listy pierwszego i ostaniego miejsca i ustawienie markera z opisem
            LatLng startLatLng = listLatLng.get(0);
            String startName = "start";
            LatLng stopLatLng = listLatLng.get(listLatLng.size() - 1);
            String stopName = "stop";
            mMap.addMarker(new MarkerOptions().position(startLatLng).title(startName)).showInfoWindow();
            mMap.addMarker(new MarkerOptions().position(stopLatLng).title(stopName)).showInfoWindow();

            //ustawienie kamery aby było widać początek i koniec polylina z listy
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startLatLng);
            builder.include(stopLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stopLatLng,15));

            //ustawienie polyline na mapie
            PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
            polylineOptions.addAll(listLatLng);
            mMap.addPolyline(polylineOptions);
        }
    }

    // jeśli serwis będzie updatowany przez broadcast reccivera - jeśli jest otwarcie mapy z ActivityWydarzenie z LIFE UPDATE (puste bez żadnych danych w intent - wszystko będzie przkazyawne przez broadcast recivera)
    public void getDataFromBroadcastReciverAndShow () {
        filter = new IntentFilter();
        filter.addAction(getApplicationContext().getPackageName());
        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                // trzeba zrobić ifa bo servis wysyła tez broadcast do ActivityWydarzenie i żeby go nie łapał
                if (intent.hasExtra(KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_MAP_POINTS)) {

                    if (mMap == null){
                        return;
                    }

                    // czyści całą mape
                    mMap.clear();

                    // pobranie danych z servisu za pomocą→ broadcast recivera
                    String stringMapPointsLifeData = intent.getStringExtra(KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_MAP_POINTS);
                    float floatDistanceLifeData = intent.getFloatExtra(KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_DISTANCE, 0.0f);
                    String stringTimeLifeData = intent.getStringExtra(KEY_TO_INTENT_OPEN_MAP_LIFE_DATA_STRING_WITH_TIME);

                    // zaokrąglenie dystansu do 0,00
                    DecimalFormat df = new DecimalFormat("0.00");
                    String distanseRounded = df.format(floatDistanceLifeData);

                    Log.d(TAG, "onReceive:  stringDistanceLifeData: " + distanseRounded);
                    Log.d(TAG, "onReceive:  stringTimeLifeData: " + stringTimeLifeData);

                    // przekształcenie pobranego stringa w listę punktów
                    Gson gson = new Gson();
                    Type listTypeToGson = new TypeToken<ArrayList<LatLng>>() {}.getType();
                    ArrayList<LatLng> listLatLng = gson.fromJson(stringMapPointsLifeData, listTypeToGson);

                    //ustawienie polyline na mapie
                    PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
                    polylineOptions.addAll(listLatLng);
                    mMap.addPolyline(polylineOptions);

                    // ustawienie kamey
                    LatLng latLng = listLatLng.get(listLatLng.size()-1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17)); // im większ cyfra tym większe zbliżenie

                    // ustawienie markera z opisem
                    mMap.addMarker(new MarkerOptions().position(latLng).title(distanseRounded + " km").snippet(stringTimeLifeData + " s")).showInfoWindow();
                    //mMap.addMarker(new MarkerOptions().position(latLng).title(distanseRounded + " km \n" + stringTimeLifeData + " s")).showInfoWindow();
                    //mMap.addMarker(new MarkerOptions().position(latLng).title("My Title").snippet("My Snippet"+"\n"+"1st Line Text"+"\n"+"2nd Line Text"+"\n"+"3rd Line Text").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                }
            }
        };
        registerReceiver(updateUIReciver,filter); // wyłączenie recivera jest w onPause


    }

    @Override
    protected void onResume() {
        super.onResume();

        // wyłączenie recivera do life data z servisu
        getDataFromBroadcastReciverAndShow();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // wyłączenie recivera do life data z servisu - bez try catch sie wywala czaem
        unregisterReceiver(updateUIReciver);
    }
}
