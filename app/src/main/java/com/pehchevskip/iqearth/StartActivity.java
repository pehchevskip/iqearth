package com.pehchevskip.iqearth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    private static final String ROLE_TAG="role";
    private static final String CLIENT="client";
    private static final String SERVER="server";
    //Debugging
    private static String TAG="StartActvity";
    private final static String NICKNAME="nickname";
    private static boolean D=true;

    private static int REQUEST_ENABLE_BT = 2;

    //Views
    TextView mTextViewNickname;
    Button mButtonCreateGame;
    Button mButtonJoinGame;

    //Bluetooth Adapter
    BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mButtonCreateGame = (Button) findViewById(R.id.create_game);
        mButtonJoinGame = (Button) findViewById(R.id.join_game);
        mTextViewNickname = (TextView) findViewById(R.id.nicname);
        String nickname=getIntent().getStringExtra(NICKNAME);
        mTextViewNickname.setText(nickname);
        mButtonCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        Intent createGame = new Intent(StartActivity.this, BluetoothGameActivity.class);
        createGame.putExtra(ROLE_TAG,SERVER);
        startActivity(createGame);

            }
        });
        mButtonJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent join_game=new Intent(StartActivity.this,BluetoothGameActivity.class);
                join_game.putExtra(ROLE_TAG,CLIENT);
                startActivity(join_game);
            }
        });

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(!mBluetoothAdapter.isEnabled()){
            Intent bt_reques=new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bt_reques,REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}



