package com.gaijin.sinopticparser.cards;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Kachulyak Ivan.
 */
public class RealmCity extends RealmObject implements ICity {

    @Required
    private String cityName;
    private String cityRegion;
    private String mainImage;
    private String cityLink;

    private int numberOfDay = 2;

    public RealmCity() {
    }

    public RealmCity(String[] resultCity) {
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

    public String toString() {
        return String.format("City name: %s Region: %s Link: %s", cityName, cityRegion, cityLink);
    }

    public void clone(City city) {
        city.setCityLink(this.cityLink);
        city.setCityName(this.cityName);
        city.setCityRegion(this.cityRegion);
        city.setMainImage(this.mainImage);
        city.setNumberOfDay(this.numberOfDay);
    }
}
