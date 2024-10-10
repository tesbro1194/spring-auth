package com.sparta.springauth.controller;

import com.sparta.springauth.dto.LoginRequestDto;
import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(@ModelAttribute SignupRequestDto requestDto) { // 생략 가능, 아래 login() 에서는 생략함
        userService.signup(requestDto);
        return "redirect:/api/user/login-page";
    }

    @PostMapping("/user/login") // HttpServletResponse res 에 관하여 아래 주석 확인
    public String login (LoginRequestDto requestDto, HttpServletResponse res) {
        try {
            userService.login(requestDto, res);
        } catch (Exception e) {
            return "redirect:/api/user/login-page?error";
        }
        return "redirect:/";
    }
}

/*
요약: 해당 메서드의 매개변수로 선언하기만 하면 사용 가능하다.
HttpServletResponse 객체는 서버가 HTTP 요청을 처리하는 동안 서블릿 컨테이너(예: 톰켓)에 의해 자동으로 생성
@PostMapping과 같은 요청 핸들러 메서드에서 HttpServletResponse 객체를 매개변수로 선언하면
스프링이 해당 요청에 대해 적절한 HttpServletResponse 객체를 자동으로 전달해줍니다.
    사용법:
1. 응답 해더 설정:
res.addHeader("Custom-Header", "Value");
2. 쿠키 설정:
Cookie cookie = new Cookie("token", jwtToken);
cookie.setHttpOnly(true); // 쿠키 보안 설정
cookie.setHttpOnly(true);  // 클라이언트에서 쿠키를 읽지 못하게 함 (보안 강화)
cookie.setPath("/");  // 쿠키의 유효 범위를 루트로 설정
res.addCookie(cookie);
3. 응답 상태 코드 설정:
res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
*/