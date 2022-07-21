package com.bi.barfdog.api.memberDto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class JwtLoginDto {

    private String email;
    private String password;
}
