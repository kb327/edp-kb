package edp.wat.edu.pl.projectkb.service.singleton;

import edp.wat.edu.pl.projectkb.service.UserService;

public class UserServiceSingl {
    private static UserService instance;

    private UserServiceSingl() {}

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
}
