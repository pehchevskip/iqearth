package com.pehchevskip.iqearth.bluetooth.server;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.util.Log;

import com.pehchevskip.iqearth.bluetooth.BluetoothRunnable;
import com.pehchevskip.iqearth.bluetooth.bus.ServerConnectionFail;
import com.pehchevskip.iqearth.bluetooth.bus.ServerConnectionSuccess;
import com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager;

import java.io.IOException;
import java.util.UUID;

import de.greenrobot.event.EventBus;

public class BluetoothServer extends BluetoothRunnable {

    private static final String TAG = BluetoothServer.class.getSimpleName();

    private UUID mUUID;
    private BluetoothServerSocket mServerSocket;

    public BluetoothServer(BluetoothAdapter bluetoothAdapter, String uuiDappIdentifier, String adressMacClient, Activity activity, BluetoothManager.MessageMode messageMode) {
        super(bluetoothAdapter, uuiDappIdentifier, activity, messageMode);
        mClientAddress = adressMacClient;
        mUUID = UUID.fromString(uuiDappIdentifier + "-" + mClientAddress.replace(":", ""));
    }

    @Override
    public void waitForConnection() {
        // NOTHING TO DO IN THE SERVER
    }

    @Override
    public void intiObjReader() throws IOException {
        mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BLTServer", mUUID);
        mSocket = mServerSocket.accept();
        mInputStream = mSocket.getInputStream();
    }

    @Override
    public void onConnectionSucess() {
        EventBus.getDefault().post(new ServerConnectionSuccess(mClientAddress));
    }

    @Override
    public void onConnectionFail() {
        EventBus.getDefault().post(new ServerConnectionFail(mClientAddress));
    }

    @Override
    public void closeConnection() {
        super.closeConnection();
        try {
            mServerSocket.close();
            mServerSocket = null;
        } catch (Exception e) {
            Log.e("", "===+++> closeConnection Exception e : "+e.getMessage());
        }
    }
}