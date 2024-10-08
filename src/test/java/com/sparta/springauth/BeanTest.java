package com.sparta.springauth;

import com.sparta.springauth.food.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BeanTest {
    @Autowired  // Bean 의 타입으로 찾고, 없으면 이름으로 찾는다. (인터페이스 Food의 구현체가 둘) 같은 타입이 둘 있으니까 오류가 난다.
    @Qualifier("pizza")  // @Qualifier("pizza") 가 붙은 클래스를 찾아 빈 객체에 넣음
    Food food;
    @Autowired
    Food pizza;
    @Autowired
    Food chicken;

    @Test
    @DisplayName("테스트")
    void test1() {
        pizza.eat();
        chicken.eat();
        food.eat();  // @Qualifier 와 @Primary 우선 순위 비교 -> 피자를 먹습니다
        // Spring에선 일반적으로 좁은 범위(@Qualifier)가 넓은 범위(@Primary)보다 우선 순위가 높음
    }


}
