package com.sparta.springauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;

// @Configuration은 해당 클래스가 Spring 설정 클래스임을 나타냅니다.
@Configuration
public class PasswordConfig {

    /*/@Bean은 이 메서드가 빈을 생성하고 관리하는 메서드임을 나타내며, passwordEncoder()가 호출될 때마다 BCryptPasswordEncoder 객체가 반환됩니다.
    Spring 컨테이너에 의해 이 메서드가 호출될 때마다 항상 동일한 MyServiceImpl 객체가 반환됩니다. 이는 기본적으로 싱글턴 스코프로 빈이 등록되기 때문입니다.
    즉, 애플리케이션 컨텍스트가 생성될 때 myService() 메서드가 한 번 호출되어 생성된 MyServiceImpl 객체가 Spring 컨테이너에 저장되고, 이후에는 같은 객체가 반환됩니다.
    메서드에 @Scope("prototype") 에노테이션을 사용하면 호출 시마다 새로운 객체가 생성됩니다.
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCryptPasswordEncoder() : 비밀번호를 암호화해주는 Hash 함수
    }
}

/*  Bean 수동 등록

- 공통 로그처리와 같은 비즈니스 로직을 지원하기 위한 부가 적이고 공통적인 기능들을 기술 지원 Bean이라 부르고 수동등록 합니다.
- 비즈니스 로직 Bean 보다는 그 수가 적기 때문에 수동으로 등록하기 부담스럽지 않습니다.
- 또한 수동등록된 Bean에서 문제가 발생했을 때 해당 위치를 파악하기 쉽다는 장점이 있습니다.

**Bean 수동 등록하는 방법 :
- Bean으로 등록하고자하는 객체를 반환하는 메서드를 선언하고 @Bean을 설정합니다.
- Bean을 등록하는 메서드가 속한 해당 클래스에 @Configuration을 설정합니다.
- Spring 서버가 뜰 때 Spring IoC 컨테이너에 'Bean'으로 저장됩니다.

*/