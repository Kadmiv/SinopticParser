package com.gaijin.sinopticparser.cards;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Kachulyak Ivan.
 */
public class City extends RealmObject{

    @Required
    private String cityName;
    private String cityRegion;
    private String mainImage;
    private String cityLink;

    private int numberOfDay = 5;

    public City() {
    }

    public City(String[] resultCity) {
        cityName = resultCity[0];
        cityRegion = resultCity[1];
        cityLink = resultCity[2];
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityRegion() {
        return cityRegion;
    }

    public String getCityLink() {
        return cityLink;
    }

    public void setCityLink(String cityLink) {
        this.cityLink = cityLink;
    }

    public int getNumberOfDay() {
        return numberOfDay;
    }

    public void setNumberOfDay(int numberOfDay) {
        this.numberOfDay = numberOfDay;
    }
}
