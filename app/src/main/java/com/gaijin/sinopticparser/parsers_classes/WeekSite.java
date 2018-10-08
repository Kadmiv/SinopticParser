package com.gaijin.sinopticparser.parsers_classes;

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

/**
 * Created by Kachulyak Ivan.
 */
public class WeekSite implements Variables {

    /*Day length in milliseconds*/
    private int LENGTH_OF_DAY = 86400000;

    /*Variable of day format
     * Need for link of day Example -> https://sinoptik.ua/погода-киев/2018-09-17*/
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /*City link on site*/
    private String cityLink;
    /*Number of day for parsing */
    private int numberOfDay;

    public WeekSite(City city) {
        this.cityLink = city.getCityLink();
        this.numberOfDay = city.getNumberOfDay();
    }

    /**
     * This function parse a certain number of days (numberOfDay) weather information for city
     *
     * @return - list, which contains all needed information for certain number of days
     */
    public ArrayList<DaySite> parseWeek() {
        ArrayList<DaySite> listOfDaySite = new ArrayList<>(numberOfDay);
        long day = new Date().getTime();

        for (int i = 0; i < numberOfDay; i++) {
            //Calculate date for creation link
            long linkDay = day + i * LENGTH_OF_DAY;
            Date findData = new Date(linkDay);
            String linkDate = dateFormat.format(findData);
            // Create a single link
            String allUrl = String.format("%s/%s/%s", MAIN_LINK, cityLink, linkDate);
            // Connect to server
            String page = httpConnect(allUrl);
            // Parse day HTML
            DaySite daySite = parseHtml(page);
            listOfDaySite.add(daySite);
        }

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
        int timeOut = 2500;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(link).openConnection();
            connection.setRequestMethod("get");
            connection.setReadTimeout(timeOut);
            connection.setConnectTimeout(timeOut);
            connection.setUseCaches(false);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
}
