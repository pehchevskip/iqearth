package com.pehchevskip.iqearth.bluetooth.service;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG="BluetoothConnectionService";
    private static final String appName="IQearth";
    private static final UUID myUuid=UUID.fromString("f9f89bf7-e40a-4d51-bc0d-f90d74919141");

    //connection state
    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    private final BluetoothAdapter bluetoothAdapter;
    static Context mContext;
    Handler handler;
    private AcceptThread InsecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice mDevice;
    private static BluetoothConnectionService service;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    public BluetoothConnectionService(Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext=context;
    }
    public void setHandler(Handler handler){
        this.handler=handler;
    }
    public static BluetoothConnectionService getInstance(Context mContext){
        if(service==null){
            service=new BluetoothConnectionService(mContext);
        }
        return service;
    }

    public void cancel() {
        InsecureAcceptThread.cancel();
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket serverSocket;

        @SuppressLint("LongLogTag")
        public AcceptThread(){
            BluetoothServerSocket tmp=null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, myUuid);
                Log.d(TAG,"AcceptThread setting up server using");
            }catch (IOException io){

            }
            serverSocket=tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket=null;

            try {
                Message message=Message.obtain();
                message.what=STATE_CONNECTING;
                handler.sendMessage(message);
                socket=serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
            if(socket!=null){
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                connected(socket,mDevice);

            }
        }

        public void cancel(){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ConnectThread extends Thread{
        private BluetoothSocket bluetoothSocket;
        public ConnectThread(BluetoothDevice device,UUID uuid){
            mDevice=device;
            deviceUUID=uuid;
        }

        @Override
        public void run() {
            BluetoothSocket tmp=null;

            try {
                tmp=mDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothSocket=tmp;
            //cancel discovery
            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
                Message msg=Message.obtain();
                msg.what=STATE_CONNECTED;
                handler.sendMessage(msg);
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }

            connected(bluetoothSocket,mDevice);
        }
        public void cancel(){
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connected(BluetoothSocket bluetoothSocket, BluetoothDevice mDevice) {
        connectedThread=new ConnectedThread(bluetoothSocket);
        connectedThread.start();
    }
    public void write(byte[] out){
        connectedThread.write(out);

    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        public ConnectedThread(BluetoothSocket socket){
            this.socket=socket;
            InputStream itmp=null;
            OutputStream otmp=null;
            try {
                mProgressDialog.dismiss();
            }catch (NullPointerException n){

            }

            try {
                itmp=socket.getInputStream();
                otmp=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=itmp;
            outputStream=otmp;
        }

        @Override
        public void run() {
            byte [] buffer=new byte[1024];
            int bytes;

            while(true){
                try {
                    bytes = inputStream.read(buffer);
                    String incomingMessage=new String(buffer,0,bytes);
                    Intent intentIncomingMessage=new Intent("incomingMessage");
                    intentIncomingMessage.putExtra("theMessage",incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intentIncomingMessage);

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }
        }
        public void write(byte[] bytes){
            String text=new String(bytes, Charset.defaultCharset());

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void cancel(){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start(){
        if(connectThread!=null){
            connectThread.cancel();
            connectThread=null;
        }
        if(InsecureAcceptThread==null){
            InsecureAcceptThread=new AcceptThread();
            InsecureAcceptThread.start();
        }
    }
    public void startClient(BluetoothDevice device,UUID uuid){
        mProgressDialog=ProgressDialog.show(mContext,"Connecting Bluetooth","Please Wait...",true);
        connectThread=new ConnectThread(device,uuid);
        connectThread.start();
    }
}
