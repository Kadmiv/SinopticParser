package com.gaijin.sinopticparser.parsers_classes;


import android.util.Log;

import com.gaijin.sinopticparser.views.fragments.WeatherView;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Kachulyak Ivan.
 * <p>
 * This class contain all information for day
 */
public class DaySite {

    /**Name of classes for find in HTML code of site */
    public static final String HEAD_OF_DAY = "main loaded";
    // public static final String ICON_OF_DAY = "weatherIco d100";
    public static final String TEMP_OF_DAY = "temperature";
    public static final String MIN_OF_DAY = "min";
    public static final String MAX_OF_DAY = "max";
    private final String DAY_INFO = "infoDaylight";
    private final String ALL_DAY_DESCRIPTION = "wDescription";

     /** Variable which contains temperature of day*/
    private String minTemp;
    private String maxTemp;
     /** Variable which contains image weather of day*/
    private String mainImage;
     /** Variable which describes the length of the day*/
    private String timeOfDay;
    private String iconOfDay;
     /** Variable which contains weather description of the day*/
    private String allDayDescription;
     /** Variable which contains weather for separate time*/
    private ArrayList<WeatherView> weatherOnDay;

    public DaySite() {
    }

    /**
     * This function parse all information for day
     *
     * @param body - day page representation on html-format
     */
    public void parseDayInfo(Element body) {

        Element dayHead = body.getElementsByClass(HEAD_OF_DAY).first();

        /*Find and pars icon of day weather*/
        iconOfDay = parseDaySourceImage(dayHead);

        /*Load min and max temperature of day*/
        Element temperature = dayHead.getElementsByClass(TEMP_OF_DAY).first();
        minTemp = parseDayTemperature(temperature, MIN_OF_DAY);
        maxTemp = parseDayTemperature(temperature, MAX_OF_DAY);

        /*Load duration of the day*/
        timeOfDay = parseLengthOfDay(body, DAY_INFO);

        /*Find weather description for a day*/
        allDayDescription = parseDayDescription(body, ALL_DAY_DESCRIPTION);

        /*Load weather upload for a few days*/
        SinopticParser sinoptic = new SinopticParser();
        //sinoptic.setLanguach("");

        System.out.println(toString());

        weatherOnDay = sinoptic.getTimesOfDay(body.html());
    }

    private String parseDayTemperature(Element temperature, String type) {
        Element temp = temperature.getElementsByClass(type).first();
        return temp.text();
    }


    private String parseDayDescription(Element body, String all_day_description) {
        Element parametersList = body.getElementsByClass(all_day_description).first();
        String text = parametersList.getElementsByClass("description").text();
        //System.out.println(text);
        return text;
    }


    private String parseLengthOfDay(Element body, String day_info) {
        /*Next find day time info*/
        Elements timeInfo = body.getElementsByClass(day_info);
        return timeInfo.text();
    }

    private String parseDaySourceImage(Element now) {
        Element image = now.select("img").first();
        return image.attr("src");
    }

    public ArrayList<WeatherView> getWeatherOnDay() {
        Log.d("MyLog", "DaySite class daySite.size() " + weatherOnDay.size());
        return weatherOnDay;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    @Override
    public String toString() {

        String info = String.format(" ICON: %s\n MIN: %s\n MAX: %s\n TIME OF DAY: %s\n DAY DESCRIPTION: %s\n ",
                iconOfDay, minTemp, maxTemp, timeOfDay, allDayDescription);

        if (weatherOnDay != null) {
            for (int i = 0; i < weatherOnDay.size(); i++) {
                info += String.format("\n\nTime of day %s\n", i + 1) + weatherOnDay.get(i).toString();
            }
        }

        return info;
    }
}
