package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    TextView cityName;
    TextView result;

    private Retrofit retrofit;
    private WeatherApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Initialize Retrofit instance
        Gson gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Create API service interface using Retrofit
        apiService = retrofit.create(WeatherApiService.class);

        // Initialize views
        cityName = findViewById(R.id.cityName);
        result = findViewById(R.id.resut);
    }

    public void search(View view) {
        String cName = cityName.getText().toString();

        // Make API call to fetch weather data
        Call<WeatherResponse> call = apiService.getWeather(cName, "15a7403ff94164b39a76008df5594957");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();

                    // Extract weather information from response
                    // Update UI with weather information
                    updateUI(weatherResponse);
                } else {
                    // Handle unsuccessful API response
                    Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle API call failure
                Log.e("WeatherActivity", "Error fetching weather data", t);
                Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(WeatherResponse weatherResponse) {
        // Update UI with weather information
        if (weatherResponse != null && weatherResponse.getMain() != null && weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
            // Display temperature
            MainData mainData = weatherResponse.getMain();
            double temperatureKelvin = mainData.getTemp();
            double temperatureCelsius = temperatureKelvin - 273.15;
            result.setText("Temperature: " + String.format("%.2f", temperatureCelsius) + "°C");

            // Display humidity
            double humidity = mainData.getHumidity();
            result.append("\nHumidity: " + humidity + "%");

            // Display weather information
            WeatherData weatherData = weatherResponse.getWeather().get(0); // Assuming you want the first weather data
            String weatherMain = weatherData.getMain();
            String weatherDescription = weatherData.getDescription();

            // Update TextView with weather information
            // Example: Display weather main and description
            String weatherInfo = "Weather: " + weatherMain + ", " + weatherDescription;
            // Append more weather info if needed
            result.append("\n" + weatherInfo);
        }
    }


}
