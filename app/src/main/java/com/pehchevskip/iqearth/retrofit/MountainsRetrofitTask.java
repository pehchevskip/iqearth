package com.pehchevskip.iqearth.retrofit;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pehchevskip on 19-May-18.
 */

public class MountainsRetrofitTask extends AsyncTask<Void, Void, List<String>> {

    private MountainsApi service;
    private AppDatabase database;

    public MountainsRetrofitTask(AppDatabase database) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/petarpehchevski/iqearth/master/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(MountainsApi.class);
        this.database = database;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        Call<List<String>> mountains = service.getMountains();
        try {
            return mountains.execute().body();
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        List<EntityMountain> mountainList = new ArrayList<>();
        for(String string : strings) {
            mountainList.add(new EntityMountain(string));
        }
        insertMountains(mountainList);
    }

    @SuppressLint("StaticFieldLeak")
    private void insertMountains(final List<EntityMountain> mountainList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoMountain().insertMountains(mountainList);
                return null;
            }
        }.execute();
    }
}
