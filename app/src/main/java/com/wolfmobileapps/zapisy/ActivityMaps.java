package com.wolfmobileapps.zapisy;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.wolfmobileapps.zapisy.ActivityWydarzenie.KEY_MAP_POINTS_TO_INTENT_OPEN_MAP;
import static com.wolfmobileapps.zapisy.MainActivity.SHARED_PREFERENCES_NAME;
import static com.wolfmobileapps.zapisy.ServiceWydarzenie.KEY_MAP_POINTS;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "ActivityMaps";

    // mapa na której jest wyświrtlane
    private GoogleMap mMap;

    // do shar pref
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

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

        // pobranie z intent stringa z punktami na mapie
        Intent intentMap = getIntent();
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
