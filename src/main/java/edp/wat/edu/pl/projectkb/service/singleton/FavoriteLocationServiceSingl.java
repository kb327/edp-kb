package edp.wat.edu.pl.projectkb.service.singleton;

import edp.wat.edu.pl.projectkb.service.FavoriteLocationService;

public class FavoriteLocationServiceSingl {
    private static FavoriteLocationService instance;

    private FavoriteLocationServiceSingl() {}

    public static FavoriteLocationService getInstance() {
        if (instance == null) {
            instance = new FavoriteLocationService();
        }
        return instance;
    }
}

