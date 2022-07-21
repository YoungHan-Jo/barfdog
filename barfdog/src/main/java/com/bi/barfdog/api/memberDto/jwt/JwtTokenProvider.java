package com.bi.barfdog.api.memberDto.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {


    // JWT 토큰 생성
    public String createToken(String memberId, String email) {
        String jwtToken = JWT.create()
                .withSubject("토큰 이름")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", memberId)
                .withClaim("email", email)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        return jwtToken;
    }


}