package com.gaijin.sinopticparser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.*;
import android.widget.ListView;
import android.widget.Toast;

import com.gaijin.sinopticparser.cards.City;
import com.gaijin.sinopticparser.cards.CityAdapter;
import com.gaijin.sinopticparser.cards.DayAdapter;
import com.gaijin.sinopticparser.cards.DaySite;
import com.gaijin.sinopticparser.cards.RealmCity;
import com.gaijin.sinopticparser.cards.Searcher;
import com.gaijin.sinopticparser.cards.WeekSite;
import com.gaijin.sinopticparser.components.Variables;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.completable.CompletableTimeout;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity implements Variables {

    private static String siteText;
    @BindView(R.id.day_view)
    RecyclerView dayView;

    Realm realm = null;

    RecyclerView citysView;
    @BindView(R.id.city_name)
    Toolbar toolbar;

    MaterialSearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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

    private void createRecyclerView(ArrayList<DaySite> daySite) {
        Log.d("MyLog", "daySite.size() "+daySite.size());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dayView.setLayoutManager(manager);
        DaySite day = daySite.get(0);
        Log.d("MyLog", "daySite.get(0).getWeatherOnDay().size() "+day.getWeatherOnDay().size());
        DayAdapter adapter = new DayAdapter(this, day.getWeatherOnDay());
        dayView.setAdapter(adapter);
    }

    private void createCityRecyclerView(ArrayList<String> cityList) {

        CityAdapter adapter = new CityAdapter(this, cityList);
        citysView.setAdapter(adapter);

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


            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
