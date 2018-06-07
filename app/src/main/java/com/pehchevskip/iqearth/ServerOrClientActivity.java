package com.pehchevskip.iqearth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ServerOrClientActivity extends AppCompatActivity {

    private static final String NICKNAME = "nickname";

    private TextView welcomeTv;
    private Button createBtn;
    private Button joinBtn;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_or_client);

        welcomeTv = findViewById(R.id.wifiWelcomeTv);
        createBtn = findViewById(R.id.wifiCreateBtn);
        createBtn.setOnClickListener(createOnClickListener);
        joinBtn = findViewById(R.id.wifiJoinBtn);
        joinBtn.setOnClickListener(joinOnClickListener);

        nickname = getIntent().getStringExtra(NICKNAME);
        welcomeTv.setText(String.format("Welcome %s!", nickname));

        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if(networkInfo.isConnected()) {
                    createBtn.setEnabled(true);
                    joinBtn.setEnabled(true);
                } else {
                    createBtn.setEnabled(false);
                    joinBtn.setEnabled(false);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(!mWifi.isConnected()) {
            Toast.makeText(this, "You are not connected to wifi!", Toast.LENGTH_SHORT).show();
            createBtn.setEnabled(false);
            joinBtn.setEnabled(false);
        } else {
            Toast.makeText(this, "Connected to wifi!", Toast.LENGTH_SHORT).show();
            createBtn.setEnabled(true);
            joinBtn.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    View.OnClickListener createOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent createGame = new Intent(ServerOrClientActivity.this, tmpServerActivity.class);
            createGame.putExtra(NICKNAME, nickname);
            startActivity(createGame);
        }
    };

    View.OnClickListener joinOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent joinGame = new Intent(ServerOrClientActivity.this, tmpClientActivity.class);
            joinGame.putExtra(NICKNAME, nickname);
            startActivity(joinGame);
        }
    };

}
