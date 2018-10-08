package com.gaijin.sinopticparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.View;
import android.widget.TabHost;

import com.gaijin.sinopticparser.cards.City;
import com.gaijin.sinopticparser.cards.DaySite;
import com.gaijin.sinopticparser.cards.RealmCity;
import com.gaijin.sinopticparser.cards.DayPagerAdapter;
import com.gaijin.sinopticparser.cards.WeekSite;
import com.gaijin.sinopticparser.components.Variables;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Variables {

    private static String siteText;

    Realm realm = null;

    TabHost tabHost;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ButterKnife.bind(this);
        Log.d("MyLog", "Load information from BD  ");
        ArrayList<RealmCity> citiesList = loadBD();

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loadWeatherForCities(citiesList);


    }

    private void loadWeatherForCities(ArrayList<RealmCity> citiesList) {

        Log.d("MyLog", "Load information from Internet  ");
        for (RealmCity city : citiesList) {
            Log.d("MyLog", city.toString());
            City newCity = new City();
            city.clone(newCity);
            Observable.just(newCity)
                    //.map(string->JsoupParser.getTodayClass(string))
                    .map(new Function<City, ArrayList<DaySite>>() {
                        @Override
                        public ArrayList<DaySite> apply(City city) throws Exception {
                            WeekSite weekSite = new WeekSite(city);
                            return weekSite.parseWeek();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(day -> createRecyclerView(day));
        }
    }

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

    private void createRecyclerView(ArrayList<DaySite> daySiteList) {
        Log.d("MyLog", "daySite.size() " + daySiteList.size());

        pager = (ViewPager) findViewById(R.id.pager);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = null;
//        switch (item.getItemId()) {
//            case R.id.add_city:
//                //Toast.makeText(this, "Add city", Toast.LENGTH_SHORT).show();
//                intent = new Intent(this, SearchCityActivity.class);
//                break;
//            case R.id.settings:
//                //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
//                intent = new Intent(this, SearchCityActivity.class);
//                break;
//            default:
//
//        }
//        startActivityForResult(intent, 1);
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String cityInfo = data.getStringExtra("cityInfo");
            RealmCity city = new RealmCity(cityInfo.split("\\|"));
            Log.d("MyLog", "Returned city is  " + cityInfo);
            realm.beginTransaction();
            realm.insert(city);
            realm.commitTransaction();
            ArrayList<RealmCity> citiesList = loadBD();

            //loadWeatherForCities(citiesList);

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
