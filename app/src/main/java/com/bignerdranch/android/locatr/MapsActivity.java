package com.bignerdranch.android.locatr;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double currentlatitude, currentlongitude;

    private static final String EXTRA_CURRENT_LATITUDE =
            "com.bignerdranch.android.criminalintent.current_latitude";
    private static final String EXTRA_CURRENT_LONGITUDE =
            "com.bignerdranch.android.criminalintent.current_longitude";

    public static Intent newIntent(Context packageContext, double latitude, double longitude) {
        Intent intent = new Intent(packageContext, MapsActivity.class);
        intent.putExtra(EXTRA_CURRENT_LATITUDE, latitude);
        intent.putExtra(EXTRA_CURRENT_LONGITUDE, longitude);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        currentlatitude = getIntent().getDoubleExtra(EXTRA_CURRENT_LATITUDE, 1337);
        currentlongitude = getIntent().getDoubleExtra(EXTRA_CURRENT_LONGITUDE, 1337);
        Log.d("lol", Double.toString(currentlatitude) + ", " + Double.toString(currentlongitude));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng currentlatlng = new LatLng(currentlatitude, currentlongitude);
        mMap.addMarker(new MarkerOptions().position(currentlatlng).title("Parking"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 100));

    }
}
