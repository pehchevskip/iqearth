package com.pehchevskip.iqearth.retrofit;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pehchevskip.iqearth.R;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pehchevskip on 18-May-18.
 */

public class AnimalsRetrofitTask extends AsyncTask<Void, Void, List<String>> {
    private AnimalsApi service;
    private AppDatabase database;

    public AnimalsRetrofitTask(AppDatabase db) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/boennemann/animals/master/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(AnimalsApi.class);
        this.database = db;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        Call<List<String>> animals = service.getAnimals();
        try {
            return animals.execute().body();
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        List<EntityAnimal> animalList = new ArrayList<>();
        for(String string : strings) {
            animalList.add(new EntityAnimal(string));
        }
        insertAnimals(animalList);
    }

    @SuppressLint("StaticFieldLeak")
    private void insertAnimals(final List<EntityAnimal> animalList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoAnimals().insertAnimals(animalList);
                return null;
            }
        }.execute();
    }
}
