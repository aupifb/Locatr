package com.bignerdranch.android.locatr;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.DuplicateFormatFlagsException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MarkerClickFragment.Callbacks {

    private GoogleMap mMap;
    double currentlatitude, currentlongitude;

    private static final String EXTRA_CURRENT_LATITUDE =
            "com.bignerdranch.android.criminalintent.current_latitude";
    private static final String EXTRA_CURRENT_LONGITUDE =
            "com.bignerdranch.android.criminalintent.current_longitude";


    //private static final String DIALOG_MARKER_REMOVE = "DialogMarkerRemove";

    //private static final int REQUEST_MARKER_REMOVE = 0;


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

    private void addFirebaseMarkers() {


        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://geoparking.firebaseio.com/");
        final Firebase spacesRef = myFirebaseRef.child("spaces");
        Query queryRef = spacesRef.orderByKey();
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Parking parks = dataSnapshot.getValue(Parking.class);
                Log.i("lol", parks.getSpaceCoordinates() + ": " + parks.getCreatorUser());
                LatLng mlatlng = new LatLng(parks.getLat(), parks.getLng());
                int legitlevel = parks.getLegitlevel();

                BitmapDescriptor icon;

                if (legitlevel == 0) {
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                } else if (legitlevel < 5) {
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                } else icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

                mMap.addMarker(new MarkerOptions()
                        .position(mlatlng)
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_play_light))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        .snippet(parks.getCreatorUser())
                        .icon(icon)
                        .title(parks.getSpaceCoordinates()));


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Log.d("lol", marker.getId() + " id, " + marker.hashCode());

                Firebase.setAndroidContext(getApplicationContext());
                Firebase myFirebaseRef = new Firebase("https://geoparking.firebaseio.com/");
                final Firebase spacesRef = myFirebaseRef.child("spaces");

                spacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot parkSnapshot : snapshot.getChildren()) {
                            Parking park = parkSnapshot.getValue(Parking.class);
                            LatLng mlatlng = new LatLng(park.getLat(), park.getLng());


                            if (marker.getPosition().equals(mlatlng)) {
                                Double lat = marker.getPosition().latitude;
                                Double lng = marker.getPosition().longitude;
                                FragmentManager manager = getSupportFragmentManager();
                                MarkerClickFragment dialog = MarkerClickFragment
                                        .newInstance(R.string.MarkerClickFragmentTitle, parkSnapshot.getKey(), lat, lng, park.getLegitlevel());
                                //dialog.setTargetFragment(MapsActivity.this, REQUEST_MARKER_REMOVE);
                                dialog.show(manager, "DialogRemoveMarker");
                                //spacesRef.child(parkSnapshot.getKey()).removeValue();
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

                return false;
            }
        });

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //LatLng currentlatlng = new LatLng(currentlatitude, currentlongitude);
        //mMap.addMarker(new MarkerOptions().position(currentlatlng).title("Parking"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlatlng, 100));
        addFirebaseMarkers();

    }

    public void removeMarker() {
       recreate();
    }

}
