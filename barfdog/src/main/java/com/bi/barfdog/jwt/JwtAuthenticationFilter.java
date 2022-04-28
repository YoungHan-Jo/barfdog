package com.bi.barfdog.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bi.barfdog.auth.PrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter 가 있음
// /login 요청해서 username, password post로 전송하면 동작함(SecurityConfig에 등록해야함)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;




    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JwtLoginDto jwtMemberDto = objectMapper.readValue(request.getInputStream(), JwtLoginDto.class); // json 파싱

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtMemberDto.getEmail(), jwtMemberDto.getPassword());

            // PrincipalDetails의 loadUserByUsernamer() 메소드가 실행된 후 정상이면 authentication을 리턴함
            // DB에 있는 username 과 password 가 일치한다.(인증완료)
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            if (authentication == null) {
                return null;
            }

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨 = " + principalDetails.getMember().getName()); // 로그인 완료

            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 메소드가 실행 됨.
    // 여기서 JWT 토큰을 만들어서 request 요청한 사용자에게 jwt 토큰을 응답

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨 = 유저 인증 완료");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("토큰 이름")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("email", principalDetails.getMember().getEmail())
                .withClaim("id", principalDetails.getMember().getId())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }


}
