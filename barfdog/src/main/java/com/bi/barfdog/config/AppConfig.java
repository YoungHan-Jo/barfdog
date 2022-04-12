package com.bi.barfdog.config;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.domain.recipe.ThumbnailImage;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.DeliveryConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.repository.RecipeRepository;
import com.bi.barfdog.repository.SettingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

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
            RecipeRepository recipeRepository;

            @Autowired
            SettingRepository settingRepository;

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

                Setting setting = Setting.builder()
                        .activityConstant(
                                new ActivityConstant(
                                        new BigDecimal("-1.50"),
                                        new BigDecimal("-0.75"),
                                        new BigDecimal("0"),
                                        new BigDecimal("0.75"),
                                        new BigDecimal("1.50")))
                        .snackConstant(
                                new SnackConstant(
                                        new BigDecimal("1.50"),
                                        new BigDecimal("0"),
                                        new BigDecimal("-1.50")))
                        .deliveryConstant(new DeliveryConstant(3000, 50000))
                        .build();

                settingRepository.save(setting);

                Recipe recipe1 = Recipe.builder()
                        .name("스타트")
                        .description("레시피 설명")
                        .uiNameKorean("레시피 한글")
                        .uiNameEnglish("RECIPE ENGLISH")
                        .pricePerGram(new BigDecimal("48.234"))
                        .gramPerKcal(new BigDecimal("1.23456"))
                        .ingredients("닭,칠면조")
                        .descriptionForSurvey("안정적인 첫 생식 적응")
                        .thumbnailImage(new ThumbnailImage("http://xxxx.com/recipe", "스타트1.jpg", "스타트2.jpg"))
                        .leaked(Leaked.LEAKED)
                        .inStock(true)
                        .status(RecipeStatus.ACTIVE)
                        .build();

                Recipe recipe2 = Recipe.builder()
                        .name("터키비프")
                        .description("레시피 설명")
                        .uiNameKorean("레시피 한글")
                        .uiNameEnglish("RECIPE ENGLISH")
                        .pricePerGram(new BigDecimal("48.234"))
                        .gramPerKcal(new BigDecimal("1.23456"))
                        .ingredients("칠면조,소")
                        .descriptionForSurvey("피로회복 면역력 향상")
                        .thumbnailImage(new ThumbnailImage("http://xxxx.com/recipe", "터키비프1.jpg", "터키비프2.jpg"))
                        .leaked(Leaked.LEAKED)
                        .inStock(true)
                        .status(RecipeStatus.ACTIVE)
                        .build();

                Recipe recipe3 = Recipe.builder()
                        .name("덕램")
                        .description("레시피 설명")
                        .uiNameKorean("레시피 한글")
                        .uiNameEnglish("RECIPE ENGLISH")
                        .pricePerGram(new BigDecimal("48.234"))
                        .gramPerKcal(new BigDecimal("1.23456"))
                        .ingredients("오리,양")
                        .descriptionForSurvey("피부와 모질강화 필요")
                        .thumbnailImage(new ThumbnailImage("http://xxxx.com/recipe", "덕램1.jpg", "덕램2.jpg"))
                        .leaked(Leaked.LEAKED)
                        .inStock(true)
                        .status(RecipeStatus.ACTIVE)
                        .build();

                Recipe recipe4 = Recipe.builder()
                        .name("램비프")
                        .description("레시피 설명")
                        .uiNameKorean("레시피 한글")
                        .uiNameEnglish("RECIPE ENGLISH")
                        .pricePerGram(new BigDecimal("48.234"))
                        .gramPerKcal(new BigDecimal("1.23456"))
                        .ingredients("양,소")
                        .descriptionForSurvey("건강한 성장과 영양보충")
                        .thumbnailImage(new ThumbnailImage("http://xxxx.com/recipe", "램비프1.jpg", "램비프2.jpg"))
                        .leaked(Leaked.LEAKED)
                        .inStock(true)
                        .status(RecipeStatus.ACTIVE)
                        .build();

                recipeRepository.save(recipe1);
                recipeRepository.save(recipe2);
                recipeRepository.save(recipe3);
                recipeRepository.save(recipe4);


            }
        };
    }
}