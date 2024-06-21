package com.example.weatherworking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    String unit = "";
    String location;
    ListView lstView;
    Map<String, String> map;
    String selected_city;
    AutoCompleteTextView auto_text;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        auto_text = findViewById(R.id.auto_text_view);
        lstView = findViewById(R.id.list1);

        map = new HashMap<>();
        map.put("Subotica", "3189595");
        map.put("Beograd", "787607");
        map.put("Újvidék", "3194360");
        map.put("Budapest", "3054638");
        map.put("Szeged", "715429");

        List<String> list_city = new ArrayList<>(map.keySet());

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, list_city);
        auto_text.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_checked, list_city);
        lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lstView.setTextFilterEnabled(true);
        lstView.setAdapter(adapter2);

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_city = (String) adapterView.getItemAtPosition(i);
                location = map.get(selected_city);
                auto_text.setText(selected_city);
            }
        });
    }

    public void onClick(View view) {
        RadioButton optMetric = findViewById(R.id.opt_metric);
        RadioButton optImperial = findViewById(R.id.opt_imperial);

        if (optMetric.isChecked()) {
            unit = "metric";
        }

        if (optImperial.isChecked()) {
            unit = "imperial";
        }

        String auto_text2 = auto_text.getEditableText().toString();
        if (auto_text2.length() > 0 && map.containsKey(auto_text2)) {
            location = map.get(auto_text2);
            selected_city = auto_text2;
        }

        SharedPreferences.Editor editor = settings.edit();
        String location_xml = settings.getString("location", "");
        String unit_type = settings.getString("unit", "");

        if (!location_xml.equals(location) && location != null)
            editor.putString("location", location);

        if (!unit_type.equals(unit))
            editor.putString("unit", unit);

        editor.putString("city_name", selected_city);
        editor.apply();

        finish();
    }
}
