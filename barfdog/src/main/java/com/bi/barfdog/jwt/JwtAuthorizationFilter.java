package com.bi.barfdog.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bi.barfdog.auth.PrincipalDetails;
import com.bi.barfdog.common.CommonUtils;
import com.bi.barfdog.config.ErrorReason;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청을 받음");

        try {

            String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);

            System.out.println("jwtHeader = " + jwtHeader);

            // header 가 있는지 확인
            if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {

                chain.doFilter(request, response);
                return;
            }

            String jwtToken = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");

            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken);
            decodedJWT.getPayload();

            String email = decodedJWT.getClaim("email").asString();

            System.out.println("email = " + email);

            // 서명이 정상적으로 됨
            if (email != null) {
                Member memberEntity = memberRepository.findByEmail(email).get();

                PrincipalDetails principalDetails = new PrincipalDetails(memberEntity);

                // jwt 토큰 서명을 통해 서명이 정상이면 강제로 authentication 객체 만들기
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

                // 강제로 시큐리티 세션SecurityContextHolder.getContext()에 접근하여 Authentication 객체를 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            }
        } catch (TokenExpiredException e) {
            CommonUtils.sendErrorMessageDto(response, HttpStatus.UNAUTHORIZED, ErrorReason.EXPIRED_TOKEN);
            return;
        }
    }
}
