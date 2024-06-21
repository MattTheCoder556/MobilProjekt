package com.example.weatherworking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WeatherAdapter extends ArrayAdapter<DailyTemperature> {

    private final Context context;
    private final List<DailyTemperature> weatherItems;

    public WeatherAdapter(Context context, List<DailyTemperature> weatherItems) {
        super(context, R.layout.textview_list, weatherItems);
        this.context = context;
        this.weatherItems = weatherItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.textview_list, parent, false);
        }

        DailyTemperature item = weatherItems.get(position);
        DailyTemperature currentWeather = weatherItems.get(position);


        ImageView iconImageView = convertView.findViewById(R.id.weatherIcon);
        TextView weatherInfoTextView = convertView.findViewById(R.id.weatherInfo);

        long unixTimestampSeconds = item.getDateTime();
        Date dateTime = new Date(unixTimestampSeconds * 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedTime = sdf.format(dateTime);

        String weatherInfo = item.getDtText().substring(0, 10) + "\n" + formattedTime +"\n"+ item.getMax() + "°/" + item.getMin() + "°";
        weatherInfoTextView.setText(weatherInfo);

        String iconUrl = "https://openweathermap.org/img/wn/" + item.getWeatherIcon() + "@2x.png";
        Glide.with(context)
                .load(iconUrl)
                .into(iconImageView);

        return convertView;
    }
}
