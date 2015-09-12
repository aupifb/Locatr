package com.bignerdranch.android.locatr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by aupifb on 12/09/2015.
 */
public class MarkerClickFragment extends DialogFragment {

    private String parkSnapshotKey;
    private Callbacks mCallbacks;


    public static MarkerClickFragment newInstance(int title, String parkSnapshotKey) {
        MarkerClickFragment frag = new MarkerClickFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putString("parkSnapshotKey", parkSnapshotKey);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String markerid = getArguments().getString("markerid");
        final String parkSnapshotKey = getArguments().getString("parkSnapshotKey");
        int title = getArguments().getInt("title");
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mCallbacks = (Callbacks)getActivity();

        //View v=inflater.inflate(R.layout.fragment_time_select_dialog, null);


        return new AlertDialog.Builder(getActivity())
                //.setView(v)
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
                            }
                        }
                )
                .create();
    }

    public interface Callbacks {
        void removeMarker();
    }
}
