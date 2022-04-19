package com.bi.barfdog.config;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.domain.recipe.ThumbnailImage;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.DeliveryConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.repository.DogRepository;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.repository.RecipeRepository;
import com.bi.barfdog.repository.SettingRepository;
import com.bi.barfdog.service.DogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
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

            @Autowired
            MemberRepository memberRepository;

            @Autowired
            SettingRepository settingRepository;
            @Autowired
            RecipeRepository recipeRepository;
            @Autowired
            DogRepository dogRepository;
            @Autowired
            DogService dogService;


            @Autowired
            AppProperties appProperties;

            @Autowired
            BCryptPasswordEncoder bCryptPasswordEncoder;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Member admin = makeMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01056785678", Gender.FEMALE, Grade.BARF, 100000, true, "ADMIN,USER");

                makeMember(appProperties.getUserEmail(), "김회원", appProperties.getUserPassword(), "01012341234", Gender.MALE, Grade.BRONZE, 0, false, "USER");

                makeSetting();

                Recipe recipe = makeRecipe("스타트", "닭,칠면조", "안정적인 첫 생식 적응", "스타트1.jpg", "스타트2.jpg");
                makeRecipe("터키비프", "칠면조,소", "피로회복 면역력 향상", "터키비프1.jpg", "터키비프2.jpg");
                makeRecipe("덕램", "오리,양", "피부와 모질강화 필요", "덕램1.jpg", "덕램2.jpg");
                makeRecipe("램비프", "양,소", "건강한 성장과 영양보충", "램비프1.jpg", "램비프2.jpg");

                makeDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                makeDog(admin, 42L, DogSize.LARGE, "14.5", ActivityLevel.MUCH, 2, 0.5, SnackCountLevel.LITTLE);
                makeDog(admin, 46L, DogSize.LARGE, "13.4", ActivityLevel.VERY_MUCH, 3, 1, SnackCountLevel.NORMAL);
                makeDog(admin, 43L, DogSize.LARGE, "15.3", ActivityLevel.LITTLE, 4, 0.5, SnackCountLevel.LITTLE);
                makeDog(admin, 46L, DogSize.LARGE, "12.3", ActivityLevel.LITTLE, 6, 1, SnackCountLevel.LITTLE);
                makeDog(admin, 34L, DogSize.LARGE, "16.7", ActivityLevel.VERY_LITTLE, 3, 0.7, SnackCountLevel.LITTLE);
                makeDog(admin, 64L, DogSize.MIDDLE, "11.3", ActivityLevel.VERY_LITTLE, 4, 0.5, SnackCountLevel.MUCH);
                makeDog(admin, 67L, DogSize.MIDDLE, "11.3", ActivityLevel.LITTLE, 6, 0.3, SnackCountLevel.NORMAL);
                makeDog(admin, 78L, DogSize.MIDDLE, "10.2", ActivityLevel.NORMAL, 8, 1.3, SnackCountLevel.MUCH);
                makeDog(admin, 72L, DogSize.MIDDLE, "10.2", ActivityLevel.VERY_MUCH, 6, 0.7, SnackCountLevel.NORMAL);
                makeDog(admin, 76L, DogSize.MIDDLE, "10.1", ActivityLevel.VERY_MUCH, 5, 1.3, SnackCountLevel.NORMAL);
                makeDog(admin, 34L, DogSize.MIDDLE, "13.7", ActivityLevel.VERY_MUCH, 4, 0.7, SnackCountLevel.LITTLE);
                makeDog(admin, 58L, DogSize.SMALL, "6.5", ActivityLevel.VERY_MUCH, 3, 0.5, SnackCountLevel.NORMAL);
                makeDog(admin, 73L, DogSize.SMALL, "7.2", ActivityLevel.NORMAL, 2, 0.7, SnackCountLevel.NORMAL);
                makeDog(admin, 56L, DogSize.SMALL, "5.5", ActivityLevel.VERY_LITTLE, 4, 1.3, SnackCountLevel.MUCH);
                makeDog(admin, 46L, DogSize.SMALL, "8.2", ActivityLevel.MUCH, 5, 0.5, SnackCountLevel.LITTLE);
                makeDog(admin, 36L, DogSize.SMALL, "8.2", ActivityLevel.MUCH, 4, 2, SnackCountLevel.NORMAL);

                DogSaveRequestDto requestDto = DogSaveRequestDto.builder()
                        .name("김바프")
                        .gender(Gender.MALE)
                        .birth("202102")
                        .oldDog(false)
                        .dogType("포메라니안")
                        .dogSize(DogSize.SMALL)
                        .weight("3.5")
                        .neutralization(true)
                        .activityLevel(ActivityLevel.NORMAL)
                        .walkingCountPerWeek("10")
                        .walkingTimePerOneTime("1.1")
                        .dogStatus(DogStatus.HEALTHY)
                        .snackCountLevel(SnackCountLevel.NORMAL)
                        .inedibleFood("NONE")
                        .inedibleFoodEtc("NONE")
                        .recommendRecipeId(recipe.getId())
                        .caution("NONE")
                        .build();
                dogService.createDogAndGetSurveyReport(requestDto, admin);


            }

            private Member makeMember(String appProperties, String 김회원, String appProperties1, String phoneNumber, Gender male, Grade bronze, int reward, boolean recommend, String USER) {
                Member member = Member.builder()
                        .email(appProperties)
                        .name(김회원)
                        .password(bCryptPasswordEncoder.encode(appProperties1))
                        .phoneNumber(phoneNumber)
                        .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                        .birthday("19991201")
                        .gender(male)
                        .agreement(new Agreement(true,true,true,true,true))
                        .myRecommendationCode(BarfUtils.generateRandomCode())
                        .grade(bronze)
                        .reward(reward)
                        .firstReward(new FirstReward(recommend, recommend))
                        .roles(USER)
                        .build();

                return memberRepository.save(member);
            }

            private void makeSetting() {
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
            }

            private Recipe makeRecipe(String 램비프, String ingredients, String 건강한_성장과_영양보충, String filename1, String filename2) {
                Recipe recipe = Recipe.builder()
                        .name(램비프)
                        .description("레시피 설명")
                        .uiNameKorean("레시피 한글")
                        .uiNameEnglish("RECIPE ENGLISH")
                        .pricePerGram(new BigDecimal("48.234"))
                        .gramPerKcal(new BigDecimal("1.23456"))
                        .ingredients(ingredients)
                        .descriptionForSurvey(건강한_성장과_영양보충)
                        .thumbnailImage(new ThumbnailImage("http://xxxx.com/recipe", filename1, filename2))
                        .leaked(Leaked.LEAKED)
                        .inStock(true)
                        .status(RecipeStatus.ACTIVE)
                        .build();
                return recipeRepository.save(recipe);
            }

            private Dog makeDog(Member admin, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
                Dog dog = Dog.builder()
                        .member(admin)
                        .name("샘플독")
                        .startAgeMonth(startAgeMonth)
                        .gender(Gender.MALE)
                        .oldDog(false)
                        .dogSize(dogSize)
                        .weight(new BigDecimal(weight))
                        .dogActivity(new DogActivity(activitylevel, walkingCountPerWeek, walkingTimePerOneTime))
                        .dogStatus(DogStatus.HEALTHY)
                        .snackCountLevel(snackCountLevel)
                        .build();
                return dogRepository.save(dog);
            }
        };
    }
}