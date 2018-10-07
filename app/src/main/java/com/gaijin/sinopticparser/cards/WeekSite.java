package com.gaijin.sinopticparser.cards;

import com.gaijin.sinopticparser.components.Variables;

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
public class WeekSite implements Variables{

    private String cityLink;

    private int LENGTH_OF_DAY = 86400000;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//Need for link for day -> https://sinoptik.ua/погода-киев/2018-09-17

//    SimpleDateFormat timeFormat = new SimpleDateFormat("HH mm");//Need for lint for day -> https://sinoptik.ua/погода-киев/2018-09-17
//    String currentTime = timeFormat.format(new Date()).replace(" ", "");

    private int numberOfDay = 5;

    public WeekSite(City city) {
        this.cityLink = city.getCityLink();
        this.numberOfDay = city.getNumberOfDay();
    }

    public WeekSite(String cityLink, int numberOfDay) {
        this.cityLink = cityLink;
        this.numberOfDay = numberOfDay;
    }


    public ArrayList<DaySite> parseWeek() {
        String sampleFile = "http_day_%s.txt";
        ArrayList<DaySite> listOfDaySite = new ArrayList<>(5);
        long day = new Date().getTime();

        for (int i = 0; i < numberOfDay; i++) {
            long linkDay = day + i * LENGTH_OF_DAY;
            Date findData = new Date(linkDay);
            String linkDate = dateFormat.format(findData);
            String allUrl = String.format("%s/%s/%s", MAIN_LINK,cityLink, linkDate);
            //String baseFile = String.format(sampleFile, findData.getDate());
            //String page = httpConnect(allUrl, baseFile);
            String page = httpConnect(allUrl);
            System.out.println("\n" + findData);
            DaySite daySite = parseHtml(page);
            System.out.println("\n New Day Site \n");
            System.out.println(daySite.toString());

            listOfDaySite.add(daySite);
        }

        return listOfDaySite;
    }

    private DaySite parseHtml(String page) {
        Document doc = Jsoup.parse(page);
        Element body = doc.body();
        DaySite day = new DaySite();
        day.parseDayInfo(body);
        return day;
    }

    private String httpConnect(String baseUrl) {
        String html = null;
        int time = 25000;

        System.out.println("File not found ");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(baseUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(time);
            connection.setConnectTimeout(time);
            connection.setUseCaches(false);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (connection.getResponseCode() == 200) {
                BufferedReader fb = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = null;
                for (line = fb.readLine(); line != null; line = fb.readLine()) {
                    html += line;
                    System.out.println(line);
                }
                fb.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //connection.disconnect();

        return html;
    }

    private String httpConnect(String baseUrl, String baseFile) {
        String html = null;
        int time = 25000;
        if (!(new File(baseFile).exists())) {
            System.out.println("File not found ");
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(baseUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(time);
                connection.setConnectTimeout(time);
                connection.setUseCaches(false);
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (connection.getResponseCode() == 200) {
                    BufferedReader fb = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(baseFile)));
                    String line = null;
                    for (line = fb.readLine(); line != null; line = fb.readLine()) {
                        html += line;
                        fw.write(line);
                        System.out.println(line);
                    }
                    fb.close();
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //connection.disconnect();
        } else {
            System.out.println("File was found ");
            BufferedReader fb = null;
            try {
                fb = new BufferedReader(new InputStreamReader(new FileInputStream(baseFile)));
                String line = null;
                for (line = fb.readLine(); line != null; line = fb.readLine()) {
                    html += line;
                    System.out.println(line);
                }
                fb.close();
            } catch (IOException e) {
            }
        }

        return html;
    }
}
