package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class WifiGameActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static final int SocketServerPORT = 8080;
    private static final String NICKNAME = "nickname";
    private static final String ISSTARTED = "isStarted?";
    private static final String STARTED = "nowStarted";
    private static final String CORRECTANSWER = "correctAnswerTag";
    private static final String HEREISMYNICK = "hereIsMyNick";
    private static final String ROLE_TAG="role";
    private static final String CLIENT="client";
    private static final String SERVER="server";
    private static final String IPADDR = "ipaddr";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private AppDatabase db;
    private static List<String> possibleCountries = new ArrayList<>();
    private static List<String> possibleAnimals = new ArrayList<>();
    private static List<String> possibleMountains = new ArrayList<>();

    private static String role;
    private static Player player;
    private static Game game;
    private static GameControler gameControler;
    private static String nickname;

    private Button enterButton;
    private EditText answerEditText;

    private CountDownTimer timer;

    private ServerSocket serverSocket;
    private static String ipAddress;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        role = getIntent().getStringExtra(ROLE_TAG);
        gameControler = GameControler.getInstance();
        game = new Game(60000);
        gameControler.setGame(game);
        nickname = getIntent().getStringExtra(NICKNAME);
        player = gameControler.getPlayers().get(0);
        player.setIpAddress(getIpAddress());

        // Database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "iqearth-db").build();
        getCountriesFromDb();
        getAnimalsFromDb();
        getMountainsFromDb();

        enterButton = findViewById(R.id.wifi_enterBt);
        answerEditText = findViewById(R.id.wifi_answerEt);

        if(role.equals(SERVER)) {
            Thread socketServerThread = new Thread(new SocketServerThread());
            socketServerThread.start();
        } else if(role.equals(CLIENT)) {
            ipAddress = getIntent().getStringExtra(IPADDR);
            sendMyNickToServer(nickname);
        }

        //start timer
        startTimer();
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
                    String replyMsg = null;
                    if(messageFromClient.equals(ISSTARTED)) {
                        replyMsg = STARTED;
                    } else if(messageFromClient.contains(HEREISMYNICK)) {
                        String nick = messageFromClient.replace(HEREISMYNICK, "");
                        Player opponent = new Player(nick);
                        opponent.setIpAddress(socket.getInetAddress().toString());
                        gameControler.addPlayer(opponent);
                        replyMsg = "you are added as my opponent";
                    } else if(messageFromClient.equals(CORRECTANSWER)) {
                        increaseScore(socket.getInetAddress().toString());
                        replyMsg = CORRECTANSWER;
                    }
                    dataOutputStream.writeUTF(replyMsg);
                    final String tmp = replyMsg;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WifiGameActivity.this, tmp, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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

    private static class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress = ipAddress;
        int dstPort = SocketServerPORT;
        String msgToServer;
        String response;

        public MyClientTask(String msgToServer) {
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
                dataOutputStream.writeUTF(msgToServer);
                response = dataInputStream.readUTF();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
    }


    public static class PlaceholderFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_wifi_game, container, false);
            final TextView textView = (TextView) rootView.findViewById(R.id.wifi_section_label);
            final int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            final EditText editText = rootView.findViewById(R.id.wifi_answerEt);
            final Button button = rootView.findViewById(R.id.wifi_enterBt);
            button.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    String answer = editText.getText().toString().toLowerCase();
                    switch (sectionNumber) {
                        case 1:
                            if(possibleCountries.contains(answer)&&!player.getAnswers("countries").contains(answer)&&checkLetter(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("countries").add(answer);
                                increaseScore(player);
                                Log.d("Score",String.valueOf(player.getScore()));
                                updateTextView(player.getAnswers("countries"), textView);
                                if(role.equals(CLIENT)) {
                                    MyClientTask myClientTask = new MyClientTask(CORRECTANSWER);
                                    myClientTask.execute();
                                }
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            if(possibleAnimals.contains(answer)&&!player.getAnswers("animals").contains(answer)&&checkLetter(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("animals").add(answer);
                                increaseScore(player);
                                Log.d("Score",String.valueOf(player.getScore()));
                                updateTextView(player.getAnswers("animals"), textView);
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            if(possibleMountains.contains(answer)&&!player.getAnswers("mountains").contains(answer)&&checkLetter(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("mountains").add(answer);
                                increaseScore(player);
                                Log.d("Score",String.valueOf(player.getScore()));
                                updateTextView(player.getAnswers("mountains"), textView);
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                            break;
                        }

                }

                private boolean checkLetter(String answer) {
                    char firstLetter=answer.charAt(0);
                    char gameLetter=gameControler.getGame().getLetter();
                    if(firstLetter==gameLetter){
                        return true;}
                    return false;
                }
            });

            return rootView;
        }
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() { }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
    }

    private static void increaseScore(Player player) {
        gameControler.increaseScore(player, 1);
    }

    private void increaseScore(String ipAddress) {
        gameControler.increaseScore(ipAddress, 1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getMountainsFromDb() {
        new AsyncTask<Void, Void, List<EntityMountain>>() {
            @Override
            protected List<EntityMountain> doInBackground(Void... voids) {
                return db.daoMountain().getMountains();
            }
            @Override
            protected void onPostExecute(List<EntityMountain> entityMountains) {
                super.onPostExecute(entityMountains);
                for(EntityMountain entityMountain : entityMountains)
                    possibleMountains.add(entityMountain.getName().toLowerCase());
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getAnimalsFromDb() {
        new AsyncTask<Void, Void, List<EntityAnimal>>() {
            @Override
            protected List<EntityAnimal> doInBackground(Void... voids) {
                return db.daoAnimals().getAnimals();
            }
            @Override
            protected void onPostExecute(List<EntityAnimal> entityAnimals) {
                super.onPostExecute(entityAnimals);
                for(EntityAnimal entityAnimal : entityAnimals)
                    possibleAnimals.add(entityAnimal.getName().toLowerCase());
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getCountriesFromDb() {
        new AsyncTask<Void, Void, List<EntityCountry>>() {
            @Override
            protected List<EntityCountry> doInBackground(Void... voids) {
                return db.daoCountry().getCountries();
            }
            @Override
            protected void onPostExecute(List<EntityCountry> entityCountries) {
                super.onPostExecute(entityCountries);
                for(EntityCountry entityCountry : entityCountries)
                    possibleCountries.add(entityCountry.getName().toLowerCase());
            }
        }.execute();
    }

    private void startTimer() {
        timer = new CountDownTimer(gameControler.getGame().getTime(),1000) {
            @Override
            public void onTick(long l) {
                Log.d("TIMER",""+l/1000.);
            }
            @Override
            public void onFinish() {
                Log.d("TIMER","DONE");
                Intent finishedGame = new Intent(WifiGameActivity.this, tmpWifiFinishActivity.class);
                GameControler.GameStatus gameStatus = gameControler.getResults();
                Log.d("GameStatus", String.valueOf(gameStatus));
                finishedGame.putExtra("GAMESTATUS",gameStatus);
                finishedGame.putExtra(ROLE_TAG, role);
                if(role.equals(CLIENT)) finishedGame.putExtra(IPADDR, ipAddress);
                if(serverSocket != null) try {  serverSocket.close(); } catch (IOException e) { e.printStackTrace(); }
                startActivity(finishedGame);
            }
        }.start();
    }

    private static void updateTextView(Set<String> answers, TextView textView) {
        StringBuilder sb = new StringBuilder();
        for(String answer : answers)
            sb.append(answer + ", ");
        textView.setText(sb.toString());
    }

    private void sendMyNickToServer(String nickname) {
        MyClientTask myClientTask = new MyClientTask(HEREISMYNICK + nickname);
        myClientTask.execute();
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

}
