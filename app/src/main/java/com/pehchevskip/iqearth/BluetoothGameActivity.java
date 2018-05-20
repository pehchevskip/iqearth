package com.pehchevskip.iqearth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothGameActivity extends AppCompatActivity {
    //role
    private static final String ROLE_TAG="role";
    private static final String CLIENT="client";
    private static final String SERVER="server";
    private static final String TAG ="BLUETOTOOFAME" ;
    //send receive
    InnerSendReceive sendReceive;

    //connection info
    private static final String APP_NAME="iqearth";
    private static final UUID myUuid=UUID.fromString("f9f89bf7-e40a-4d51-bc0d-f90d74919141");
    //connection state
    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    //views
    TextView status,roleTv,msg;
    EditText editText;
    Button send;
    ListView mGAmes;
    //
    public BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> mGames;
    private ArrayList<BluetoothDevice> bdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_game);
        String role=getIntent().getStringExtra(ROLE_TAG);
        //connect with views
        status=(TextView)findViewById(R.id.status);
        roleTv=(TextView)findViewById(R.id.role);
        editText=(EditText)findViewById(R.id.edit_text);
        send=(Button)findViewById(R.id.send);
        mGAmes=(ListView)findViewById(R.id.list_games);
        msg=(TextView)findViewById(R.id.msg);
        //initialing adapter
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        bdList=new ArrayList<>();
        mGames=new ArrayList<>();

        if(role.equals(CLIENT)){
            roleTv.setText(CLIENT);
            Set<BluetoothDevice> bd=mBluetoothAdapter.getBondedDevices();

            for(BluetoothDevice bdd:bd){
                bdList.add(bdd);
                mGames.add(bdd.getName());
            }

        }
        if(role.equals(SERVER))
        {
            roleTv.setText(SERVER);
            InnerServerClass serverClass=new InnerServerClass();
            serverClass.start();
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<>(BluetoothGameActivity.this,android.R.layout.simple_list_item_1,mGames);
        mGAmes.setAdapter(adapter);
        mGAmes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InnerClientClass clientClass=new InnerClientClass(bdList.get(i));
                clientClass.start();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reply=String.valueOf(editText.getText());
                sendReceive.write(reply.getBytes());
            }
        });

    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff=(byte[])message.obj;
                    String tempMsg=new String(readBuff,0,message.arg1);
                    msg.setText(tempMsg);

                    break;
            }
            return true;


        }
    });
    private class InnerServerClass extends Thread{
        private BluetoothServerSocket mServerSocket;
        public InnerServerClass(){
            try{
                mServerSocket=mBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,myUuid);
            }catch (IOException e)
            {
                Log.d(TAG,"Failer server socket",e);
            }


        }

        @Override
        public void run() {

            BluetoothSocket socket=null;
            while(socket==null) {
                {
                    try{
                        Message message=Message.obtain();
                        message.what=STATE_CONNECTING;
                        handler.sendMessage(message);
                        socket=mServerSocket.accept();
                    }
                    catch (IOException e)
                    {
                        Log.d(TAG,"server connection faled",e);
                        Message message=Message.obtain();
                        message.what=STATE_CONNECTION_FAILED;
                        handler.sendMessage(message);
                        break;
                    }
                    if(socket!=null){
                        Message message=Message.obtain();
                        message.what=STATE_CONNECTED;
                        handler.sendMessage(message);
                        //send receive
                        sendReceive=new InnerSendReceive(socket);
                        sendReceive.start();
                        break;
                    }
                }
            }
        }
    }
    private class InnerClientClass extends Thread{
        private BluetoothSocket socket;
        private BluetoothDevice device;

        public InnerClientClass(BluetoothDevice device1){
            this.device=device1;

            try{
                socket=device.createRfcommSocketToServiceRecord(myUuid);
            }catch (IOException e){
                Log.d(TAG,"Failed in client class",e);
            }

        }

        @Override
        public void run() {

            mBluetoothAdapter.cancelDiscovery();
            try{
                socket.connect();
                Message msg=Message.obtain();
                msg.what=STATE_CONNECTED;
                handler.sendMessage(msg);
                sendReceive=new InnerSendReceive(socket);
                sendReceive.start();
            }catch (IOException e){
                Log.d(TAG,"Coudnt connect() in client thread",e);
                Message msg=Message.obtain();
                msg.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(msg);
            }
        }
    }
    private class InnerSendReceive extends Thread{
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;


        public InnerSendReceive(BluetoothSocket socket){
            this.socket=socket;

            InputStream tempIn=null;
            OutputStream tempOut=null;
            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            }catch (IOException e)
            {

            }
            inputStream=tempIn;
            outputStream=tempOut;

        }

        @Override
        public void run() {

            byte[] buffer=new byte[1024];
            int bytes;
            while(true){
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
