package com.sparta.springauth.jwt;

import com.sparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    // Header KEY 값 즉 쿠키의 Name값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY , 권한 구분, 권한을 읽을 때
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자, `Bearer `: 해당 값이 토큰임을 알리는 접두사, 컨벤션
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분 기준 : ms

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey , import org.springframework.beans.factory.annotation.Value;
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    // @PostConstruct: 이 에노테이션이 붙은 메서드는 Spring 컨테이너가 해당 빈을 생성하고 모든 의존성이 주입된 후 자동으로 실행됩니다.
    // 반환값이 없어야 하며, 파라미터를 받지 않는 것이 원칙입니다.
    // 주요 사용처: 리소스 초기화, 데이터 세팅, 로그 남기기 // 아래에선 변수 key 를 세팅함.
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);  // 디코딩
        key = Keys.hmacShaKeyFor(bytes); // hmacShaKeyFor() 를 통해 Key 로 케스팅
    }

    // JWT 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한, 인자: Key, Value
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간 , date.getTime() : 현재 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }
    // JWT Cookie 에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value , import jakarta.servlet.http.Cookie;
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    // JWT 토큰 자르기: 앞에 붙은 `Bearer ` 을 떼어내기 위해
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}


/*
JWT :
JWT(Json Web Token)란 JSON 포맷을 이용하여 사용자에 대한 속성을 저장하는 Claim 기반의 Web Token 입니다.
일반적으로 쿠키 저장소를 사용하여 JWT를 저장합니다.
사용자 로그인 정보를 Server 에 저장하지 않고, Client 에 로그인 정보를 JWT 로 암호화하여 저장 → JWT 통해 인증/인가
모든 서버에서 동일한 Secret Key 소유, Secret Key 통한 암호화 / 위조 검증

Util 클래스란 특정 매개 변수(파라미터)에 대한 작업을 수행하는 메서드들이 존재하는 클래스를 뜻합니다.
쉽게 설명하자면 다른 객체에 의존하지 않고 하나의 모듈로서 동작하는 클래스라고 생각하시면 좋습니다.
우리는 JWT 관련 기능들을 가진 JwtUtil이라는 클래스를 만들어 JWT 관련 기능을 수행시킬 예정입니다.
<JWT 관련 기능>
1. JWT 생성
2. 생성된 JWT를 Cookie에 저장
3. Cookie에 들어있던 JWT 토큰을 Substring
4. JWT 검증
5. JWT에서 사용자 정보 가져오기
*/