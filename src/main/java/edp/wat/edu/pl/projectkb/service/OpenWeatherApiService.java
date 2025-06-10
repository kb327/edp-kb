package edp.wat.edu.pl.projectkb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.WeatherList;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class OpenWeatherApiService {
    public OpenWeatherApiService() {
    }

    public WeatherList getWeatherList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openweathermap.org/data/2.5/forecast?q=Warsaw&appid=ed70f69d4a0c293274f219fc888272d4"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readValue(response.body(), WeatherList.class);
    }
}
