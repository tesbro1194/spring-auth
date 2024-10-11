package com.sparta.springauth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ProductRequestDto {
    @NotBlank
    private String name;
    @Email
    private String email;
    @Positive(message = "양수만 가능합니다.")
    private int price;
    @Negative(message = "음수만 가능합니다.")
    private int discount;
    @Size(min=2, max=10)
    private String link;
    @Max(10)
    private int max;
    @Min(2)
    private int min;
}

/* Bean Validation
- 데이터 검증을 간편하게 할 수 있는 여러 애너테이션을 제공 해줍니다.
@NotNull : null 불가
@NotEmpty : null, "" 불가
@NotBlank : null, ""."" 불가
@Size : 문자 길이 측정
@Max : 최대값
@Min : 최소값
@Positive : 양수
@Negative : 음수
@Email : E-mail 형식
@Pattern : 정규 표현식
*/