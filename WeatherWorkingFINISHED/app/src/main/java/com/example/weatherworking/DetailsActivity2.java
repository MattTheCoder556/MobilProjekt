package com.example.weatherworking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;



import org.w3c.dom.Text;

public class DetailsActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);
        TextView tv = findViewById(R.id.textView3);

        tv.setText("Version: 1.0");
    }
}