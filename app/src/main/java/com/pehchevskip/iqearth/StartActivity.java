package com.pehchevskip.iqearth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    //Debugging
    private static String TAG="StartActvity";
    private static boolean D=true;

    private static int REQUEST_ENABLE_BT = 2;

    //Views
    TextView mTextViewNickname;
    Button mButtonCreateGame;
    Button mButtonJoinGame;

    //Bluetooth Adapter
    //BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mButtonCreateGame = (Button) findViewById(R.id.create_game);
        mButtonJoinGame = (Button) findViewById(R.id.join_game);
        mTextViewNickname = (TextView) findViewById(R.id.nicname);
        mButtonCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        Intent createGame = new Intent(StartActivity.this, CreateGame.class);
        startActivity(createGame);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}



