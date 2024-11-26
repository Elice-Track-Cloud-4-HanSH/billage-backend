package com.team01.billage.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;


public class CookieUtil {
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        cookie.setHttpOnly(true);  // HttpOnly 속성 설정
        cookie.setSecure(true);  // Secure 속성 설정
        // SameSite 속성 설정: 쿠키를 CSRF 공격에 대해 보호하기 위해 Lax로 설정
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setHttpOnly(true);  // HttpOnly 속성 설정
                cookie.setSecure(true);  // Secure 속성 설정
                cookie.setAttribute("SameSite", "Lax");
                response.addCookie(cookie);
            }
        }
    }

    public static void deleteTokenCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);

        cookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setAttribute("SameSite", "Lax");
        cookie.setPath("/"); // 동일한 경로
        cookie.setMaxAge(0); // 쿠키 삭제 설정

        response.addCookie(cookie); // 삭제할 쿠키를 response에 추가
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
