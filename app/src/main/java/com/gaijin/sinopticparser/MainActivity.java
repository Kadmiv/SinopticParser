package com.gaijin.sinopticparser;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.support.v7.widget.*;
import android.widget.ListView;

import com.gaijin.sinopticparser.cards.City;
import com.gaijin.sinopticparser.cards.CityAdapter;
import com.gaijin.sinopticparser.cards.DayAdapter;
import com.gaijin.sinopticparser.cards.DaySite;
import com.gaijin.sinopticparser.cards.Searcher;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener {
    String baseUrl = "https://sinoptik.ua/";
    private static String siteText;
    @BindView(R.id.day_view)
    RecyclerView dayView;

    Realm realm = null;

    RecyclerView citysView;
    @BindView(R.id.city_name)
    Toolbar toolbar;

    MaterialSearchView searchView;

    ListView list;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
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
        list = (ListView) findViewById(R.id.listview);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setMenuItem(searchItem);
        searchView.setOnQueryTextListener(this);
        List<String> suggestions = Arrays.asList("udarnik","loboda","clava","udarnik","loboda","clava","udarnik","loboda","clava","udarnik","loboda","clava");//new String[]{"udarnik","loboda","clava"};
        adapter = new ListViewAdapter(this, suggestions);
        //searchView.setSuggestions(suggestions);
        //searchView.setAdapter(adapter);
        list.setAdapter(adapter);

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
//                        ArrayList<String> cities = searcher.getSearchingResult(cityName);
//                        //Log.d("MyLog","Size "+cityList.size()+" firs"+cityList.get(0));
//                        return cities;
//                    }
//                })
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(cities -> setSearchSuggestions(cities));
        return true;
    }

    private void setSearchSuggestions(ArrayList<String> cities) {

        String[] suggestions = new String[cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            suggestions[i] = cities.get(i);
        }
        searchView.setSuggestions(suggestions);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
