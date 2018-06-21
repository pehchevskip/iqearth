package com.pehchevskip.iqearth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pehchevskip.iqearth.ipAddressToHash.ipAddressHashCode;
import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class tmpServerActivity extends AppCompatActivity {

    private static final String NICKNAME = "nickname";
    private static final String ISSTARTED = "isStarted?";
    private static final String NOTSTARTED = "notStarted";
    private static final String ROLE_TAG="role";
    private static final String SERVER="server";
    private static final int SocketServerPORT = 8080;

    TextView infoip, connectedDevicesTv;
//    TextView msg;
    Button startButton;
    public ipAddressHashCode coder;
    String message = "";
    ServerSocket serverSocket;

    Game game;
    Player player;
    String nickname;
    List<String> connectedIps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_server);

        infoip = findViewById(R.id.infoipTv);
//        msg = findViewById(R.id.msgTv);
        connectedDevicesTv = findViewById(R.id.connectedDevicesTv);
        startButton = findViewById(R.id.serverStartBt);
        startButton.setOnClickListener(startButtonOnClickListener);

        nickname = getIntent().getStringExtra(NICKNAME);
        connectedIps = new ArrayList<>();
        coder=new ipAddressHashCode();
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    View.OnClickListener startButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent startGame = new Intent(tmpServerActivity.this, WifiGameActivity.class);
            startGame.putExtra(NICKNAME, nickname);
            startGame.putExtra(ROLE_TAG, SERVER);
            if(serverSocket != null) {
                try { serverSocket.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            startActivity(startGame);
        }
    };

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String code=coder.transform(getIpAddress());
                        infoip.setText(String.format("%s", code));
                    }
                });

                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    final String messageFromClient = dataInputStream.readUTF();
                    message = messageFromClient + " connected! (" + socket.getInetAddress() + ')';
                    final Socket finalSocket = socket; // na majtiii
                    tmpServerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!messageFromClient.equals(ISSTARTED)) {
//                                msg.setText(message);
                                addConnectedDevice(messageFromClient, finalSocket.getInetAddress().toString());
                            }
                        }
                    });
                    String replyMsg;
                    if(messageFromClient.equals(ISSTARTED)) {
                        replyMsg = NOTSTARTED;
                    } else {
                        replyMsg = "hi " + messageFromClient + ", here " + nickname + '(' + coder.transform(getIpAddress()) + ')';
                    }
                    dataOutputStream.writeUTF(replyMsg);
                    startButton.setEnabled(true);
                }
            } catch (final IOException e) {
                e.printStackTrace();
                tmpServerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        msg.setText(e.toString());
                    }
                });
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
        }
    }

    private void addConnectedDevice(String nickname, String ip) {
        if(connectedIps.contains(ip)) return;
        String tmp = connectedDevicesTv.getText().toString();
        tmp += nickname + " (" + ip + ')' + '\n';
        connectedDevicesTv.setText(tmp);
        connectedIps.add(ip);
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterface = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterface.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterface.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if(inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something went wrong!" + e.toString() + "\n";
        }
        return ip;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
