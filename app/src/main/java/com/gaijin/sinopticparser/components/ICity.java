package com.gaijin.sinopticparser.components;

/**
 * Created by Kachulyak Ivan.
 */
public interface ICity {
    String getCityName();

    String getCityRegion();

    String getCityLink();

    void setCityLink(String cityLink);

    int getNumberOfDay();

    void setNumberOfDay(int numberOfDay);
}
