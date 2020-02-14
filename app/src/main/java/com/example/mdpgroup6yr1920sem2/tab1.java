package com.example.mdpgroup6yr1920sem2;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;


/**
 * A simple {@link Fragment} subclass.
 */
public class tab1 extends Fragment {
    ImageButton upBtn;
    ImageButton leftBtn;
    ImageButton downBtn;
    ImageButton rightBtn;
    ToggleButton waypointBtn;

    public MainActivity mainActivityObj;


    public tab1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivityObj = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.map, container, false);
        upBtn = (ImageButton) view.findViewById(R.id.btnTop);
        leftBtn = (ImageButton) view.findViewById(R.id.btnLeft);
        rightBtn = (ImageButton) view.findViewById(R.id.btnRight);
        downBtn = (ImageButton) view.findViewById(R.id.btnBottom);
        waypointBtn = (ToggleButton) view.findViewById(R.id.waypointbtn);

        //Save switch state in shared preferences
        SharedPreferences pref = mainActivityObj.getSharedPreferences("waypointState", mainActivityObj.MODE_PRIVATE);
        waypointBtn.setChecked(pref.getBoolean("value", waypointBtn.isChecked()));

        upBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] bytes = ("f").getBytes(Charset.defaultCharset());
                mainActivityObj.mBluetoothConnection.write(bytes);
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] bytes = ("tl").getBytes(Charset.defaultCharset());
                mainActivityObj.mBluetoothConnection.write(bytes);
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] bytes = ("tr").getBytes(Charset.defaultCharset());
                mainActivityObj.mBluetoothConnection.write(bytes);
            }
        });

        downBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] bytes = ("r").getBytes(Charset.defaultCharset());
                mainActivityObj.mBluetoothConnection.write(bytes);
            }
        });

        waypointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(waypointBtn.isChecked()){
                    MainActivity.wayPointChecked = true;
                }
                else{
                    MainActivity.wayPointChecked = false;
                }
                Toast.makeText(getContext(),"Waypoint button pressed!",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
