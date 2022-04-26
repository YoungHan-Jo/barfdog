package com.bi.barfdog.config;

import com.bi.barfdog.jwt.JwtAuthenticationFilter;
import com.bi.barfdog.jwt.JwtAuthorizationFilter;
import com.bi.barfdog.oauth.PrincipalOauth2UserService;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final MemberRepository memberRepository;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.cors(); // cors 설정 2번째꺼
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용x
                .and()
                .addFilter(corsFilter) // 모든 요청은 이 필터를 거친다.
                .formLogin().disable() // 폼 로그인 사용 x
                .httpBasic().disable() // httpbasic 암호화 방식 사용 x
//                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),memberRepository))
                // ==== 권한 설정 ====
                .authorizeRequests()
                .antMatchers("/api/banners/**")
                .access("hasRole('ROLE_ADMIN')")

                .antMatchers("/api/members/publicationCoupon")
                .access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/members/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_SUBSCRIBER') or hasRole('ROLE_ADMIN')")

                .anyRequest().permitAll()

                .and()
                .logout()
                .logoutSuccessUrl("/") // 로그아웃 시 이동하는 주소
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
        ;
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
////        config.setAllowedOrigins(Arrays.asList("*"));
////        config.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT"));
////        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
//        config.addAllowedOriginPattern("*");
//        config.addAllowedHeader("*"); // 모든 header 에 응답을 허용하겠다.
//        config.addAllowedMethod("*"); // 모든 post,get,put,delete,patch 요청을 허용하겠다.
//        config.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
}
