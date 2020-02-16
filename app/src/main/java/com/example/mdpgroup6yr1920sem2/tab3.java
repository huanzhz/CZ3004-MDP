package com.example.mdpgroup6yr1920sem2;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.Charset;


/**
 * A simple {@link Fragment} subclass.
 */
public class tab3 extends Fragment {


    public tab3() {
        // Required empty public constructor
    }

    public MainActivity mainActivityObj;
    private TextView incomingMessages;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        if (view == null) {
            // get the main activity from MainActivity class
            mainActivityObj = (MainActivity) getActivity();

            view = inflater.inflate(R.layout.fragment_tab3, container, false);

            // Send button
            Button btnSend = (Button) view.findViewById(R.id.btnSend);
            final EditText etSend = (EditText) view.findViewById(R.id.editText);

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
                    mainActivityObj.mBluetoothConnection.write(bytes);

                    etSend.setText("");
                }
            });

            // Receive Messages
            incomingMessages = (TextView) view.findViewById(R.id.incomingMessage);
        }
        return view;
    }

    public void setIncomingText(StringBuilder yourText){
        incomingMessages.setText(yourText);
    }

}
