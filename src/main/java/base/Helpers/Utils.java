package base.Helpers;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    public static String generateRandomString(int digits) {
        char[] characters = new char[] {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        };

        StringBuilder sb = new StringBuilder(digits);

        for (int i = 0; i < digits; i++) {
            sb.append(characters[getRandomNumber(0, 52)]);
        }

        return sb.toString();
    }

    public static boolean isUrlValid(String clientUrl) {
        try {
            new URL(clientUrl);
        } catch (MalformedURLException ex) {
            return false;
        }

        return true;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr;

        if (request == null) {
            return null;
        }

        remoteAddr = request.getHeader("X-FORWARDED-FOR");

        if (remoteAddr == null) {
            remoteAddr = request.getRemoteAddr();
        }

        return remoteAddr;
    }
}
