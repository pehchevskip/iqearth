package com.pehchevskip.iqearth;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.Game;

public class ServerOrClientActivity extends AppCompatActivity {

    private static final String NICKNAME = "nickname";

    TextView welcomeTv;
    Button createBtn;
    Button joinBtn;
    String nickname;

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
    }

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
