package com.bi.barfdog.config;

import com.bi.barfdog.jwt.JwtAuthenticationFilter;
import com.bi.barfdog.jwt.JwtAuthorizationFilter;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final MemberRepository memberRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용x
                .and()
                .addFilter(corsFilter) // 모든 요청은 이 필터를 거친다.
                .formLogin().disable() // 폼 로그인 사용 x
                .httpBasic().disable() // httpbasic 암호화 방식 사용 x
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),memberRepository))
                .authorizeRequests()
                .antMatchers("/api/banners/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
        ;
    }
}
