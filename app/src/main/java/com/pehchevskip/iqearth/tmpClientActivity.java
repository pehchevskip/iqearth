package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.model.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class tmpClientActivity extends AppCompatActivity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    EditText message;

    Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_client);

        editTextAddress = findViewById(R.id.addressEt);
        editTextPort = findViewById(R.id.portEt);
        buttonConnect = findViewById(R.id.connectBt);
        buttonClear = findViewById(R.id.clearBt);
        textResponse = findViewById(R.id.responseTv);
        message = findViewById(R.id.msgForServerEt);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textResponse.setText("");
            }
        });
    }

    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            MyClientTask myClientTask = new MyClientTask(editTextAddress.getText().toString(), Integer.parseInt(editTextPort.getText().toString()));
//            myClientTask.execute();
            String tMsg = message.getText().toString();
            if(tMsg.equals("")) {
                tMsg = null;
                Toast.makeText(tmpClientActivity.this, "No message for server sent!", Toast.LENGTH_SHORT);
            }
            String ip = editTextAddress.getText().toString();
            int port = Integer.parseInt(editTextPort.getText().toString());
            MyClientTask myClientTask = new MyClientTask(ip, port, tMsg);
            myClientTask.execute();
        }
    };

    @SuppressLint("StaticFieldLeak")
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

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
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
//                byte[] buffer = new byte[1024];
//
//                int bytesRead;
//                InputStream inputStream = socket.getInputStream();
//                /*
//                 * notice:
//                 * inputStream.read() will block if no data return
//                 */
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    byteArrayOutputStream.write(buffer, 0, bytesRead);
//                    response += byteArrayOutputStream.toString("UTF-8");
//                }
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
            textResponse.setText(response);
            super.onPostExecute(aVoid);
        }
    }
}
