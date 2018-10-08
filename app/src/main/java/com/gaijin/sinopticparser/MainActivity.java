package com.gaijin.sinopticparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gaijin.sinopticparser.views.fragments.City;
import com.gaijin.sinopticparser.views.adapters.DayPagerAdapter;
import com.gaijin.sinopticparser.parsers_classes.DaySite;
import com.gaijin.sinopticparser.db.RealmCity;
import com.gaijin.sinopticparser.parsers_classes.WeekSite;
import com.gaijin.sinopticparser.components.Variables;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Variables {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    // Object of DB
    Realm realm = null;
    // Adapter for PagerView
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Load city from BD
        ArrayList<RealmCity> citiesList = loadBD();
        // Find and load information in web site Sinoptic for all cities
        loadWeatherForCities(citiesList);

    }

    /**
     * This function load cities information from Realm DB
     *
     * @return list of cities for further processing
     */
    private ArrayList<RealmCity> loadBD() {

        // Initialize Realm (just once per application)
        Realm.init(this);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<RealmCity> cityList = realm.where(RealmCity.class).findAll();

        ArrayList<RealmCity> list = new ArrayList<>();
        for (RealmCity city : cityList) {
            list.add(city);
        }
        realm.commitTransaction();

        return list;
    }

    /**
     * This function check all Cities class from list and create separate threads
     * for get information from the web site Sinoptic
     *
     * @param citiesList - list which contains all information for choose cities
     */
    private void loadWeatherForCities(ArrayList<RealmCity> citiesList) {

        Log.d("MyLog", "Load information from Internet  ");
        for (RealmCity city : citiesList) {
            Log.d("MyLog", city.toString());
            City newCity = new City();
            city.clone(newCity);
            Observable.just(newCity)
                    .map(new Function<City, ArrayList<DaySite>>() {
                        @Override
                        public ArrayList<DaySite> apply(City city) throws Exception {
                            WeekSite weekSite = new WeekSite(city);
                            // Parse information from site for choose cities
                            return weekSite.parseWeek();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    // Load the received information for view to user
                    .subscribe(day -> createPagerView(day));
        }
    }

    /**
     * This function displays information received from web site
     *
     * @param daySiteList - received information from the site for each day of each city
     */
    private void createPagerView(ArrayList<DaySite> daySiteList) {

        // Create adapter for normalization information for view
        pagerAdapter = new DayPagerAdapter(getSupportFragmentManager(), daySiteList);
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d("MyLog", "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.add_city:
                //Toast.makeText(this, "Add city", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, SearchCityActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.settings:
                //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                //intent = new Intent(this, SearchCityActivity.class);
                break;
            default:

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    // Receive information from Search City Action and add new City to DB
                    String cityInfo = data.getStringExtra("cityInfo");
                    RealmCity city = new RealmCity(cityInfo.split("\\|"));
                    Log.d("MyLog", "Returned city is  " + cityInfo);
                    realm.beginTransaction();
                    realm.insert(city);
                    realm.commitTransaction();

                    ArrayList<RealmCity> citiesList = loadBD();

                    loadWeatherForCities(citiesList);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
