package com.pehchevskip.iqearth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pehchevskip.iqearth.bluetooth.BluetoothControler;
import com.pehchevskip.iqearth.bluetooth.ServerConnectThread;
import com.pehchevskip.iqearth.bluetooth.service.BluetoothConnectionService;
import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class BluetoothGameActivity extends AppCompatActivity {
    //role
    private static final String ROLE_TAG="role";
    private static final String CLIENT="client";
    private static final String SERVER="server";
    private static final String TAG ="BLUETOTOOFAME" ;
    //Enum for Bluetooth messaging
    public enum BluetoothMessaging{
        RegisterOpponent,SettingLetter,Other
    }

    //connection info
    private static final String APP_NAME="iqearth";
    private static final UUID myUuid=UUID.fromString("f9f89bf7-e40a-4d51-bc0d-f90d74919141");
    //connection state
    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    static final int STARTED_GAME=6;
    static final int REGISTER_OPPONENT=7;
    private static final int GAME_BEGIN = 8;
    //views
    TextView status,roleTv,msg;
    EditText editText;
    Button send,start_game;
    ListView mGAmes;
    //
    public BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> mGames;
    private ArrayList<BluetoothDevice> bdList;
    //role
    public String role;
    //Bluetooth Contorler
    BluetoothControler controler;
    BluetoothConnectionService bluetoothConnectionService;
    //Game Controler
    GameControler gameControler;
    //Game
    Game game;
    Player player;
    Player opponent;
    String nickname;
    //Enum
    BluetoothMessaging btMessaging;

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_game);
        //get role from start activity
        role=getIntent().getStringExtra(ROLE_TAG);
        //connect with views
        status=(TextView)findViewById(R.id.status);
        roleTv=(TextView)findViewById(R.id.role);
        gameControler=GameControler.getInstance();
        mGAmes=(ListView)findViewById(R.id.list_games);
        msg=(TextView)findViewById(R.id.msg);
        start_game=(Button)findViewById(R.id.start_game);
        //initialing adapter
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        bdList=new ArrayList<>();
        mGames=new ArrayList<>();
        nickname=getIntent().getStringExtra("nickname");
        controler=BluetoothControler.getInstance();
        controler.setHandler(handler);
        bluetoothConnectionService=BluetoothConnectionService.getInstance(this);
        bluetoothConnectionService.setHandler(handler);
        if(role.equals(CLIENT)){
            roleTv.setText(CLIENT);
            Set<BluetoothDevice> bd=mBluetoothAdapter.getBondedDevices();
            for(BluetoothDevice bdd:bd){
                bdList.add(bdd);
                mGames.add(bdd.getName());

            }

        }
        if(role.equals(SERVER))
        {

            roleTv.setText(SERVER);

            bluetoothConnectionService.start();

        }
        //local broadcast manager for incoming message

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter("incomingMessage"));
        ArrayAdapter<String> adapter=new ArrayAdapter<>(BluetoothGameActivity.this,android.R.layout.simple_list_item_1,mGames);
        mGAmes.setAdapter(adapter);
        mGAmes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothConnectionService.startClient(bdList.get(i),myUuid);
                controler.setBluetoothDevice(bdList.get(i));


            }
        });


        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent startGame=new Intent(BluetoothGameActivity.this,tmpActivity.class);
                startGame.putExtra(ROLE_TAG,role);
                startGame.putExtra("nickname",nickname);
                startActivity(startGame);
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       

    }

    public BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text=intent.getStringExtra("theMessage");
            switch (btMessaging){
                case RegisterOpponent:
                    opponent=new Player(text);
                    gameControler.addPlayer(opponent);
                    start_game.setVisibility(View.VISIBLE);
                    break;

            }

        }
    };
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    //register oppponent
                    Message msg=Message.obtain();
                    msg.what=REGISTER_OPPONENT;
                    handler.sendMessage(msg);
                    btMessaging=BluetoothMessaging.RegisterOpponent;

                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:



                    break;
                case STARTED_GAME:



                   break;
                case REGISTER_OPPONENT:

                   bluetoothConnectionService.write(nickname.getBytes());

                    break;


            }
            return true;


        }
    });

}
