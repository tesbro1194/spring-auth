package com.sparta.springauth.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
public class AuthController {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @GetMapping("/create-cookie")
    public String createCookie(HttpServletResponse res) {
        addCookie("Robbie Auth", res);
        return "createCookie";
    }

    @GetMapping("/get-cookie")
    // @CookieValue(AUTHORIZATION_HEADER) : () 안에 건내줄 쿠키의 Name 을 넣으면 변수 value 에 그 쿠키의 Value 가 들어감
    public String getCookie(@CookieValue(AUTHORIZATION_HEADER) String value) {
        System.out.println("value = " + value);

        return "getCookie : " + value;
    }

    @GetMapping("/create-session")
    public String createSession(HttpServletRequest req) {  // 서블릿에 요청 들어올 때 객체가 생성됨.
        // 세션이 존재할 경우 세션 반환, 없을 경우 새로운 세션을 생성한 후 반환
        HttpSession session = req.getSession(true);

        // 세션에 저장될 정보 Name - Value 를 추가합니다.
        session.setAttribute(AUTHORIZATION_HEADER, "Robbie Auth");

        return "createSession";
    }

    @GetMapping("/get-session")
    public String getSession(HttpServletRequest req) {
        // 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
        HttpSession session = req.getSession(false);

        if (session != null) {
            String value = (String) session.getAttribute(AUTHORIZATION_HEADER); // 가져온 세션에 저장된 Value 를 Name 을 사용하여 가져옵니다.
            System.out.println("value = " + value);

            return "getSession : " + value;
        } else {
            throw new IllegalArgumentException("인증 받으시오");
        }
    }

    public static void addCookie(String cookieValue, HttpServletResponse res) {
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue); // 쿠키의 Name-Value
            cookie.setPath("/");
            cookie.setMaxAge(30 * 60);

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}