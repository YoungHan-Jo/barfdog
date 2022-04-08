package com.bi.barfdog.config;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
//    }

    @Bean // 앱이 구동 될때 테스트 계정 하나 만들기
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired MemberRepository memberRepository;

            @Autowired
            AppProperties appProperties;

            @Autowired
            BCryptPasswordEncoder bCryptPasswordEncoder;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Member admin = Member.builder()
                        .email(appProperties.getAdminEmail())
                        .name("관리자")
                        .password(bCryptPasswordEncoder.encode(appProperties.getAdminPassword()))
                        .phoneNumber("01056785678")
                        .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                        .birthday("19991201")
                        .gender(Gender.FEMALE)
                        .agreement(new Agreement(true,true,true,true,true))
                        .myRecommendationCode(BarfUtils.generateRandomCode())
                        .grade(Grade.BARF)
                        .reward(100000)
                        .firstReward(new FirstReward(true,true))
                        .roles("ADMIN,USER")
                        .build();
                memberRepository.save(admin);

                Member user = Member.builder()
                        .email(appProperties.getUserEmail())
                        .name("김회원")
                        .password(bCryptPasswordEncoder.encode(appProperties.getUserPassword()))
                        .phoneNumber("01012341234")
                        .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                        .birthday("19991201")
                        .gender(Gender.MALE)
                        .agreement(new Agreement(true,true,true,true,true))
                        .myRecommendationCode(BarfUtils.generateRandomCode())
                        .grade(Grade.BRONZE)
                        .reward(0)
                        .firstReward(new FirstReward(false,false))
                        .roles("USER")
                        .build();

                memberRepository.save(user);
            }
        };
    }
}