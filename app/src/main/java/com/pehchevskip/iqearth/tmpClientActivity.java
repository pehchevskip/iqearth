package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.ipAdressToHash.ipAdressHashCode;
import com.pehchevskip.iqearth.model.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class tmpClientActivity extends AppCompatActivity {

    private static final String NICKNAME = "nickname";
    private static final String ISSTARTED = "isStarted?";
    private static final String STARTED = "nowStarted";
    private static final String NOTSTARTED = "notStarted";
    private static final String ROLE_TAG = "role";
    private static final String CLIENT = "client";
    private static final String IPADDR = "ipaddr";
    private static final int SocketServerPORT = 8080;

    TextView textResponse;
    EditText editTextAddress;
    Button buttonConnect, buttonClear, buttonStart;

    String nickname;
    ipAdressHashCode coder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_client);

        editTextAddress = findViewById(R.id.addressEt);
        buttonConnect = findViewById(R.id.connectBt);
        buttonClear = findViewById(R.id.clearBt);
        buttonStart = findViewById(R.id.startBt);
        textResponse = findViewById(R.id.responseTv);

        nickname = getIntent().getStringExtra(NICKNAME);
        coder=new ipAdressHashCode();
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textResponse.setText("");
            }
        });
        buttonStart.setOnClickListener(buttonStartOnClickListener);
    }

    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String tMsg = nickname;
            String ip =coder.decode(editTextAddress.getText().toString());
            MyClientTask myClientTask = new MyClientTask(ip, SocketServerPORT, tMsg);
            myClientTask.execute();
        }
    };

    View.OnClickListener buttonStartOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ip = editTextAddress.getText().toString();
            MyClientTask myClientTask = new MyClientTask(ip, SocketServerPORT, ISSTARTED);
            myClientTask.execute();
        }
    };

    @SuppressLint("StaticFieldLeak")
    private class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String msgToServer;

        MyClientTask(String addr, int port, String msgToServer) {
            dstAddress = addr;
            dstPort = port;
            this.msgToServer = msgToServer;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                if(msgToServer != null) {
                    dataOutputStream.writeUTF(msgToServer);
                }
                response = dataInputStream.readUTF();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException" + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException" + e.toString();
            } finally {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!msgToServer.equals(ISSTARTED)){
                textResponse.setText(response);
                buttonStart.setEnabled(true);
                super.onPostExecute(aVoid);
            } else {
                if(response.equals(STARTED)) {
                    Toast.makeText(tmpClientActivity.this, "Server started!", Toast.LENGTH_SHORT).show();
                    Intent startGame = new Intent(tmpClientActivity.this, WifiGameActivity.class);
                    startGame.putExtra(NICKNAME, nickname);
                    startGame.putExtra(ROLE_TAG, CLIENT);
                    String ip = editTextAddress.getText().toString();
                    startGame.putExtra(IPADDR, ip);
                    startActivity(startGame);
                } else if(response.equals(NOTSTARTED)) {
                    Toast.makeText(tmpClientActivity.this, "Server haven't started the game yet!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
