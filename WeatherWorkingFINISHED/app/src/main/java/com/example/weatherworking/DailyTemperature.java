package com.example.weatherworking;

public class DailyTemperature {

    private long dateTime;
    private int min;
    private int max;
    private double windspeed;
    private int temp;
    private int pressure;
    private int humidity;
    private String weatherIcon;
    private String weatherDesc;
    private String mainDesc;
    private String dtText;


    public DailyTemperature(long dateTime, int max, int min, int pressure, int humidity, String mainDesc, String dtText, double windspeed, int temp, String weatherDesc, String weatherIcon) {
        this.dateTime = dateTime;
        this.min = min;
        this.max = max;
        this.pressure = pressure;
        this.humidity = humidity;
        this.mainDesc = mainDesc;
        this.dtText = dtText;
        this.windspeed = windspeed;
        this.temp = temp;
        this.weatherDesc = weatherDesc;
        this.weatherIcon = weatherIcon;
    }

    public long getDateTime() {
        return dateTime;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public double getWindspeed() {
        return windspeed;
    }


    public int getTemp() {
        return temp;
    }


    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getMainDesc() {
        return mainDesc;
    }

    public String getDtText() {
        return dtText;
    }

    @Override
    public String toString() {
        return dtText + "\n" + max + "-" + min + "\n" + mainDesc;
    }
}
