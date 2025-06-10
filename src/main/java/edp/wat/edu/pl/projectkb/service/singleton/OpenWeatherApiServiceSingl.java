package edp.wat.edu.pl.projectkb.service.singleton;

import edp.wat.edu.pl.projectkb.service.OpenWeatherApiService;


//Singleton
public class OpenWeatherApiServiceSingl {
    private static OpenWeatherApiService instance;

    private OpenWeatherApiServiceSingl() {}

    public static OpenWeatherApiService getInstance() {
        if (instance == null) {
            instance = new OpenWeatherApiService();
        }
        return instance;
    }
}
