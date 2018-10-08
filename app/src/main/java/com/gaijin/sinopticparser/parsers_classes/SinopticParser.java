package com.gaijin.sinopticparser.parsers_classes;

import android.util.Log;

import com.gaijin.sinopticparser.views.fragments.SeparateTime;

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

    /**
     * Name of classes for find in HTML code of site
     */
    private final String LEFT_PART = "lSide";
    private final String IMAGE_OF_NOW = "img";
    private final String TEMP_OF_NOW = "today-temp";
    private final List<String> dayTimesList_1 = Arrays.asList("p1 ", "p2 bR ", "p3 ", "p4 bR ",
            "p5 ", "p6 bR ", "p7 ", "p8 ");
    final List<String> dayTimesList_2 = Arrays.asList("p1 bR ", "p2 bR ", "p3 bR ", "p4 ");

    private final String currentTag = "cur";
    private final List<String> nowList = Arrays.asList(IMAGE_OF_NOW, TEMP_OF_NOW);

    private final List<String> METRICS_1 = Arrays.asList("Температура: %sC", "чувствуется как: %sC",
            "Давление: %s мм", "Влажность: %s %%", "Ветер: %s", "Вероятность осадков: %s %%", "Время: %s");


    //View weather for now
    private SeparateTime nowView;

    private String lang = "ru";

    /**
     * This function parse separate time of day
     *
     * @param html - day page representation on html-format
     * @return - list of SeparateTime class which contains information for each separate time of  day
     */
    public ArrayList<SeparateTime> getTimesOfDay(String html) {
        Document doc = Jsoup.parse(html);

        Element body = doc.body();
        Elements now = body.getElementsByClass(LEFT_PART);

        // On this part we check lang for metrics
        List<String> metrics = METRICS_1;

        ArrayList<SeparateTime> views = parseAllDay(body, metrics);
        try {
            parseNow(now, nowList);
        } catch (Exception exe) {

        }
        //Log.d("MyLog", "SinopticParser class daySite.size() " + views.size());
        return views;
    }

    /**
     * Function parse html representation of day to separate time views
     *
     * @param body    - html representation of day
     * @param metrics - metrics for finding
     * @return - list of SeparateTime class which contains information for each separate time of day
     */
    private ArrayList<SeparateTime> parseAllDay(Element body, List<String> metrics) {

        ArrayList<SeparateTime> viewList = new ArrayList<>();

        List<String> dayTimesList = dayTimesList_1;
        String time = dayTimesList.get(0);

        /*Determining the number of times on the site page*/
        Elements timeOfDay = body.getElementsByClass(time);
        if (timeOfDay.size() == 0) {
            dayTimesList = dayTimesList_2;
        }

        for (int i = 0; i < dayTimesList.size(); i++) {

            time = dayTimesList.get(i);
            timeOfDay = body.getElementsByClass(time);
            SeparateTime newView;

            if (timeOfDay.size() == 0) {
                String changedItem = dayTimesList.get(i) + currentTag;
                dayTimesList.set(i, changedItem);
                time = dayTimesList.get(i);
                timeOfDay = body.getElementsByClass(time);
                if (timeOfDay.size() == 0) {
                    continue;
                } else {
                    newView = parseSeparateTime(timeOfDay, metrics);
                    nowView = newView;
                }
            } else {
                newView = parseSeparateTime(timeOfDay, metrics);
            }

            viewList.add(newView);
        }
        Log.d("MyLog", "Sinoptic parser 2 daySite.size() " + viewList.size());
        return viewList;
    }

    /**
     * This function parse information for one separate time of day information
     *
     * @param timeOfDay - separate time of day information
     * @param metrics   - metrics for finding
     * @return - SeparateTime object which contains all information for one separate time of day
     */
    private SeparateTime parseSeparateTime(Elements timeOfDay, List<String> metrics) {
        SeparateTime newView = new SeparateTime();

        /*Parse image*/
        String scrImage = parseSourceImage(timeOfDay);
        newView.setImage(scrImage);
        /*Parse short Description*/
        Element description = timeOfDay.get(1).select("div").first();
        String shortDescription = description.attr("title");
        newView.setShortDescription(shortDescription);
        /*Parse temperature*/
        String temp_1 = String.format(metrics.get(0), timeOfDay.get(2).text());
        newView.setTemp(temp_1);
        /*Parse temperature 2*/
        String temp_2 = String.format(metrics.get(1), timeOfDay.get(3).text());
        newView.setTemp_2(temp_2);
        /*Parse Atmospher Pressure*/
        String pressure = String.format(metrics.get(2), timeOfDay.get(4).text());
        newView.setAtmoPressure(pressure);
        /*Parse Humidity*/
        String humidity = String.format(metrics.get(3), timeOfDay.get(5).text());
        newView.setHumidity(humidity);
        /*Parse Wind*/
        Element wind = timeOfDay.get(6).select("div").first();
        String winds = wind.attr("data-tooltip");
        winds = String.format(metrics.get(4), winds);
        newView.setWind(winds);
        /*Parse Precipitation*/
        String precipit = timeOfDay.get(7).text();
        if (precipit.equals("-")) {
            precipit = "0";
        }
        precipit = String.format(metrics.get(5), precipit);
        newView.setPrecipitation(precipit);
        /*Parse Local time*/
        String localTime = timeOfDay.get(0).text().replace(" ", "");
        localTime = String.format(metrics.get(6), localTime);
        newView.setTime(localTime);

        return newView;
    }

    /**
     * This function change information in SeparateTime list for now time view
     *
     * @param nowView     - html representation of now weather information
     * @param dayViewList - separate view of times
     */
    private void parseNow(Elements nowView, List<String> dayViewList) {

        /*First parse image of weather*/
        String srcImage = parseSourceImage(nowView);
        Element image = nowView.select("img").first();
        String shortDescription = image.attr("alt");
        this.nowView.setImage(srcImage);
        this.nowView.setShortDescription(shortDescription);

        /*Next find temp of now*/
        Elements temp = nowView.get(0).getElementsByClass(dayViewList.get(1));
        String nowTemp = temp.get(0).text();
        this.nowView.setTemp(nowTemp);
    }

    /**
     * This function parse image
     *
     * @param elements - html representation for parsing
     * @return - link for image
     */
    private String parseSourceImage(Elements elements) {
        Element image = elements.select("img").first();
        return image.attr("src");
    }

}
