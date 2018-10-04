package com.gaijin.sinopticparser.cards;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kachulyak Ivan.
 */
public class WeekSite {

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

    public void parseWeek() {
        String sampleFile = "http_day_%s.txt";

        long day = new Date().getTime();

        for (int i = 0; i < numberOfDay; i++) {
            long linkDay = day + i * LENGTH_OF_DAY;
            Date findData = new Date(linkDay);
            String linkDate = dateFormat.format(findData);
            String allUrl = String.format("%s/%s", cityLink, linkDate);
            String baseFile = String.format(sampleFile, findData.getDate());
            String page = httpConnect(allUrl, baseFile);
            System.out.println("\n" + findData);
            DaySite daySite = parseHtml(page);
        }

    }

    private DaySite parseHtml(String page) {
        Document doc = Jsoup.parse(page);
        Element body = doc.body();
        DaySite day = new DaySite();
        day.parseDayInfo(body);
        System.out.println(day.toString());
        return null;
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
