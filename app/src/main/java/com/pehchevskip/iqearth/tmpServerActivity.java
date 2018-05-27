package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class tmpServerActivity extends AppCompatActivity {

    TextView infoip, msg;
    Button replyButton;
    String message = "";
    ServerSocket serverSocket;

    Game game;
    Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_server);

        infoip = findViewById(R.id.infoipTv);
        msg = findViewById(R.id.msgTv);
        replyButton = findViewById(R.id.replyBt);
//        replyButton.setOnClickListener(onClickListenerForReplyBt);

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
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

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                tmpServerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoip.setText("Waiting on: " + getIpAddress() + ':' + serverSocket.getLocalPort());
                    }
                });

                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String messageFromClient = "";
                    messageFromClient = dataInputStream.readUTF();
                    count++;
                    message += "#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n"
                            + messageFromClient + '\n';
                    tmpServerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });
                    String replyMsg = "hi from server #" + count;
                    dataOutputStream.writeUTF(replyMsg);
                }
            } catch (final IOException e) {
                e.printStackTrace();
                tmpServerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(e.toString());
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

    /*View.OnClickListener onClickListenerForReplyBt = new View.OnClickListener() {
        @SuppressLint("StaticFieldLeak") @Override
        public void onClick(View view) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    socketServerReplyThread.run();
                    return null;
                }
            }.execute();
        }
    };*/

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
                        ip += "SiteLocalAddress: " + inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something went wrong!" + e.toString() + "\n";
        }
        return ip;
    }

}
