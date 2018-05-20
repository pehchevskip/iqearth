package com.pehchevskip.iqearth.retrofit;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pehchevskip.iqearth.R;
import com.pehchevskip.iqearth.model.api.Country;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityCity;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pehchevskip on 18-May-18.
 */

public class CountryRetrofitTask extends AsyncTask<Void, Void, List<Country>> {

    private CountryApi service;
    private AppDatabase database;

    public CountryRetrofitTask(AppDatabase db) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://restcountries.eu/rest/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(CountryApi.class);
        this.database = db;
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
        List<EntityCountry> countriesList = new ArrayList<>();
        List<EntityCity> citiesList = new ArrayList<>();
        for(Country country : countries) {
            countriesList.add(new EntityCountry(country.name));
            citiesList.add(new EntityCity(country.capital));
        }
        insertCountries(countriesList);
        insertCities(citiesList);
    }

    @SuppressLint("StaticFieldLeak")
    private void insertCities(final List<EntityCity> citiesList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoCity().insertCities(citiesList);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void insertCountries(final List<EntityCountry> countryList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoCountry().insertCountries(countryList);
                return null;
            }
        }.execute();
    }
}
