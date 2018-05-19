package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.bluetooth.GameServerService;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;
import com.pehchevskip.iqearth.retrofit.AnimalsRetrofitTask;
import com.pehchevskip.iqearth.retrofit.CountryRetrofitTask;
import com.pehchevskip.iqearth.retrofit.MountainsRetrofitTask;

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

    // Database
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnected=(TextView)findViewById(R.id.connected);
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){

        }

        // Database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "iqearth-db").build();
        checkForExistingDataInDatabase();

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

    private void checkForExistingDataInDatabase() {
        checkForExistingCountries();
        checkForExistingAnimals();
        checkForExistingMountains();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingCountries() {
        new AsyncTask<Void, Void, List<EntityCountry>>() {
            @Override
            protected List<EntityCountry> doInBackground(Void... voids) {
                return db.daoCountry().getCountries();
            }
            @Override
            protected void onPostExecute(List<EntityCountry> entityCountries) {
                super.onPostExecute(entityCountries);
                if(entityCountries.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No countries in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertCountriesInDb();
                } else {
                    Toast.makeText(MainActivity.this, entityCountries.size() + " countries", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingAnimals() {
        new AsyncTask<Void, Void, List<EntityAnimal>>() {
            @Override
            protected List<EntityAnimal> doInBackground(Void... voids) {
                return db.daoAnimals().getAnimals();
            }
            @Override
            protected void onPostExecute(List<EntityAnimal> entityAnimals) {
                super.onPostExecute(entityAnimals);
                if(entityAnimals.isEmpty()){
                    Toast.makeText(MainActivity.this, "No animals in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertAnimalsInDb();
                } else {
                    Toast.makeText(MainActivity.this, entityAnimals.size() + " animals", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingMountains() {
        new AsyncTask<Void, Void, List<EntityMountain>>() {
            @Override
            protected List<EntityMountain> doInBackground(Void... voids) {
                return db.daoMountain().getMountains();
            }
            @Override
            protected void onPostExecute(List<EntityMountain> entityMountains) {
                super.onPostExecute(entityMountains);
                if(entityMountains.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No mountains in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertMountainsInDb();
                } else {
                    Toast.makeText(MainActivity.this, entityMountains.size() + " mountains", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getAndInsertCountriesInDb() {
        CountryRetrofitTask task = new CountryRetrofitTask(db);
        task.execute();
    }

    private void getAndInsertAnimalsInDb() {
        AnimalsRetrofitTask task = new AnimalsRetrofitTask(db);
        task.execute();
    }

    private void getAndInsertMountainsInDb() {
        MountainsRetrofitTask task = new MountainsRetrofitTask(db);
        task.execute();
    }


}

