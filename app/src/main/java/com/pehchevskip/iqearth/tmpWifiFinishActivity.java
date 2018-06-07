package com.pehchevskip.iqearth;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class tmpWifiFinishActivity extends AppCompatActivity {

    private static final int SocketServerPORT = 8080;
    private static final String IPADDR = "ipaddr";
    private static final String ROLE_TAG = "role";
    private static final String ISSTARTED = "isStarted?";
    private static final String NOTSTARTED = "notStarted";
    private static final String CLIENT = "client";
    private static final String SERVER = "server";
    private static final String SCORE = "SCORE";

    private String role;
    private String ipAddress;
    private ServerSocket serverSocket;

    private GameControler gameControler;
    private Player me;

    private TextView resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_wifi_finish);

        resultTv = findViewById(R.id.wifi_resultTv);
        role = getIntent().getStringExtra(ROLE_TAG);
        gameControler = GameControler.getInstance();
        me = gameControler.getCurrentPlayer();
        resultTv.setText("My score: " + me.getScore() + "\n\n");

        if(role.equals(SERVER)) {
            listOtherPlayers();
            Thread serverThread = new Thread(new SocketServerThread());
            serverThread.start();
        } else if(role.equals(CLIENT)) {
            ipAddress = getIntent().getStringExtra(IPADDR);
            new MyClientTask().execute();
        }
    }

    private class SocketServerThread extends Thread {
        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                while(true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    final String messageFromClient = dataInputStream.readUTF();
                    // GAME STATES HERE ....
                    String replyMsg = "";
                    if(messageFromClient.equals("giveMeResults")) {
                        replyMsg = gameControler.giveResults(socket.getInetAddress().toString());
                    } else if(messageFromClient.contains(SCORE)) {
                        String score = messageFromClient.replace(SCORE, "");
                        gameControler.increaseScore(socket.getInetAddress().toString(), Integer.parseInt(score));
                        updateScores();
                        replyMsg = "added score";
                    } else if(messageFromClient.equals(ISSTARTED)) {
                        replyMsg = NOTSTARTED;
                    }
                    dataOutputStream.writeUTF(replyMsg);
                }
            } catch (IOException e) { e.printStackTrace(); }
            finally {
                // Closing the things ...
                if(socket != null)
                    try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
                if(dataInputStream != null)
                    try { dataInputStream.close(); } catch (IOException e) { e.printStackTrace(); }
                if(dataOutputStream != null)
                    try { dataOutputStream.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    private class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress = ipAddress;
        int dstPort = SocketServerPORT;
        String response;
        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF("giveMeResults");
                response = dataInputStream.readUTF();
            } catch (UnknownHostException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
            finally {
                // Closing the things ...
                if(socket != null)
                    try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
                if(dataInputStream != null)
                    try { dataInputStream.close(); } catch (IOException e) { e.printStackTrace(); }
                if(dataOutputStream != null)
                    try { dataOutputStream.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            resultTv.setText(resultTv.getText() + response);
        }
    }

    private void listOtherPlayers() {
        List<Player> opponents = gameControler.getOpponents();
        final StringBuilder sb = new StringBuilder();
        sb.append(resultTv.getText());
        for(Player player : opponents)
            sb.append(player.getNickname() + ": " + player.getScore() + '\n');
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTv.setText(sb.toString());
            }
        });
    }

    private void updateScores() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTv.setText("My score: " + me.getScore() + "\n\n");
            }
        });
        listOtherPlayers();
    }
}
