package sch_helper.sch_manager.auth;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie createCookie(String key, String value, int expiredS) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiredS);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
