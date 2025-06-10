package edp.wat.edu.pl.projectkb.session;

import lombok.Getter;
import lombok.Setter;

public class SessionContext {
    @Getter
    @Setter
    private static Long loggedInUserId;
    @Getter
    @Setter
    private static String loggedInUserEmail;

    public static void clear(){
        loggedInUserId = null;
    }
}
