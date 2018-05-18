package com.pehchevskip.iqearth.retrofit;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pehchevskip.iqearth.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pehchevskip on 18-May-18.
 */

public class AnimalsTask extends AsyncTask<Void, Void, List<String>> {
    AnimalsApi service;
    View view;

    public AnimalsTask(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/boennemann/animals/master/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(AnimalsApi.class);
        this.view = view;
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
        TextView textView = view.findViewById(R.id.text1);
        String tmp = "";
        for (String animal : strings) {
            tmp += animal + ", ";
        }
        textView.setText(tmp);
    }
}
