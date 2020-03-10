package com.example.mdpgroup6yr1920sem2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.os.Handler;

import java.nio.charset.Charset;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapTab extends Fragment implements SensorEventListener {
    ImageButton upBtn;
    ImageButton leftBtn;
    ImageButton downBtn;
    ImageButton rightBtn;
    ImageButton updateBtn;
    ToggleButton waypointBtn;
    Button startBtn;
    Button exploreBtn;
    Button resetBtn;
    Button caliFrontBtn;
    Button caliRightBtn;
    MapView mapView;
    Switch autoManualSwitch;

    private static final String TAG = "Tab1";
    private int Fastest_Row = 1, Fastest_Col = 1, Fastest_Count = 0;
    private String Fastest_Direction = "Right";
    private String[] Fastest_Commands = new String[150];
    public MainActivity mainActivityObj;
    private View view;
    private TextView statusMessages;
    Handler handler = new Handler();

    //Accelerometer
    private Sensor mySensor;
    private SensorManager SM;

    public MapTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {

            //Accelerometer
            SM = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
            // Accelerometer Sensor
            mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

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
            resetBtn = (Button) view.findViewById(R.id.resetbtn);
            //Temp
            caliFrontBtn = (Button) view.findViewById(R.id.calFront);
            caliRightBtn = (Button) view.findViewById(R.id.calRight);
            autoManualSwitch = (Switch) view.findViewById(R.id.autoSwitch);
            autoManualSwitch.setChecked(true);

            // status Messages
            statusMessages = (TextView) view.findViewById(R.id.txtRobotStatus);

            upBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        //pMANUAL|f
                        //Log.d(TAG, "hf\n");
                        byte[] bytes = ("hf\n").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            leftBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        //"pMANUAL|l
                        byte[] bytes = ("hl\n").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            rightBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        //pMANUAL|r
                        byte[] bytes = ("hr\n").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            downBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        //pMANUAL|b
                        byte[] bytes = ("hb\n").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //temp
            caliFrontBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("ho\n").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            caliRightBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("hi\n").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            startBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("pFASTEST").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            exploreBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        byte[] bytes = ("pEXPLORE").getBytes(Charset.defaultCharset());
                        mainActivityObj.mBluetoothConnection.write(bytes);
                    } else {
                        Toast.makeText(getContext(), "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            resetBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mainActivityObj.mBluetoothConnection != null) {
                        //byte[] bytes = ("reset").getBytes(Charset.defaultCharset());
                        //mainActivityObj.mBluetoothConnection.write(bytes);
                    }
                    mapView.resetMap();
                    Toast.makeText(getContext(), "Map Reset!", Toast.LENGTH_SHORT).show();
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

                    }
                    //Disable cos no need to set robot position
                    //To enable it, go to mapview also
                    /*else {
                        //sendRobotCoordinates(col, row);
                    }*/
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

    public void setRobotCoordinates(int row, int col) {
        mapView.setRobotCoordinates(row, col);
    }

    public void setRobotDirection(String direction) {
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

    public void displayNumberID(String numberIDString) {
        // 1. x 2. y 3. numberID 4. direction
        numberIDString = numberIDString.trim();
        String[] numberArr = numberIDString.split(",");
        mapView.initNumberID(numberArr);
    }

    public void runFastestThread(String[] commands) {
        Fastest_Col = 1;
        Fastest_Row = 1;
        Fastest_Count = 1;
        Fastest_Direction = "Right";
        Fastest_Commands = commands;
        handler.post(runnable);
    }

    final Runnable runnable = new Runnable() {
        public void run() {
            if (Fastest_Count++ < Fastest_Commands.length - 1) {
                //FASTEST|rflf
                Log.d("TAG", "Count: " + Fastest_Count);

                if (Fastest_Commands[Fastest_Count].charAt(0) == 'f') {
                    if (Fastest_Direction.contains("Top")) {
                        Log.d(TAG, "Test");
                        Fastest_Row = Fastest_Row + 1;
                        setRobotCoordinates(Fastest_Row, Fastest_Col);
                    } else if (Fastest_Direction.contains("Left")) {
                        Log.d(TAG, "Test1");
                        Fastest_Col = Fastest_Col - 1;
                        setRobotCoordinates(Fastest_Row, Fastest_Col);
                    } else if (Fastest_Direction.contains("Right")) {
                        Log.d(TAG, "Test2");
                        Fastest_Col = Fastest_Col + 1;
                        setRobotCoordinates(Fastest_Row, Fastest_Col);
                    } else {
                    }
                } else if (Fastest_Commands[Fastest_Count].charAt(0) == 'l') {
                    if (Fastest_Direction.contains("Top")) {
                        Fastest_Direction = "Left";
                        setRobotDirection("Left");
                    } else if (Fastest_Direction.contains("Right")) {
                        Fastest_Direction = "Top";
                        setRobotDirection("Top");
                    } else if (Fastest_Direction.contains("Left")) {
                        Fastest_Direction = "Down";
                        setRobotDirection("Down");
                    } else {
                        // if(Fastest_Direction.contains("Down"))
                        Fastest_Direction = "Right";
                        setRobotDirection("Right");
                    }
                } else if (Fastest_Commands[Fastest_Count].charAt(0) == 'r') {
                    if (Fastest_Direction.contains("Top")) {
                        Fastest_Direction = "Right";
                        setRobotDirection("Right");
                    } else if (Fastest_Direction.contains("Right")) {
                        Fastest_Direction = "Down";
                        setRobotDirection("Down");
                    } else if (Fastest_Direction.contains("Left")) {
                        Fastest_Direction = "Top";
                        setRobotDirection("Top");
                    } else {
                        // if(Fastest_Direction.contains("Down"))
                        Fastest_Direction = "Left";
                        setRobotDirection("Left");
                    }
                } else {
                    //Do Nothing
                }
                handler.postDelayed(this, 500);
            }
        }
    };

    Thread thread = new Thread() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
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

    public void onSensorChanged(SensorEvent event) {
        if (event.values[1] < -2 && (event.values[0] >= -2 || event.values[0] <= 2)) {
            Log.d(TAG, "Forward");
        } else if (event.values[0] > 6 && (event.values[1] >= -2 || event.values[1] <= 2)) {
            Log.d(TAG, "Left");
        } else if (event.values[0] < -6 && (event.values[1] >= -2 || event.values[1] <= 2)) {
            Log.d(TAG, "Right");
        } else if (event.values[1] < 10 && (event.values[0] >= -2 || event.values[0] <= 2)) {
            Log.d(TAG, "Back");
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
