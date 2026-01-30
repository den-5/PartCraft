package com.partcraft.back.util;

import com.partcraft.back.config.AppProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    private final AppProperties appProperties;

    public CookieUtils(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = buildCookie("accessToken", accessToken, appProperties.getCookie().getAccessTokenMaxAge());
        ResponseCookie refreshCookie = buildCookie("refreshToken", refreshToken, appProperties.getCookie().getRefreshTokenMaxAge());

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = buildCookie("accessToken", "", 0);
        ResponseCookie refreshCookie = buildCookie("refreshToken", "", 0);
        ResponseCookie sessionCookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());
    }

    private ResponseCookie buildCookie(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(appProperties.getCookie().isSecure())
                .path("/")
                .sameSite(appProperties.getCookie().getSameSite())
                .maxAge(maxAge)
                .build();
    }
}
