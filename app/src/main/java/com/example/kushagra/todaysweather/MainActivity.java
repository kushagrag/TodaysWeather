package com.example.kushagra.todaysweather;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final String API_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric&appid=6532f2ee68ccc53a87b2c9d7b4d9eee4&q=";
    public String city_arg;
    public TextView text;
    public String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.cityWeather);
        final Spinner spinner = (Spinner) findViewById(R.id.citySelector);
        spinner.setOnItemSelectedListener(this);

        final ArrayList<String> list = new ArrayList<String>();
        try {
            AssetManager am = this.getAssets();
            InputStreamReader is = new InputStreamReader(am.open("City_list.json"));
            BufferedReader streamReader = new BufferedReader(is);
            String response = streamReader.readLine();
            while((response = streamReader.readLine()) != null)
            {
                JSONObject json = new JSONObject(response);
                list.add(json.getString("name"));
            }
            Collections.sort(list);
            }catch (Exception e){
            Log.v("kush",e.toString());
        }

    final StableAdapterClass adapter = new StableAdapterClass(this,
                android.R.layout.simple_list_item_1, list);
       spinner.setAdapter(adapter);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String city = parent.getItemAtPosition(pos).toString();
        Log.v("kush",city);
        getWeather(city);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        String city = parent.getItemAtPosition(0).toString();
        Log.v("kush",city);
    }

    public void getWeather(String city) {
        city_arg = city;
        new Thread(new Runnable() {
            @Override
            public void run() {

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    InputStream is = null;

                    try {
                        URL url = new URL(API_URL + city_arg);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.connect();
                        BufferedReader streamReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String response = streamReader.readLine();
                        JSONObject json;

                        try {
                            json = new JSONObject(response);
                            JSONObject json_main = json.getJSONObject("main");
                            result = "City:   " + city_arg + "\n\nTemperature:   " + json_main.getString("temp")
                                    + "\n\nMax_Temperature:   " + json_main.getString("temp_max")
                                    + "\n\nMin_Temperature:   " + json_main.getString("temp_min")
                                    + "\n\nHumidity:   " + json_main.getString("humidity");
                            text.post(new Runnable() {
                                          @Override
                                          public void run() {
                                              text.setText(result);
                                          }
                                      }
                            );
                        }catch (org.json.JSONException e)
                        {
                            Log.v("Exception", e.toString());
                        }

                    }catch (java.io.IOException e){
                        Log.v("Exception", e.toString());
                    }
                }

            }
        }).start();
    }

}
