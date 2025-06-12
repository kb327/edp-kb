package edp.wat.edu.pl.projectkb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.WeatherList;
import edp.wat.edu.pl.projectkb.model.weatherApiModel.LocationResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Arrays;

public class OpenWeatherApiService {
    public OpenWeatherApiService() {}

    public WeatherList getWeatherList(String city) throws IOException, InterruptedException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String uri = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=ed70f69d4a0c293274f219fc888272d4&units=metric&lang=pl",
                encodedCity
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), WeatherList.class);
    }

    public List<LocationResult> searchLocations(String city) throws IOException, InterruptedException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String uri = String.format(
                "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=5&appid=ed70f69d4a0c293274f219fc888272d4",
                encodedCity
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(response.body(), LocationResult[].class));
    }

    public WeatherList getWeatherByCoordinates(double lat, double lon) throws IOException, InterruptedException {
        String uri = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?lat=%.4f&lon=%.4f&appid=ed70f69d4a0c293274f219fc888272d4&units=metric&lang=pl",
                lat, lon
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), WeatherList.class);
    }
}
