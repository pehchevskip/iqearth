package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.pehchevskip.iqearth.bluetooth.BluetoothControler;
import com.pehchevskip.iqearth.bluetooth.service.BluetoothConnectionService;
import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class tmpActivity extends AppCompatActivity {
    public enum BluetoothMessaging{
        RegisterOpponent,SettingLetter,AnsweredQuestion,Other
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static AppDatabase db;
    private static List<String> possibleCountries = new ArrayList<>();
    private static List<String> possibleAnimals = new ArrayList<>();
    private static List<String> possibleMountains = new ArrayList<>();
    //connection info
    private static final String APP_NAME="iqearth";
    private static final UUID myUuid=UUID.fromString("f9f89bf7-e40a-4d51-bc0d-f90d74919141");
    private static Player player ;
    private static Player opponent;
    private static Game game;
    static GameControler gameControler;
    //connection state
    static final int STATE_LISTENING=1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    static final int STARTED_GAME=6;
    static final int ANSWERED_QUESTION=7;
    static final int OPPONENT_REGISTER=8;
    static final int GENERATE_LETTER=9;
    //role tag
    private static final String ROLE_TAG="role";
    private static final String CLIENT="client";
    private static final String SERVER="server";
    static String role;
    //timer
    static CountDownTimer timer;
    //nickname for opponent
    static String nickname;

    //letter
    static char letterGame;
    //BluetoothMessaging
    static BluetoothMessaging bluetoothMessaging;
    public boolean flag;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //Views
    static TextView remaining_time;
    TextView my_score;
    TextView opp_score;
    static TextView letter;

    static BluetoothControler controler;
    static BluetoothConnectionService bluetoothConnectionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //controler
        controler=BluetoothControler.getInstance();
        controler.setHandler(handler);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        my_score=(TextView)findViewById(R.id.myscore);
        opp_score=(TextView)findViewById(R.id.opp_score);
        remaining_time=(TextView)findViewById(R.id.time_remaining);
        letter=findViewById(R.id.bluetooth_letter);

        //connecting with game controler
        gameControler=GameControler.getInstance();
        game=gameControler.getGame();
        nickname=getIntent().getStringExtra("nickname");
        player=gameControler.getPlayers().get(0);
        Log.d("size of players",String.valueOf(gameControler.getPlayers().size()));
        opponent = gameControler.getPlayers().get(1);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
     /*   fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // Database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "iqearth-db").build();
        getCountriesFromDb();
        getAnimalsFromDb();
        getMountainsFromDb();

        //start timer
        bluetoothConnectionService=BluetoothConnectionService.getInstance(this);
        bluetoothConnectionService.setHandler(handler);
        role=getIntent().getStringExtra(ROLE_TAG);
        game=new Game(60000);
        gameControler.setGame(game);
        if(role.equals(SERVER)){
            letterGame=game.generateLetter();
            SetLetterView(letterGame);
            Message msg=Message.obtain();
            msg.what=GENERATE_LETTER;
            handler.sendMessage(msg);
            //bluetoothConnectionService.start();
        }
        if(role.equals(CLIENT)){
            //BluetoothDevice bd=controler.getBdDevice();
            //bluetoothConnectionService.startClient(bd,myUuid);
        }

        //local broadcast manager for incoming message

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter("incomingMessage"));

    }
    public BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text=intent.getStringExtra("theMessage");
            switch (bluetoothMessaging){
                case RegisterOpponent:
                    opponent=new Player(text);
                    gameControler.addPlayer(opponent);
                    break;
                case SettingLetter:
                    game.setLetter(text.charAt(0));
                    SetLetterView(text.charAt(0));
                    Message msg=Message.obtain();
                    msg.what=STARTED_GAME;
                    handler.sendMessage(msg);
                    break;
                case AnsweredQuestion:
                    increaseScore(opponent);
                    Log.d("opp_score", String.valueOf(opponent.getScore()));
                    break;
            }

        }
    };
      Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case STATE_LISTENING:
                    Log.d("handler","connecting");
                    break;
                case STATE_CONNECTING:
                    //status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    //status.setText("Connected");
                    //start_game.setVisibility(View.VISIBLE);
                    //connection info
                    Log.d("tmpActivity","Connected");


                    break;
                case STATE_CONNECTION_FAILED:
                    //status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:





                    break;
                case STARTED_GAME:
                    Log.d("Handler","Started GAme");
                    startTimerStatic();
                    break;
                case ANSWERED_QUESTION:

                    break;
                case GENERATE_LETTER:



                        bluetoothMessaging=BluetoothMessaging.SettingLetter;

                        bluetoothConnectionService.write(String.valueOf(letterGame).getBytes());

                        break;








            }
            return true;


        }
    });

    private void startTimerStatic() {
        startTimer();
    }

    private static void SetLetterView(char letterGame){
         letter.setText((String.valueOf(gameControler.getGame().getLetter())).toUpperCase());
     }
    private static void increaseScore(Player p)
    {
       gameControler.increaseScore(p,1);
    }
    private void startTimer(){
        timer=new CountDownTimer(gameControler.getGame().getTime(),1000) {
            @Override
            public void onTick(long l) {
                remaining_time.setText("Seconds Remaining"+l/1000);
                Log.d("TIMER",""+l/1000.);
            }

            @Override
            public void onFinish() {
                remaining_time.setText("Game Finished");
                Log.d("TIMER","DONE");
                Intent finishedGame=new Intent(tmpActivity.this,finishedGameActivity.class);
                GameControler.GameStatus gameStatus=gameControler.getResults();
                Log.d("GameStatus", String.valueOf(gameStatus));
                finishedGame.putExtra("GAMESTATUS",gameStatus);
                startActivity(finishedGame);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //controler.sendReceive.cancel();
        unregisterReceiver(mReceiver);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tmp, menu);
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
    public void answeredQuestion(){
        Message msg=Message.obtain();
        msg.what=ANSWERED_QUESTION;
        handler.sendMessage(msg);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    @SuppressLint("ValidFragment")
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_tmp, container, false);
            final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            final int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            textView.setText(getString(R.string.section_format, sectionNumber));

            final StringBuilder sb = new StringBuilder();
            final EditText editText = rootView.findViewById(R.id.tmpEditText1);
            final Button button = rootView.findViewById(R.id.tmpButton1);
            button.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    String answer = editText.getText().toString().toLowerCase().trim();

                    switch (sectionNumber) {
                        case 1:
                            if(possibleCountries.contains(answer)&&!player.getAnswers("countries").contains(answer)&&checkLetter(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("countries").add(answer);
                                increaseScore(player);
                                Log.d("Score",String.valueOf(player.getScore()));
                                bluetoothMessaging=BluetoothMessaging.AnsweredQuestion;
                                bluetoothConnectionService.write(String.valueOf(player.getScore()).getBytes());
                                updateTextView(player.getAnswers("countries"), textView);
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();

                            break;
                        case 2:
                            if(possibleAnimals.contains(answer)&&!player.getAnswers("animals").contains(answer)&&checkLetter(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("animals").add(answer);
                                increaseScore(player);
                                bluetoothMessaging=BluetoothMessaging.AnsweredQuestion;
                                bluetoothConnectionService.write(String.valueOf(player.getScore()).getBytes());
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
                                bluetoothMessaging=BluetoothMessaging.AnsweredQuestion;
                                bluetoothConnectionService.write(String.valueOf(player.getScore()).getBytes());
                                updateTextView(player.getAnswers("mountains"), textView);
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();

                            break;
                    }
                    editText.setText(' ');
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

        private void updateTextView(Set<String> answers, TextView textView) {
            StringBuilder sb = new StringBuilder();
            for(String answer : answers)
                sb.append(answer + ", ");
            textView.setText(sb.toString());
        }

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
}
