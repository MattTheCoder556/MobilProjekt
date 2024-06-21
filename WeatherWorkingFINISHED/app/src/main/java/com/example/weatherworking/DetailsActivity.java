package com.example.weatherworking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private LinearLayout detailsLayout;
    private Button toggleButton;
    private boolean isDetailsVisible = false;
    private SharedPreferences sh;
    long selectedDate;
    ListView list2;
    private List<DailyTemperature> weatherList;

    int max;
    int min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsLayout = findViewById(R.id.detailsLayout);
        toggleButton = findViewById(R.id.toggleButton);
        list2 = findViewById(R.id.lv5);

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        SharedPreferences settings = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String cityName = settings.getString("city_name", "City");

        TextView nameTV = findViewById(R.id.Cityname);
        nameTV.setText(cityName);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDetailsVisible) {
                    detailsLayout.setVisibility(View.GONE);
                    toggleButton.setText("Show Details");
                } else {
                    detailsLayout.setVisibility(View.VISIBLE);
                    toggleButton.setText("Hide Details");
                }
                isDetailsVisible = !isDetailsVisible;
            }
        });

        selectedDate = intent.getLongExtra(Utils.OWM_DT, 0);

        TextView tempTV = findViewById(R.id.temp);
        TextView maxTV = findViewById(R.id.max);
        ImageView maxIV = findViewById(R.id.maxView);
        TextView minTV = findViewById(R.id.min);
        ImageView minIV = findViewById(R.id.minView);
        TextView pressureTV = findViewById(R.id.pressure);
        TextView humidityTV = findViewById(R.id.humidity);
        TextView windspeedTV = findViewById(R.id.windspeed);
        TextView descriptionTV = findViewById(R.id.description);
        TextView fullDescTV = findViewById(R.id.fullDesc);

        int temp = intent.getIntExtra(Utils.OWM_TEMPERATURE, 0);
        tempTV.setText(String.valueOf(temp) + getString(R.string.degree));

        int max = intent.getIntExtra(Utils.OWM_MAX, 0);
        maxTV.setText(String.valueOf(max) + getString(R.string.degree));

        int min = intent.getIntExtra(Utils.OWM_MIN, 0);
        minTV.setText(String.valueOf(min) + getString(R.string.degree));

        int press = intent.getIntExtra(Utils.OWM_PRESSURE, 0);
        pressureTV.setText("Pressure: " + String.valueOf(press) + "Pa");

        int hum = intent.getIntExtra(Utils.OWM_HUMIDITY, 0);
        humidityTV.setText("Humidity: " + String.valueOf(hum) + "%");

        double wind = intent.getDoubleExtra(Utils.OWM_WINDSPEED, 0);
        windspeedTV.setText("Wind Speed: " + String.valueOf(wind) + "km/h");

        String desc = intent.getStringExtra(Utils.OWM_DESCRIPTION);
        if (desc.equals("Clouds")) {
            descriptionTV.setText(getString(R.string.desc1));
        } else if (desc.equals("Clear")) {
            descriptionTV.setText(getString(R.string.desc2));
        }
        fullDescTV.setText(intent.getStringExtra(Utils.OWM_WEATHER_DESC));

        weatherList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String location = sh.getString("location", "3189595");
        String unit = sh.getString("unit", "metric");

        if (haveNetworkConnection()) {
            String url = Utils.buildUrl(location, unit);
            download(url);
        } else {
            alertDialog();
        }
    }

    private void download(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DetailsActivity", "Error downloading data: " + error.getMessage());
                alertDialog();
            }
        });

        queue.add(stringRequest);
    }

    private void parseJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray dailyArr = object.getJSONArray(Utils.OWM_LIST);

            weatherList = new ArrayList<>();

            for (int i = 0; i < dailyArr.length(); i++) {
                JSONObject dailyData = dailyArr.getJSONObject(i);
                long dt = dailyData.getLong(Utils.OWM_DT);

                JSONObject main = dailyData.getJSONObject(Utils.OWM_WEATHER_MAIN);
                min = main.getInt(Utils.OWM_MIN);
                max = main.getInt(Utils.OWM_MAX);
                int pressure = main.getInt(Utils.OWM_PRESSURE);
                int humidity = main.getInt(Utils.OWM_HUMIDITY);
                int temp = main.getInt(Utils.OWM_TEMPERATURE);

                JSONArray weatherData = dailyData.getJSONArray(Utils.OWM_WEATHER);
                JSONObject weatherObject = weatherData.getJSONObject(0);
                String weatherMain = weatherObject.getString(Utils.OWM_WEATHER_MAIN);
                String weatherDescription = weatherObject.getString(Utils.OWM_WEATHER_DESC);
                String weatherIcon = weatherObject.getString(Utils.OWM_WEATHER_ICON);

                double windSpeed = dailyData.getJSONObject(Utils.OWM_WIND).getDouble(Utils.OWM_WINDSPEED);
                String dtText = dailyData.getString(Utils.OWM_DT_TXT);

                DailyTemperature dailyTemperature = new DailyTemperature(dt, max, min, pressure, humidity, weatherMain, dtText, windSpeed, temp, weatherDescription, weatherIcon);

                weatherList.add(dailyTemperature);
            }

            fillListView();

        } catch (JSONException e) {
            Log.e("DetailsActivity", "Error parsing JSON: " + e.getMessage());
        }
    }

    private void fillListView() {
        Log.e("DetailsActivity", "Timestamp: " + selectedDate);
        List<DailyTemperature> filteredList = new ArrayList<>();

        String selectedDateString = "";

        for (DailyTemperature temperature : weatherList) {
            if (temperature.getDateTime() == selectedDate) {

                selectedDateString = temperature.getDtText().substring(0, 10);
                break;
            }
        }

        for (DailyTemperature temperature : weatherList) {
            if (temperature.getDtText().startsWith(selectedDateString)) {
                filteredList.add(temperature);
            }
        }

        WeatherAdapter adapter = new WeatherAdapter(this, filteredList);
        list2.setAdapter(adapter);
    }

    private void alertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Weather");
        dialog.setMessage("No network connection. Please connect to the internet.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
