package com.bignerdranch.android.locatr;


import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerViewAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by aupifb on 07/09/2015.
 */
public class LocatrFragment extends android.support.v4.app.Fragment {
    private GoogleApiClient mClient;
    private GoogleMap mMap;

    private Location mCurrentLocation;
    private Address mCurrentAddress;

    private RecyclerView.Adapter mAdapter;

    private Button mButton, mButtonMap, mButtonSync, mButtonMarkers;
    private EditText mEditTextSync;
    private TextView mTextViewLegit;

    private Double mLat, mLong;
    private LatLng mLatLng;


    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_locatr, container, false);
        setRetainInstance(true);

        mEditTextSync = (EditText)v.findViewById(R.id.editText_sync);

        mButton = (Button)v.findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findLocation();
            }
        });

        mButtonSync = (Button)v.findViewById(R.id.button_sync);
        mButtonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LocationRequest request = LocationRequest.create();
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                request.setNumUpdates(1);
                request.setInterval(0);

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mClient, request, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                mCurrentLocation = location;
                                Log.i("lol", "Got a fix: " + location);
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                mLat = mCurrentLocation.getLatitude();
                                mLong = mCurrentLocation.getLongitude();

                                Firebase.setAndroidContext(getContext());
                                Firebase myFirebaseRef = new Firebase("https://geoparking.firebaseio.com/");
                                Firebase spacesRef = myFirebaseRef.child("spaces");
                                Parking park = new Parking(mEditTextSync.getText().toString(), "Jojo", mLat, mLong, 0);
                                spacesRef.push().setValue(park);
                                spacesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onCancelled(FirebaseError error) {
                                    }
                                });
                            }
                        });





            }
        });

        mButtonMap = (Button)v.findViewById(R.id.button_map);
        mButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });
        
        mButtonMarkers = (Button)v.findViewById(R.id.button_markers);
        mButtonMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapMarkers();
            }
        });
        
        
        
        

        RecyclerView recycler = (RecyclerView) v.findViewById(R.id.parking_recycler_view);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Firebase.setAndroidContext(getContext());
        Firebase myFirebaseRef = new Firebase("https://geoparking.firebaseio.com/");
        Firebase spacesRef = myFirebaseRef.child("spaces");

        mAdapter = new FirebaseRecyclerViewAdapter<Parking, ParkingSpaceViewHolder>(Parking.class, android.R.layout.two_line_list_item, ParkingSpaceViewHolder.class, spacesRef) {
            @Override
            public void populateViewHolder(ParkingSpaceViewHolder parkingSpaceViewHolder, Parking parking) {
                parkingSpaceViewHolder.coordinatesText.setText(parking.getSpaceCoordinates());
                parkingSpaceViewHolder.creatorText.setText(parking.getCreatorUser());
            }
        };
        recycler.setAdapter(mAdapter);

        return(v);
    }

    private void showMapMarkers() {
    }


    private static class ParkingSpaceViewHolder extends RecyclerView.ViewHolder {
        TextView coordinatesText;
        TextView creatorText;

        public ParkingSpaceViewHolder(View itemView) {
            super(itemView);
            coordinatesText = (TextView)itemView.findViewById(android.R.id.text1);
            creatorText = (TextView) itemView.findViewById(android.R.id.text2);
        }
    }


    public void findLocation(){

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = location;
                        Log.i("lol", "Got a fix: " + location);
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        String result = null;
                        double latitude = mCurrentLocation.getLatitude();
                        double longitude = mCurrentLocation.getLongitude();
                        List<Address> mCurrentList = null;
                        try {
                            mCurrentList = geocoder.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCurrentAddress = mCurrentList.get(0);
                        result = mCurrentAddress.getAddressLine(0) + ", " + mCurrentAddress.getLocality() + ", " + mCurrentAddress.getCountryCode();
                        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showMap() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = location;
                        Log.i("lol", "Got a fix: " + location);
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        String result = null;
                        double latitude = mCurrentLocation.getLatitude();
                        double longitude = mCurrentLocation.getLongitude();
                        List<Address> mCurrentList = null;
                        try {
                            mCurrentList = geocoder.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mCurrentAddress = mCurrentList.get(0);
                        result = mCurrentAddress.getAddressLine(0) + ", " + mCurrentAddress.getLocality() + ", " + mCurrentAddress.getCountryCode();
                        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                        //Intent i = MapsActivity.newIntent(getActivity(), mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                        Intent i = new Intent(getActivity(), MapsActivity.class);
                        startActivity(i);
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        /*getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
            }
        });*/

    }

    @Override
    public void onStart() {
        super.onStart();

        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }

}
