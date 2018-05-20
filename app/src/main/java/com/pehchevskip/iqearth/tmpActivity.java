package com.pehchevskip.iqearth;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pehchevskip.iqearth.model.Player;
import com.pehchevskip.iqearth.persistance.AppDatabase;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class tmpActivity extends AppCompatActivity {

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

    private static Player player = new Player("Petar");
    

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "iqearth-db").build();
        getCountriesFromDb();
        getAnimalsFromDb();
        getMountainsFromDb();
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

    /**
     * A placeholder fragment containing a simple view.
     */
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
                    String answer = editText.getText().toString().toLowerCase();
                    switch (sectionNumber) {
                        case 1:
                            if(possibleCountries.contains(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("countries").add(answer);
                                updateTextView(player.getAnswers("countries"), textView);
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            if(possibleAnimals.contains(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("animals").add(answer);
                                updateTextView(player.getAnswers("animals"), textView);
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            if(possibleMountains.contains(answer)) {
                                Toast.makeText(rootView.getContext(), "Correct", Toast.LENGTH_SHORT).show();
                                player.getAnswers("mountains").add(answer);
                                updateTextView(player.getAnswers("mountains"), textView);
                            }
                            else
                                Toast.makeText(rootView.getContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                            break;
                    }
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
