package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pehchevskip.iqearth.controlers.GameControler;
import com.pehchevskip.iqearth.model.Game;
import com.pehchevskip.iqearth.model.Player;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;
import com.pehchevskip.iqearth.retrofit.AnimalsRetrofitTask;
import com.pehchevskip.iqearth.retrofit.CountryRetrofitTask;
import com.pehchevskip.iqearth.retrofit.MountainsRetrofitTask;

import java.util.List;

public class EnterNickname extends AppCompatActivity {

    private final static String NICKNAME="nickname";
    //Views
    EditText mEditTextnickname;
    Button mButtonSumbit;

    //Nickame of player
    String nickname;

    //Game controler
    GameControler gameControler;
    //player
    Player player;
    // Database
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_nickname);
        mEditTextnickname=(EditText) findViewById(R.id.nickname);
        mButtonSumbit=(Button)findViewById(R.id.submit);
        gameControler=GameControler.getInstance();
        mButtonSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEditTextnickname.getText().toString().trim().equals("")){
                    nickname=mEditTextnickname.getText().toString();
                    Intent start_activity;
//                    start_activity = new Intent(EnterNickname.this,StartActivity.class);
                    start_activity = new Intent(EnterNickname.this,ServerOrClientActivity.class);
                    start_activity.putExtra(NICKNAME,nickname);
                    player=new Player(nickname);
                    gameControler.addPlayer(player);
                    startActivity(start_activity);
                }
            }
        });
        // Database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "iqearth-db").build();
        checkForExistingDataInDatabase();
    }
    private void checkForExistingDataInDatabase() {
        checkForExistingCountries();
        checkForExistingAnimals();
        checkForExistingMountains();
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
                    Toast.makeText(EnterNickname.this, "No countries in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertCountriesInDb();
                } else {
                    Toast.makeText(EnterNickname.this, entityCountries.size() + " countries", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EnterNickname.this, "No animals in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertAnimalsInDb();
                } else {
                    Toast.makeText(EnterNickname.this, entityAnimals.size() + " animals", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EnterNickname.this, "No mountains in db found!", Toast.LENGTH_SHORT).show();
                    getAndInsertMountainsInDb();
                } else {
                    Toast.makeText(EnterNickname.this, entityMountains.size() + " mountains", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getAndInsertCountriesInDb() {
        CountryRetrofitTask task = new CountryRetrofitTask(db);
        task.execute();
    }

    private void getAndInsertAnimalsInDb() {
        AnimalsRetrofitTask task = new AnimalsRetrofitTask(db);
        task.execute();
    }

    private void getAndInsertMountainsInDb() {
        MountainsRetrofitTask task = new MountainsRetrofitTask(db);
        task.execute();
    }

}
