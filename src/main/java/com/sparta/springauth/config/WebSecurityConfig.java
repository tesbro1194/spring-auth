package com.sparta.springauth.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Bean 수동 등록
@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
public class WebSecurityConfig {

    @Bean // 아래 메서드를 Bean 으로 수동 등록, 반환값을 Bean 으로 등록함
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정, 사용 안함
        http.csrf((csrf) -> csrf.disable());

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        // resources 에 대한 모든 접근 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // /api/user 로 시작하는 모든 접근 허용
                        .requestMatchers("/api/user/**").permitAll()
                        // 그 외 모든 요청(접근)에 대해서는 검증 처리를 하겠다.
                        .anyRequest().authenticated()
        );

        // 로그인 사용
        http.formLogin(Customizer.withDefaults());

        return http.build(); // Bean 으로 등록될 객체: http
    }
}

/*
Spring Security 프레임워크는 Spring 서버에 필요한 `인증 및 인가`를 위해 많은 기능을 제공해 줌으로써 개발의 수고를 덜어 줍니다.
마치 'Spring' 프레임워크가 웹 서버 구현에 편의를 제공해 주는 것과 같습니다.

Spring Security 의 default 로그인 기능
- Username: user
- Password: Spring 로그 확인 (서버 시작 시마다 변경됨)
Form Login 기반 인증은 인증이 필요한 URL 요청이 들어왔을 때 인증이 되지 않았다면 로그인 페이지를 반환합니다.

- Spring 에서 모든 호출은 DispatcherServlet 을 통과하게 되고 이후에 각 요청을 담당하는 Controller 로 분배됩니다.
- 이 때, 각 요청에 대해서 공통적으로 처리해야할 필요가 있을 때 DispatcherServlet 이전에 단계가 필요하며 이것이 Filter 입니다.
- Spring Security 에서도 인증 및 인가를 처리하기 위해 Filter 를 사용하고, FilterChainProxy 를 통해서 상세로직을 구현하고 있습니다.

- 일반적으로 Form Login 기반을 사용할 때 username 과 password 확인하여 인증합니다. 이때 사용되는 필터가 아래의 필터입니다.
- UsernamePasswordAuthenticationFilter 의 인증 과정:
    1. ``사용자가 username, password 를 제출``하면 UsernamePasswordAuthenticationFilter 는 인증된 사용자의 정보가 담기는 인증 객체인
    Authentication 의 종류 중 하나인  ``UsernamePasswordAuthenticationToken 을 만들어 AuthenticationManager 에게 넘겨 인증을 시도``합니다.
    2. 실패하면 SecurityContextHolder 를 비웁니다.
    3. 성공하면 SecurityContextHolder 에 Authentication(=UsernamePasswordAuthenticationToken)를 세팅합니다.

- SecurityContextHolder 내부에 SecurityContext 가 존재합니다.
- SecurityContext 는 인증이 완료된 사용자의 상세 정보(Authentication)를 저장합니다.

Authentication: principal, credentials, authorities
- principal: 사용자를 식별합니다. 일반적으로 UserDetails 인스턴스를 사용합니다.
- credentials: 주로 비밀번호, 대부분 사용자 인증에 사용한 후 비웁니다.
- authorities: 사용자에게 부여한 권한을 GrantedAuthority 로 추상화하여 사용합니다.

UserDetails: UsernamePasswordAuthenticationToken 타입의 Authentication를 만들 때 사용되며 해당 인증객체는 SecurityContextHolder에 세팅됩니다. Custom하여 사용 가능합니다.
UserDetailsService: username/password 인증방식을 사용할 때 사용자를 조회하고 검증한 후 UserDetails를 반환합니다. Custom하여 Bean으로 등록 후 사용 가능합니다.

*/