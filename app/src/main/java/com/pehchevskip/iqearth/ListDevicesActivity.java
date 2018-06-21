package com.pehchevskip.iqearth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pehchevskip.iqearth.Adapters.DeviceListAdapter;

import java.util.ArrayList;
import java.util.Set;

public class ListDevicesActivity extends AppCompatActivity {
    private static final String NICKNAME ="nickname" ;
    private static String TAG="ListDevicesActivity";
    private ArrayList<BluetoothDevice> mBtDevices=new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> mDiscoveredDevice=new ArrayList<>();
    private DeviceListAdapter adapter;
    private RecyclerView recyclerView;
    private BluetoothAdapter bluetoothAdapter;
    private TextView tvNoDevices;
    private Button btnScan;
    private Button btnContinue;
    //Nickname
    private String nickname;

    private boolean isReceiver3Registered = false;

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED
    private final BroadcastReceiver mbroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);
                    switch (state){
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG,"OFF");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d(TAG,"TURNING OFF");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG,"ON");

                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG,"TURNING ON");
                            break;
                    }


            }
        }
    };
    // Create a BroadcastReceiver for ACTION_SCAN_MODE_CHANGED
    private final BroadcastReceiver mbroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,bluetoothAdapter.ERROR);
                switch (mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG,"Discoverability Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG," Discoverability Disabled Able to receive Connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG,"Discoverability Disabled");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG,"Connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"Connected");
                        break;
                }


            }
        }
    };
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mbroadcastReceiver3 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDiscoveredDevice.add(device);
                DeviceListAdapter adapter=new DeviceListAdapter(mDiscoveredDevice);
                recyclerView.setAdapter(adapter);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };
    private final BroadcastReceiver mbroadcastReceiver4=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    Log.d("Broadcastreceiver","BONDED");

                }
                if(device.getBondState()==BluetoothDevice.BOND_BONDING){
                    Log.d("Broadcastreceiver","BONDING");
                }
                if(device.getBondState()==BluetoothDevice.BOND_NONE){
                    Log.d("Broadcastreceiver","NONE");
                }
            }
        }
    };

    private void enableDiscoverable() {
        Intent enableDiscoverable=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enableDiscoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(enableDiscoverable);

        IntentFilter intentFilter=new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mbroadcastReceiver2,intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_devices);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        enableBT();
        enableDiscoverable();
        //get nickname
        nickname=getIntent().getStringExtra("nickname");
        //connect views
        recyclerView=(RecyclerView)findViewById(R.id.rv_devices);
        tvNoDevices=(TextView)findViewById(R.id.tv_nodevices);
        btnScan=(Button)findViewById(R.id.bt_scan);
        btnContinue=(Button)findViewById(R.id.btn_continue);
        //onclick button scan event
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Looking for unpaired devices");
                if(bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();

                    bluetoothAdapter.startDiscovery();
                    IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mbroadcastReceiver3,intentFilter);
                    isReceiver3Registered = true;
                }
                if(!bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.startDiscovery();
                    IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mbroadcastReceiver3,intentFilter);
                    isReceiver3Registered = true;
                }
            }
        });

        //continue on click
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ListDevicesActivity.this,StartActivity.class);
                intent.putExtra(NICKNAME,nickname);
                startActivity(intent);
            }
        });

        //action bond change
        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mbroadcastReceiver4,intentFilter);
        fillPairedDevices();
        //Layout manager
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //initialize adapter
        adapter=new DeviceListAdapter(mBtDevices);
        //set adapter
        recyclerView.setAdapter(adapter);
    }



    private void fillPairedDevices() {
        Set<BluetoothDevice> btDevices=bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bd:btDevices){
            mBtDevices.add(bd);
        }
        if(!(btDevices.size() !=0)){
            tvNoDevices.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mbroadcastReceiver1);
        unregisterReceiver(mbroadcastReceiver2);
        if(isReceiver3Registered) {
            unregisterReceiver(mbroadcastReceiver3);
        }
    }

    private void enableBT() {
        if (bluetoothAdapter==null){
            Log.d(TAG,"Device not support Bluetooth");
        }
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBt=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBt);

            IntentFilter intentFilter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mbroadcastReceiver1,intentFilter);
        }
        if(bluetoothAdapter.isEnabled()){
            IntentFilter intentFilter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mbroadcastReceiver1,intentFilter);

        }
    }
}
