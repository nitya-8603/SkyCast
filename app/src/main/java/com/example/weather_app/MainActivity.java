package com.example.weather_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject mainObject = jsonObject.getJSONObject("main");

                // Extract temperature values
                double tempKelvin = mainObject.getDouble("temp");
                double feelsLikeKelvin = mainObject.getDouble("feels_like");
                double tempMaxKelvin = mainObject.getDouble("temp_max");
                double tempMinKelvin = mainObject.getDouble("temp_min");

                // Convert temperatures to Celsius
                double tempCelsius = tempKelvin - 273.15;
                double feelsLikeCelsius = feelsLikeKelvin - 273.15;
                double tempMaxCelsius = tempMaxKelvin - 273.15;
                double tempMinCelsius = tempMinKelvin - 273.15;

                // Format the temperature values to two decimal places
                String tempCelsiusFormatted = String.format("%.2f°C", tempCelsius);
                String feelsLikeCelsiusFormatted = String.format("%.2f°C", feelsLikeCelsius);
                String tempMaxCelsiusFormatted = String.format("%.2f°C", tempMaxCelsius);
                String tempMinCelsiusFormatted = String.format("%.2f°C", tempMinCelsius);

                // Replace the temperature values in the JSON string
                String weatherInfo = mainObject.toString();
                weatherInfo = weatherInfo.replace("\"temp\":" + tempKelvin, "\"temp\":" + tempCelsiusFormatted);
                weatherInfo = weatherInfo.replace("\"feels_like\":" + feelsLikeKelvin, "\"feels_like\":" + feelsLikeCelsiusFormatted);
                weatherInfo = weatherInfo.replace("\"temp_max\":" + tempMaxKelvin, "\"temp_max\":" + tempMaxCelsiusFormatted);
                weatherInfo = weatherInfo.replace("\"temp_min\":" + tempMinKelvin, "\"temp_min\":" + tempMinCelsiusFormatted);

                // Replace keys with emojis and format the string
                weatherInfo = weatherInfo.replace("temp", "🌡️  Temperature");
                weatherInfo = weatherInfo.replace("feels_like", "🌝  Feels Like");
                weatherInfo = weatherInfo.replace("temp_max", "🌤️  Temperature Max");
                weatherInfo = weatherInfo.replace("temp_min", "🌛  Temperature Min");
                weatherInfo = weatherInfo.replace("pressure", "🍃  Pressure");
                weatherInfo = weatherInfo.replace("humidity", "💧  Humidity");
                weatherInfo = weatherInfo.replace("sea_level", " 🌊  Sea Level");
                weatherInfo = weatherInfo.replace("grnd_level", "🏝️  Ground Level");
                weatherInfo = weatherInfo.replace("{", "");
                weatherInfo = weatherInfo.replace("}", "");
                weatherInfo = weatherInfo.replace(",", "\n\n");
                weatherInfo = weatherInfo.replace(":", " : ");

                // Display the formatted weather information
                show.setText(weatherInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        final String[] temp = {""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                try {
                    if (city != null) {
                        url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=bfd4cd35a4cc48a09a24f4015112346c";
                    } else {
                        Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp[0] = task.execute(url).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (temp[0] == null) {
                    show.setText("Cannot fetch weather details");
                }


            }
        });
    }
}