package com.example.mdpgroup6yr1920sem2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final int RECONNECT_MAXIMUM_TIMES = 5;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT_F1 = "textF1";
    public static final String TEXT_F2 = "textF2";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar bluetoothToolBar;
    private TextView bluetoothToolBarText;

    private int[] tabIcons = {
            R.drawable.tab_map,
            R.drawable.tab_bluetooth
    };
    private TabItem tab1, tab2, tab3;
    public PageAdapter pageradapter;


    private static final String TAG = "MainActivity";
    private int reconnectCount = 0;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;

    StringBuilder messages;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothDevice mBTDevice;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    public DeviceListAdapter mDeviceListAdapter;

    ListView lvNewDevices;

    public static boolean wayPointChecked = false;

    Dialog myReconfigureDialog;

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
               /* if (device.getAddress().contains("B8:27:EB:67:AA:2A")) {
                    mBTDevices.add(device);
                }*/
                //Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    //Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are listen for disconnection
     */
    private BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                showBluetoothConnected();
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                //Log.d(TAG, "Device Disconnected");
                //reconnect();

                showBluetoothDisconnected();

                // reconnect to the device 5 times
                if (reconnectCount < RECONNECT_MAXIMUM_TIMES) {
                    // wait 3second before calling the reconnect function
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reconnect();
                        }
                    }, 3000);
                }
            }
        }
    };

    private void reconnect() {
        startConnection();

        if (mBluetoothConnection.getBluetoothState()) {
            reconnectCount = 0;
        } else {
            reconnectCount++;
        }
    }


    @Override
    protected void onDestroy() {
        //Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);

        // For tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tab1 = (TabItem) findViewById(R.id.Tab1);
        tab2 = (TabItem) findViewById(R.id.Tab2);
        tab3 = (TabItem) findViewById(R.id.Tab3);
        viewPager = findViewById(R.id.viewpager);

        // For bluetooth status bar
        bluetoothToolBar = (Toolbar) findViewById(R.id.btToolbar);
        bluetoothToolBarText = (TextView) findViewById(R.id.bluetoothTextView);

        messages = new StringBuilder();

        myReconfigureDialog = new Dialog(this);

        pageradapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageradapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    pageradapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    pageradapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 2) {
                    pageradapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        setupTabIcons();

        // bluetooth variables
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTDevices = new ArrayList<>();

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        //Broadcast when disconnected
        IntentFilter disconnectedDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastReceiver2, disconnectedDevicesIntent);

        //Broadcast when connected
        IntentFilter connectedDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mBroadcastReceiver2, connectedDevicesIntent);


        // Set up broadcast for receiving message
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

    }

    //create method for starting connection
//***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection() {
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        //Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device, uuid);
    }


    //Added the UI for Bluetooth
    //Call it in the BluetoothConnectionService to display
    public void showBluetoothConnected() {
        bluetoothToolBar.setBackgroundColor(Color.parseColor("#2196F3"));
        bluetoothToolBarText.setText("Bluetooth: Connected");
    }

    public void showBluetoothDisconnected() {
        bluetoothToolBar.setBackgroundColor(Color.parseColor("#6C6D6D"));
        bluetoothToolBarText.setText("Bluetooth: Not Connected");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btnDiscover() {
        //Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        Toast.makeText(getApplicationContext(), "Scanning...", Toast.LENGTH_LONG).show();

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            //Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     * <p>
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            //Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    // Broadcast Receiver function
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");

            Log.d(TAG, "Text: " + text);
            /* EXPLORE|
            FFC07F80FE01F800E001800300000000000000000000000000000000000000000007000E001F
            |000002000800200080000000000000000000000000000000000000000000000000000000000
            |[2,2]|Left */

            if (text.contains("EXPLORE") || text.contains("DONE")) {

                String statusTag;
                if (text.contains("EXPLORE")) {
                    statusTag = "EXPLORE";
                } else if (text.contains("DONE")) {
                    statusTag = "REACHED GOAL";
                } else {
                    statusTag = "IDLE";
                }

                Log.d(TAG, "Status Tag: " + statusTag);
                ((MapTab) pageradapter.fragment1).setIncomingText(statusTag);

                String[] RPiString = new String[4];
                String[] coordinates = new String[1];

                //Split the string into multiple parts and save to array
                RPiString = text.trim().split("\\|+");

                //Remove square brackets and comma
                coordinates = RPiString[3].replaceAll("\\[", "").replaceAll("\\]", "").trim().split(",");

                ((MapTab) pageradapter.fragment1).setMapExploredObstacles(RPiString[1], RPiString[2]);

                //Row / Column
                ((MapTab) pageradapter.fragment1).setRobotCoordinates(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
                ((MapTab) pageradapter.fragment1).setRobotDirection(RPiString[4]);

            } else if (text.contains("FASTEST")) {
                //Insert Fastest Path code here;
            } else if (text.contains("sendNumberID")) {
                //example {"sendNumberID":("x, y, NumberID, direction")}
                text = text.replace("\"sendNumberID\"", "");
                Pattern pattern = Pattern.compile("\"(.*?)\"");
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    text = matcher.group();
                    text = text.replace("\"", "");
                    Log.d(TAG, text);
                    ((MapTab) pageradapter.fragment1).displayNumberID(text);
                }
            } else {
                messages.append(text + "\n");
                ((CommsTab) pageradapter.fragment3).setIncomingText(messages);
            }
        }
    };

}