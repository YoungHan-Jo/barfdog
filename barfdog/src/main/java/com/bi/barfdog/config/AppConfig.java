package com.bi.barfdog.config;

import com.bi.barfdog.api.bannerDto.MainBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.MyPageBannerSaveRequestDto;
import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
import com.bi.barfdog.domain.blog.*;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.domain.recipe.ThumbnailImage;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.DeliveryConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.repository.*;
import com.bi.barfdog.repository.article.ArticleRepository;
import com.bi.barfdog.repository.banner.BannerRepository;
import com.bi.barfdog.repository.blog.BlogImageRepository;
import com.bi.barfdog.repository.blog.BlogRepository;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.service.BannerService;
import com.bi.barfdog.service.DogService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static com.bi.barfdog.config.finalVariable.AutoCoupon.*;

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


    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            MemberRepository memberRepository;

            @Autowired
            BannerService bannerService;

            @Autowired
            BannerRepository bannerRepository;

            @Autowired
            BlogImageRepository blogImageRepository;

            @Autowired
            SettingRepository settingRepository;
            @Autowired
            RecipeRepository recipeRepository;
            @Autowired
            DogRepository dogRepository;
            @Autowired
            DogService dogService;
            @Autowired
            CouponRepository couponRepository;
            @Autowired
            MemberCouponRepository memberCouponRepository;
            @Autowired
            ArticleRepository articleRepository;
            @Autowired
            BlogRepository blogRepository;


            @Autowired
            AppProperties appProperties;

            @Autowired
            BCryptPasswordEncoder bCryptPasswordEncoder;

            @Override
            public void run(ApplicationArguments args) throws Exception {

                for (int i = 1; i <= 4; ++i) {
                    createMainBanner(i);
                }
                createMyPageBanner();




                Member admin = makeMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01056785678", Gender.FEMALE, Grade.BARF, 100000, true, "ADMIN,USER");
                Member manager = makeMember("develope07@binter.co.kr", "관리자계정", appProperties.getAdminPassword(), "01056781234", Gender.FEMALE, Grade.BARF, 100000, true, "ADMIN,USER");

                Member member = makeMember(appProperties.getUserEmail(), "김회원", appProperties.getUserPassword(), "01012341234", Gender.MALE, Grade.BRONZE, 0, false, "USER");
                makeMember("abc@gmail.com", "박회원", appProperties.getUserPassword(), "01012341111", Gender.MALE, Grade.BRONZE, 0, false, "USER");


                makeSetting();

                Recipe recipe = makeRecipe("스타트", "닭,칠면조", "안정적인 첫 생식 적응", "스타트1.jpg", "스타트2.jpg");
                makeRecipe("터키비프", "칠면조,소", "피로회복 면역력 향상", "터키비프1.jpg", "터키비프2.jpg");
                makeRecipe("덕램", "오리,양", "피부와 모질강화 필요", "덕램1.jpg", "덕램2.jpg");
                makeRecipe("램비프", "양,소", "건강한 성장과 영양보충", "램비프1.jpg", "램비프2.jpg");

                makeRepresentativeDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                makeRepresentativeDog(manager, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                makeRepresentativeDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
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

                Coupon subsCoupon = makeAutoCoupon(SUBSCRIBE_COUPON, "정기구독 할인 쿠폰", DiscountType.FIXED_RATE, 50, 0, CouponTarget.SUBSCRIBE);
                Coupon dogBirthCoupon = makeAutoCoupon(DOG_BIRTH_COUPON, "반려견 생일 쿠폰", DiscountType.FIXED_RATE, 10, 0, CouponTarget.ALL);
                Coupon memberBirthCoupon = makeAutoCoupon(MEMBER_BIRTH_COUPON, "견주 생일 쿠폰", DiscountType.FIXED_RATE, 15, 0, CouponTarget.ALL);

                makeAutoCoupon(SILVER_COUPON,"실버 쿠폰", DiscountType.FLAT_RATE,1000,20000, CouponTarget.ALL);
                makeAutoCoupon(GOLD_COUPON,"골드 쿠폰", DiscountType.FLAT_RATE,2000,30000, CouponTarget.ALL);
                makeAutoCoupon(PLATINUM_COUPON,"플래티넘 쿠폰", DiscountType.FLAT_RATE,2500,30000, CouponTarget.ALL);
                makeAutoCoupon(DIAMOND_COUPON,"다이아 쿠폰", DiscountType.FLAT_RATE,3000,40000, CouponTarget.ALL);
                makeAutoCoupon(BARF_COUPON,"더바프 쿠폰", DiscountType.FLAT_RATE,4000,50000, CouponTarget.ALL);


                makeMemberCoupon(member, subsCoupon);
                makeMemberCoupon(member, dogBirthCoupon);
                makeMemberCoupon(member, memberBirthCoupon);

                createDogAndGetSurveyReport(admin, recipe);


            }



            private void generateBlogImage(Blog blog1, int i) {
                BlogImage blogImage = BlogImage.builder()
                        .blog(blog1)
                        .folder("/folder/folder/")
                        .filename("blogImage" + i + ".jpg")
                        .build();

                blogImageRepository.save(blogImage);
            }



            private SurveyReport createDogAndGetSurveyReport(Member admin, Recipe recipe) {
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
                return dogService.createDogAndGetSurveyReport(requestDto, admin);
            }

            private void makeMemberCoupon(Member member, Coupon subsCoupon) {
                MemberCoupon memberCoupon = MemberCoupon.builder()
                        .member(member)
                        .coupon(subsCoupon)
                        .expiredDate(LocalDateTime.of(2023, 12, 31, 23, 59, 59))
                        .remaining(3)
                        .memberCouponStatus(CouponStatus.ACTIVE)
                        .build();

                memberCouponRepository.save(memberCoupon);
            }

            private void createMainBanner(int i) throws IOException, URISyntaxException {

                MultipartFile mFilePc = getMultipartFile("C:/upload/default/mainBanner" + i + ".jpg");

                MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder()
                        .name("메인배너" + i)
                        .targets(BannerTargets.ALL)
                        .status(BannerStatus.LEAKED)
                        .pcLinkUrl("")
                        .mobileLinkUrl("")
                        .build();

                bannerService.saveMainBanner(requestDto, mFilePc, mFilePc);
            }

            private void createMyPageBanner() throws IOException, URISyntaxException {

                MultipartFile mFilePc = getMultipartFile("C:/upload/default/mypageBanner_pc.png");

                MultipartFile mFileMobile = getMultipartFile("C:/upload/default/mypageBanner_mobile.png");

                MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                        .name("마이페이지 배너")
                        .status(BannerStatus.LEAKED)
                        .pcLinkUrl("")
                        .mobileLinkUrl("")
                        .build();
                bannerService.saveMyPageBanner(requestDto, mFilePc, mFileMobile);
            }

            private Coupon makeAutoCoupon(String name, String description, DiscountType discountType, int discountDegree, int availableMinPrice, CouponTarget couponTarget) {
                Coupon coupon = Coupon.builder()
                        .name(name)
                        .couponType(CouponType.AUTO_PUBLISHED)
                        .code("")
                        .description(description)
                        .amount(1)
                        .discountType(discountType)
                        .discountDegree(discountDegree)
                        .availableMaxDiscount(100000)
                        .availableMinPrice(availableMinPrice)
                        .couponTarget(couponTarget)
                        .couponStatus(CouponStatus.ACTIVE)
                        .build();

                return couponRepository.save(coupon);
            }

            private Member makeMember(String email, String name, String password, String phoneNumber, Gender gender, Grade grade, int reward, boolean recommend, String roles) {
                Member member = Member.builder()
                        .email(email)
                        .name(name)
                        .password(bCryptPasswordEncoder.encode(password))
                        .phoneNumber(phoneNumber)
                        .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                        .birthday("19991201")
                        .gender(gender)
                        .agreement(new Agreement(true,true,true,true,true))
                        .myRecommendationCode(BarfUtils.generateRandomCode())
                        .grade(grade)
                        .reward(reward)
                        .accumulatedAmount(0)
                        .firstReward(new FirstReward(recommend, recommend))
                        .roles(roles)
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

            private Dog makeRepresentativeDog(Member admin, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
                Dog dog = Dog.builder()
                        .member(admin)
                        .name("대표견")
                        .representative(true)
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

    private MultipartFile getMultipartFile(String pathname) throws IOException {
        File file = new File(pathname);

        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
        try {
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            // Or faster..
            // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (IOException ex) {
            // do something.
        }
        //jpa.png -> multipart 변환
        return new CommonsMultipartFile(fileItem);
    }
}