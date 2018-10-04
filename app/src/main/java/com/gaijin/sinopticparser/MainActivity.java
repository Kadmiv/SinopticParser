package com.gaijin.sinopticparser;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.gaijin.sinopticparser.cards.City;
import com.gaijin.sinopticparser.cards.CityAdapter;
import com.gaijin.sinopticparser.cards.DayAdapter;
import com.gaijin.sinopticparser.cards.DaySite;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    String baseUrl = "https://sinoptik.ua/";
    private static String siteText;
    @BindView(R.id.day_view)
    RecyclerView dayView;

    Realm realm = null;

    RecyclerView citysView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<City> cityList = loadBD();

        //Observable<String> values = Observable.just(baseUrl);
//        JsoupParser jsoupParser = new JsoupParser();
//
//        Observable.just(baseUrl)
//                //.map(string->JsoupParser.getTodayClass(string))
//                .map(new Function<String, DaySite>() {
//                    @Override
//                    public DaySite apply(String s) throws Exception {
//                        return jsoupParser.getTodayClass(s);
//                    }
//                })
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(day -> createRecyclerView(day));


    }

    private ArrayList<City> loadBD() {

        // Initialize Realm (just once per application)
        Realm.init(this);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<City> cityList = realm.where(City.class).findAll();
        ArrayList<City> list = new ArrayList<>();
        for (City city : cityList) {
            list.add(city);
        }
        realm.commitTransaction();

        return list;
    }

    private void createRecyclerView(DaySite daySite) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dayView.setLayoutManager(manager);
        DayAdapter adapter = new DayAdapter(this, daySite.getWeatherOnDay());
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
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);


        // Create search manager, for searching cities
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(
//                new ComponentName(this, MainActivity.class)));
//        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String cityName) {
        //запускаем поисковый intent

//        Observable.just(baseUrl)
//                //.map(string->JsoupParser.getTodayClass(string))
//                .map(new Function<String, ArrayList<String>>() {
//                    @Override
//                    public ArrayList<String> apply(String link) throws Exception {
//                        Searcher searcher = new Searcher(link);
//                        ArrayList<String> cityList = searcher.getSearchingResult(cityName);
//                        return cityList;
//                    }
//                })
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(day -> createCityRecyclerView(day));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String cityName) {
//        Observable.just(baseUrl)
//                //.map(string->JsoupParser.getTodayClass(string))
//                .map(new Function<String, ArrayList<String>>() {
//                    @Override
//                    public ArrayList<String> apply(String link) throws Exception {
//                        Searcher searcher = new Searcher(link);
//                        ArrayList<String> cityList = searcher.getSearchingResult(cityName);
//                        return cityList;
//                    }
//                })
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(day -> createCityRecyclerView(day));
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
