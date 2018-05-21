package com.pehchevskip.iqearth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BluetoothControler {
    //connection info
    private static final String APP_NAME="iqearth";
    private static final UUID myUuid=UUID.fromString("f9f89bf7-e40a-4d51-bc0d-f90d74919141");
    //connection state
    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    static final BluetoothAdapter  mBluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
    static Handler handler;
   public static InnerSendReceive sendReceive;
    public InnerServerClass serverClass;
    public InnerClientClass clientClass;
    public static BluetoothControler controler;
    public BluetoothControler(){

    }
    public void setHandler(Handler handler1){
        handler=handler1;}
    public static BluetoothControler getInstance(){
        if(controler==null) {
            controler=new BluetoothControler();
        }
        return controler;
    }

    //classes for controler
   public static class InnerServerClass extends Thread{

        private BluetoothServerSocket mServerSocket;
        public InnerServerClass(){
            try{
                mServerSocket=mBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,myUuid);
            }catch (IOException e)
            {
                Log.d(TAG,"Failer server socket",e);
            }


        }

        @Override
        public void run() {

            BluetoothSocket socket=null;
            while(socket==null) {
                {
                    try{
                        Message message=Message.obtain();
                        message.what=STATE_CONNECTING;
                        handler.sendMessage(message);
                        socket=mServerSocket.accept();
                    }
                    catch (IOException e)
                    {
                        Log.d(TAG,"server connection faled",e);

                            try {
                                socket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        Message message=Message.obtain();
                        message.what=STATE_CONNECTION_FAILED;
                        handler.sendMessage(message);
                        break;
                    }
                    if(socket!=null){
                        Message message=Message.obtain();
                        message.what=STATE_CONNECTED;
                        handler.sendMessage(message);
                        //send receive
                        sendReceive=new InnerSendReceive(socket);
                        sendReceive.start();
                        break;
                    }
                }
            }
        }
    }
    public static class InnerClientClass extends Thread{
        private BluetoothSocket socket;
        private BluetoothDevice device;

        public InnerClientClass(BluetoothDevice device1){
            this.device=device1;

            try{
                socket=device.createRfcommSocketToServiceRecord(myUuid);
            }catch (IOException e){
                Log.d(TAG,"Failed in client class",e);
            }

        }

        @Override
        public void run() {

            mBluetoothAdapter.cancelDiscovery();
            try{
                socket.connect();
                Message msg=Message.obtain();
                msg.what=STATE_CONNECTED;
                handler.sendMessage(msg);
                sendReceive=new InnerSendReceive(socket);
                sendReceive.start();
            }catch (IOException e){
                Log.d(TAG,"Coudnt connect() in client thread",e);
                Message msg=Message.obtain();
                msg.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(msg);
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    public static class InnerSendReceive extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;


        public InnerSendReceive(BluetoothSocket socket){
            this.socket=socket;

            InputStream tempIn=null;
            OutputStream tempOut=null;
            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            }catch (IOException e)
            {

            }
            inputStream=tempIn;
            outputStream=tempOut;

        }

        @Override
        public void run() {

            byte[] buffer=new byte[1024];
            int bytes;
            while(true){
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

