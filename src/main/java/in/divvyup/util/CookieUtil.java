package in.divvyup.util;

import java.time.ZonedDateTime;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import jakarta.servlet.http.Cookie;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class CookieUtil {
    public static Cookie createCookie(CookieConfig cookieConfig) {
        Cookie cookie = new Cookie(cookieConfig.getName(), cookieConfig.getValue());
        cookie.setComment("__SAME_SITE_NONE__");
        if (cookieConfig.isSecure()) {
            cookie.setSecure(true);
        }
        cookie.setPath(cookieConfig.getPath());
        int maxAge = 0;
        if (Objects.nonNull(cookieConfig.getExpiresAt())) {
            maxAge = new Long(cookieConfig.getExpiresAt().toEpochSecond() - DateTimeUtil.currentTime().toEpochSecond()).intValue();
        }
        cookie.setMaxAge(maxAge);
        if (StringUtils.isNotBlank(cookieConfig.getDomain())) {
            cookie.setDomain(cookieConfig.getDomain());
        }
        return cookie;
    }

    @Getter
    public static class CookieConfig {
        private String name;

        private String value;

        private String path;

        private boolean secure;

        @Setter
        private String domain;

        private ZonedDateTime expiresAt;

        @Builder
        private CookieConfig(String name, String value, String path, boolean secure, String domain, ZonedDateTime expiresAt) {
            this.name = name;
            this.value = value;
            this.path = path;
            this.secure = secure;
            this.expiresAt = expiresAt;
            this.domain = domain;
        }
    }
}
