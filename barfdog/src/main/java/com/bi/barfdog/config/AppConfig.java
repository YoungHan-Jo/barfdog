package com.bi.barfdog.config;

import com.bi.barfdog.api.bannerDto.MainBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.MyPageBannerSaveRequestDto;
import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.orderDto.OrderSheetGeneralRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
import com.bi.barfdog.domain.banner.TopBanner;
import com.bi.barfdog.domain.blog.Article;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.*;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.domain.recipe.ThumbnailImage;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.DeliveryConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.domain.surveyReport.*;
import com.bi.barfdog.iamport.Iamport_API;
import com.bi.barfdog.repository.article.ArticleRepository;
import com.bi.barfdog.repository.banner.BannerRepository;
import com.bi.barfdog.repository.blog.BlogImageRepository;
import com.bi.barfdog.repository.blog.BlogRepository;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.BeforeSubscribeRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import com.bi.barfdog.service.BannerService;
import com.bi.barfdog.service.DogService;
import com.siot.IamportRestClient.IamportClient;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.bi.barfdog.config.finalVariable.AutoCoupon.*;
import static com.bi.barfdog.config.finalVariable.StandardVar.*;
import static com.bi.barfdog.config.finalVariable.StandardVar.LACTATING;

@Configuration
public class AppConfig {

    @Value("${spring.servlet.multipart.location}") // yml에 있는거 읽어 옴
    private String uploadRootPath;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(Iamport_API.API_KEY, Iamport_API.API_SECRET);
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
            SubscribeRepository subscribeRepository;
            @Autowired
            AppProperties appProperties;
            @Autowired
            BCryptPasswordEncoder bCryptPasswordEncoder;
            @Autowired
            ItemRepository itemRepository;
            @Autowired
            ItemImageRepository itemImageRepository;
            @Autowired
            OrderRepository orderRepository;
            @Autowired
            OrderItemRepository orderItemRepository;
            @Autowired
            SubscribeRecipeRepository subscribeRecipeRepository;
            @Autowired
            ItemOptionRepository itemOptionRepository;
            @Autowired
            SurveyReportRepository surveyReportRepository;
            @Autowired
            BeforeSubscribeRepository beforeSubscribeRepository;
            @Autowired
            DeliveryRepository deliveryRepository;

            @Override
            public void run(ApplicationArguments args) throws Exception {

                // ===================================================================================================
                // ====== 최초 필요한 데이터 시작 ========================================================================
                // ===================================================================================================
                // ===================================================================================================
                // ====== 실제 서버 최초로 빌드할 때만 적용시키기 ==========================================================
                // ====== 운영 중인 서버에서는 &&&&&&&&반드시 주석처리&&&&&&&& =============================================
                // ===================================================================================================
                settingRepository.deleteAll();
                generateSetting();
                bannerRepository.deleteAll();
                generateMypageBanner();
                generateTopBanner();

                memberRepository.deleteAll();
                Member admin = generateMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01056785678", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
                Member member = generateMember(appProperties.getUserEmail(), "김회원", appProperties.getUserPassword(), "01099038544", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);

                couponRepository.deleteAll();
                Coupon subsCoupon = generateCouponAuto(JOIN_SUBSCRIBE_COUPON, "정기구독 할인 쿠폰", DiscountType.FIXED_RATE, 50, 0, CouponTarget.SUBSCRIBE);
                Coupon dogBirthCoupon = generateCouponAuto(DOG_BIRTH_COUPON, "반려견 생일 쿠폰", DiscountType.FIXED_RATE, 10, 0, CouponTarget.ALL);
                Coupon memberBirthCoupon = generateCouponAuto(MEMBER_BIRTH_COUPON, "견주 생일 쿠폰", DiscountType.FIXED_RATE, 15, 0, CouponTarget.ALL);

                generateCouponAuto(SILVER_COUPON,"실버 쿠폰", DiscountType.FLAT_RATE,1000,20000, CouponTarget.ALL);
                generateCouponAuto(GOLD_COUPON,"골드 쿠폰", DiscountType.FLAT_RATE,2000,30000, CouponTarget.ALL);
                generateCouponAuto(PLATINUM_COUPON,"플래티넘 쿠폰", DiscountType.FLAT_RATE,2500,30000, CouponTarget.ALL);
                generateCouponAuto(DIAMOND_COUPON,"다이아 쿠폰", DiscountType.FLAT_RATE,3000,40000, CouponTarget.ALL);
                generateCouponAuto(BARF_COUPON,"더바프 쿠폰", DiscountType.FLAT_RATE,4000,50000, CouponTarget.ALL);



                // =================================
                // ====== 최초 필요한 데이터 끝 =======
                // =================================

                // ==========================================================
//              // =============테스트 용 더미 데이터 시작=======================
                // ==========================================================

                for (int i = 1; i <= 4; ++i) {
                    generateBannerMain(i);
                }

                generateArticle(1);
                generateArticle(2);

                Member manager = generateMember("dev@biventures.kr", "관리자계정", appProperties.getAdminPassword(), "01056781234", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
                generateMember("abc@gmail.com", "박회원", appProperties.getUserPassword(), "01012341111", Gender.MALE, Grade.브론즈, 0, false, "USER", false);

                Recipe recipe = generateRecipe("스타트", "닭,칠면조", "안정적인 첫 생식 적응", "스타트1.jpg", "스타트2.jpg");
                generateRecipe("터키비프", "칠면조,소", "피로회복 면역력 향상", "터키비프1.jpg", "터키비프2.jpg");
                generateRecipe("덕램", "오리,양", "피부와 모질강화 필요", "덕램1.jpg", "덕램2.jpg");
                generateRecipe("램비프", "양,소", "건강한 성장과 영양보충", "램비프1.jpg", "램비프2.jpg");

                Subscribe subscribe = generateSubscribe();
                Dog memberRepresentativeDog = generateDogRepresentative(member, subscribe,18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                generateDogRepresentative(admin, null,18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                generateDogRepresentative(manager,null, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 42L, DogSize.LARGE, "14.5", ActivityLevel.MUCH, 2, 0.5, SnackCountLevel.LITTLE);
                generateDogBeforePaymentSubscribe(admin, 46L, DogSize.LARGE, "13.4", ActivityLevel.VERY_MUCH, 3, 1, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 43L, DogSize.LARGE, "15.3", ActivityLevel.LITTLE, 4, 0.5, SnackCountLevel.LITTLE);
                generateDogBeforePaymentSubscribe(admin, 46L, DogSize.LARGE, "12.3", ActivityLevel.LITTLE, 6, 1, SnackCountLevel.LITTLE);
                generateDogBeforePaymentSubscribe(admin, 34L, DogSize.LARGE, "16.7", ActivityLevel.VERY_LITTLE, 3, 0.7, SnackCountLevel.LITTLE);
                generateDogBeforePaymentSubscribe(admin, 64L, DogSize.MIDDLE, "11.3", ActivityLevel.VERY_LITTLE, 4, 0.5, SnackCountLevel.MUCH);
                generateDogBeforePaymentSubscribe(admin, 67L, DogSize.MIDDLE, "11.3", ActivityLevel.LITTLE, 6, 0.3, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 78L, DogSize.MIDDLE, "10.2", ActivityLevel.NORMAL, 8, 1.3, SnackCountLevel.MUCH);
                generateDogBeforePaymentSubscribe(admin, 72L, DogSize.MIDDLE, "10.2", ActivityLevel.VERY_MUCH, 6, 0.7, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 76L, DogSize.MIDDLE, "10.1", ActivityLevel.VERY_MUCH, 5, 1.3, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 34L, DogSize.MIDDLE, "13.7", ActivityLevel.VERY_MUCH, 4, 0.7, SnackCountLevel.LITTLE);
                generateDogBeforePaymentSubscribe(admin, 58L, DogSize.SMALL, "6.5", ActivityLevel.VERY_MUCH, 3, 0.5, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 73L, DogSize.SMALL, "7.2", ActivityLevel.NORMAL, 2, 0.7, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(admin, 56L, DogSize.SMALL, "5.5", ActivityLevel.VERY_LITTLE, 4, 1.3, SnackCountLevel.MUCH);
                generateDogBeforePaymentSubscribe(admin, 46L, DogSize.SMALL, "8.2", ActivityLevel.MUCH, 5, 0.5, SnackCountLevel.LITTLE);
                generateDogBeforePaymentSubscribe(admin, 36L, DogSize.SMALL, "8.2", ActivityLevel.MUCH, 4, 2, SnackCountLevel.NORMAL);



                generateMemberCoupon(member, subsCoupon);
                generateMemberCoupon(member, dogBirthCoupon);
                generateMemberCoupon(member, memberBirthCoupon);

                // ============= 작성 가능한 리뷰 데이터 시작 ==============
                Item item1 = generateItem(1);
                Item item2 = generateItem(2);
                Item item3 = generateItem(3);
                Item item4 = generateItem(4);

                IntStream.range(1,4).forEach(i -> {
                    generateItemImage(item1, i);
                    generateItemImage(item2, i);
                    generateItemImage(item3, i);
                    generateItemImage(item4, i);
                });

                IntStream.range(1,4).forEach(i -> {
                    generateOrderItemsAndOrder(member, item3, item4);
                    generateOrderItemsAndOrder(admin, item1, item2);
                });

                IntStream.range(1,7).forEach(i -> {
                    generateWriteableReviewSubscribe(member);
                });
                // ============= 작성 가능한 리뷰 데이터 끝 ==============


                // ============= 구독 주문서 조회용 데이터 시작 =============== //
                Dog dogRepresentative = generateDogRepresentativeBeforePaymentSubscribe(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(member, 23L, DogSize.SMALL, "10.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
                generateDogBeforePaymentSubscribe(member, 25L, DogSize.MIDDLE, "13.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

                generateSubscribeBeforePayment(dogRepresentative, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, 100000);
                // ============= 구독 주문서 조회용 데이터 끝 =============== //


                // ============= 일반 주문서 조회용 데이터 시작 =============== //
                SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtc(member, 1, OrderStatus.PAYMENT_DONE);

                Item generalOrderItem1 = generateItem(1);
                ItemOption option1 = generateOption(generalOrderItem1, 1);
                ItemOption option2 = generateOption(generalOrderItem1, 2);

                Item generalOrderItem2 = generateItem(2);
                ItemOption option3 = generateOption(generalOrderItem2, 3);
                ItemOption option4 = generateOption(generalOrderItem2, 4);

                List<OrderSheetGeneralRequestDto.OrderItemDto> orderItemDtoList = new ArrayList<>();
                addOrderItemDto(generalOrderItem1, option1, option2, orderItemDtoList, 1);
                addOrderItemDto(generalOrderItem2, option3, option4, orderItemDtoList, 2);

                // ============= 일반 주문서 조회용 데이터 끝 =============== //

                // ==========================================================
                // =================테스트 용 더미 데이터 끝=====================
                // ==========================================================

            } // run

            private void generateTopBanner() {
                TopBanner topBanner = TopBanner.builder()
                        .name("상단 띠 배너")
                        .pcLinkUrl("pc link url")
                        .mobileLinkUrl("mobile link url")
                        .status(BannerStatus.HIDDEN)
                        .backgroundColor("CA1010")
                        .fontColor("FFFFFF")
                        .build();
                bannerRepository.save(topBanner);
            }

            private ItemOption generateOption(Item item, int i) {
                ItemOption itemOption = ItemOption.builder()
                        .item(item)
                        .name("옵션" + i)
                        .optionPrice(i * 1000)
                        .remaining(999)
                        .build();
                return itemOptionRepository.save(itemOption);
            }

            private void addOrderItemDto(Item item1, ItemOption option1, ItemOption option2, List<OrderSheetGeneralRequestDto.OrderItemDto> orderItemDtoList, int amount) {
                OrderSheetGeneralRequestDto.ItemDto itemDto = OrderSheetGeneralRequestDto.ItemDto.builder()
                        .itemId(item1.getId())
                        .amount(amount)
                        .build();

                List<OrderSheetGeneralRequestDto.ItemOptionDto> itemOptionDtoList = new ArrayList<>();
                addItemOptionDto(option1, itemOptionDtoList);
                addItemOptionDto(option2, itemOptionDtoList);

                OrderSheetGeneralRequestDto.OrderItemDto orderItemDto = OrderSheetGeneralRequestDto.OrderItemDto.builder()
                        .itemDto(itemDto)
                        .itemOptionDtoList(itemOptionDtoList)
                        .build();
                orderItemDtoList.add(orderItemDto);
            }

            private void addItemOptionDto(ItemOption option1, List<OrderSheetGeneralRequestDto.ItemOptionDto> itemOptionDtoList) {
                OrderSheetGeneralRequestDto.ItemOptionDto itemOptionDto = OrderSheetGeneralRequestDto.ItemOptionDto.builder()
                        .itemOptionId(option1.getId())
                        .amount(2)
                        .build();
                itemOptionDtoList.add(itemOptionDto);
            }

            private SubscribeOrder generateSubscribeOrderAndEtc(Member member, int i, OrderStatus orderStatus) {

                Recipe recipe1 = recipeRepository.findAll().get(0);
                Recipe recipe2 = recipeRepository.findAll().get(1);

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
                        .recommendRecipeId(recipe1.getId())
                        .caution("NONE")
                        .build();

                String birth = requestDto.getBirth();

                DogSize dogSize = requestDto.getDogSize();
                Long startAgeMonth = getTerm(birth + "01");
                boolean oldDog = requestDto.isOldDog();
                boolean neutralization = requestDto.isNeutralization();
                DogStatus dogStatus = requestDto.getDogStatus();
                SnackCountLevel snackCountLevel = requestDto.getSnackCountLevel();
                BigDecimal weight = new BigDecimal(requestDto.getWeight());

                Delivery delivery = generateDelivery(member, i);

                Subscribe subscribe = generateSubscribeBeforePayment(i);
                BeforeSubscribe beforeSubscribe = generateBeforeSubscribe(i);
                subscribe.setBeforeSubscribe(beforeSubscribe);

                generateSubscribeRecipe(recipe1, subscribe);
                generateSubscribeRecipe(recipe2, subscribe);

                List<Dog> dogs = dogRepository.findByMember(member);
                Recipe findRecipe = recipeRepository.findById(requestDto.getRecommendRecipeId()).get();

                Dog dog = Dog.builder()
                        .member(member)
                        .representative(dogs.size() == 0 ? true : false)
                        .name(requestDto.getName())
                        .gender(requestDto.getGender())
                        .birth(birth)
                        .startAgeMonth(startAgeMonth)
                        .oldDog(oldDog)
                        .dogType(requestDto.getDogType())
                        .dogSize(dogSize)
                        .weight(weight)
                        .neutralization(neutralization)
                        .dogActivity(getDogActivity(requestDto))
                        .dogStatus(dogStatus)
                        .snackCountLevel(snackCountLevel)
                        .inedibleFood(requestDto.getInedibleFood())
                        .inedibleFoodEtc(requestDto.getInedibleFoodEtc())
                        .recommendRecipe(findRecipe)
                        .caution(requestDto.getCaution())
                        .subscribe(subscribe)
                        .build();
                dogRepository.save(dog);
                subscribe.setDog(dog);

                SurveyReport surveyReport = SurveyReport.builder()
                        .dog(dog)
                        .ageAnalysis(getAgeAnalysis(startAgeMonth))
                        .weightAnalysis(getWeightAnalysis(dogSize, weight))
                        .activityAnalysis(getActivityAnalysis(dogSize, dog))
                        .walkingAnalysis(getWalkingAnalysis(member, dog))
                        .foodAnalysis(getDogAnalysis(requestDto, findRecipe, dogSize, startAgeMonth, oldDog, neutralization, dogStatus, requestDto.getActivityLevel(), snackCountLevel))
                        .snackAnalysis(getSnackAnalysis(dog))
                        .build();
                surveyReportRepository.save(surveyReport);
                dog.setSurveyReport(surveyReport);

                Coupon coupon = generateGeneralCoupon(1);
                MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);

                SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                        .impUid("imp_uid"+i)
                        .merchantUid("merchant_uid"+i)
                        .orderStatus(orderStatus)
                        .member(member)
                        .orderPrice(120000)
                        .deliveryPrice(0)
                        .discountTotal(0)
                        .discountReward(0)
                        .discountCoupon(0)
                        .paymentPrice(120000)
                        .paymentMethod(PaymentMethod.CREDIT_CARD)
                        .isPackage(false)
                        .delivery(delivery)
                        .subscribe(subscribe)
                        .memberCoupon(memberCoupon)
                        .subscribeCount(subscribe.getSubscribeCount())
                        .orderConfirmDate(LocalDateTime.now().minusHours(3))
                        .build();
                orderRepository.save(subscribeOrder);

                return subscribeOrder;
            }
            private BeforeSubscribe generateBeforeSubscribe(int i) {
                BeforeSubscribe beforeSubscribe = BeforeSubscribe.builder()
                        .subscribeCount(i)
                        .plan(SubscribePlan.HALF)
                        .oneMealRecommendGram(BigDecimal.valueOf(140.0))
                        .recipeName("덕램")
                        .build();
                return beforeSubscribeRepository.save(beforeSubscribe);
            }

            private Coupon generateGeneralCoupon(int i) {
                Coupon coupon = Coupon.builder()
                        .name("관리자 직접 발행 쿠폰" + i)
                        .couponType(CouponType.GENERAL_PUBLISHED)
                        .code("")
                        .description("설명")
                        .amount(1)
                        .discountType(DiscountType.FIXED_RATE)
                        .discountDegree(10)
                        .availableMaxDiscount(10000)
                        .availableMinPrice(5000)
                        .couponTarget(CouponTarget.ALL)
                        .couponStatus(CouponStatus.ACTIVE)
                        .build();

                return couponRepository.save(coupon);
            }

            private Subscribe generateSubscribeBeforePayment(int i) {
                Subscribe subscribe = Subscribe.builder()
                        .subscribeCount(i+1)
                        .plan(SubscribePlan.FULL)
                        .nextPaymentDate(LocalDateTime.now().plusDays(6))
                        .nextDeliveryDate(LocalDate.now().plusDays(8))
                        .nextPaymentPrice(120000)
                        .status(SubscribeStatus.SUBSCRIBING)
                        .build();
                subscribeRepository.save(subscribe);
                return subscribe;
            }

            private MemberCoupon generateMemberCoupon(Member member, Coupon coupon, int remaining, CouponStatus status) {
                MemberCoupon memberCoupon = MemberCoupon.builder()
                        .member(member)
                        .coupon(coupon)
                        .expiredDate(LocalDateTime.now().plusDays(remaining))
                        .remaining(remaining)
                        .memberCouponStatus(status)
                        .build();
                return memberCouponRepository.save(memberCoupon);
            }

            private Delivery generateDelivery(Member member, int i) {
                Delivery delivery = Delivery.builder()
                        .deliveryNumber("cj023923423" + i)
                        .recipient(Recipient.builder()
                                .name(member.getName())
                                .phone(member.getPhoneNumber())
                                .zipcode(member.getAddress().getZipcode())
                                .street(member.getAddress().getStreet())
                                .detailAddress(member.getAddress().getDetailAddress())
                                .build())
                        .departureDate(LocalDateTime.now().minusDays(4))
                        .arrivalDate(LocalDateTime.now().minusDays(1))
                        .status(DeliveryStatus.DELIVERY_START)
                        .nextDeliveryDate(LocalDate.now().plusDays(2))
                        .request("안전배송 부탁드립니다.")
                        .build();
                deliveryRepository.save(delivery);
                return delivery;
            }

            private SnackAnalysis getSnackAnalysis(Dog dog) {
                double avgSnackCountInLargeDog = getAvgSnackByDogSize(DogSize.LARGE);
                double avgSnackCountInMiddleDog = getAvgSnackByDogSize(DogSize.MIDDLE);
                double avgSnackCountInSmallDog = getAvgSnackByDogSize(DogSize.SMALL);

                int mySnackCount = getMySnackCount(dog);

                SnackAnalysis snackAnalysis = SnackAnalysis.builder()
                        .avgSnackCountInLargeDog(avgSnackCountInLargeDog)
                        .avgSnackCountInMiddleDog(avgSnackCountInMiddleDog)
                        .avgSnackCountInSmallDog(avgSnackCountInSmallDog)
                        .mySnackCount(mySnackCount)
                        .build();
                return snackAnalysis;
            }

            private int getMySnackCount(Dog dog) {
                int mySnackCount;

                switch (dog.getSnackCountLevel()) {
                    case LITTLE: mySnackCount = 1;
                        break;
                    case NORMAL: mySnackCount = 2;
                        break;
                    default: mySnackCount = 3;
                        break;
                }

                return mySnackCount;
            }

            private double getAvgSnackByDogSize(DogSize dogSize) {
                List<String> snackGroupByDogSize = dogRepository.findSnackGroupByDogSize(dogSize);

                double sum = 0.0;

                for (String s : snackGroupByDogSize) {
                    double d = Double.valueOf(s);
                    sum += d;
                }
                return Math.round(sum / snackGroupByDogSize.size() * 10.0) / 10.0;
            }

            private WalkingAnalysis getWalkingAnalysis(Member member, Dog dog) {
                List<Long> ranks = dogRepository.findRanksById(dog.getId());
                int rank = 1;
                for (Long id : ranks) {
                    if (id == dog.getId()) {
                        break;
                    }
                    rank++;
                }

                double highRankPercent = Math.round((double) rank / ranks.size() * 1000.0) / 10.0;

                double totalWalkingTime = dog.getDogActivity().getWalkingTimePerOneTime() * dog.getDogActivity().getWalkingCountPerWeek();
                double avgWalkingTimeInCity = dogRepository.findAvgTotalWalkingTimeByCity(member.getAddress().getCity());
                double avgTotalWalkingTimeByAge = dogRepository.findAvgTotalWalkingTimeByAge(Math.floor(dog.getStartAgeMonth() / 12));
                double avgWalkingTimeInDogSize = dogRepository.findAvgTotalWalkingTimeByDogSize(dog.getDogSize());

                WalkingAnalysis walkingAnalysis = WalkingAnalysis.builder()
                        .highRankPercent(highRankPercent)
                        .walkingCountPerWeek(dog.getDogActivity().getWalkingCountPerWeek())
                        .totalWalingTime(Math.round(totalWalkingTime * 10.0) / 10.0)
                        .avgWalkingTimeInCity(Math.round(avgWalkingTimeInCity * 10.0) / 10.0)
                        .avgWalkingTimeInAge(Math.round(avgTotalWalkingTimeByAge * 10.0) / 10.0)
                        .avgWalkingTimeInDogSize(Math.round(avgWalkingTimeInDogSize * 10.0) / 10.0)
                        .build();
                return walkingAnalysis;
            }

            private ActivityAnalysis getActivityAnalysis(DogSize dogSize, Dog dog) {
                List<String> activityGroup = dogRepository.findActivityGroupByDogSize(dogSize);

                int activityGroupOneCount = 0;
                int activityGroupTwoCount = 0;
                int activityGroupThreeCount = 0;
                int activityGroupFourCount = 0;
                int activityGroupFiveCount = 0;

                double sum = 0.0;

                for (String s : activityGroup) {
                    switch (s) {
                        case "1": activityGroupOneCount++;
                            break;
                        case "2": activityGroupTwoCount++;
                            break;
                        case "3": activityGroupThreeCount++;
                            break;
                        case "4": activityGroupFourCount++;
                            break;
                        case "5": activityGroupFiveCount++;
                            break;
                        default:
                            break;
                    }
                    sum += Double.valueOf(s);
                }

                ActivityLevel avgActivityLevel = getAvgActivityLevel(activityGroup, sum);

                int myActivityGroup = getMyActivityGroup(dog);

                ActivityAnalysis activityAnalysis = ActivityAnalysis.builder()
                        .avgActivityLevel(avgActivityLevel)
                        .activityGroupOneCount(activityGroupOneCount)
                        .activityGroupTwoCount(activityGroupTwoCount)
                        .activityGroupThreeCount(activityGroupThreeCount)
                        .activityGroupFourCount(activityGroupFourCount)
                        .activityGroupFiveCount(activityGroupFiveCount)
                        .myActivityGroup(myActivityGroup)
                        .build();
                return activityAnalysis;
            }

            private ActivityLevel getAvgActivityLevel(List<String> activityGroup, double sum) {
                ActivityLevel avgActivityLevel;

                int round = (int) Math.round(sum / activityGroup.size());
                switch (round) {
                    case 1:
                        avgActivityLevel = ActivityLevel.VERY_LITTLE;
                        break;
                    case 2:
                        avgActivityLevel = ActivityLevel.LITTLE;
                        break;
                    case 3:
                        avgActivityLevel = ActivityLevel.NORMAL;
                        break;
                    case 4:
                        avgActivityLevel = ActivityLevel.MUCH;
                        break;
                    default:
                        avgActivityLevel = ActivityLevel.VERY_MUCH;
                        break;
                }
                return avgActivityLevel;
            }

            private int getMyActivityGroup(Dog dog) {
                int myActivityGroup;

                switch (dog.getDogActivity().getActivityLevel()) {
                    case VERY_LITTLE: myActivityGroup = 1;
                        break;
                    case LITTLE: myActivityGroup = 2;
                        break;
                    case NORMAL: myActivityGroup = 3;
                        break;
                    case MUCH: myActivityGroup = 4;
                        break;
                    default: myActivityGroup = 5;
                        break;
                }
                return myActivityGroup;
            }

            private WeightAnalysis getWeightAnalysis(DogSize dogSize, BigDecimal weight) {
                double avgWeightByDogSize = dogRepository.findAvgWeightByDogSize(dogSize);

                double avgWeight = Math.round(avgWeightByDogSize * 10.0) / 10.0;

                double fattestWeightByDogSize = dogRepository.findFattestWeightByDogSize(dogSize);
                double lightestWeight = dogRepository.findLightestWeightByDogSize(dogSize);

                double weightRange = Math.round(((fattestWeightByDogSize-lightestWeight)/5.0) * 10.0) / 10.0;

                List<String> weightGroup = dogRepository.findWeightGroupByDogSize(dogSize, lightestWeight, weightRange);

                int weightGroupOneCount = 0;
                int weightGroupTwoCount = 0;
                int weightGroupThreeCount = 0;
                int weightGroupFourCount = 0;
                int weightGroupFiveCount = 0;

                for (String s : weightGroup) {
                    switch (s) {
                        case "1": weightGroupOneCount++;
                            break;
                        case "2": weightGroupTwoCount++;
                            break;
                        case "3": weightGroupThreeCount++;
                            break;
                        case "4": weightGroupFourCount++;
                            break;
                        case "5": weightGroupFiveCount++;
                            break;
                        default:
                            break;
                    }
                }

                int myWeightGroup = getMyWeightGroup(weight, lightestWeight, weightRange);

                WeightAnalysis weightAnalysis = WeightAnalysis.builder()
                        .avgWeight(avgWeight)
                        .weightGroupOneCount(weightGroupOneCount)
                        .weightGroupTwoCount(weightGroupTwoCount)
                        .weightGroupThreeCount(weightGroupThreeCount)
                        .weightGroupFourCount(weightGroupFourCount)
                        .weightGroupFiveCount(weightGroupFiveCount)
                        .myWeightGroup(myWeightGroup)
                        .weightInLastReport(weight)
                        .build();

                return weightAnalysis;
            }

            private int getMyWeightGroup(BigDecimal weight, double lightestWeight, double weightRange) {
                int myWeightGroup;

                if (includedInRange(weight, lightestWeight + weightRange)) {
                    myWeightGroup = 1;
                } else if(includedInRange(weight, lightestWeight + weightRange * 2.0)) {
                    myWeightGroup = 2;
                } else if(includedInRange(weight, lightestWeight + weightRange * 3.0)) {
                    myWeightGroup = 3;
                } else if (includedInRange(weight, lightestWeight + weightRange * 4.0)) {
                    myWeightGroup = 4;
                } else {
                    myWeightGroup = 5;
                }
                return myWeightGroup;
            }

            private boolean includedInRange(BigDecimal weight, double weightRange) {
                int compare = weight.compareTo(BigDecimal.valueOf(weightRange));

                if (compare <= 0) {
                    return true;
                }
                return false;
            }

            private AgeAnalysis getAgeAnalysis(Long startAgeMonth) {
                double avgAgeMonth = dogRepository.findAvgStartAgeMonth();

                int oldestMonth = dogRepository.findOldestMonth();

                long monthRange = Math.round(oldestMonth / 5.0);

                int avgMonth = (int) Math.round(avgAgeMonth);

                List<String> ageGroup = dogRepository.findAgeGroup(monthRange);

                int ageGroupOneCount = 0;
                int ageGroupTwoCount = 0;
                int ageGroupThreeCount = 0;
                int ageGroupFourCount = 0;
                int ageGroupFiveCount = 0;

                for (String s : ageGroup) {
                    switch (s) {
                        case "1": ageGroupOneCount++;
                            break;
                        case "2": ageGroupTwoCount++;
                            break;
                        case "3": ageGroupThreeCount++;
                            break;
                        case "4": ageGroupFourCount++;
                            break;
                        case "5": ageGroupFiveCount++;
                            break;
                        default:
                            break;
                    }
                }

                int myAgeGroup = getMyAgeGroup(startAgeMonth, monthRange);

                AgeAnalysis ageAnalysis = AgeAnalysis.builder()
                        .avgAgeMonth(avgMonth)
                        .ageGroupOneCount(ageGroupOneCount)
                        .ageGroupTwoCount(ageGroupTwoCount)
                        .ageGroupThreeCount(ageGroupThreeCount)
                        .ageGroupFourCount(ageGroupFourCount)
                        .ageGroupFiveCount(ageGroupFiveCount)
                        .myAgeGroup(myAgeGroup)
                        .myStartAgeMonth(startAgeMonth)
                        .build();
                return ageAnalysis;
            }

            private int getMyAgeGroup(Long startAgeMonth, long monthRange) {
                int myAgeGroup;

                if (startAgeMonth < monthRange) {
                    myAgeGroup = 1;
                } else if (startAgeMonth < monthRange * 2) {
                    myAgeGroup = 2;
                } else if (startAgeMonth < monthRange * 3) {
                    myAgeGroup = 3;
                } else if (startAgeMonth < monthRange * 4) {
                    myAgeGroup = 4;
                } else {
                    myAgeGroup = 5;
                }
                return myAgeGroup;
            }

            private DogActivity getDogActivity(DogSaveRequestDto requestDto) {
                return new DogActivity(requestDto.getActivityLevel(), Integer.valueOf(requestDto.getWalkingCountPerWeek()), Double.valueOf(requestDto.getWalkingTimePerOneTime()));
            }

            private FoodAnalysis getDogAnalysis(DogSaveRequestDto requestDto, Recipe recipe, DogSize dogSize, Long startAge, boolean oldDog, boolean neutralization, DogStatus dogStatus, ActivityLevel activityLevel, SnackCountLevel snackCountLevel) {
                BigDecimal rootVar = BigDecimal.valueOf(70.0);
                BigDecimal standardVar = getStandardVar(dogSize, startAge, oldDog, neutralization, dogStatus);

                BigDecimal rootXWeightX075 = rootVar.multiply(BigDecimal.valueOf(Math.pow(new Double(requestDto.getWeight()), 0.75)));

                Setting setting = settingRepository.findAll().get(0);
                ActivityConstant activityConstant = setting.getActivityConstant();
                SnackConstant snackConstant = setting.getSnackConstant();

                BigDecimal activityVar = getActivityVar(activityLevel, activityConstant);

                BigDecimal snackVar = getSnackVar(snackCountLevel, snackConstant);

                BigDecimal recommendKcal = rootXWeightX075.multiply(standardVar.multiply(activityVar.multiply(snackVar))).divide(BigDecimal.valueOf(10000.0))
                        .setScale(4, BigDecimal.ROUND_HALF_UP);

                BigDecimal gramPerKcal = recipe.getGramPerKcal();

                BigDecimal oneDayRecommendGram = gramPerKcal.multiply(recommendKcal).setScale(0,BigDecimal.ROUND_HALF_UP);

                BigDecimal oneMealRecommendGram = oneDayRecommendGram.divide(BigDecimal.valueOf(2)).setScale(0,BigDecimal.ROUND_HALF_UP);

                FoodAnalysis foodAnalysis = new FoodAnalysis(recommendKcal, oneDayRecommendGram, oneMealRecommendGram);
                return foodAnalysis;
            }

            private BigDecimal getSnackVar(SnackCountLevel snackCountLevel, SnackConstant snackConstant) {
                switch (snackCountLevel) {
                    case LITTLE: return BigDecimal.valueOf(100.0).add(snackConstant.getSnackLittle());
                    case NORMAL: return BigDecimal.valueOf(100.0);
                    case MUCH: return BigDecimal.valueOf(100.0).subtract(snackConstant.getSnackMuch());
                    default: return BigDecimal.valueOf(0);
                }
            }



            private BigDecimal getActivityVar(ActivityLevel activityLevel, ActivityConstant activityConstant) {
                switch (activityLevel) {
                    case VERY_LITTLE: return BigDecimal.valueOf(100.0).subtract(activityConstant.getActivityVeryLittle());
                    case LITTLE: return BigDecimal.valueOf(100.0).subtract(activityConstant.getActivityLittle());
                    case NORMAL: return BigDecimal.valueOf(100.0);
                    case MUCH: return BigDecimal.valueOf(100.0).add(activityConstant.getActivityMuch());
                    case VERY_MUCH: return BigDecimal.valueOf(100.0).add(activityConstant.getActivityVeryMuch());
                    default: return BigDecimal.valueOf(0);
                }
            }

            private BigDecimal getStandardVar(DogSize dogSize, Long age, boolean oldDog, boolean neutralization, DogStatus dogStatus) {
                BigDecimal var;

                if (oldDog == false) {
                    if (dogSize == DogSize.LARGE) { // 대형견
                        if (age <= 18L) { // 어린 개
                            var = BigDecimal.valueOf(YOUNG_DOG);
                        } else{
                            var = switchDogStatus(neutralization, dogStatus);
                        }
                    } else { // 소,중형견
                        if (age <= 12L) { // 어린 개
                            var = BigDecimal.valueOf(YOUNG_DOG);
                        } else{
                            var = switchDogStatus(neutralization, dogStatus);
                        }
                    }
                } else { // 노견
                    var = BigDecimal.valueOf(OLD_DOG);
                }

                return var;
            }

            private BigDecimal switchDogStatus(boolean neutralization, DogStatus dogStatus) {
                switch (dogStatus) {
                    case HEALTHY: return neutralization ? BigDecimal.valueOf(NEUTRALIZATION_TRUE) : BigDecimal.valueOf(NEUTRALIZATION_FALSE);
                    case NEED_DIET: return BigDecimal.valueOf(NEED_DIET);
                    case OBESITY: return BigDecimal.valueOf(OBESITY);
                    case PREGNANT: return BigDecimal.valueOf(PREGNANT);
                    case LACTATING: return BigDecimal.valueOf(LACTATING);
                    default: return BigDecimal.valueOf(0);
                }
            }


            public Long getTerm(String birthday) {
                Long month = 0L;
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate startDate = LocalDate.parse(birthday, formatter);
                    LocalDate endDate = LocalDate.now();
                    month = ChronoUnit.MONTHS.between(startDate, endDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return month;
            }

            private Dog generateDogRepresentativeBeforePaymentSubscribe(Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
                Dog dog = Dog.builder()
                        .member(member)
                        .name("대표견")
                        .birth("202103")
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

            private Dog generateDogBeforePaymentSubscribe(Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
                Dog dog = Dog.builder()
                        .member(member)
                        .name("대표견")
                        .birth("202103")
                        .representative(false)
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

            private Subscribe generateSubscribeBeforePayment(Dog dog, SubscribePlan plan, SubscribeStatus status, int nextPaymentPrice) {
                List<Recipe> recipes = recipeRepository.findAll();

                Subscribe subscribe = Subscribe.builder()
                        .status(status)
                        .plan(plan)
                        .subscribeCount(0)
                        .nextPaymentPrice(nextPaymentPrice)
                        .build();
                subscribeRepository.save(subscribe);

                SubscribeRecipe subscribeRecipe1 = generateSubscribeRecipe(recipes.get(0), subscribe);
                SubscribeRecipe subscribeRecipe2 = generateSubscribeRecipe(recipes.get(1), subscribe);
                subscribe.addSubscribeRecipe(subscribeRecipe1);
                subscribe.addSubscribeRecipe(subscribeRecipe2);

                dog.setSubscribe(subscribe);

                return subscribeRepository.save(subscribe);
            }

            private SubscribeRecipe generateSubscribeRecipe(Recipe recipe, Subscribe subscribe) {
                SubscribeRecipe subscribeRecipe = SubscribeRecipe.builder()
                        .recipe(recipe)
                        .subscribe(subscribe)
                        .build();

                return subscribeRecipeRepository.save(subscribeRecipe);
            }

            private Item generateItem(int i) {
                Item item = Item.builder()
                        .itemType(ItemType.RAW)
                        .name("상품" + i)
                        .description("상품설명" + i)
                        .originalPrice(10000)
                        .discountType(DiscountType.FLAT_RATE)
                        .discountDegree(1000)
                        .salePrice(9000)
                        .inStock(true)
                        .remaining(999)
                        .contents("상세 내용" + i)
                        .deliveryFree(true)
                        .status(ItemStatus.LEAKED)
                        .build();
                return itemRepository.save(item);
            }
            private ItemImage generateItemImage(Item item, int i) {
                ItemImage itemImage = ItemImage.builder()
                        .item(item)
                        .leakOrder(i)
                        .folder("folder" + i)
                        .filename("filename" + i + ".jpg")
                        .build();
                return itemImageRepository.save(itemImage);
            }
            private void generateOrderItemsAndOrder(Member member, Item item1, Item item2) {
                Order order = GeneralOrder.builder()
                        .member(member)
                        .build();
                Order savedOrder = orderRepository.save(order);


                generateOrderItem(item1, (GeneralOrder) savedOrder);
                generateOrderItem(item2, (GeneralOrder) savedOrder);
            }
            private void generateOrderItem(Item item1, GeneralOrder savedOrder) {
                OrderItem orderItem = OrderItem.builder()
                        .generalOrder(savedOrder)
                        .item(item1)
                        .status(OrderStatus.CONFIRM)
                        .build();
                orderItemRepository.save(orderItem);
            }
            private void generateWriteableReviewSubscribe(Member member) {
                List<Recipe> recipes = recipeRepository.findAll();

                Subscribe savedSubscribe = generateSubscribe();

                generateSubscribeOrder(member, savedSubscribe);

                Dog savedDog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE,
                        1, 1, SnackCountLevel.NORMAL, recipes.get(0));
                savedDog.setSubscribe(savedSubscribe);
            }
            private Subscribe generateSubscribe() {
                Subscribe subscribe = Subscribe.builder()
                        .status(SubscribeStatus.SUBSCRIBING)
                        .writeableReview(true)
                        .build();
                return subscribeRepository.save(subscribe);
            }
            private Order generateSubscribeOrder(Member member, Subscribe savedSubscribe) {
                Order order = SubscribeOrder.builder()
                        .member(member)
                        .subscribe(savedSubscribe)
                        .orderStatus(OrderStatus.CONFIRM)
                        .build();
                Order savedOrder = orderRepository.save(order);
                return savedOrder;
            }
            private Dog generateDog(Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel, Recipe recipe) {
                Dog dog = Dog.builder()
                        .member(member)
                        .name("구독강아지")
                        .startAgeMonth(startAgeMonth)
                        .gender(Gender.MALE)
                        .oldDog(false)
                        .dogSize(dogSize)
                        .weight(new BigDecimal(weight))
                        .dogActivity(new DogActivity(activitylevel, walkingCountPerWeek, walkingTimePerOneTime))
                        .dogStatus(DogStatus.HEALTHY)
                        .snackCountLevel(snackCountLevel)
                        .recommendRecipe(recipe)
                        .build();
                return dogRepository.save(dog);
            }





            private void generateArticle(int i) {
                Blog blog = generateBlog(i);
                Article article = Article.builder()
                        .number(i)
                        .blog(blog)
                        .build();
                articleRepository.save(article);
            }
            private Blog generateBlog(int i) {
                Blog blog = Blog.builder()
                        .status(BlogStatus.LEAKED)
                        .title("제목" + i)
                        .category(BlogCategory.HEALTH)
                        .contents("컨텐츠 내용")
                        .build();
                return blogRepository.save(blog);
            }

            private void generateMemberCoupon(Member member, Coupon subsCoupon) {
                MemberCoupon memberCoupon = MemberCoupon.builder()
                        .member(member)
                        .coupon(subsCoupon)
                        .expiredDate(LocalDateTime.of(2023, 12, 31, 23, 59, 59))
                        .remaining(3)
                        .memberCouponStatus(CouponStatus.ACTIVE)
                        .build();

                memberCouponRepository.save(memberCoupon);
            }

            private void generateBannerMain(int i) throws IOException, URISyntaxException {

//                MultipartFile mFilePc = getMultipartFile(uploadRootPath+"/default/mainBanner" + i + ".jpg");

                //======================= 서버 업로드 버전 ====================
                MultipartFile mFilePc = getMultipartFile(uploadRootPath+"/default/mainBanner" + i + ".jpg");

                MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder()
                        .name("메인배너" + i)
                        .targets(BannerTargets.ALL)
                        .status(BannerStatus.LEAKED)
                        .pcLinkUrl("")
                        .mobileLinkUrl("")
                        .build();

                bannerService.saveMainBanner(requestDto, mFilePc, mFilePc);
            }

            private void generateMypageBanner() throws IOException, URISyntaxException {

//                MultipartFile mFilePc = getMultipartFile("C:/upload/default/mypageBanner_pc.png");
//                MultipartFile mFileMobile = getMultipartFile("C:/upload/default/mypageBanner_mobile.png");

                //======================= 서버 업로드 버전 =======================
                MultipartFile mFilePc = getMultipartFile(uploadRootPath + "/default/mypageBanner_pc.png");
                MultipartFile mFileMobile = getMultipartFile(uploadRootPath + "/default/mypageBanner_mobile.png");

                MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                        .name("마이페이지 배너")
                        .status(BannerStatus.LEAKED)
                        .pcLinkUrl("")
                        .mobileLinkUrl("")
                        .build();
                bannerService.saveMyPageBanner(requestDto, mFilePc, mFileMobile);
            }

            private Coupon generateCouponAuto(String name, String description, DiscountType discountType, int discountDegree, int availableMinPrice, CouponTarget couponTarget) {
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

            private Member generateMember(String email, String name, String password, String phoneNumber, Gender gender, Grade grade, int reward, boolean recommend, String roles, boolean isSubscribe) {
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
                        .accumulatedAmount(1000000)
                        .accumulatedSubscribe(3)
                        .isSubscribe(isSubscribe)
                        .firstReward(new FirstReward(recommend, recommend))
                        .roles(roles)
                        .build();

                return memberRepository.save(member);
            }

            private void generateSetting() {
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

            private Recipe generateRecipe(String 램비프, String ingredients, String 건강한_성장과_영양보충, String filename1, String filename2) {
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

            private Dog generateDogRepresentative(Member admin,Subscribe subscribe, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
                Dog dog = Dog.builder()
                        .member(admin)
                        .name("대표견")
                        .subscribe(subscribe)
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

            private Dog generateDog(Member admin, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
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