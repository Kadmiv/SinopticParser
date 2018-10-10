package com.gaijin.sinopticparser.views.fragments;

import com.gaijin.sinopticparser.components.ICity;

import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Kachulyak Ivan.
 * <p>
 * This class is clone of RealmCity
 * This class was create because RealmCity class and other RealmObjects
 * can't used in not main threads
 */
public class City implements ICity {

    private static final String WEATHER_IN = "Погода в ";

    @PrimaryKey
    private long id;
    /* Name of city */
    private String cityName;
    /* Region of city1 */
    private String cityRegion;
    /* Image of city or region - now not used*/
    private String mainImage;
    /* Ling from web site Sinoptioc for city */
    private String cityLink;
    /* Numbers of day, which will parsed from site for city */
    private int numberOfDay ;

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

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityRegion(String cityRegion) {
        this.cityRegion = cityRegion;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String toString() {
        return String.format("City name: %s Region: %s Link: %s", cityName, cityRegion, cityLink);
    }

    public String getWeatherIn(){
        return WEATHER_IN+cityName;
    }
}
