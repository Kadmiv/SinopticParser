package com.gaijin.sinopticparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends AppCompatActivity implements Variables {

    private static String siteText;

    Realm realm = null;

    TabHost tabHost;

    ViewPager pager;
    PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public boolean onCreateOptionsMenu(Menu menu) {

        // Create menu
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem addItem = menu.findItem(R.id.add_city);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.add_city:
                //Toast.makeText(this, "Add city", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, SearchCityActivity.class);
                break;
            case R.id.settings:
                //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, SearchCityActivity.class);
                break;
            default:

        }
        startActivityForResult(intent, 1);
        return super.onOptionsItemSelected(item);
    }

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
