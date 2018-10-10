package com.gaijin.sinopticparser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.Group;
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
import android.view.SubMenu;
import android.view.View;

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
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnLongClickListener, Variables {

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
    DayPagerAdapter pagerAdapter;
    // Change cities submenu in NavigationView
    SubMenu subMenu;
    ArrayList<WeekSite> weekSiteList;
    RealmResults<RealmCity> cityList;
    SharedPreferences sharedCity;
    final String cityKey = "city_key";
    int cityNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedCity = getSharedPreferences(cityKey, Context.MODE_PRIVATE);
        cityNumber = sharedCity.getInt(cityKey, 0);

        this.setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Load city from BD
        ArrayList<RealmCity> citiesList = loadBD();

        // Find and load information in web site Sinoptic for all cities
        ArrayList<City> cities = realmListToList(citiesList);
        loadWeatherForCities(cities);

    }

    private ArrayList<City> realmListToList(ArrayList<RealmCity> citiesList) {
        ArrayList<City> cloneList = new ArrayList<>();
        int i = 0;
        for (RealmCity city : citiesList) {
            City newCity = new City();
            city.clone(newCity);
            cloneList.add(newCity);
            i++;
        }

        return cloneList;
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
        cityList = realm.where(RealmCity.class).findAll();

        ArrayList<RealmCity> list = new ArrayList<>();
        for (RealmCity city : cityList) {
            list.add(city);
        }
        realm.commitTransaction();

        createCitiesMenu(list);

        return list;
    }

    private void createCitiesMenu(ArrayList<RealmCity> cities) {
        // Create Cities menu
        if (subMenu == null) {
            Menu menu = navigationView.getMenu();
            subMenu = menu.addSubMenu("Cities:");
        }
        if (!cities.isEmpty()) {
            // Remove all items from subMenu of city list
            for (int i = 0; i < cities.size(); i++) {
                try {
                    subMenu.removeItem(i);
                } catch (Exception ex) {
                }
            }
            //Add  Cities to Group
            for (int itemId = 0; itemId < cities.size(); itemId++) {
                RealmCity city = cities.get(itemId);
                Log.d("MyLog", "City - " + city.getCityName() + " itemID " + itemId);
                subMenu.add(R.id.city_group, itemId, Menu.NONE, city.getCityName());
            }
            navigationView.invalidate();
        }
    }

    /**
     * This function check all Cities class from list and create separate threads
     * for get information from the web site Sinoptic
     *
     * @param citiesList - list which contains all information for choose cities
     */
    private void loadWeatherForCities(ArrayList<City> citiesList) {

        Log.d("MyLog", "Load information from Internet  ");

        Observable.fromArray(citiesList)
                .map(new Function<ArrayList<City>, ArrayList<WeekSite>>() {
                    @Override
                    public ArrayList<WeekSite> apply(ArrayList<City> citiesList) throws Exception {
                        ArrayList<WeekSite> weekSites = new ArrayList<>();
                        for (int i = 0; i < citiesList.size(); i++) {
                            City newCity = citiesList.get(i);
                            Log.d("MyLog", "\n##################\n"
                                    + newCity.toString()
                                    + "\n##################\n");
                            WeekSite weekSite = new WeekSite(newCity);
                            // Parse information from site for choose cities
                            weekSite.parseWeek();
                            weekSites.add(i, weekSite);
                        }

                        return weekSites;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                // Load the received information for view to user
                .subscribe(day -> createPagerView(day));
    }

    /**
     * This function displays information received from web site
     *
     * @param weekSiteList - received information from the site for each day of each city
     */
    private void createPagerView(ArrayList<WeekSite> weekSiteList) {
        this.weekSiteList = weekSiteList;

        if (cityNumber < weekSiteList.size() && weekSiteList.get(cityNumber) != null) {
            Log.d("MyLog", "Pager view must be loaded City id =" + weekSiteList.get(cityNumber).getCity());
            ArrayList<DaySite> daySiteList = weekSiteList.get(cityNumber).getListOfDaySite();
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

        switch (id) {
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
                // Change first loaded city
                SharedPreferences.Editor editor = sharedCity.edit();
                editor.putInt(cityKey, id);
                editor.commit();
                reloadPagerView(id);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reloadPagerView(int cityNumber) {

        Log.d("MyLog", "Reload Page View");
        Log.d("MyLog", "CIty " + weekSiteList.get(cityNumber).getCity());

        //pagerAdapter.reloadData(weekSiteList.get(cityNumber).getListOfDaySite());
        WeekSite weekForCity = weekSiteList.get(cityNumber);
        City cityForDisplaying = weekForCity.getCity();
        toolbar.setTitle(cityForDisplaying.getWeatherIn());
        pagerAdapter = new DayPagerAdapter(getSupportFragmentManager(), weekForCity.getListOfDaySite());
        pager.setAdapter(pagerAdapter);
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

                    loadWeatherForCities(realmListToList(citiesList));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
