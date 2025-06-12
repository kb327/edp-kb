package edp.wat.edu.pl.projectkb.service.singleton;

import edp.wat.edu.pl.projectkb.service.FavoriteLocationService;
import edp.wat.edu.pl.projectkb.service.WeatherHistoryService;

public class WeatherHistoryServiceSingl {
    private static WeatherHistoryService instance;

    private WeatherHistoryServiceSingl() {}

    public static WeatherHistoryService getInstance() {
        if (instance == null) {
            instance = new WeatherHistoryService();
        }
        return instance;
    }
}
