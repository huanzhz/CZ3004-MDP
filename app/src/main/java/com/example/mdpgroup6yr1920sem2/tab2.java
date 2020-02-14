package com.example.mdpgroup6yr1920sem2;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.nio.charset.Charset;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class tab2 extends Fragment {


    public tab2() {
        // Required empty public constructor
    }

    // Variables
    public MainActivity mainActivityObj;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            // get the main activity from MainActivity class
            mainActivityObj = (MainActivity) getActivity();

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_tab1, container, false);

            Button btnEnableDisable_Discoverable = (Button) view.findViewById(R.id.btnDiscoverable_on_off);
            Button btnDiscover = (Button) view.findViewById(R.id.btnFindUnpairedDevices);

            mainActivityObj.lvNewDevices = (ListView) view.findViewById(R.id.lvNewDevices);
            mainActivityObj.lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //first cancel discovery because its very memory intensive.
                    mainActivityObj.mBluetoothAdapter.cancelDiscovery();

                    //Log.d(TAG, "onItemClick: You Clicked on a device.");
                    String deviceName = mainActivityObj.mBTDevices.get(position).getName();
                    String deviceAddress = mainActivityObj.mBTDevices.get(position).getAddress();

                    //Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                    //Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);
                    Toast.makeText(mainActivityObj.getApplicationContext(), "Connecting to " + deviceName, Toast.LENGTH_LONG).show();

                    //create the bond.
                    //NOTE: Requires API 17+? I think this is JellyBean
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        //Log.d(TAG, "Trying to pair with " + deviceName);
                        mainActivityObj.mBTDevices.get(position).createBond();

                        mainActivityObj.mBTDevice = mainActivityObj.mBTDevices.get(position);
                        mainActivityObj.mBluetoothConnection = new BluetoothConnectionService(mainActivityObj);
                    }
                }
            });

            btnDiscover.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    //Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                    mainActivityObj.btnDiscover();
                }
            });

            btnEnableDisable_Discoverable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                    mainActivityObj.btnEnableDisableDiscoverable();
                }
            });

            // Connection and Send Message
            Button btnStartConnection = (Button) view.findViewById(R.id.btnStartConnection);

            btnStartConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivityObj.startConnection();
                }
            });

        }
        return view;
    }

}
