package com.sparta.springauth.filter;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.jwt.JwtUtil;
import com.sparta.springauth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j(topic = "AuthFilter")
//@Component
@Order(2)
public class AuthFilter implements Filter {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthFilter(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(url) &&
                (url.startsWith("/api/user") || url.startsWith("/css") || url.startsWith("/js"))
        ) {
            log.info("인증 처리를 하지 않는 url: {}", url);
            // 회원가입, 로그인 관련 API 는 인증 필요없이 요청 진행
            chain.doFilter(request, response); // 다음 Filter 로 이동
        } else {
            log.info("인증 처리를 진행하는 url: {}", url);
            // 나머지 API 요청은 인증 처리 진행
            // 토큰 확인, DispatcherServlet 보다 선행되기에 @CookieValue 사용 못함. 직접 쿠키를 뽑아오는 코드를 구현해야함
            String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest);

            if (StringUtils.hasText(tokenValue)) { // 토큰이 존재하면 검증 시작
                // JWT 토큰 substring
                String token = jwtUtil.substringToken(tokenValue);

                // 토큰 검증
                if (!jwtUtil.validateToken(token)) {
                    throw new IllegalArgumentException("Token Error");
                }

                // 토큰에서 사용자 정보 가져오기
                Claims info = jwtUtil.getUserInfoFromToken(token);

                User user = userRepository.findByUsername(info.getSubject()).orElseThrow(() ->
                        new NullPointerException("Not Found User")
                );

                request.setAttribute("setUser", user); // request 가 controller 로 넘어가는데, 그때 controller 에서 위의 객체 user 를 사용할 수 있도록 "user" 이름을 줌.
                chain.doFilter(request, response); // 다음 Filter 로 이동
            } else {
                throw new IllegalArgumentException("Not Found Token");
            }
        }
    }
}
/*
- Filter란 Web 애플리케이션에서 관리되는 영역으로 Client로 부터 오는 요청과 응답에 대해 최초/최종 단계의 위치하며
이를 통해 요청과 응답의 정보를 변경하거나 부가적인 기능을 추가할 수 있습니다.
- 주로 범용적으로 처리해야 하는 작업들, 예를들어 로깅 및 보안 처리에 활용합니다.
    - 또한 인증, 인가와 관련된 로직들을 처리할 수도 있습니다.
    - Filter를 사용하면 인증, 인가와 관련된 로직을 비즈니스 로직과 분리하여 관리할 수 있다는 장점이 있습니다.
- Filter는 한 개만 존재하는 것이 아니라 이렇게 여러 개가 Chain 형식으로 묶여서 처리될 수 있습니다.
*/