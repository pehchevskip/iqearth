package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.bluetooth.GameServerService;
import com.pehchevskip.iqearth.model.Player;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Debugging
    private static final String TAG = "IQEarth";
    private static final boolean D = true;
    //Map from all possible answers<<Countries>>
    private Map<String, List<String>> possibleAnswers;
    //local bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    //instance of gameserverservice
    private GameServerService mGameServerService;
    //Message types send from the GameServerService
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    //Key names received form the GameServerService Handler
    public static final String DEVICE_NAME="Server-Rooms";
    public static final String TOAST="toast";

    //Layout Views
    private TextView mConnected;

    //Name of the connected device
    private String mConnectedDeviceName=null;

    //Array adapter for the players
    private ArrayAdapter<String> mConversationArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnected=(TextView)findViewById(R.id.connected);
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }else{
            if(mGameServerService==null) setupGame();
        }

    }

    @Override
    protected synchronized void onResume() {
        super.onResume();
        if(mGameServerService!=null){
            if(mGameServerService.getState()==GameServerService.STATE_NONE){
                mGameServerService.start();
            }
        }
    }
    //Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case GameServerService.STATE_CONNECTED:
                            mConnected.setText("Connected");
                            mConnected.append(mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;
                        case GameServerService.STATE_CONNECTING:
                            mConnected.setText("Connecting");
                            break;
                        case GameServerService.STATE_LISTEN:
                        case GameServerService.STATE_NONE:
                            mConnected.setText("Not Connected");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.length() > 0) {
                        mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    //if (!msg.getData().getString(TOAST).contains("Unable to connect device")) {
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    //}
                    break;
            }
        }
    };
    protected void setupGame(){
        mGameServerService=new GameServerService(this,mHandler);
    }


}

