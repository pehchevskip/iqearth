package com.pehchevskip.iqearth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class JoinGame extends BluetoothActivity  {

    //Debugging
    private static String TAG="JoinGame";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        setTimeDiscoverable(com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager.BLUETOOTH_TIME_DICOVERY_120_SEC);
        selectClientMode();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public String setUUIDappIdentifier() {
        return UUID.randomUUID().toString();
    }

    @Override
    public int myNbrClientMax() {
        return 7;
    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {
            createClient(device.getAddress());
        Log.e(TAG,"On onBluetoothDeviceFound");
    }

    @Override
    public void onClientConnectionSuccess() {
                sendMessageStringToAll("Hello");
        Log.e(TAG,"On clientConnectionSuccess()");
    }

    @Override
    public void onClientConnectionFail() {

    }

    @Override
    public void onServeurConnectionSuccess() {
            sendMessageStringToAll("Hi Server");
    }

    @Override
    public void onServeurConnectionFail() {

    }

    @Override
    public void onBluetoothStartDiscovery() {

    }

    @Override
    public void onBluetoothMsgStringReceived(String message) {

    }

    @Override
    public void onBluetoothMsgObjectReceived(Object message) {

    }

    @Override
    public void onBluetoothMsgBytesReceived(byte[] message) {

    }

    @Override
    public void onBluetoothNotAviable() {

    }
}
