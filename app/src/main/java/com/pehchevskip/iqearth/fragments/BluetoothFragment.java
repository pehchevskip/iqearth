package com.pehchevskip.iqearth.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;

import com.pehchevskip.iqearth.bluetooth.bus.BluetoothCommunicatorBytes;
import com.pehchevskip.iqearth.bluetooth.bus.BluetoothCommunicatorObject;
import com.pehchevskip.iqearth.bluetooth.bus.BluetoothCommunicatorString;
import com.pehchevskip.iqearth.bluetooth.bus.BoundedDevice;
import com.pehchevskip.iqearth.bluetooth.bus.ClientConnectionFail;
import com.pehchevskip.iqearth.bluetooth.bus.ClientConnectionSuccess;
import com.pehchevskip.iqearth.bluetooth.bus.ServerConnectionFail;
import com.pehchevskip.iqearth.bluetooth.bus.ServerConnectionSuccess;

import de.greenrobot.event.EventBus;

public abstract class BluetoothFragment extends Fragment {

    protected com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager mBluetoothManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBluetoothManager = new com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager(getActivity());
        checkBluetoothAviability();
        mBluetoothManager.setUUIDappIdentifier(setUUIDappIdentifier());
        mBluetoothManager.setNbrClientMax(myNbrClientMax());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        closeAllConnexion();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager.REQUEST_DISCOVERABLE_CODE) {
            if (resultCode == com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager.BLUETOOTH_REQUEST_REFUSED) {
                getActivity().finish();
            } else if (resultCode == com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager.BLUETOOTH_REQUEST_ACCEPTED) {
                onBluetoothStartDiscovery();
            } else {
                getActivity().finish();
            }
        }
    }

    public void closeAllConnexion(){
        mBluetoothManager.closeAllConnexion();
    }

    public void checkBluetoothAviability(){
        if(!mBluetoothManager.checkBluetoothAviability()){
            onBluetoothNotAviable();
        }
    }

    public void setTimeDiscoverable(int timeInSec){
        mBluetoothManager.setTimeDiscoverable(timeInSec);
    }

    public void startDiscovery(){
        mBluetoothManager.startDiscovery();
    }

    public boolean isConnected(){
        return mBluetoothManager.isConnected;
    }

    public void scanAllBluetoothDevice(){
        mBluetoothManager.scanAllBluetoothDevice();
    }

    public void disconnectClient(){
        mBluetoothManager.disconnectClient(true);
    }

    public void disconnectServer(){
        mBluetoothManager.disconnectServer(true);
    }

    public void createServeur(String address){
        mBluetoothManager.createServeur(address);
    }

    public void selectServerMode(){
        mBluetoothManager.selectServerMode();
    }
    public void selectClientMode(){
        mBluetoothManager.selectClientMode();
    }

    public com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager.TypeBluetooth getTypeBluetooth(){
        return mBluetoothManager.mType;
    }

    public com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager.TypeBluetooth getBluetoothMode(){
        return mBluetoothManager.mType;
    }

    public void createClient(String addressMac){
        mBluetoothManager.createClient(addressMac);
    }

    public void setMessageMode(com.pehchevskip.iqearth.bluetooth.manager.BluetoothManager.MessageMode messageMode){
        mBluetoothManager.setMessageMode(messageMode);
    }

    public void sendMessageStringToAll(String message){
        mBluetoothManager.sendStringMessageForAll(message);
    }
    public void sendMessageString(String adressMacTarget, String message){
        mBluetoothManager.sendStringMessage(adressMacTarget, message);
    }
    public void sendMessageObjectToAll(Object message){
        mBluetoothManager.sendObjectForAll(message);
    }
    public void sendMessageObject(String adressMacTarget, Object message){
        mBluetoothManager.sendObject(adressMacTarget, message);
    }
    public void sendMessageBytesForAll(byte[] message){
        mBluetoothManager.sendBytesForAll(message);
    }
    public void sendMessageBytes(String adressMacTarget, byte[] message){
        mBluetoothManager.sendBytes(adressMacTarget, message);
    }

    public abstract String setUUIDappIdentifier();
    public abstract int myNbrClientMax();
    public abstract void onBluetoothDeviceFound(BluetoothDevice device);
    public abstract void onClientConnectionSuccess();
    public abstract void onClientConnectionFail();
    public abstract void onServeurConnectionSuccess();
    public abstract void onServeurConnectionFail();
    public abstract void onBluetoothStartDiscovery();
    public abstract void onBluetoothMsgStringReceived(String message);
    public abstract void onBluetoothMsgObjectReceived(Object message);
    public abstract void onBluetoothMsgBytesReceived(byte[] message);
    public abstract void onBluetoothNotAviable();

    public void onEventMainThread(BluetoothDevice device){
        if(!mBluetoothManager.isNbrMaxReached()){
            onBluetoothDeviceFound(device);
            createServeur(device.getAddress());
        }
    }

    public void onEventMainThread(ClientConnectionSuccess event){
        mBluetoothManager.isConnected = true;
        onClientConnectionSuccess();
    }

    public void onEventMainThread(ClientConnectionFail event){
        mBluetoothManager.isConnected = false;
        onClientConnectionFail();
    }

    public void onEventMainThread(ServerConnectionSuccess event){
        mBluetoothManager.isConnected = true;
        mBluetoothManager.onServerConnectionSuccess(event.mClientAdressConnected);
        onServeurConnectionSuccess();
    }

    public void onEventMainThread(ServerConnectionFail event){
        mBluetoothManager.onServerConnectionFailed(event.mClientAdressConnectionFail);
        onServeurConnectionFail();
    }

    public void onEventMainThread(BluetoothCommunicatorString event){
        onBluetoothMsgStringReceived(event.mMessageReceive);
    }

    public void onEventMainThread(BluetoothCommunicatorObject event){
        onBluetoothMsgObjectReceived(event.mObject);
    }

    public void onEventMainThread(BluetoothCommunicatorBytes event){
        onBluetoothMsgBytesReceived(event.mBytesReceive);
    }

    public void onEventMainThread(BoundedDevice event){
        //mBluetoothManager.sendMessage("BondedDevice");
    }
}