package com.bignerdranch.android.locatr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

/**
 * Created by aupifb on 12/09/2015.
 */
public class MarkerClickFragment extends DialogFragment {

    private String parkSnapshotKey;
    private Callbacks mCallbacks;

    private TextView mTextViewLegitLevel;
    private Button mButtonLegitLevel;


    public static MarkerClickFragment newInstance(int title, String parkSnapshotKey, Double lat, Double lng, int legitlevel) {
        MarkerClickFragment frag = new MarkerClickFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putString("parkSnapshotKey", parkSnapshotKey);
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        args.putInt("legitlevel", legitlevel);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String markerid = getArguments().getString("markerid");
        final String parkSnapshotKey = getArguments().getString("parkSnapshotKey");
        final Double lat = getArguments().getDouble("lat");
        final Double lng = getArguments().getDouble("lng");
        final int legitlevel = getArguments().getInt("legitlevel");
        int title = getArguments().getInt("title");
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mCallbacks = (Callbacks)getActivity();

        View v=inflater.inflate(R.layout.fragment_marker_click_dialog, null);

        mTextViewLegitLevel = (TextView)v.findViewById(R.id.textview_legit_level);
        mTextViewLegitLevel.setText("Legit level: " + legitlevel);

        mButtonLegitLevel = (Button)v.findViewById(R.id.button_legit_level);
        mButtonLegitLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase myFirebaseRef = new Firebase("https://geoparking.firebaseio.com/");
                final Firebase spacesRef = myFirebaseRef.child("spaces");

                spacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot parkSnapshot : snapshot.getChildren()) {
                            Parking park = parkSnapshot.getValue(Parking.class);
                            Log.d("lol", parkSnapshot.getKey());


                            if (park.getLat().equals(lat) && park.getLng().equals(lng)) {
                                int legitlevel = park.getLegitlevel();
                                legitlevel++;
                                spacesRef.child(parkSnapshotKey).child("legitlevel").setValue(legitlevel);
                                mTextViewLegitLevel.setText("Legit level: " + legitlevel);
                            } else {
                                Log.d("lol", "equals: false");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Firebase myFirebaseRef = new Firebase("https://geoparking.firebaseio.com/");
                                Firebase spacesRef = myFirebaseRef.child("spaces");
                                spacesRef.child(parkSnapshotKey).removeValue();
                                mCallbacks.removeMarker();
                                dismiss();
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mCallbacks.removeMarker();
                                dismiss();
                            }
                        }
                )
                .create();
    }

    public interface Callbacks {
        void removeMarker();
    }
}
