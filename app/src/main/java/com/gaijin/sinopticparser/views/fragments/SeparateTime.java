package com.gaijin.sinopticparser.views.fragments;

/**
 * Created by Kachulyak Ivan.
 * <p>
 * This class contains information of weather for separate time of day
 */
public class SeparateTime {

    /*Variable which contains link for weather type description*/
    private String image = "";
    /*Variable which contains time of separate part */
    private String time = "";
    /*Variables which contains weather parameters*/
    private String temp_1 = "";
    private String temp_2 = "";
    private String atmoPressure = "";
    private String humidity = "";
    private String wind = "";
    private String precipitation = "";
    /*Variable which contains short description of weather*/
    private String shortDescription = "";
    private String windDirection;

    public SeparateTime() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = "http:" + image;
    }

    public String getTemp_1() {
        return temp_1;
    }

    public void setTemp_1(String temp_1) {
        this.temp_1 = temp_1;
    }

    public String getTemp_2() {
        return temp_2;
    }

    public void setTemp_2(String temp_2) {
        this.temp_2 = temp_2;
    }

    public String getAtmoPressure() {
        return atmoPressure;
    }

    public void setAtmoPressure(String atmoPressure) {
        this.atmoPressure = atmoPressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String toString() {
        return String.format(" Description: %s\n Image: %s\n Temp 1: %s\n Temp 2: %s\n Atmo Pressure: %s\n " +
                        "Humidity: %s\n Wind: %s\n WindDirection: %s\nPrecipitation: %s",
                getShortDescription(), getImage(), getTemp_1(), getTemp_2(), getAtmoPressure(),
                getHumidity(), getWind(), getWindDirection(), getPrecipitation());
    }
}
