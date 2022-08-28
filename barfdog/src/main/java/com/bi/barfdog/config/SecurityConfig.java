package com.bi.barfdog.config;

import com.bi.barfdog.common.CommonUtils;
import com.bi.barfdog.api.memberDto.jwt.JwtAuthenticationFilter;
import com.bi.barfdog.api.memberDto.jwt.JwtAuthorizationFilter;
import com.bi.barfdog.oauth.PrincipalOauth2UserService;
import com.bi.barfdog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final MemberRepository memberRepository;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors(); // cors 설정 제일 밑에 있는 @Bean 설정한거
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용x
                .and()
//                .addFilter(corsFilter) // cors 설정 필터
                .formLogin().disable() // 폼 로그인 사용 x
                .httpBasic().disable() // httpbasic 암호화 방식 사용 x
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),memberRepository))
                // ==== 권한 설정 ====
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/login","/login","/join","/iamport-webhook").permitAll()
                .antMatchers(HttpMethod.PUT,"/api/admin/password").permitAll()
                .antMatchers(HttpMethod.POST,"/api/admin/guests").permitAll()
//                .antMatchers(HttpMethod.PUT,"/api/banners/main/**").permitAll()
                .antMatchers("/api/baskets").access("hasRole('ROLE_USER')")
                .antMatchers("/api/orders/sheet/**").access("hasRole('ROLE_USER')")
                .antMatchers("/api/admin/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/banners/**").access("hasRole('ROLE_ADMIN')")

                .antMatchers("/api/members/password").access("hasRole('ROLE_USER')")
                .antMatchers("/api/dogs").access("hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.PUT,"/api/dogs/**").access("hasRole('ROLE_USER')")


                .antMatchers("/api/members/publication").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/members/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_SUBSCRIBER') or hasRole('ROLE_ADMIN')")

                .anyRequest().permitAll()

                .and()
                .logout()
                .logoutSuccessUrl("/") // 로그아웃 시 이동하는 주소
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    CommonUtils.sendErrorMessageDto(response, HttpStatus.UNAUTHORIZED, ErrorReason.UNAUTHORIZED);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    CommonUtils.sendErrorMessageDto(response, HttpStatus.FORBIDDEN, ErrorReason.FORBIDDEN);
                })
                ;
    }



    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
//        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*"); // 모든 header 에 응답을 허용하겠다.
        config.addAllowedMethod("*"); // 모든 post,get,put,delete,patch 요청을 허용하겠다.
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
