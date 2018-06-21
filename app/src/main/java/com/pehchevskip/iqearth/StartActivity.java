package com.pehchevskip.iqearth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    private static final String ROLE_TAG="role";
    private static final String CLIENT="client";
    private static final String SERVER="server";
    //Debugging
    private static String TAG="StartActvity";
    private final static String NICKNAME="nickname";
    private static boolean D=true;



    //Views
    TextView mTextViewNickname;
    Button mButtonCreateGame;
    Button mButtonJoinGame;

    //Bluetooth Adapter
    BluetoothAdapter mBluetoothAdapter;

    //Game

    public GameControler gameControler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mButtonCreateGame = (Button) findViewById(R.id.create_game);
        mButtonJoinGame = (Button) findViewById(R.id.join_game);
        mTextViewNickname = (TextView) findViewById(R.id.nicname);
        final String nickname=getIntent().getStringExtra(NICKNAME);
        mTextViewNickname.setText(nickname);
        //inicialiazing game controler
        gameControler=GameControler.getInstance();

        //
        mButtonCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        Intent createGame = new Intent(StartActivity.this, BluetoothGameActivity.class);
        createGame.putExtra(ROLE_TAG,SERVER);
        createGame.putExtra("nickname",nickname);

        startActivity(createGame);

            }
        });
        mButtonJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent join_game=new Intent(StartActivity.this,BluetoothGameActivity.class);
                join_game.putExtra(ROLE_TAG,CLIENT);
                join_game.putExtra("nickname",nickname);
                startActivity(join_game);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}



