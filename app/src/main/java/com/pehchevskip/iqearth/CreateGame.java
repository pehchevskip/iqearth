package com.pehchevskip.iqearth;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager;

import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CreateGame extends BluetoothActivity implements DiscoveredDialogFragment.DiscoveredDialogListener {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private String mServerAdress;


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                // TODO stuff if you need
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        //***** IMPORTANT FOR ANDROID SDK >= 6.0 *****//
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
                // TODO stuff if u need
            }
        } else {
            // TODO stuff if u need
        }

        mBluetoothManager = new BluetoothManager(this);
        setMessageMode(BluetoothManager.MessageMode.String);
        showDiscoveredDevicesDialog();
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    public void serverCreated(String adress) {
        setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_3600_SEC);
        selectServerMode();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_3600_SEC);
        selectServerMode();
    }

    private void showDiscoveredDevicesDialog() {
        String tag = DiscoveredDialogFragment.class.getSimpleName();
        DiscoveredDialogFragment fragment = DiscoveredDialogFragment.newInstance();
        fragment.setListener(this);
        showDialogFragment(fragment, tag);
    }

    private void showDialogFragment(DialogFragment dialogFragment, String tag) {
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(dialogFragment, tag);
        ft.commitAllowingStateLoss();
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

    }

    @Override
    public void onClientConnectionSuccess() {

    }

    @Override
    public void onClientConnectionFail() {

    }

    @Override
    public void onServeurConnectionSuccess() {

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

    @Override
    public void onDeviceSelectedForConnection(String addressMac) {

    }

    @Override
    public void onScanClicked() {
        scanAllBluetoothDevice();
    }

}
