package com.pehchevskip.iqearth.retrofit;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pehchevskip.iqearth.R;
import com.pehchevskip.iqearth.model.api.Country;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pehchevskip on 18-May-18.
 */

public class CountryTask extends AsyncTask<Void, Void, List<Country>> {

    CountryApi service;
    View view;

    public CountryTask(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://restcountries.eu/rest/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(CountryApi.class);
        this.view = view;
    }

    @Override
    protected List<Country> doInBackground(Void... voids) {
        Call<List<Country>> countries = service.getCountries();
        try {
            return countries.execute().body();
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<Country> countries) {
        super.onPostExecute(countries);
        TextView textView = view.findViewById(R.id.text1);
        String tmp = "";
        for (Country country : countries) {
            tmp += country.name + ", ";
        }
        textView.setText(tmp);
    }
}
