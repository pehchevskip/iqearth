package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.MyIntClass;
import com.pehchevskip.iqearth.model.Player;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;
import com.pehchevskip.iqearth.persistance.entities.EntityCity;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;
import com.pehchevskip.iqearth.persistance.entities.EntityPlayer;
import com.pehchevskip.iqearth.retrofit.AnimalsRetrofitTask;
import com.pehchevskip.iqearth.retrofit.CountryRetrofitTask;
import com.pehchevskip.iqearth.retrofit.MountainsRetrofitTask;

import java.util.List;

public class EnterNickname extends AppCompatActivity {

    private final static String NICKNAME="nickname";
    private static final String ROLE_TAG="role";
    private static final String ONEPLAYER = "one";
    // Views
    EditText mEditTextnickname;
    Button mButtonSubmit;
    TextView mEnterNickname;
    RadioButton onePlayerRb;
    RadioButton twoPlayersRb;
    RadioButton morePlayersRb;

    // Nickname of player
    String nickname;

    // Game controler
    GameControler gameControler;

    // Player
    Player player;

    // Existance of data
    int countEntities = 0;
    MyIntClass countEntitiesO = new MyIntClass(0, this);

    // Database
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_nickname);
        mEditTextnickname=(EditText) findViewById(R.id.nickname);
        mButtonSubmit =(Button)findViewById(R.id.submit);
        mEnterNickname = findViewById(R.id.enterNicknameTv);
        onePlayerRb = findViewById(R.id.oneplayerRb);
        twoPlayersRb = findViewById(R.id.twoplayersRb);
        morePlayersRb = findViewById(R.id.moreplayersRb);
        gameControler=GameControler.getInstance();
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEditTextnickname.getText().toString().trim().equals("")){
                    nickname=mEditTextnickname.getText().toString();
                    insertPlayer(nickname);
                    Intent start_activity;
                    if(onePlayerRb.isChecked()) {
                        start_activity = new Intent(EnterNickname.this,WifiGameActivity.class);
                        start_activity.putExtra(ROLE_TAG, ONEPLAYER);
                    } else if(twoPlayersRb.isChecked()) {
                        start_activity = new Intent(EnterNickname.this,StartActivity.class);
                    } else {
                        start_activity = new Intent(EnterNickname.this,ServerOrClientActivity.class);
                    }
                    start_activity.putExtra(NICKNAME,nickname);
                    player=new Player(nickname);
                    gameControler.addPlayer(player);
                    startActivity(start_activity);
                } else {
                    Toast.makeText(EnterNickname.this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "iqearth-db").build();
        checkForExistingDataInDatabase();
    }

    private void checkForExistingDataInDatabase() {
        checkForExistingPlayer();
        checkForExistingCountries();
        checkForExistingCities();
        checkForExistingAnimals();
        checkForExistingMountains();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingPlayer() {
        new AsyncTask<Void, Void, List<EntityPlayer>>() {
            @Override
            protected List<EntityPlayer> doInBackground(Void... voids) {
                return db.daoPlayer().getPlayers();
            }
            @Override
            protected void onPostExecute(final List<EntityPlayer> entityPlayers) {
                if(entityPlayers.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEnterNickname.setText(R.string.enter_nickname);
                            mEditTextnickname.requestFocus();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEnterNickname.setText(R.string.edit_nickname);
                            mEditTextnickname.setText(entityPlayers.get(0).getNickname());
                        }
                    });
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingCountries() {
        new AsyncTask<Void, Void, List<EntityCountry>>() {
            @Override
            protected List<EntityCountry> doInBackground(Void... voids) {
                return db.daoCountry().getCountries();
            }
            @Override
            protected void onPostExecute(List<EntityCountry> entityCountries) {
                super.onPostExecute(entityCountries);
                if(entityCountries.isEmpty()) {
//                    Toast.makeText(EnterNickname.this, "No countries in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertCountriesInDb();
                } else {
                    countEntities++;
                    if(countEntities >= 3) {
                        mButtonSubmit.setEnabled(true);
                    }
//                    Toast.makeText(EnterNickname.this, entityCountries.size() + " countries", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingCities() {
        new AsyncTask<Void, Void, List<EntityCity>>() {
            @Override
            protected List<EntityCity> doInBackground(Void... voids) {
                return db.daoCity().getCities();
            }
            @Override
            protected void onPostExecute(List<EntityCity> entityCities) {
                if (entityCities.isEmpty()) {
//                    Toast.makeText(EnterNickname.this, "No cities in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertCountriesInDb();
                } else {
                    countEntities++;
                    if(countEntities >= 3) {
                        mButtonSubmit.setEnabled(true);
                    }
//                    Toast.makeText(EnterNickname.this, entityCities.size() + " cities", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingAnimals() {
        new AsyncTask<Void, Void, List<EntityAnimal>>() {
            @Override
            protected List<EntityAnimal> doInBackground(Void... voids) {
                return db.daoAnimals().getAnimals();
            }
            @Override
            protected void onPostExecute(List<EntityAnimal> entityAnimals) {
                super.onPostExecute(entityAnimals);
                if(entityAnimals.isEmpty()){
//                    Toast.makeText(EnterNickname.this, "No animals in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertAnimalsInDb();
                } else {
                    countEntities++;
                    if(countEntities >= 3) {
                        mButtonSubmit.setEnabled(true);
                    }
//                    Toast.makeText(EnterNickname.this, entityAnimals.size() + " animals", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForExistingMountains() {
        new AsyncTask<Void, Void, List<EntityMountain>>() {
            @Override
            protected List<EntityMountain> doInBackground(Void... voids) {
                return db.daoMountain().getMountains();
            }
            @Override
            protected void onPostExecute(List<EntityMountain> entityMountains) {
                super.onPostExecute(entityMountains);
                if(entityMountains.isEmpty()) {
//                    Toast.makeText(EnterNickname.this, "No mountains in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertMountainsInDb();
                } else {
                    countEntities++;
                    if(countEntities >= 3) {
                        mButtonSubmit.setEnabled(true);
                    }
//                    Toast.makeText(EnterNickname.this, entityMountains.size() + " mountains", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void insertPlayer(final String nickname) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.daoPlayer().deleteAll();
                db.daoPlayer().insertPlayer(new EntityPlayer(nickname, 0));
                return null;
            }
        }.execute();
    }

    private void getAndInsertCountriesInDb() {
        CountryRetrofitTask task = new CountryRetrofitTask(db, countEntitiesO, mButtonSubmit);
        task.execute();
    }

    private void getAndInsertAnimalsInDb() {
        AnimalsRetrofitTask task = new AnimalsRetrofitTask(db, countEntitiesO, mButtonSubmit);
        task.execute();
    }

    private void getAndInsertMountainsInDb() {
        MountainsRetrofitTask task = new MountainsRetrofitTask(db, countEntitiesO, mButtonSubmit);
        task.execute();
    }

}

