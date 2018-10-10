package com.gaijin.sinopticparser.parsers_classes;

import android.util.Log;

import com.gaijin.sinopticparser.components.ConnectionReader;
import com.gaijin.sinopticparser.components.Variables;
import com.gaijin.sinopticparser.views.fragments.City;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.SchedulerRunnableIntrospection;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kachulyak Ivan.
 */
public class WeekSite implements Variables {

    /*Variable of day format
     * Need for link of day Example -> https://sinoptik.ua/погода-киев/2018-09-17*/
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dayDateFormat = new SimpleDateFormat("dd.MM.yy");
    /**/
    private City city;
    /*City link on site*/
    private String cityLink;
    /*Number of day for parsing */
    private int numberOfDay;
    private ArrayList<DaySite> listOfDaySite;

    public WeekSite(City city) {
        this.city = city;
        this.cityLink = city.getCityLink();
        this.numberOfDay = city.getNumberOfDay();
    }

    /**
     * This function parse a certain number of days (numberOfDay) weather information for city
     *
     * @return -
     */
    public void parseWeek() {

        long day = new Date().getTime();

        //so that there is no error java.lang.IndexOutOfBoundsException
        listOfDaySite = new ArrayList<>();
//        for (int i = 0; i < numberOfDay; i++) {
//            listOfDaySite.add(null);
//        }
//        Observable.range(0, numberOfDay)
//                .flatMap(integer -> Observable.just(integer)
//                        .map(new Function<Integer, DaySite>() {
//                            @Override
//                            public DaySite apply(Integer i) throws Exception {
//                                //Calculate date for creation link
//                                long linkDay = day + i * LENGTH_OF_DAY;
//                                Date findData = new Date(linkDay);
//                                String linkDate = dateFormat.format(findData);
//                                // Create a single link
//                                String allUrl = String.format("%s/%s/%s", MAIN_LINK, cityLink, linkDate);
//                                Log.d("MyLog", "Link " + allUrl + " day " + i);
//                                // Connect to server
//                                String page = Jsoup.connect(allUrl)
//                                        .timeout(5000)
//                                        .get().html();
//                                // httpConnect(allUrl);
//                                // Parse day HTML
//                                DaySite daySite = parseHtml(page);
//                                // Set date of day
//                                daySite.setDate(dayDateFormat.format(findData));
//                                daySite.setPositionInList(i);
//                                return daySite;
//                            }
//                        })
//                        .subscribeOn(Schedulers.computation())
//                )
//                //.subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.computation())
//                .subscribe(daySite -> addToDaySiteList(daySite));

        for (int i = 0; i < numberOfDay; i++) {
            //Calculate date for creation link
            long linkDay = day + i * LENGTH_OF_DAY;
            Date findData = new Date(linkDay);
            String linkDate = dateFormat.format(findData);
            // Create a single link
            String allUrl = String.format("%s/%s/%s", MAIN_LINK, cityLink, linkDate);
            // Connect to server
            String page = null;
            try {
                page = Jsoup.connect(allUrl)
                        .timeout(5000)
                        .get().html();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Parse day HTML
            DaySite daySite = parseHtml(page);
            // Set date of day
            daySite.setDate(dayDateFormat.format(findData));
            listOfDaySite.add(daySite);
        }
    }

    private void addToDaySiteList(DaySite daySite) {
        //Log.e("MyLog", "Add to daySiteList" + daySite.getCity().getCityName());
        listOfDaySite.add(daySite.getPositionInList(), daySite);
    }

    /**
     * This function return list, which contains all needed information for certain number of days
     */
    public ArrayList<DaySite> getListOfDaySite() {
        return listOfDaySite;
    }

    /**
     * This function implements the connection to the site server
     *
     * @param link - URL for connection
     * @return - HTML representation of web page
     */
    private String httpConnect(String link) {
        String html = null;
        // Connect to sever
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(link).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(TIME_OUT);
            connection.setConnectTimeout(TIME_OUT);
            connection.setUseCaches(false);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get information from connection
        ConnectionReader connector = new ConnectionReader(connection);
        try {
            html = connector.getConnectionResult();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return html;
    }

    /**
     * This function parse HTML page
     *
     * @param page - HTML page
     * @return - DaySite class, which contains all information of weather in day
     */
    private DaySite parseHtml(String page) {
        Document doc = Jsoup.parse(page);
        // Cut body from HTML page
        Element body = doc.body();
        // Parse information from body
        DaySite day = new DaySite();
        day.parseDayInfo(body);
        return day;
    }

    public City getCity() {
        return city;
    }
}
