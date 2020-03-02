package com.example.mdpgroup6yr1920sem2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.util.Log;

import java.nio.charset.Charset;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapTab extends Fragment {
    ImageButton upBtn;
    ImageButton leftBtn;
    ImageButton downBtn;
    ImageButton rightBtn;
    ImageButton updateBtn;
    ToggleButton waypointBtn;
    Button startBtn;
    Button exploreBtn;
    MapView mapView;
    Switch autoManualSwitch;

    private static final String TAG = "Tab1";
    public MainActivity mainActivityObj;
    private View view;
    private TextView statusMessages;

    public MapTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {

            // Inflate the layout for this fragment
            mainActivityObj = (MainActivity) getActivity();

            view = inflater.inflate(R.layout.map, container, false);

            mapView = (MapView) view.findViewById(R.id.idMapView);

            upBtn = (ImageButton) view.findViewById(R.id.btnTop);
            leftBtn = (ImageButton) view.findViewById(R.id.btnLeft);
            rightBtn = (ImageButton) view.findViewById(R.id.btnRight);
            downBtn = (ImageButton) view.findViewById(R.id.btnBottom);
            updateBtn = (ImageButton) view.findViewById(R.id.btnUpdateMap);
            waypointBtn = (ToggleButton) view.findViewById(R.id.waypointbtn);
            startBtn = (Button) view.findViewById(R.id.startbtn);
            exploreBtn = (Button) view.findViewById(R.id.explorebtn);
            autoManualSwitch = (Switch) view.findViewById(R.id.autoSwitch);
            autoManualSwitch.setChecked(false);

            // status Messages
            statusMessages = (TextView) view.findViewById(R.id.txtRobotStatus);

            upBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("f").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    }
                }
            });

            leftBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("tl").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    }
                }
            });

            rightBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("tr").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    }
                }
            });

            startBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("pFASTEST").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    }
                }
            });

            exploreBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("pEXPLORE").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    }
                }
            });

            downBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("r").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    }
                }
            });

            autoManualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (mainActivityObj.mBluetoothConnection != null) {
                            thread.start();
                        }
                        autoManualSwitch.setTextSize(14);
                        autoManualSwitch.setText("Auto");
                        updateBtn.setVisibility(View.GONE);


                    } else {
                        if (mainActivityObj.mBluetoothConnection != null) {
                            thread.interrupt();
                        }
                        autoManualSwitch.setTextSize(12);
                        autoManualSwitch.setText("Manual");
                        updateBtn.setVisibility(View.VISIBLE);
                    }
                }
            });

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        //Call the function once to fetch
                        fetchMapCoordinates();
                        Toast.makeText(getContext(), "Fetching Map Info!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            waypointBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (waypointBtn.isChecked()) {
                        MainActivity.wayPointChecked = true;
                    } else {
                        MainActivity.wayPointChecked = false;
                    }
                    Toast.makeText(getContext(), "Waypoint button pressed!", Toast.LENGTH_SHORT).show();
                }
            });

            //noinspection AndroidLintClickableViewAccessibility
            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getX();
                    float y = event.getY();
                    int[] info = mapView.setWaypointOrRobot(x, y);
                    int col = info[0];
                    //Flip the row
                    int row = 19 - info[1];
                    int isWaypoint = info[2];
                    if (isWaypoint == 1) {
                        sendWaypointCoordinates(col, row);

                    } else {
                        sendRobotCoordinates(col, row);
                    }
                    return false;
                }
            });
        }

        return view;
    }

    public void setIncomingText(String yourText) {
        statusMessages.setText(yourText);
    }

    public void setMapExploredObstacles(String exploredHex, String obstaclesHex) {
        mapView.setMapExploredObstacles(exploredHex, obstaclesHex);
    }

    public void setRobotCoordinates(int col, int row){
        mapView.setRobotCoordinates(col, row);
    }

    public void setRobotDirection(String direction){
        mapView.setRobotDirection(direction);
    }

    public void sendWaypointCoordinates(int col, int row) {
        if (mainActivityObj.mBluetoothConnection != null) {
            String waypointMessage = "p|WAYPOINT|" + row + "|" + col;
            byte[] bytes = waypointMessage.getBytes(Charset.defaultCharset());
            mainActivityObj.mBluetoothConnection.write(bytes);
        }
    }

    public void sendRobotCoordinates(int col, int row) {
        if (mainActivityObj.mBluetoothConnection != null) {
            String robotMessage = "Robot at (" + col + "," + row + ")";
            byte[] bytes = robotMessage.getBytes(Charset.defaultCharset());
            mainActivityObj.mBluetoothConnection.write(bytes);
        }
    }

    public void fetchMapCoordinates() {
        // Send the string "sendArena" to AMDTool
        if (mainActivityObj.mBluetoothConnection != null) {
            byte[] bytes = ("sendArena").getBytes(Charset.defaultCharset());
            mainActivityObj.mBluetoothConnection.write(bytes);
        }
    }

    public void displayNumberID(String numberIDString){
        // 1. x 2. y 3. numberID 4. direction
        numberIDString = numberIDString.trim();
        String[] numberArr = numberIDString.split(",");
        mapView.initNumberID(numberArr);
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);
                    mainActivityObj.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fetchMapCoordinates();
                            //Log.d(TAG, "Worked!");
                        }
                    });
                } catch (InterruptedException e) {
                    //End the loop on interruption
                    break;
                }
            }
        }
    };
}
