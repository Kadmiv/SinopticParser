package com.gaijin.sinopticparser.cards;

/**
 * Created by Kachulyak Ivan.
 */
public class WeatherView {

    private String image = "";
    private String temp = "";
    private String temp_2 = "";
    private String atmoPressure = "";
    private String humidity = "";
    private String wind = "";
    private String precipitation = "";
    private String shortDescription = "";

    public WeatherView() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
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


    public String toString() {
        return String.format(" Description: %s\n Image: %s\n Temp 1: %s\n Temp 2: %s\n Atmo Pressure: %s\n " +
                        "Humidity: %s\n Wind: %s\n Precipitation: %s",
                getShortDescription(), getImage(), getTemp(), getTemp_2(), getAtmoPressure(), getHumidity(), getWind(),getPrecipitation());
    }
}
