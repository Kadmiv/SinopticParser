package com.gaijin.sinopticparser.cards;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kachulyak Ivan.
 */
public class SinopticParser {


    //Температура, °C чувствуется как Давление, мм Влажность, % Ветер, м/сек Вероятность осадков, %

    /*Name of classes for find in HTML code of site */
    /*Common parameters*/

    /*Value for finding elements in left part*/
    private final String LEFT_PART = "lSide";
    private final String IMAGE_OF_NOW = "img";
    private final String TEMP_OF_NOW = "today-temp";

    private final List<String> METRICS_1 = Arrays.asList("Температура: %sC", "чувствуется как: %sC",
            "Давление: %s мм", "Влажность: %s %%", "Ветер: %s", "Вероятность осадков: %s %%");

    private static final List<String> METRICS_2 = Arrays.asList("Температура: %sC", "відчувається як: %sC",
            "Атмосферний тиск: %s мм", "Вологість повітря: %s %%", "Вітер: %s", "Вірогідність опадів: %s %%");

    private static final List<String> METRICS_3 = Arrays.asList("Температура: %sC", "чувствуется как: %sC",
            "Давление: %s мм", "Влажность: %s %%", "Ветер: %s", "Вероятность осадков: %s %%");

    private final List<String> nowList = Arrays.asList(IMAGE_OF_NOW, TEMP_OF_NOW);

    private final List<String> dayTimesList = Arrays.asList("p1 bR ", "p3 bR ", "p4 ", "p1 ", "p2 bR ", "p3 ", "p4 bR ",
            "p5 ", "p6 bR ", "p7 ", "p8 ");
    private final List<String> Times_1 = Arrays.asList("0:00", "3:00", "6:00", "9:00", "12:00", "15:00", "18:00", "21:00");
    private final List<String> Times_2 = Arrays.asList("3:00", "9:00", "15:00", "21:00");

    private WeatherView nowView;
    private String currentTag = "cur";
    private String lang = "ru";


    public ArrayList<WeatherView> getTodayClass(String html) {
        Document doc = Jsoup.parse(html);
        Element body = doc.body();
        // System.out.println(body);
        Elements now = doc.body().getElementsByClass(LEFT_PART);

        // On this part we check lang for metrics
        List<String> metrics = null;
        switch (lang) {
            case "ru":
                metrics = METRICS_1;
                break;
            case "ua":
                metrics = METRICS_2;
                break;
            case "en":
                metrics = METRICS_3;
                break;
        }

        ArrayList<WeatherView> views = parseDay(doc.body(), dayTimesList, metrics);
        try {
            parseNow(now, nowList);
        } catch (Exception exe) {

        }
        Log.d("MyLog", "SinopticParser class daySite.size() " + views.size());
        return views;
    }


    private void parseNow(Elements now, List<String> nowList) {

        /*First parse image of weather*/
        String srcImage = parseSourceImage(now);
        Element image = now.select("img").first();
        String shortDescription = image.attr("alt");
        nowView.setImage(srcImage);
        nowView.setShortDescription(shortDescription);

        /*Next find temp of now*/
        Elements temp = now.get(0).getElementsByClass(nowList.get(1));
        String nowTemp = temp.get(0).text();
        nowView.setTemp(nowTemp);
    }

    private String parseSourceImage(Elements now) {
        Element image = now.select("img").first();
        return image.attr("src");
    }

    private ArrayList<WeatherView> parseDay(Element doc, List<String> dayTimesList, List<String> metrics) {

        ArrayList<WeatherView> viewList = new ArrayList<>();

        Elements parameters = doc.getElementsByClass(dayTimesList.get(0));
        List<String> timeOfView = Times_2;
        if (parameters.size() == 0) {
            timeOfView = Times_1;
        }

//        String changedItem = dayTimesList.get(itemForChange) + currentTag;
//        dayTimesList.set(itemForChange, changedItem);
        for (int i = 0; i < dayTimesList.size(); i++) {

            String time = dayTimesList.get(i);
            parameters = doc.getElementsByClass(time);
            WeatherView newView;

            // day.get
            if (parameters.size() == 0) {
                String changedItem = dayTimesList.get(i) + currentTag;
                dayTimesList.set(i, changedItem);
                time = dayTimesList.get(i);
                parameters = doc.getElementsByClass(time);
                if (parameters.size() == 0) {
                    continue;
                } else {
                    newView = parseView(parameters, metrics);
                    nowView = newView;
                }
            } else {
                newView = parseView(parameters, metrics);
            }
//            newView.setTime(timeOfView.get(i));
            viewList.add(newView);
        }
        Log.d("MyLog", "Sinoptic parser 2 daySite.size() " + viewList.size());
        return viewList;
    }


    private WeatherView parseView(Elements day, List<String> metrics) {
        WeatherView newView = new WeatherView();

        /*Parse image*/
        String scrImage = parseSourceImage(day);
        newView.setImage(scrImage);
        /*Parse short Description*/
        Element description = day.get(1).select("div").first();
        String shortDescription = description.attr("title");
        newView.setShortDescription(shortDescription);
        /*Parse temperature*/
        String temp_1 = String.format(metrics.get(0), day.get(2).text());
        newView.setTemp(temp_1);
        /*Parse temperature 2*/
        String temp_2 = String.format(metrics.get(1), day.get(3).text());
        newView.setTemp_2(temp_2);
        /*Parse Atmospher Pressure*/
        String pressure = String.format(metrics.get(2), day.get(4).text());
        newView.setAtmoPressure(pressure);
        /*Parse Humidity*/
        String humidity = String.format(metrics.get(3), day.get(5).text());
        newView.setHumidity(humidity);
        /*Parse Wind*/
        Element wind = day.get(6).select("div").first();
        String winds = wind.attr("data-tooltip");
        winds = String.format(metrics.get(4), winds);
        newView.setWind(winds);
        /*Parse Precipitation*/
        String precipit = day.get(7).text();
        if (precipit.equals("-")) {
            precipit = "0";
        }
        precipit = String.format(metrics.get(5), precipit);
        newView.setPrecipitation(precipit);

        return newView;
    }

    private int findCurrent(String currentTime) {
        int time = Integer.parseInt(currentTime);

        System.out.println(time);
        List<Integer> limit = Arrays.asList(
                130,
                430,
                730,
                1030,
                1330,
                1630,
                1930);
        // 0.00->1.30
        if (time <= limit.get(0)) {
            return 0;//"p1"
            // 1.30->4.30
        } else if (time > limit.get(0) && time <= limit.get(1)) {
            return 1;//"p2"
            // 4.30->7.30
        } else if (time > limit.get(1) && time <= limit.get(2)) {
            return 2;//"p3"
            // 7.30->10.30
        } else if (time > limit.get(2) && time <= limit.get(3)) {
            return 3;//"p4"
            // 10.30->13.30
        } else if (time > limit.get(3) && time <= limit.get(4)) {
            return 4;//"p5"
            // 13.30->16.30
        } else if (time > limit.get(4) && time <= limit.get(5)) {
            return 5;//"p6"
            // 16.30->19.30
        } else if (time > limit.get(5) && time <= limit.get(6)) {
            return 6;//"p7"
        }
        // 19.30->00.00
        return 6;//"p8"
    }


}
