package com.bi.barfdog.api;

import com.bi.barfdog.api.barfDto.SendInviteSmsDto;
import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.directsend.PhoneAuthRequestDto;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.banner.*;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.domain.member.Agreement;
import com.bi.barfdog.domain.member.FirstReward;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.*;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.orderItem.SelectOption;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.review.*;
import com.bi.barfdog.domain.reward.*;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.surveyReport.*;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.api.memberDto.jwt.JwtProperties;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.banner.BannerRepository;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogPictureRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.orderItem.SelectOptionRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.review.BestReviewRepository;
import com.bi.barfdog.repository.review.ItemReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.repository.review.SubscribeReviewRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import com.bi.barfdog.snsLogin.ConnectSnsRequestDto;
import com.bi.barfdog.snsLogin.NaverLoginDto;
import com.bi.barfdog.snsLogin.SnsProvider;
import com.bi.barfdog.snsLogin.SnsResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static com.bi.barfdog.config.finalVariable.StandardVar.*;
import static com.bi.barfdog.config.finalVariable.StandardVar.LACTATING;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class IndexApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;
    @Autowired
    AppProperties appProperties;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemImageRepository itemImageRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    DogRepository dogRepository;
    @Autowired
    SubscribeRepository subscribeRepository;
    @Autowired
    ReviewImageRepository reviewImageRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ItemReviewRepository itemReviewRepository;
    @Autowired
    SubscribeReviewRepository subscribeReviewRepository;
    @Autowired
    BestReviewRepository bestReviewRepository;
    @Autowired
    MemberCouponRepository memberCouponRepository;
    @Autowired
    SelectOptionRepository selectOptionRepository;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    SettingRepository settingRepository;
    @Autowired
    DogPictureRepository dogPictureRepository;

    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    BannerRepository bannerRepository;
    @Autowired
    SurveyReportRepository surveyReportRepository;

    @Autowired BCryptPasswordEncoder bCryptPasswordEncoder;

    @Before
    public void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();
        surveyReportRepository.deleteAll();
        dogRepository.deleteAll();
    }

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));


    // TODO: 2022-07-29 대쉬보드 MemoryDB -> MariaDB
    @Ignore
    @Test
    @DisplayName("관리자 대시보드 조회")
    public void queryAdminDashBoard() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1, 2).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.FAILED);

            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
            generateSubscribeOrder(member, i, OrderStatus.PAYMENT_DONE);

            generateGeneralOrder(member, i, OrderStatus.DELIVERY_START);
            generateGeneralOrder(member, i, OrderStatus.DELIVERY_START);
            generateSubscribeOrder(member, i, OrderStatus.DELIVERY_START);

            generateGeneralOrder(member, i, OrderStatus.CANCEL_REQUEST);
            generateGeneralOrder(member, i, OrderStatus.CANCEL_REQUEST);
            generateSubscribeOrder(member, i, OrderStatus.CANCEL_REQUEST);
            generateSubscribeOrder(member, i, OrderStatus.CANCEL_REQUEST);

            generateGeneralOrder(member, i, OrderStatus.RETURN_REQUEST);
            generateGeneralOrder(member, i, OrderStatus.RETURN_REQUEST);
            generateGeneralOrder(member, i, OrderStatus.RETURN_REQUEST);
            generateSubscribeOrder(member, i, OrderStatus.RETURN_REQUEST);
            generateSubscribeOrder(member, i, OrderStatus.RETURN_REQUEST);

            generateGeneralOrder(member, i, OrderStatus.EXCHANGE_REQUEST);
            generateGeneralOrder(member, i, OrderStatus.EXCHANGE_REQUEST);
            generateGeneralOrder(member, i, OrderStatus.EXCHANGE_REQUEST);
            generateSubscribeOrder(member, i, OrderStatus.EXCHANGE_REQUEST);
            generateSubscribeOrder(member, i, OrderStatus.EXCHANGE_REQUEST);
            generateSubscribeOrder(member, i, OrderStatus.EXCHANGE_REQUEST);
        });

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String from = now.minusDays(7).format(formatter);
        String to = now.format(formatter);

        //when & then
        mockMvc.perform(get("/api/admin/dashBoard")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("from", from)
                        .param("to", to))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_dashBoard",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestParameters(
                                parameterWithName("from").description("검색 날짜 from 'yyyy-MM-dd'"),
                                parameterWithName("to").description("검색 날짜 to 'yyyy-MM-dd'")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("newOrderCount").description("신규 주문 수"),
                                fieldWithPath("newMemberCount").description("신규 가입 수"),
                                fieldWithPath("subscribePendingCount").description("구독 보류 수"),
                                fieldWithPath("orderStatusCountDtoList[0].orderstatus").description("주문 상태 [CANCEL_REQUEST, DELIVERY_START, EXCHANGE_REQUEST" +
                                        ", FAILED, PAYMENT_DONE, RETURN_REQUEST"),
                                fieldWithPath("orderStatusCountDtoList[0].count").description("주문 상태에 해당하는 수"),
                                fieldWithPath("generalOrderCountByMonthList[0].month").description("년월"),
                                fieldWithPath("generalOrderCountByMonthList[0].generalCount").description("년월에 해당하는 일반 주문"),
                                fieldWithPath("subscribeOrderCountByMonthList[0].month").description("년월"),
                                fieldWithPath("subscribeOrderCountByMonthList[0].subscribeCount").description("년월에 해당하는 구독 주문"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }


//    @Ignore
    @Test
    @DisplayName("정상적으로 home 화면에 필요한 값 조회")
    public void homePage() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,4).forEach(i -> {
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN);
            generateBestReview(adminReview, i);
        });
        IntStream.range(4,7).forEach(i -> {
            Review approvalReview = generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateBestReview(approvalReview, i);
        });

        TopBanner topBanner = TopBanner.builder()
                .name("top Banner name")
                .pcLinkUrl("pc link url")
                .mobileLinkUrl("mobile link url")
                .status(BannerStatus.LEAKED)
                .backgroundColor("red")
                .fontColor("write")
                .build();
        bannerRepository.save(topBanner);

        //when & then
        mockMvc.perform(get("/api/home")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("home_page",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("topBannerDto.name").description("상단 배너 이름"),
                                fieldWithPath("topBannerDto.backgroundColor").description("상단 배너 배경 색"),
                                fieldWithPath("topBannerDto.fontColor").description("상단 배너 폰트 색"),
                                fieldWithPath("topBannerDto.pcLinkUrl").description("상단 배너 클릭 pc 링크 url"),
                                fieldWithPath("topBannerDto.mobileLinkUrl").description("상단 배너 클릭 mobile 링크 url"),
                                fieldWithPath("mainBannerDtoList[0].id").description("메인 배너 id"),
                                fieldWithPath("mainBannerDtoList[0].leakedOrder").description("메인 배너 노출 순서"),
                                fieldWithPath("mainBannerDtoList[0].name").description("메인 배너 이름"),
                                fieldWithPath("mainBannerDtoList[0].pcFilename").description("pc 파일 파일 이름"),
                                fieldWithPath("mainBannerDtoList[0].pcImageUrl").description("pc 이미지 url"),
                                fieldWithPath("mainBannerDtoList[0].pcLinkUrl").description("메인배너 클릭 pc 링크 url"),
                                fieldWithPath("mainBannerDtoList[0].mobileFilename").description("mobile 이미지 파일 이름"),
                                fieldWithPath("mainBannerDtoList[0].mobileImageUrl").description("mobile 이미지 url"),
                                fieldWithPath("mainBannerDtoList[0].mobileLinkUrl").description("메인배너 클릭 mobile 링크 url"),
                                fieldWithPath("recipeDtoList[0].id").description("레시피 id"),
                                fieldWithPath("recipeDtoList[0].name").description("레시피 이름"),
                                fieldWithPath("recipeDtoList[0].description").description("레시피 설명"),
                                fieldWithPath("recipeDtoList[0].uiNameKorean").description("레시피 한글 UI"),
                                fieldWithPath("recipeDtoList[0].uiNameEnglish").description("레시피 영어 UI"),
                                fieldWithPath("recipeDtoList[0].filename1").description("레시피 썸네일1 이미지 파일 이름"),
                                fieldWithPath("recipeDtoList[0].imageUrl1").description("레시피 썸네일1 이미지 url "),
                                fieldWithPath("recipeDtoList[0].filename2").description("레시피 썸네일2 이미지 파일 이름"),
                                fieldWithPath("recipeDtoList[0].imageUrl2").description("레시피 썸네일2 이미지 url"),
                                fieldWithPath("queryBestReviewsDtoList[0].id").description("리뷰 id"),
                                fieldWithPath("queryBestReviewsDtoList[0].imageUrl").description("리뷰 이미지 url"),
                                fieldWithPath("queryBestReviewsDtoList[0].leakedOrder").description("리뷰 노출 순서"),
                                fieldWithPath("queryBestReviewsDtoList[0].contents").description("리뷰 내용"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("홈 화면 조회 시 메인 배너 GUEST 일 경우")
    public void homePage_guest() throws Exception {
        //given

        bannerRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,4).forEach(i -> {
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN);
            generateBestReview(adminReview, i);
        });
        IntStream.range(4,7).forEach(i -> {
            Review approvalReview = generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateBestReview(approvalReview, i);
        });

        TopBanner topBanner = TopBanner.builder()
                .name("top Banner name")
                .pcLinkUrl("pc link url")
                .mobileLinkUrl("mobile link url")
                .status(BannerStatus.LEAKED)
                .backgroundColor("red")
                .fontColor("write")
                .build();
        bannerRepository.save(topBanner);

        IntStream.range(1, 5).forEach(i -> {
            generateMainBanner(i, BannerTargets.ALL);
        });
        IntStream.range(5, 8).forEach(i -> {
            generateMainBanner(i, BannerTargets.GUEST);
        });
        IntStream.range(8, 12).forEach(i -> {
            generateMainBanner(i, BannerTargets.USER);
        });
        IntStream.range(13, 17).forEach(i -> {
            generateMainBanner(i, BannerTargets.SUBSCRIBER);
        });




        //when & then
        mockMvc.perform(get("/api/home")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("mainBannerDtoList", hasSize(7)))
        ;

    }

    @Test
    @DisplayName("홈 화면 조회 시 메인 배너 일반 USER 일 경우")
    public void homePage_USER() throws Exception {
        //given

        bannerRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,4).forEach(i -> {
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN);
            generateBestReview(adminReview, i);
        });
        IntStream.range(4,7).forEach(i -> {
            Review approvalReview = generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateBestReview(approvalReview, i);
        });

        TopBanner topBanner = TopBanner.builder()
                .name("top Banner name")
                .pcLinkUrl("pc link url")
                .mobileLinkUrl("mobile link url")
                .status(BannerStatus.LEAKED)
                .backgroundColor("red")
                .fontColor("write")
                .build();
        bannerRepository.save(topBanner);

        IntStream.range(1, 5).forEach(i -> {
            generateMainBanner(i, BannerTargets.ALL);
        });
        IntStream.range(5, 8).forEach(i -> {
            generateMainBanner(i, BannerTargets.GUEST);
        });
        IntStream.range(8, 12).forEach(i -> {
            generateMainBanner(i, BannerTargets.USER);
        });
        IntStream.range(13, 17).forEach(i -> {
            generateMainBanner(i, BannerTargets.SUBSCRIBER);
        });




        //when & then
        mockMvc.perform(get("/api/home")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("mainBannerDtoList", hasSize(12)))
        ;

    }

    @Test
    @DisplayName("홈 화면 조회 시 메인 배너 SUBSCRIBER 일 경우")
    public void homePage_SUBSCRIBER() throws Exception {
        //given

        bannerRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        member.subscribe();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,4).forEach(i -> {
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN);
            generateBestReview(adminReview, i);
        });
        IntStream.range(4,7).forEach(i -> {
            Review approvalReview = generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateBestReview(approvalReview, i);
        });

        TopBanner topBanner = TopBanner.builder()
                .name("top Banner name")
                .pcLinkUrl("pc link url")
                .mobileLinkUrl("mobile link url")
                .status(BannerStatus.LEAKED)
                .backgroundColor("red")
                .fontColor("write")
                .build();
        bannerRepository.save(topBanner);

        IntStream.range(1, 5).forEach(i -> {
            generateMainBanner(i, BannerTargets.ALL);
        });
        IntStream.range(5, 8).forEach(i -> {
            generateMainBanner(i, BannerTargets.GUEST);
        });
        IntStream.range(8, 12).forEach(i -> {
            generateMainBanner(i, BannerTargets.USER);
        });
        IntStream.range(12, 17).forEach(i -> {
            generateMainBanner(i, BannerTargets.SUBSCRIBER);
        });

        //when & then
        mockMvc.perform(get("/api/home")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("mainBannerDtoList", hasSize(13)))
        ;
    }

    @Test
    @DisplayName("마이페이지 상단 조회")
    public void queryMypage() throws Exception {
       //given
        memberCouponRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();

        Dog dog = generateDog(member, 1, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true);
        String dogName = dog.getName();
        generateDogPicture(dog);


        IntStream.range(1, 2).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
            generateGeneralOrder(member, i, OrderStatus.PRODUCING);
            generateGeneralOrder(member, i, OrderStatus.DELIVERY_READY);
            generateGeneralOrder(member, i, OrderStatus.DELIVERY_START);
        });
        IntStream.range(1,5).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.CANCEL_REQUEST);
            generateGeneralOrder(member, i, OrderStatus.CONFIRM);
        });

        IntStream.range(1,5).forEach(i -> {
            generateMemberCoupon(member, i, i, CouponStatus.ACTIVE);
        });
        IntStream.range(1,3).forEach(i -> {
            generateMemberCoupon(member, i, i, CouponStatus.INACTIVE);
        });


        //when & then
        mockMvc.perform(get("/api/mypage")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("mypageMemberDto.reward").value(reward))
                .andExpect(jsonPath("mypageDogDto.dogName").value(dogName))
                .andExpect(jsonPath("mypageDogDto.thumbnailUrl").isNotEmpty())
                .andExpect(jsonPath("deliveryCount").value(4))
                .andExpect(jsonPath("couponCount").value(4))
                .andDo(document("my_page",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("mypageMemberDto.id").description("유저 id"),
                                fieldWithPath("mypageMemberDto.memberName").description("유저 이름"),
                                fieldWithPath("mypageMemberDto.grade").description("유저 등급"),
                                fieldWithPath("mypageMemberDto.myRecommendationCode").description("유저 추천코드"),
                                fieldWithPath("mypageMemberDto.reward").description("보유 적립금"),
                                fieldWithPath("mypageDogDto.thumbnailUrl").description("강아지 사진 . 없으면 mypageDogDto.thumbnailUrl = null"),
                                fieldWithPath("mypageDogDto.dogName").description("대표견 이름 . 대표견 없으면 mypageDogDto = null"),
                                fieldWithPath("deliveryCount").description("배송 현황 건"),
                                fieldWithPath("couponCount").description("보유 쿠폰 수"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

    }

    @Test
    @DisplayName("마이페이지 상단 조회 대표견이 없을 경우")
    public void queryMypage_noDog() throws Exception {
        //given
        memberCouponRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();


        IntStream.range(1, 2).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
            generateGeneralOrder(member, i, OrderStatus.PRODUCING);
            generateGeneralOrder(member, i, OrderStatus.DELIVERY_READY);
            generateGeneralOrder(member, i, OrderStatus.DELIVERY_START);
        });
        IntStream.range(1,5).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.CANCEL_REQUEST);
            generateGeneralOrder(member, i, OrderStatus.CONFIRM);
        });

        IntStream.range(1,5).forEach(i -> {
            generateMemberCoupon(member, i, i, CouponStatus.ACTIVE);
        });
        IntStream.range(1,3).forEach(i -> {
            generateMemberCoupon(member, i, i, CouponStatus.INACTIVE);
        });


        //when & then
        mockMvc.perform(get("/api/mypage")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("mypageMemberDto.reward").value(reward))
                .andExpect(jsonPath("deliveryCount").value(4))
                .andExpect(jsonPath("couponCount").value(4))
                .andExpect(jsonPath("mypageDogDto").isEmpty())
        ;

    }

    @Test
    @DisplayName("마이페이지 상단 조회 대표견이 있으나 사진이 없는 경우")
    public void queryMypage_noPicture() throws Exception {
        //given
        memberCouponRepository.deleteAll();
        dogRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();

        Dog dog = generateDog(member, 1, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true);
        String dogName = dog.getName();


        //when & then
        mockMvc.perform(get("/api/mypage")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("mypageMemberDto.reward").value(reward))
                .andExpect(jsonPath("mypageDogDto.dogName").value(dogName))
                .andExpect(jsonPath("mypageDogDto.thumbnailUrl").isEmpty())
        ;

    }

    @Test
    @DisplayName("친구 초대 문자 보내기")
    public void sendInviteSms() throws Exception {
       //given

        SendInviteSmsDto requestDto = SendInviteSmsDto.builder()
                .name("김바프")
                .phone("01099038544")
                .homePageUrl("https://www.barfdog.com")
                .build();

        //when & then
        mockMvc.perform(post("/api/mypage/inviteSms")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("send_inviteSms",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestFields(
                                fieldWithPath("name").description("친구 이름"),
                                fieldWithPath("phone").description("친구 휴대전화번호 '010xxxxxxxx' '-'제거한 문자열"),
                                fieldWithPath("homePageUrl").description("문자로 보낼 바프독 홈 화면url 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("responseCode").description("응답코드 (200 이외의 값이면 다이렉트센드 내부 에러)"),
                                fieldWithPath("status").description("다이렉트 센드 상태 코드"),
                                fieldWithPath("msg").description("다이렉트 센드 상태 메시지"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }
    


    @Test
    @DisplayName("추천인 코드ㅇ/수신동의ㅇ 적립금 3000원으로 회원 가입이 완료되는 테스트")
    public void join() throws Exception {
       //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01012348544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .recommendCode(sampleMember.getMyRecommendationCode())
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("join",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("login").description("로그인 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("provider").description("간편로그인 제공사 ['naver'/'kakao'] , 연동 없으면 null"),
                                fieldWithPath("providerId").description("간편로그인 제공사 회원 고유id값, 없으면 null"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("email").description("회원 이메일(로그인 ID)"),
                                fieldWithPath("password").description("회원 비밀번호"),
                                fieldWithPath("confirmPassword").description("회원 비밀번호 확인"),
                                fieldWithPath("phoneNumber").description("휴대폰 번호 '010xxxxxxxx' -없는 문자열"),
                                fieldWithPath("address.zipcode").description("우편 번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명주소"),
                                fieldWithPath("address.detailAddress").description("상세주소"),
                                fieldWithPath("birthday").description("생년월일 'yyyymmdd' 길이 8 문자열"),
                                fieldWithPath("gender").description("성별 [MALE, FEMALE, NONE]"),
                                fieldWithPath("recommendCode").description("추천인 코드"),
                                fieldWithPath("agreement.servicePolicy").description("이용약관 동의 [true/false]"),
                                fieldWithPath("agreement.privacyPolicy").description("개인정보 제공 동의 [true/false]"),
                                fieldWithPath("agreement.receiveSms").description("sms 수신 여부 [true/false]"),
                                fieldWithPath("agreement.receiveEmail").description("email 수신 여부 [true/false]"),
                                fieldWithPath("agreement.over14YearsOld").description("14세 이상 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("name").description("가입한 유저 이름"),
                                fieldWithPath("email").description("가입한 유저 이메일"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.login.href").description("로그인 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member targetMember = memberRepository.findByMyRecommendationCode(sampleMember.getMyRecommendationCode()).get();
        Member findMember = memberRepository.findByEmail("verin4494@gmail.com").get();
        assertThat(findMember.getProvider()).isNull();
        assertThat(findMember.getProviderId()).isNull();

        Reward reward = rewardRepository.findByMember(findMember).get(0);
        assertThat(reward.getRewardType()).isEqualTo(RewardType.RECOMMEND);
        assertThat(reward.getRewardStatus()).isEqualTo(RewardStatus.SAVED);
        assertThat(reward.getTradeReward()).isEqualTo(RewardPoint.RECOMMEND);
        assertThat(reward.getName()).isEqualTo(RewardName.RECOMMEND + " ("+targetMember.getName()+")");
        assertThat(findMember.getFirstReward().isRecommend()).isTrue();
        assertThat(findMember.getReward()).isEqualTo(3000);
        assertThat(findMember.getRecommendCode()).isEqualTo(sampleMember.getMyRecommendationCode());
        assertThat(findMember.getAgreement().isReceiveSms()).isTrue();
        assertThat(findMember.getAgreement().isReceiveEmail()).isTrue();
    }


    @Test
    @DisplayName("추천인 코드ㅇ 적립금3000원으로 회원 가입이 완료되는 테스트")
    public void join_recommendCode() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01091234544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .recommendCode(sampleMember.getMyRecommendationCode())
                .agreement(new Agreement(true,true,false,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION));

        Member findMember = memberRepository.findByEmail("verin4494@gmail.com").get();
        assertThat(findMember.getReward()).isEqualTo(3000);
        assertThat(findMember.getRecommendCode()).isEqualTo(sampleMember.getMyRecommendationCode());
        assertThat(findMember.getAgreement().isReceiveSms()).isFalse();
        assertThat(findMember.getAgreement().isReceiveEmail()).isTrue();

        Reward reward = rewardRepository.findByMember(findMember).get(0);
        assertThat(reward.getRewardType()).isEqualTo(RewardType.RECOMMEND);
        assertThat(reward.getRewardStatus()).isEqualTo(RewardStatus.SAVED);
        assertThat(reward.getTradeReward()).isEqualTo(RewardPoint.RECOMMEND);
        assertThat(findMember.getFirstReward().isRecommend()).isTrue();


    }

    @Test
    @DisplayName("수신여부ㅇ 적립금0원으로 회원 가입이 완료되는 테스트")
    public void join_receive_agree() throws Exception {
        //Given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099030002")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION));

        Member findMember = memberRepository.findByEmail("verin4494@gmail.com").get();
        assertThat(findMember.getReward()).isEqualTo(0);
        assertThat(findMember.getAgreement().isReceiveSms()).isTrue();
        assertThat(findMember.getAgreement().isReceiveEmail()).isTrue();
    }

    @Test
    @DisplayName("수신여부x/추천인x 적립금 0원으로 회원 가입이 완료되는 테스트")
    public void join_no_point() throws Exception {
        //Given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038521")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,false,false,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION));

        Member findMember = memberRepository.findByEmail("verin4494@gmail.com").get();
        assertThat(findMember.getReward()).isEqualTo(0);
        assertThat(findMember.getAgreement().isReceiveSms()).isFalse();
        assertThat(findMember.getAgreement().isReceiveEmail()).isFalse();
    }

    @Test
    @DisplayName("간편로그인으로 회원 가입 하기")
    public void join_sns() throws Exception {
        //Given
        String provider = SnsProvider.NAVER;
        String providerId = "alskdjfolsdigjlssdlkfj";
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .provider(provider)
                .providerId(providerId)
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099030909")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,false,false,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION));

        Member findMember = memberRepository.findByEmail("verin4494@gmail.com").get();
        assertThat(findMember.getProvider()).isEqualTo(provider);
        assertThat(findMember.getProviderId()).isEqualTo(providerId);
        assertThat(findMember.getReward()).isEqualTo(0);
        assertThat(findMember.getAgreement().isReceiveSms()).isFalse();
        assertThat(findMember.getAgreement().isReceiveEmail()).isFalse();
    }

    @Test
    @DisplayName("부족한 입력값으로 회원 가입한 경우 bad request 나오는 테스트")
    public void join_Bad_Request() throws Exception {
       //Given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder().build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 서로 다를 경우 404 bad request 나오는 테스트")
    public void join_Passwords_Different_Bad_Request() throws Exception {
        //Given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("43214321")
                .phoneNumber("01012341234")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("email 중복으로 가입할 경우 409Conflict 나오는 테스트")
    public void join_Email_Duplicate_Conflict() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email(sampleMember.getEmail())
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("휴대폰번호 중복으로 가입할 경우 409Conflict 나오는 테스트")
    public void join_PhoneNumber_Duplicate_Conflict() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber(sampleMember.getPhoneNumber())
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/api/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

//    @Ignore
    @Test
    @DisplayName("정상적으로 휴대폰 인증 번호 보내기")
    public void phoneAuth() throws Exception {

        memberCouponRepository.deleteAll();
        dogRepository.deleteAll();
        memberRepository.deleteAll();

        em.flush();
        em.clear();

       //Given
        PhoneAuthRequestDto requestDto = PhoneAuthRequestDto.builder()
                .phoneNumber("01099038544")
                .build();

        //when & then
        mockMvc.perform(post("/api/join/phoneAuth")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("join_phoneAuth",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("phoneNumber").description("메일을 보낼 휴대폰 번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("authNumber").description("휴대폰 인증번호"),
                                fieldWithPath("responseCode").description("응답코드 (200 이외의 값이면 다이렉트센드 내부 에러)"),
                                fieldWithPath("status").description("다이렉트 센드 상태 코드"),
                                fieldWithPath("msg").description("다이렉트 센드 상태 메시지"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("폰 인증 번호 중복 시 Conflict 나오는 테스트")
    public void phoneAuth_Duplicate_Conflict() throws Exception {
       //Given
        Member member = generateSampleMember();

        PhoneAuthRequestDto requestDto = PhoneAuthRequestDto.builder()
                .phoneNumber(member.getPhoneNumber())
                .build();

        //when & then
        mockMvc.perform(post("/api/join/phoneAuth")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

//    @Test
//    @DisplayName("회원가입 휴대폰 인증 요청 중 내부 에러 발생시 500 응답하는 테스트")
//    public void phoneAuth_Internal_Error() throws Exception {
//        //Given
//        PhoneAuthRequestDto requestDto = PhoneAuthRequestDto.builder()
//                .phoneNumber("00000000000")
//                .build();
//
//        //when & then
//        mockMvc.perform(post("/join/phoneAuth")
//                        .accept(MediaTypes.HAL_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andDo(print())
//                .andExpect(status().isInternalServerError());
//    }

    @Test
    @DisplayName("정상적으로 이메일 중복체크하는 테스트")
    public void duplicateEmail() throws Exception {
       //given

        

        //when & then
        mockMvc.perform(get("/api/email/duplication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("email","random@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("email_duplication",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParameters(
                                parameterWithName("email").description("중복 조회할 이메일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));


    }

    @Test
    @DisplayName("이메일 중복체크시 중복일 경우 409")
    public void duplicateEmail_conflict() throws Exception {
        //given


        //when & then
        mockMvc.perform(get("/api/email/duplication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("email", appProperties.getUserEmail()))
                .andDo(print())
                .andExpect(status().isConflict())
        ;

    }


//    @Ignore
    @Test
    @DisplayName("정상적으로 이메일 인증보내는 테스트")
    public void adminPasswordEmailAuth() throws Exception {
       //given

        String email = "jyh@binter.co.kr";
        Member member = Member.builder()
                .name("jyh")
                .email(email)
                .agreement(new Agreement())
                .firstReward(new FirstReward())
                .roles("ADMIN,USER")
                .build();

        memberRepository.save(member);

        EmailAuthDto requestDto = EmailAuthDto.builder()
                .email(email)
                .build();

        //when & then
        mockMvc.perform(post("/api/adminPasswordEmailAuth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("email_auth_admin_password",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("changeAdminPassword").description("관리자 비밀번호 재설정 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("인증번호를 보낼 email 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("authNumber").description("이메일 인증번호"),
                                fieldWithPath("responseCode").description("응답코드 (200 이외의 값이면 다이렉트센드 내부 에러)"),
                                fieldWithPath("status").description("다이렉트 센드 상태 코드"),
                                fieldWithPath("msg").description("다이렉트 센드 상태 메시지"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.changeAdminPassword.href").description("관리자 비밀번호 재설정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("관리자가 아닌 email일 경우 400 나오는 테스트")
    public void adminPasswordEmailAuth_NotAdmin() throws Exception {
        //given

        String email = "jyh@binter.co.kr";
        Member member = Member.builder()
                .name("jyh")
                .email(email)
                .agreement(new Agreement())
                .firstReward(new FirstReward())
                .roles("USER")
                .build();

        memberRepository.save(member);

        EmailAuthDto requestDto = EmailAuthDto.builder()
                .email(email)
                .build();

        //when & then
        mockMvc.perform(post("/api/adminPasswordEmailAuth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 email일 경우 404 나오는 테스트")
    public void adminPasswordEmailAuth_not_found() throws Exception {
        //given

        String email = "jyh@binter.co.kr";

        EmailAuthDto requestDto = EmailAuthDto.builder()
                .email(email)
                .build();

        //when & then
        mockMvc.perform(post("/api/adminPasswordEmailAuth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("정상적으로 관리자 비밀번호 재설정하는 테스트")
    public void updateAdminPassword() throws Exception {
       //given

        String password = "admin1234";
        String email = "admin@gmail.com";
        UpdateAdminPasswordRequestDto requestDto = UpdateAdminPasswordRequestDto.builder()
                .email(email)
                .password(password)
                .passwordConfirm(password)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/password")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("change_admin_password",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("login").description("login 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("비밀번호를 변경할 관리자 이메일 주소"),
                                fieldWithPath("password").description("새 비밀번호"),
                                fieldWithPath("passwordConfirm").description("새 비밀번호 확인")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.login.href").description("login 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        Member findMember = memberRepository.findByEmail(email).get();
        assertThat(bCryptPasswordEncoder.matches(password, findMember.getPassword())).isTrue();


    }

    @Test
    @DisplayName("존재하지 않는 이메일일 경우 404")
    public void updateAdminPassword_not_found() throws Exception {
        //given
        String password = "admin1234";
        String email = "admin1234@gmail.com";
        UpdateAdminPasswordRequestDto requestDto = UpdateAdminPasswordRequestDto.builder()
                .email(email)
                .password(password)
                .passwordConfirm(password)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/password")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("관리자가 아닌 이메일일 경우 bad request")
    public void updateAdminPassword_not_admin() throws Exception {
        //given

        String password = "user1234";
        String email = "user@gmail.com";
        UpdateAdminPasswordRequestDto requestDto = UpdateAdminPasswordRequestDto.builder()
                .email(email)
                .password(password)
                .passwordConfirm(password)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/password")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("비밀번호 확인 다를 경우 400")
    public void updateAdminPassword_bad_request_passwordConfirm() throws Exception {
        //given

        String password = "admin1234";
        String email = "admin@gmail.com";
        UpdateAdminPasswordRequestDto requestDto = UpdateAdminPasswordRequestDto.builder()
                .email(email)
                .password(password)
                .passwordConfirm(password+1234)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/password")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    @DisplayName("정상적으로 로그인 성공 후 jwt 토큰 받는 테스트")
    public void login() throws Exception {
       //Given
        Member member = generateSampleMember();

        Member findMember = memberRepository.findById(member.getId()).get();

        System.out.println("findMember = " + findMember.getEmail());

        LoginDto requestDto = LoginDto.builder()
                .email(member.getEmail())
                .password("1234")
                .tokenValidDays(10)
                .build();

        //when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtProperties.HEADER_STRING))
                .andDo(document("login",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("회원 이메일(로그인 ID)"),
                                fieldWithPath("password").description("회원 비밀번호"),
                                fieldWithPath("tokenValidDays").description("토큰 유효 날짜 설정 null 이면 기본 2시간짜리 토큰, ex) 10을 입력하면 10일 짜리 토큰이 발급됨")
                        ),
                        responseHeaders(
                                headerWithName("Authorization").description("bearer 방식 JWT 토큰")
                        ),
                        responseFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("roleList").description("역할 리스트 [USER,SUBSCRIBER,ADMIN], 하위 역할을 포함 함"),
                                fieldWithPath("expiresAt").description("토큰 만료 시간")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 비밀번호 잘못된 경우 400")
    public void login_wrongPassword() throws Exception {
        //Given
        Member member = generateSampleMember();

        JwtLoginDto requestDto = JwtLoginDto.builder()
                .email(member.getEmail())
                .password("wrongPassword")
                .build();

        //when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인한 유저가 탈퇴한 유저일 경우")
    public void login_withdrawnMember() throws Exception {
        //Given
        String password = "1234";
        Member member = generateSampleMember(password);
        member.withdrawal();

        JwtLoginDto requestDto = JwtLoginDto.builder()
                .email(member.getEmail())
                .password(password)
                .build();

        //when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("정상적으로 아이디 찾는 테스트")
    public void findEmail() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("name", sampleMember.getName());
        params.add("phoneNumber", sampleMember.getPhoneNumber());

        //when & then
        mockMvc.perform(get("/api/email")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(sampleMember.getEmail()))
                .andDo(document("find_email",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("login").description("로그인 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParameters(
                                parameterWithName("name").description("이름"),
                                parameterWithName("phoneNumber").description("휴대폰 번호 '010xxxxxxxx' -없는 문자열")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("provider").description("sns 로그인 제공사 / 없으면 Null"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.login.href").description("로그인 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("찾을 아이디가 없을 경우 not found 나오는 테스트")
    public void findEmail_Not_Found() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("name", "jo young han ");
        params.add("phoneNumber", sampleMember.getPhoneNumber());

        //when & then
        mockMvc.perform(get("/api/email")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

//    @Ignore
    @Test
    @DisplayName("정상적으로 비밀번호 찾는 테스트")
    public void findPassword() throws Exception {
        //Given
        Member sampleMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder()
                .email(sampleMember.getEmail())
                .name(sampleMember.getName())
                .phoneNumber(sampleMember.getPhoneNumber())
                .build();

        //when & then
        mockMvc.perform(put("/api/temporaryPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find_password",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("login").description("로그인 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("이메일 주소"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("phoneNumber").description("휴대전화 번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("responseCode").description("응답코드 (200 이외의 값이면 다이렉트센드 내부 에러)"),
                                fieldWithPath("status").description("다이렉트 센드 상태 코드"),
                                fieldWithPath("msg").description("다이렉트 센드 상태 메시지"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.login.href").description("로그인 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 찾기 중 입력값이 부족할때 bad request 나오는 테스트")
    public void findPassword_bad_request() throws Exception {
        //Given
        FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder().build();

        //when & then
        mockMvc.perform(put("/api/temporaryPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 찾는 중 일치하는 회원이 존재하지 않을 경우 not found 나오는 테스트")
    public void findPassword_not_found() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder()
                .email("jyh@gmail.com")
                .name(sampleMember.getName())
                .phoneNumber(sampleMember.getPhoneNumber())
                .build();

        //when & then
        mockMvc.perform(put("/api/temporaryPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @Ignore
    @DisplayName("정상적으로 네이버 회원 정보 호출")
    public void naverLogin_newMember() throws Exception {
       //given

        String accessToken = SnsResponse.TEST_NAVER_ACCESS_TOKEN;

        NaverLoginDto requestDto = NaverLoginDto.builder()
                .accessToken(accessToken)
                .tokenValidDays(10)
                .build();

        //when & then
        mockMvc.perform(post("/api/login/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultcode").value(SnsResponse.NEW_MEMBER_CODE))
                .andExpect(jsonPath("message").value(SnsResponse.NEW_MEMBER_MESSAGE))
                .andDo(document("login_naver",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("accessToken").description("네이버 api 엑세스 토큰"),
                                fieldWithPath("tokenValidDays").description("토큰 유효 날짜 설정 null 이면 기본 2시간짜리 토큰, ex) 10을 입력하면 10일 짜리 토큰이 발급됨")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("resultcode").description("결과 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("response.id").description("네이버 고유 식별 provider id"),
                                fieldWithPath("response.gender").description("성별 ['F' or 'M' or 'U'] 각 여성/남성/확인불가"),
                                fieldWithPath("response.email").description("네이버 내정보에 등록된 이메일"),
                                fieldWithPath("response.mobile").description("휴대전화번호 'xxx-xxxx-xxxx'"),
                                fieldWithPath("response.mobile_e164").description("국제번호포함 휴대전화번호 '+8210xxxxxxxx'"),
                                fieldWithPath("response.name").description("사용자 이름"),
                                fieldWithPath("response.birthday").description("사용자 태어난 월-일 'MM-dd'"),
                                fieldWithPath("response.birthyear").description("사용자 태어난 년도 'yyyy'"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }
    @Ignore
    @Test
    @DisplayName("기존회원은 있지만 간편로그인 연동이 되어있지않음")
    public void naverLogin_needToconnectSns() throws Exception {
        //given

        Member member = generateSampleMember();

        String accessToken = SnsResponse.TEST_NAVER_ACCESS_TOKEN;

        NaverLoginDto requestDto = NaverLoginDto.builder()
                .accessToken(accessToken)
                .build();

        //when & then
        mockMvc.perform(post("/api/login/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultcode").value(SnsResponse.NEED_TO_CONNECT_NEW_SNS_CODE))
                .andExpect(jsonPath("message").value(SnsResponse.NEED_TO_CONNECT_NEW_SNS_MESSAGE))
        ;
    }
    @Ignore
    @Test
    @DisplayName("네이버가 아닌 카카오로 이미 연동되어있음")
    public void naverLogin_connectedByKakao() throws Exception {
        //given

        Member member = generateSampleMember();
        member.connectSns("kakao","sdjfaksdlfjaksdjfiasldf");

        String accessToken = SnsResponse.TEST_NAVER_ACCESS_TOKEN;

        NaverLoginDto requestDto = NaverLoginDto.builder()
                .accessToken(accessToken)
                .build();

        //when & then
        mockMvc.perform(post("/api/login/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultcode").value(SnsResponse.CONNECTED_BY_KAKAO_CODE))
                .andExpect(jsonPath("message").value(SnsResponse.CONNECTED_BY_KAKAO_MESSAGE))
        ;
    }

    @Ignore
    @Test
    @DisplayName("연동이되어있고 로그인 성공해서 토큰을 받음")
    public void naverLogin_success() throws Exception {
        //given

        Member member = generateSampleMember();
        member.connectSns("naver","p4N4jAY5Q0qszLDW8Wx2W30K3eKkRUlHEVivAHgR0XQ");

        String accessToken = SnsResponse.TEST_NAVER_ACCESS_TOKEN;

        NaverLoginDto requestDto = NaverLoginDto.builder()
                .accessToken(accessToken)
                .build();

        //when & then
        mockMvc.perform(post("/api/login/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("resultcode").value(SnsResponse.SUCCESS_CODE))
                .andExpect(jsonPath("message").value(SnsResponse.SUCCESS_MESSAGE))
                .andExpect(header().exists(JwtProperties.HEADER_STRING))
        ;
    }


    @Test
    @DisplayName("정상적으로 sns 연동")
    public void connectSns() throws Exception {
       //given
        String password = "password1234";
        Member member = generateSampleMember(password);

        String providerId = "asldkfjoiwejglskjsodifj";
        String provider = SnsProvider.NAVER;
        ConnectSnsRequestDto requestDto = ConnectSnsRequestDto.builder()
                .phoneNumber("01099030000")
                .password(password)
                .provider(provider)
                .providerId(providerId)
                .build();

        //when & then
        mockMvc.perform(post("/api/connectSns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("connect_sns",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("phoneNumber").description("휴대전화 번호 '010xxxxxxxx' "),
                                fieldWithPath("password").description("유저 비밀번호"),
                                fieldWithPath("provider").description("sns api 제공사 ['naver'/'kakao']"),
                                fieldWithPath("providerId").description("sns api 제공사 해당 유저 고유 id 값"),
                                fieldWithPath("tokenValidDays").description("토큰 유효 날짜 설정 null 이면 기본 2시간짜리 토큰, ex) 10을 입력하면 10일 짜리 토큰이 발급됨")
                        ),
                        responseHeaders(
                                headerWithName("Authorization").description("bearer 방식 JWT 토큰")
                        ),
                        responseFields(
                                fieldWithPath("email").description("연동된 이메일"),
                                fieldWithPath("provider").description("연동된 sns ['naver','kakao']"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getProvider()).isEqualTo(provider);
        assertThat(findMember.getProviderId()).isEqualTo(providerId);

    }

    @Test
    @DisplayName("sns 연동 요청 시 전화번호 존재하지않음")
    public void connectSns_phoneNumber_notFound() throws Exception {
        //given
        generateSampleMember();

        ConnectSnsRequestDto requestDto = ConnectSnsRequestDto.builder()
                .phoneNumber("01022222222")
                .password("sldkfjsdlkfj")
                .provider(SnsProvider.NAVER)
                .providerId("asldkfjoiwejglskjsodifj")
                .build();

        //when & then
        mockMvc.perform(post("/api/connectSns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("sns 연동 시 잘못된 비밀번호 입력 404")
    public void connectSns_wrong_password() throws Exception {
        //given
        String password = "password1234";
        Member member = generateSampleMember(password);

        String providerId = "asldkfjoiwejglskjsodifj";
        String provider = SnsProvider.NAVER;
        ConnectSnsRequestDto requestDto = ConnectSnsRequestDto.builder()
                .phoneNumber("01099038544")
                .password("wrongPassword")
                .provider(provider)
                .providerId(providerId)
                .build();

        //when & then
        mockMvc.perform(post("/api/connectSns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getProvider()).isNull();
        assertThat(findMember.getProviderId()).isNull();

    }









    private Member generateSampleMember() {
        return generateSampleMember("1234");
    }

    private Member generateSampleMember(String password) {
        Member member = Member.builder()
                .name("샘플Member")
                .email("jo.younghan8544@gmail.com")
                .password(bCryptPasswordEncoder.encode(password))
                .phoneNumber("01099030000")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("20000521")
                .gender(Gender.FEMALE)
                .agreement(new Agreement(true, true, true, true, true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .roles("USER,SUBSCRIBER,ADMIN")
                .reward(0)
                .firstReward(new FirstReward(true,true))
                .build();

        return memberRepository.save(member);
    }


    private BestReview generateBestReview(Review review, int i) {
        BestReview bestReview = BestReview.builder()
                .review(review)
                .leakedOrder(i)
                .build();
        return bestReviewRepository.save(bestReview);
    }


    private Review generateSubscribeReview(Member member, int i, ReviewStatus status) {
        List<Recipe> recipes = recipeRepository.findAll();

        Subscribe subscribe = Subscribe.builder()
                .build();
        subscribeRepository.save(subscribe);

        Dog dog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE,
                1, 1, SnackCountLevel.NORMAL, recipes.get(0));
        dog.setSubscribe(subscribe);

        SubscribeReview subscribeReview = SubscribeReview.builder()
                .member(member)
                .writtenDate(LocalDate.now())
                .username(member.getName())
                .star(3)
                .contents("열글자 이상의 구독 리뷰"+i)
                .status(status)
                .returnReason("상품에 맞지 않은 리뷰 내용"+i)
                .subscribe(subscribe)
                .build();
        reviewRepository.save(subscribeReview);

        IntStream.range(1,4).forEach(j -> {
            generateReviewImage(j, subscribeReview);
        });

        return subscribeReview;
    }


    private ItemReview generateItemReview(Member member, Item item, int i, ReviewStatus status) {
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(LocalDate.now().minusDays(30L))
                .username(member.getName())
                .star((i + 5) % 5)
                .contents("열글자 이상의 내용"+i)
                .status(status)
                .item(item)
                .build();
        itemReviewRepository.save(itemReview);

        IntStream.range(1,4).forEach(j -> {
            generateReviewImage(j, itemReview);
        });

        return itemReview;
    }

    private ReviewImage generateReviewImage(int i, Review review) {
        ReviewImage reviewImage = ReviewImage.builder()
                .review(review)
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return reviewImageRepository.save(reviewImage);
    }


    private ReviewImage generateReviewImage(int i) {
        ReviewImage reviewImage = ReviewImage.builder()
                .folder("folder" + i)
                .filename("filename" + i +".jpg")
                .build();
        return reviewImageRepository.save(reviewImage);
    }



    private void generateOrderItem(Item item1, GeneralOrder savedOrder) {
        OrderItem orderItem = OrderItem.builder()
                .generalOrder(savedOrder)
                .item(item1)
                .status(OrderStatus.CONFIRM)
                .build();
        orderItemRepository.save(orderItem);
    }

    private Order generateOrder(Member member, Subscribe savedSubscribe) {
        Order order = SubscribeOrder.builder()
                .member(member)
                .subscribe(savedSubscribe)
                .build();
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    private Subscribe generateSubscribe() {
        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.SUBSCRIBING)
                .writeableReview(true)
                .build();
        return subscribeRepository.save(subscribe);
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










    private Banner generateMainBanner(int i, BannerTargets bannerTargets) {
        MainBanner banner = MainBanner.builder()
                .name("메인배너" + i)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .status(BannerStatus.LEAKED)
                .leakedOrder(i)
                .imgFile(new ImgFile("C:/Users/verin/jyh/upload/test/banners", "filenamePc.jpg", "filenameMobile.jpg"))
                .targets(bannerTargets)
                .build();
        return bannerRepository.save(banner);
    }

    private GeneralOrder generateGeneralOrder(Member member, int i, OrderStatus orderstatus) {


        Delivery delivery = generateDelivery(member, i);
        GeneralOrder generalOrder = GeneralOrder.builder()
                .impUid("imp_uid" + i)
                .merchantUid("merchant_uid" + i)
                .orderStatus(orderstatus)
                .member(member)
                .orderPrice(120000)
                .deliveryPrice(0)
                .discountTotal(10000)
                .discountReward(10000)
                .discountCoupon(0)
                .paymentPrice(110000)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .orderConfirmDate(LocalDateTime.now().minusHours(3))
                .build();

        IntStream.range(1,3).forEach(j -> {
            Item item = generateItem(j);
            generateOption(item, j);

            OrderItem orderItem = generateOrderItem(member, generalOrder, j, item, orderstatus);

            IntStream.range(1,j+1).forEach(k -> {
                SelectOption selectOption = SelectOption.builder()
                        .orderItem(orderItem)
                        .name("옵션" + k)
                        .price(1000 * k)
                        .amount(k)
                        .build();
                selectOptionRepository.save(selectOption);
            });

        });


        return orderRepository.save(generalOrder);
    }

    private OrderItem generateOrderItem(Member member, GeneralOrder generalOrder, int j, Item item, OrderStatus orderStatus) {

        OrderItem orderItem = OrderItem.builder()
                .generalOrder(generalOrder)
                .item(item)
                .salePrice(item.getSalePrice())
                .amount(j)
                .finalPrice(item.getSalePrice() * j)
                .status(orderStatus)
                .build();
        return orderItemRepository.save(orderItem);
    }

    private MemberCoupon generateMemberCoupon(Member member, int i, int remaining, CouponStatus status) {
        Coupon coupon = generateGeneralCoupon(i);
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .expiredDate(LocalDateTime.now().plusDays(remaining))
                .remaining(remaining)
                .memberCouponStatus(status)
                .build();
        return memberCouponRepository.save(memberCoupon);
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

    private SubscribeOrder generateSubscribeOrder(Member member, int i, OrderStatus orderStatus) {
        Delivery delivery = generateDelivery(member, i);
        Dog dog = generateDog(member, i, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, false);

        Subscribe subscribe = Subscribe.builder()
                .dog(dog)
                .subscribeCount(i)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBE_PENDING)
                .build();
        subscribeRepository.save(subscribe);

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
                .build();
        return orderRepository.save(subscribeOrder);
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
                .build();
        deliveryRepository.save(delivery);
        return delivery;
    }

    private Dog generateDog(Member member, int i, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel, boolean representative) {
        Dog dog = Dog.builder()
                .member(member)
                .name("강아지" + i)
                .birth("202103")
                .startAgeMonth(startAgeMonth)
                .gender(Gender.MALE)
                .oldDog(false)
                .dogSize(dogSize)
                .weight(new BigDecimal(weight))
                .dogActivity(new DogActivity(activitylevel, walkingCountPerWeek, walkingTimePerOneTime))
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(snackCountLevel)
                .representative(representative)
                .build();
        return dogRepository.save(dog);
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

    private void generateDogPicture(Dog dog) {
        DogPicture dogPicture = DogPicture.builder()
                .dog(dog)
                .folder("folder")
                .filename("filename")
                .build();
        dogPictureRepository.save(dogPicture);
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




    private String getAdminToken() throws Exception {
        return getBearerToken(appProperties.getAdminEmail(), appProperties.getAdminPassword());
    }

    private String getUserToken() throws Exception {
        return getBearerToken(appProperties.getUserEmail(), appProperties.getUserPassword());
    }

    private String getBearerToken(String appProperties, String appProperties1) throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .email(appProperties)
                .password(appProperties1)
                .build();

        //when & then
        ResultActions perform = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        MockHttpServletResponse response = perform.andReturn().getResponse();
        return response.getHeaders("Authorization").get(0);
    }


}