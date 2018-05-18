package com.pehchevskip.iqearth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pehchevskip.iqearth.retrofit.AnimalsTask;
import com.pehchevskip.iqearth.retrofit.CountryTask;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, List<String>> possibleAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initGettingCountries();
        initGettingAnimals();
    }

    private void initGettingAnimals() {
        AnimalsTask animalsTask = new AnimalsTask(findViewById(R.id.text1));
        animalsTask.execute();
    }

    private void initGettingCountries() {
        CountryTask countryTask = new CountryTask(findViewById(R.id.text1));
        countryTask.execute();
    }
}

