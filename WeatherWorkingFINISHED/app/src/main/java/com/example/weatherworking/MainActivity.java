package com.example.weatherworking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DailyTemperature[] dailyTemperaturesArr = null;
    String[] listToMainActivity = null;
    ListView list1;
    SharedPreferences sh;
    int[] dailyIndices = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.weatherlogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        list1 = findViewById(R.id.list_view);

        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int startIndex = dailyIndices[i];
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(Utils.OWM_DT, dailyTemperaturesArr[startIndex].getDateTime());
                intent.putExtra(Utils.OWM_DT_TXT, dailyTemperaturesArr[startIndex].getDtText());
                intent.putExtra(Utils.OWM_MAX, dailyTemperaturesArr[startIndex].getMax());
                intent.putExtra(Utils.OWM_MIN, dailyTemperaturesArr[startIndex].getMin());
                intent.putExtra(Utils.OWM_WINDSPEED, dailyTemperaturesArr[startIndex].getWindspeed());
                intent.putExtra(Utils.OWM_PRESSURE, dailyTemperaturesArr[startIndex].getPressure());
                intent.putExtra(Utils.OWM_HUMIDITY, dailyTemperaturesArr[startIndex].getHumidity());
                intent.putExtra(Utils.OWM_DESCRIPTION, dailyTemperaturesArr[startIndex].getMainDesc());
                intent.putExtra(Utils.OWM_TEMPERATURE, dailyTemperaturesArr[startIndex].getTemp());
                intent.putExtra(Utils.OWM_WEATHER_DESC, dailyTemperaturesArr[startIndex].getWeatherDesc());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateWeather();
    }

    private void download(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("11111", response);
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("12345", "Baj van!");
            }
        });

        queue.add(stringRequest);
    }

    private void parseJson(String json) {
        Log.d("0000", "B" + json);
        int arraySize = 0;
        try {
            JSONObject object = new JSONObject(json);
            JSONArray dailyArr = object.getJSONArray(Utils.OWM_LIST);
            arraySize = dailyArr.length();
            dailyTemperaturesArr = new DailyTemperature[arraySize];
            Log.d("2222", "" + arraySize);
            for (int i = 0; i < arraySize; i++) {
                JSONObject dailyData = dailyArr.getJSONObject(i);
                JSONObject main = dailyData.getJSONObject("main");
                int min = main.getInt("temp_min");
                int max = main.getInt("temp_max");
                int temp = main.getInt("temp");
                int pressure = main.getInt("pressure");
                int humidity = main.getInt("humidity");
                JSONArray weatherData = dailyData.getJSONArray("weather");
                JSONObject weatherObject = weatherData.getJSONObject(0);
                String weatherMain = weatherObject.getString("main");
                String weatherDescription = weatherObject.getString("description");
                String weatherIcon = weatherObject.getString("icon");
                int weatherID = weatherObject.getInt("id");
                JSONObject windData = dailyData.getJSONObject("wind");
                double windSpeed = windData.getDouble("speed");
                int windDeg = windData.getInt("deg");
                long dt = dailyData.getLong("dt");
                String dtText = dailyData.getString("dt_txt");
                dailyTemperaturesArr[i] = new DailyTemperature(dt, max, min, pressure, humidity, weatherMain, dtText, windSpeed, temp, weatherDescription, weatherIcon);
                Log.d("bakker", dailyTemperaturesArr[i].toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataSelection();
    }

    private void dataSelection() {
        String temp = dailyTemperaturesArr[0].getDtText().substring(0, 10);
        int sameDay = 0;
        for (int i = 0; i < 8; i++) {
            String tempDay = dailyTemperaturesArr[i].getDtText().substring(0, 10);
            if (tempDay.equals(temp))
                sameDay++;
        }
        listToMainActivity = new String[5];
        dailyIndices = new int[5];
        int j = 0;
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                listToMainActivity[0] = dataForListView1(0, sameDay);
                dailyIndices[0] = 0;
                j = sameDay;
            } else {
                listToMainActivity[i] = dataForListView1(j, j + 8);
                dailyIndices[i] = j;
                j = j + 8;
            }
        }
        fillListView2();
    }

    private String dataForListView1(int firstDay, int lastDay) {
        int min = dailyTemperaturesArr[firstDay].getMin();
        int max = dailyTemperaturesArr[firstDay].getMax();
        for (int i = firstDay; i < lastDay; i++) {
            if (max < dailyTemperaturesArr[i].getMax())
                max = dailyTemperaturesArr[i].getMax();
            if (min > dailyTemperaturesArr[i].getMin())
                min = dailyTemperaturesArr[i].getMin();
        }
        return dailyTemperaturesArr[firstDay].getDtText().substring(0, 10) + "\n" + String.valueOf(max) + "°/" + String.valueOf(min) + "°";
    }

    private void fillListView2() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.textview_list,
                R.id.weatherInfo,
                listToMainActivity
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView weatherInfo = view.findViewById(R.id.weatherInfo);
                ImageView weatherIcon = view.findViewById(R.id.weatherIcon);

                String info = listToMainActivity[position];
                weatherInfo.setText(info);

                String weatherIconUrl = "https://openweathermap.org/img/wn/" + dailyTemperaturesArr[position].getWeatherIcon() + "@2x.png";
                Glide.with(MainActivity.this).load(weatherIconUrl).into(weatherIcon);

                return view;
            }
        };
        list1.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_details) {
            Intent i = new Intent(this, DetailsActivity2.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String location = sh.getString("location", "3189595");
        String unit = sh.getString("unit", "metric");

        if (haveNetworkConnection()) {
            String url = Utils.buildUrl(location, unit);
            Log.d("3333", url);

            download(url);
        } else {
            alertDialog();
        }
    }

    private void alertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Weatheres");
        dialog.setMessage("Kapcsolódjon...");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent((Settings.ACTION_WIFI_SETTINGS)));
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        AlertDialog alertdialog = dialog.create();
        alertdialog.show();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo WifiStatus = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileStatus = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (WifiStatus.isConnected())
            haveConnectedWifi = true;
        if (mobileStatus.isConnected())
            haveConnectedMobile = true;

        return haveConnectedWifi || haveConnectedMobile;
    }
}
