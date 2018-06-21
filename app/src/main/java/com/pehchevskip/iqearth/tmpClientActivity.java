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

import com.pehchevskip.iqearth.ipAddressToHash.ipAddressHashCode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

//    TextView textResponse;
    EditText editTextAddress;
    Button buttonConnect, buttonStart;

    String nickname;
    ipAddressHashCode coder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_client);

        editTextAddress = findViewById(R.id.addressEt);
        buttonConnect = findViewById(R.id.connectBt);
        buttonStart = findViewById(R.id.startBt);
//        textResponse = findViewById(R.id.responseTv);

        nickname = getIntent().getStringExtra(NICKNAME);
        coder=new ipAddressHashCode();
        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonStart.setOnClickListener(buttonStartOnClickListener);
    }

    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!editTextAddress.getText().toString().trim().equals("")){
                String tMsg = nickname;
                String ip = coder.decode(editTextAddress.getText().toString());
                MyClientTask myClientTask = new MyClientTask(ip, SocketServerPORT, tMsg);
                myClientTask.execute();
            }
        }
    };

    View.OnClickListener buttonStartOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ip = coder.decode(editTextAddress.getText().toString());
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
                runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(tmpClientActivity.this, "Error with connecting to the host!", Toast.LENGTH_SHORT).show(); } });
                response = "error";
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException" + e.toString();
            } finally {
                if(socket != null) { try { socket.close(); } catch (IOException e) { e.printStackTrace(); } }
                if(dataInputStream != null) { try { dataInputStream.close(); } catch (IOException e) { e.printStackTrace(); } }
                if(dataOutputStream != null) { try { dataOutputStream.close(); } catch (IOException e) { e.printStackTrace(); } }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!msgToServer.equals(ISSTARTED) && !response.equals("error")){
                buttonStart.setEnabled(true);
                Toast.makeText(tmpClientActivity.this, response, Toast.LENGTH_SHORT).show();
            } else {
                if(response.equals(STARTED)) {
                    Toast.makeText(tmpClientActivity.this, "Server started!", Toast.LENGTH_SHORT).show();
                    Intent startGame = new Intent(tmpClientActivity.this, WifiGameActivity.class);
                    startGame.putExtra(NICKNAME, nickname);
                    startGame.putExtra(ROLE_TAG, CLIENT);
                    String ip = dstAddress;
                    startGame.putExtra(IPADDR, ip);
                    startActivity(startGame);
                } else if(response.equals(NOTSTARTED)) {
                    Toast.makeText(tmpClientActivity.this, "Server haven't started the game yet!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
