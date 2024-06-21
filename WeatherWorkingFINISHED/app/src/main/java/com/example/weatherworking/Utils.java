package com.example.weatherworking;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final  String OWM_CITY = "city";
    public static final  String OWM_CITY_NAME = "name";
    public static final  String OWM_COORD = "coord";

    public static final  String OWM_LATITUDE = "lat";
    public static final  String OWM_LONGITUDE = "lon";
    public static final  String OWM_LIST = "list";
    public static final  String OWM_PRESSURE = "pressure";
    public static final  String OWM_HUMIDITY = "humidity";
    public static final  String OWM_WINDSPEED = "speed";
    public static final  String OWM_WIND_DIRECTION = "deg";
    public static final  String  OWM_DT="dt";

    public static final String OWM_FILTERED_LIST = "filtered_list";

    public static final  String OWM_TEMPERATURE = "temp";
    public static final  String OWM_MAX = "temp_max";
    public static final  String OWM_MIN = "temp_min";

    public static String getDateFromTimestamp(long timestamp) {
        try {
            Date date = new Date(timestamp * 1000L);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static final  String OWM_WEATHER = "weather";
    public static final  String OWM_DESCRIPTION = "main";
    public static final  String OWM_WEATHER_MAIN = "main";
    public static final  String OWM_WEATHER_DESC = "description";
    public static final  String OWM_WEATHER_ID = "id";
    public static final  String OWM_WEATHER_ICON = "icon";

    public static final  String OWM_WIND = "wind";

    public static final  String OWM_DT_TXT = "dt_txt";

    public static String buildUrl(String city, String units){
        //https://api.openweathermap.org/data/2.5/forecast/?id=3189595&units=metric&appid=5459d1266c4c46f65063264f743f6b15

        String code="5459d1266c4c46f65063264f743f6b15";
        //String city="3189595";

        final String BASE_URL="https://api.openweathermap.org/data/2.5/forecast/?";
        final String QUERY_PARAM="id";
        final String UNITS_PARAM="units";
        final String CODE_PARAM="APPID";

        Uri builtUri=Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM,city)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(CODE_PARAM, code)
                .build();
        return builtUri.toString();
    }


}