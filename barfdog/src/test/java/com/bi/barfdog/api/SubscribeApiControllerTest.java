package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.orderDto.StopSubscribeDto;
import com.bi.barfdog.api.subscribeDto.UpdateGramDto;
import com.bi.barfdog.api.subscribeDto.UpdatePlanDto;
import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.api.subscribeDto.UseCouponDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.*;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.orderItem.SelectOption;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.domain.surveyReport.*;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.repository.card.CardRepository;
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
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.BeforeSubscribeRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.bi.barfdog.config.finalVariable.StandardVar.*;
import static com.bi.barfdog.config.finalVariable.StandardVar.LACTATING;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class SubscribeApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;
    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    DogRepository dogRepository;
    @Autowired
    SubscribeRepository subscribeRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    SubscribeRecipeRepository subscribeRecipeRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    SelectOptionRepository selectOptionRepository;
    @Autowired
    SettingRepository settingRepository;
    @Autowired
    SurveyReportRepository surveyReportRepository;
    @Autowired
    BeforeSubscribeRepository beforeSubscribeRepository;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    MemberCouponRepository memberCouponRepository;
    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DogPictureRepository dogPictureRepository;
    @Autowired
    ItemImageRepository itemImageRepository;

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
        memberCouponRepository.deleteAll();
        couponRepository.deleteAll();

        subscribeRecipeRepository.deleteAll();
        beforeSubscribeRepository.deleteAll();
        subscribeRepository.deleteAll();

    }



    @Test
    @DisplayName("플랜 레시피 선택")
    public void selectPlanRecipes() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        member.changeGrade(Grade.플래티넘);

        Dog dog = generateDog(member);

        Subscribe subscribe = Subscribe.builder()
                .dog(dog)
                .build();
        subscribeRepository.save(subscribe);

        List<Long> recipeIdList = getRecipeIdList();

        int nextPaymentPrice = 100000;
        SubscribePlan plan = SubscribePlan.FULL;
        UpdateSubscribeDto requestDto = UpdateSubscribeDto.builder()
                .plan(plan)
                .recipeIdList(recipeIdList)
                .nextPaymentPrice(nextPaymentPrice)
                .build();

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/planRecipes", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("select_subscribe_planRecipes",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orderSheet_subscribe").description("구독 주문서 작성에 필요값 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("plan").description("선택한 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("recipeIdList").description("선택한 레시피 id 리스트"),
                                fieldWithPath("nextPaymentPrice").description("구독 상품 금액")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orderSheet_subscribe.href").description("구독 주문서 작성에 필요값 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        int discountGrade = 0;
        double percent;
        Grade grade = member.getGrade();
        switch (grade) {
            case 골드: percent = 1.0;
                break;
            case 플래티넘: percent = 3.0;
                break;
            case 다이아몬드: percent = 5.0;
                break;
            case 더바프: percent = 7.0;
                break;
            default: percent = 0.0;
                break;
        }

        if (percent > 0.0) {
            discountGrade = (int) Math.round(nextPaymentPrice * percent / 100.0);
        }

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getPlan()).isEqualTo(plan);
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(nextPaymentPrice);
        assertThat(findSubscribe.getDiscountGrade()).isEqualTo(discountGrade);

        List<SubscribeRecipe> subscribeRecipeList = subscribeRecipeRepository.findAll();
        assertThat(subscribeRecipeList.size()).isEqualTo(2);

        for (int i = 0; i < subscribeRecipeList.size(); i++) {
            assertThat(subscribeRecipeList.get(i).getRecipe().getId()).isEqualTo(recipeIdList.get(i));
        }



    }

    private Dog generateDog(Member member) {
        Dog dog = Dog.builder()
                .member(member)
                .name("강아지")
                .birth("202103")
                .representative(false)
                .startAgeMonth(12L)
                .gender(Gender.MALE)
                .oldDog(false)
                .dogSize(DogSize.SMALL)
                .weight(new BigDecimal(2.5))
                .dogActivity(new DogActivity(ActivityLevel.LITTLE, 2, 1))
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(SnackCountLevel.MUCH)
                .build();
        dogRepository.save(dog);
        return dog;
    }


    @Test
    @DisplayName("정상적으로 구독 업데이트하는 테스트")
    public void updateSubscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        List<Long> recipeIdList = getRecipeIdList();

        int nextPaymentPrice = 100000;
        SubscribePlan plan = SubscribePlan.FULL;
        UpdateSubscribeDto requestDto = UpdateSubscribeDto.builder()
                .plan(plan)
                .recipeIdList(recipeIdList)
                .nextPaymentPrice(nextPaymentPrice)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/subscribes/{id}", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orderSheet_subscribe").description("구독 주문서 작성에 필요값 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("plan").description("선택한 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("recipeIdList").description("선택한 레시피 id 리스트"),
                                fieldWithPath("nextPaymentPrice").description("구독 상품 금액")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orderSheet_subscribe.href").description("구독 주문서 작성에 필요값 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(nextPaymentPrice);
        assertThat(findSubscribe.getPlan()).isEqualTo(plan);

        List<SubscribeRecipe> subscribeRecipeList = subscribeRecipeRepository.findBySubscribe(findSubscribe);
        assertThat(subscribeRecipeList.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("구독 업데이트시 과거 구독 정보 저장")
    public void updateSubscribe_beforeSubscribe() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();
        SubscribePlan beforePlan = subscribe.getPlan();
        int beforeNextPaymentPrice = subscribe.getNextPaymentPrice();
        int beforeSubscribeCount = subscribe.getSubscribeCount();

        List<Long> recipeIdList = getRecipeIdList();

        int nextPaymentPrice = 100000;
        SubscribePlan plan = SubscribePlan.HALF;
        UpdatePlanDto requestDto = UpdatePlanDto.builder()
                .plan(plan)
                .recipeIdList(recipeIdList)
                .nextPaymentPrice(nextPaymentPrice)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/subscribes/{id}", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(nextPaymentPrice);
        assertThat(findSubscribe.getPlan()).isEqualTo(plan);


    }

    @Test
    @DisplayName("업데이트할 구독이 존재하지않음")
    public void updateSubscribe_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = generateSubscribe(dogRepresentative);

        List<Long> recipeIdList = getRecipeIdList();

        int nextPaymentPrice = 100000;
        SubscribePlan plan = SubscribePlan.FULL;
        UpdateSubscribeDto requestDto = UpdateSubscribeDto.builder()
                .plan(plan)
                .recipeIdList(recipeIdList)
                .nextPaymentPrice(nextPaymentPrice)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/subscribes/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("구독 리스트 조회")
    public void querySubscribes() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,14).forEach(i -> {
            generateSubscribeOrderAndEtcUseCoupon(member, i, OrderStatus.PAYMENT_DONE);
        });

       //when & then
        mockMvc.perform(get("/api/subscribes")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andExpect(jsonPath("page.totalPages").value(3))
                .andDo(document("query_subscribes",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.subscribeId").description("구독 id"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.pictureUrl").description("프로필사진 url"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.plan").description("플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.dogName").description("강아지 이름"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.countSkipOneTime").description("이번 회차 구독 한 회 건너뛰기 횟수, 다음회차 때 0으로 초기화"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.countSkipOneWeek").description("이번 회차 구독 한 주 건너뛰기 횟수, 다음회차 때 0으로 초기화"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.nextPaymentDate").description("다음 결제일"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.nextPaymentPrice").description("다음 결제 금액(할인분 제외. 구독상품 원가), 실제결제 예정 금액은 nextPaymentPrice-(discountCoupon+discountGrade)"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.discountCoupon").description("쿠폰 할인 분"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].subscribeDto.discountGrade").description("등급 할인 분"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0].recipeNames").description("레시피 이름 xxx,xxx 으로 구분"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0]._links.query_subscribe.href").description("구독 상세보기 링크"),
                                fieldWithPath("_embedded.querySubscribesDtoList[0]._links.skip_subscribe.href").description("구독 건너뛰기 링크"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }
    
    @Test
    @DisplayName("구독 하나 조회")
    public void querySubscribe() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/subscribes/{id}", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")

                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("subscribeDto.id").description("구독 id"),
                                fieldWithPath("subscribeDto.dogId").description("강아지 id"),
                                fieldWithPath("subscribeDto.dogName").description("강아지 이름"),
                                fieldWithPath("subscribeDto.subscribeCount").description("구독 회차"),
                                fieldWithPath("subscribeDto.plan").description("구독 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("subscribeDto.oneMealRecommendGram").description("한끼 급여량"),
                                fieldWithPath("subscribeDto.nextPaymentDate").description("다음 결제일"),
                                fieldWithPath("subscribeDto.countSkipOneTime").description("해당 구독 회차 1회 건너뛰기 누적횟수"),
                                fieldWithPath("subscribeDto.countSkipOneWeek").description("해당 구독 회차 1주 건너뛰기 누적횟수"),
                                fieldWithPath("subscribeDto.nextPaymentPrice").description("다음 회차 결제 금액(할인전 상품 원가) -> 실제로는 nextPaymentPrice-(discountCoupon + discountGrade) 금액이 결제 됨"),
                                fieldWithPath("subscribeDto.discountCoupon").description("쿠폰할인 할인분"),
                                fieldWithPath("subscribeDto.discountGrade").description("등급할인 할인분"),
                                fieldWithPath("subscribeDto.nextDeliveryDate").description("다음 배송일"),
                                fieldWithPath("subscribeDto.usingMemberCouponId").description("사용한 보유쿠폰(memberCoupon) id, 없으면 null"),
                                fieldWithPath("subscribeDto.couponName").description("적용된 쿠폰 이름, 없으면 null"),
                                fieldWithPath("subscribeRecipeDtoList[0].recipeId").description("구독한 레시피 id"),
                                fieldWithPath("subscribeRecipeDtoList[0].recipeName").description("구독한 레시피 이름"),
                                fieldWithPath("memberCouponDtoList[0].memberCouponId").description("보유 쿠폰 id"),
                                fieldWithPath("memberCouponDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("memberCouponDtoList[0].discountType").description("할인 유형 [FIXED_RATE, FLAT_RATE]"),
                                fieldWithPath("memberCouponDtoList[0].discountDegree").description("쿠폰 할인 정도"),
                                fieldWithPath("memberCouponDtoList[0].availableMaxDiscount").description("최대 할인가능 금액"),
                                fieldWithPath("memberCouponDtoList[0].availableMinPrice").description("쿠폰 적용 가능한 최소 주문 금액"),
                                fieldWithPath("memberCouponDtoList[0].remaining").description("남은 매수"),
                                fieldWithPath("memberCouponDtoList[0].expiredDate").description("쿠폰 만료일"),
                                fieldWithPath("recipeDtoList[0].id").description("레시피 id"),
                                fieldWithPath("recipeDtoList[0].name").description("레시피 이름"),
                                fieldWithPath("recipeDtoList[0].description").description("레시피 설명"),
                                fieldWithPath("recipeDtoList[0].pricePerGram").description("1그램 당 가격"),
                                fieldWithPath("recipeDtoList[0].gramPerKcal").description("1칼로리 당 그램"),
                                fieldWithPath("recipeDtoList[0].inStock").description("재고 유무 여부 true/false , 각 유/무"),
                                fieldWithPath("recipeDtoList[0].imgUrl").description("레시피 이미지 url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("구독 하나 조회 not found")
    public void querySubscribe_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/subscribes/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("구독에 쿠폰 적용")
    public void useCouponToSubscribe() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcNoCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        Coupon coupon = generateGeneralCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);
        int remaining = memberCoupon.getRemaining();

        int discount = 3000;
        UseCouponDto requestDto = UseCouponDto.builder()
                .memberCouponId(memberCoupon.getId())
                .discount(discount)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/coupon",subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("use_coupon_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_subscribe").description("구독 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("memberCouponId").description("사용한 보유쿠폰id"),
                                fieldWithPath("discount").description("쿠폰 할인량")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_subscribe.href").description("구독 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getMemberCoupon().getId()).isEqualTo(memberCoupon.getId());
        assertThat(findSubscribe.getDiscountCoupon()).isEqualTo(discount);

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId()).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining - 1);
        if (findMemberCoupon.getRemaining() == 0) {
            assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.INACTIVE);
        } else {
            assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        }
    }

    @Test
    @DisplayName("구독에 쿠폰 적용 - 이미 적용된 쿠폰이 있었음")
    public void useCouponToSubscribe_modify() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();
        MemberCoupon beforeMemberCoupon = subscribe.getMemberCoupon();
        int beforeMemberCouponRemaining = beforeMemberCoupon.getRemaining();

        Coupon coupon = generateGeneralCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);
        int remaining = memberCoupon.getRemaining();

        int discount = 3000;
        UseCouponDto requestDto = UseCouponDto.builder()
                .memberCouponId(memberCoupon.getId())
                .discount(discount)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/coupon",subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getMemberCoupon().getId()).isEqualTo(memberCoupon.getId());
        assertThat(findSubscribe.getDiscountCoupon()).isEqualTo(discount);

        MemberCoupon findBeforeMemberCoupon = memberCouponRepository.findById(beforeMemberCoupon.getId()).get();
        assertThat(findBeforeMemberCoupon.getRemaining()).isEqualTo(beforeMemberCouponRemaining + 1);

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId()).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining - 1);
        if (findMemberCoupon.getRemaining() == 0) {
            assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.INACTIVE);
        } else {
            assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        }
    }

    @Test
    @DisplayName("구독에 쿠폰 적용 not found")
    public void useCouponToSubscribe_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcNoCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        Coupon coupon = generateGeneralCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);
        int remaining = memberCoupon.getRemaining();

        int discount = 3000;
        UseCouponDto requestDto = UseCouponDto.builder()
                .memberCouponId(memberCoupon.getId())
                .discount(discount)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/999999/coupon")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("그램 수 변경")
    public void updateGram() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();
        int subscribeCount = subscribe.getSubscribeCount();
        SubscribePlan plan = subscribe.getPlan();
        int nextPaymentPrice = subscribe.getNextPaymentPrice();
        BeforeSubscribe beforeSubscribe = beforeSubscribeRepository.findBySubscribe(subscribe).get();


        int gram = 100;
        int totalPrice = 4000;
        UpdateGramDto requestDto = UpdateGramDto.builder()
                .gram(gram)
                .totalPrice(totalPrice)
                .build();

        Coupon coupon = subscribe.getMemberCoupon().getCoupon();
        DiscountType discountType = coupon.getDiscountType();
        int discountDegree = coupon.getDiscountDegree();
        int discount = 0;
        if (discountType == DiscountType.FIXED_RATE) {
            discount = (int) Math.round(totalPrice * discountDegree / 100.0);
        }
        int availableMaxDiscount = coupon.getAvailableMaxDiscount();
        if (discount > availableMaxDiscount) {
            discount = availableMaxDiscount;
        }

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/gram", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_gram_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_subscribe").description("구독 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("gram").description("변경할 gram"),
                                fieldWithPath("totalPrice").description("gram 변경 후 구독 금액")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_subscribe.href").description("구독 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(totalPrice);

        SurveyReport surveyReport = findSubscribe.getDog().getSurveyReport();
        assertThat(surveyReport.getFoodAnalysis().getOneMealRecommendGram()).isEqualTo(BigDecimal.valueOf(gram * 1.0).setScale(2));
        assertThat(surveyReport.getFoodAnalysis().getOneDayRecommendGram()).isEqualTo(BigDecimal.valueOf(gram * 2.0).setScale(2));

        Optional<BeforeSubscribe> optionalBeforeSubscribe = beforeSubscribeRepository.findById(beforeSubscribe.getId());
        assertThat(optionalBeforeSubscribe.isPresent()).isFalse();

        BeforeSubscribe findBeforeSubscribe = beforeSubscribeRepository.findBySubscribe(findSubscribe).get();
        assertThat(findBeforeSubscribe.getSubscribeCount()).isEqualTo(subscribeCount);
        assertThat(findBeforeSubscribe.getPlan()).isEqualTo(plan);
        assertThat(findBeforeSubscribe.getPaymentPrice()).isEqualTo(nextPaymentPrice);


    }

    @Test
    @DisplayName("그램 수 변경 - 쿠폰 사용 불가일 경우")
    public void updateGram_cannot_use_coupon() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        MemberCoupon memberCoupon = subscribe.getMemberCoupon();
        int remaining = memberCoupon.getRemaining();
        int availableMinPrice = memberCoupon.getCoupon().getAvailableMinPrice();

        int gram = 100;
        int totalPrice = availableMinPrice - 1;
        UpdateGramDto requestDto = UpdateGramDto.builder()
                .gram(gram)
                .totalPrice(totalPrice)
                .build();

        Coupon coupon = memberCoupon.getCoupon();
        DiscountType discountType = coupon.getDiscountType();
        int discountDegree = coupon.getDiscountDegree();
        int discount = 0;
        if (discountType == DiscountType.FIXED_RATE) {
            discount = (int) Math.round(totalPrice * discountDegree / 100.0);
        }
        int availableMaxDiscount = coupon.getAvailableMaxDiscount();
        if (discount > availableMaxDiscount) {
            discount = availableMaxDiscount;
        }
        if (totalPrice < coupon.getAvailableMinPrice()) {
            discount = 0;
        }

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/gram", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(totalPrice);
        assertThat(findSubscribe.getDiscountCoupon()).isEqualTo(0);
        assertThat(findSubscribe.getMemberCoupon()).isNull();

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId()).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining + 1);

        SurveyReport surveyReport = findSubscribe.getDog().getSurveyReport();
        assertThat(surveyReport.getFoodAnalysis().getOneMealRecommendGram()).isEqualTo(BigDecimal.valueOf(gram * 1.0).setScale(2));
        assertThat(surveyReport.getFoodAnalysis().getOneDayRecommendGram()).isEqualTo(BigDecimal.valueOf(gram * 2.0).setScale(2));


    }

    @Test
    @DisplayName("그램 수 변경 not found")
    public void updateGram_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        int gram = 100;
        int totalPrice = 40000;
        UpdateGramDto requestDto = UpdateGramDto.builder()
                .gram(gram)
                .totalPrice(totalPrice)
                .build();

        Coupon coupon = subscribe.getMemberCoupon().getCoupon();
        DiscountType discountType = coupon.getDiscountType();
        int discountDegree = coupon.getDiscountDegree();
        int discount = 0;
        if (discountType == DiscountType.FIXED_RATE) {
            discount = (int) Math.round(totalPrice * discountDegree / 100.0);
        }
        int availableMaxDiscount = coupon.getAvailableMaxDiscount();
        if (discount > availableMaxDiscount) {
            discount = availableMaxDiscount;
        }

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/999999/gram")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }

    @Test
    @DisplayName("구독 플랜 업데이트")
    public void updatePlan() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        Recipe recipe = recipeRepository.findAll().get(2);
        List<Long> recipeIdList = new ArrayList<>();
        Long recipeId = recipe.getId();
        recipeIdList.add(recipeId);

        SubscribePlan plan = SubscribePlan.TOPPING;
        int nextPaymentPrice = 40000;
        UpdatePlanDto requestDto = UpdatePlanDto.builder()
                .plan(plan)
                .nextPaymentPrice(nextPaymentPrice)
                .recipeIdList(recipeIdList)
                .build();

        MemberCoupon memberCoupon = subscribe.getMemberCoupon();
        int remaining = memberCoupon.getRemaining();
        int availableMinPrice = memberCoupon.getCoupon().getAvailableMinPrice();
        Coupon coupon = subscribe.getMemberCoupon().getCoupon();
        DiscountType discountType = coupon.getDiscountType();
        int discountDegree = coupon.getDiscountDegree();
        int discount = 0;
        if (discountType == DiscountType.FIXED_RATE) {
            discount = (int) Math.round(nextPaymentPrice * discountDegree / 100.0);
        }
        int availableMaxDiscount = coupon.getAvailableMaxDiscount();
        if (discount > availableMaxDiscount) {
            discount = availableMaxDiscount;
        }

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/plan", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_plan_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_subscribe").description("구독 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("plan").description("변경할 플랜 [FULL, HALF, TOPPING]"),
                                fieldWithPath("nextPaymentPrice").description("변경 후 구독 가격(쿠폰할인 적용 전)"),
                                fieldWithPath("recipeIdList").description("변경할 레시피 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_subscribe.href").description("구독 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getPlan()).isEqualTo(plan);
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(nextPaymentPrice);
        assertThat(findSubscribe.getDiscountCoupon()).isEqualTo(discount);

        List<SubscribeRecipe> subscribeRecipes = subscribeRecipeRepository.findBySubscribe(findSubscribe);
        assertThat(subscribeRecipes.size()).isEqualTo(1);
        assertThat(subscribeRecipes.get(0).getRecipe().getId()).isEqualTo(recipeId);


    }

    @Test
    @DisplayName("구독 플랜 업데이트 not found")
    public void updatePlan_not_found() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();

        Recipe recipe = recipeRepository.findAll().get(2);
        List<Long> recipeIdList = new ArrayList<>();
        Long recipeId = recipe.getId();
        recipeIdList.add(recipeId);

        SubscribePlan plan = SubscribePlan.TOPPING;
        int nextPaymentPrice = 40000;
        UpdatePlanDto requestDto = UpdatePlanDto.builder()
                .plan(plan)
                .nextPaymentPrice(nextPaymentPrice)
                .recipeIdList(recipeIdList)
                .build();

        MemberCoupon memberCoupon = subscribe.getMemberCoupon();
        int remaining = memberCoupon.getRemaining();
        int availableMinPrice = memberCoupon.getCoupon().getAvailableMinPrice();
        Coupon coupon = subscribe.getMemberCoupon().getCoupon();
        DiscountType discountType = coupon.getDiscountType();
        int discountDegree = coupon.getDiscountDegree();
        int discount = 0;
        if (discountType == DiscountType.FIXED_RATE) {
            discount = (int) Math.round(nextPaymentPrice * discountDegree / 100.0);
        }
        int availableMaxDiscount = coupon.getAvailableMaxDiscount();
        if (discount > availableMaxDiscount) {
            discount = availableMaxDiscount;
        }

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/999999/plan")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }


    @Test
    @DisplayName("구독 1주 건너뛰기")
    public void skipSubscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();
        int countSkipOneTime = subscribe.getCountSkipOneTime();
        int countSkipOneWeek = subscribe.getCountSkipOneWeek();
        LocalDateTime nextPaymentDate = subscribe.getNextPaymentDate();
        LocalDate nextDeliveryDate = subscribe.getNextDeliveryDate();

        String type = "week";

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/skip/{type}", subscribe.getId(), type)
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("skip_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_subscribe").description("구독 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id"),
                                parameterWithName("type").description("건너뛰기 타입 [WEEK, ONCE]")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_subscribe.href").description("구독 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getNextDeliveryDate()).isEqualTo(nextDeliveryDate.plusDays(7));
        assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(nextPaymentDate.plusDays(7));
        assertThat(findSubscribe.getCountSkipOneTime()).isEqualTo(countSkipOneTime);
        assertThat(findSubscribe.getCountSkipOneWeek()).isEqualTo(countSkipOneWeek + 1);

        String nextOrderMerchant_uid = findSubscribe.getNextOrderMerchantUid();
        SubscribeOrder findOrder = orderRepository.findByMerchantUid(nextOrderMerchant_uid).get();
        assertThat(findOrder.getDelivery().getNextDeliveryDate()).isEqualTo(nextDeliveryDate.plusDays(7));
    }

    @Test
    @DisplayName("구독 1회 건너뛰기")
    public void skipSubscribe_once() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.PAYMENT_DONE);
        Subscribe subscribe = subscribeOrder.getSubscribe();
        SubscribePlan plan = subscribe.getPlan();
        int countSkipOneTime = subscribe.getCountSkipOneTime();
        int countSkipOneWeek = subscribe.getCountSkipOneWeek();
        LocalDateTime nextPaymentDate = subscribe.getNextPaymentDate();
        LocalDate nextDeliveryDate = subscribe.getNextDeliveryDate();

        String type = "once";

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/skip/{type}", subscribe.getId(), type)
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getNextDeliveryDate()).isEqualTo(nextDeliveryDate.plusDays(plan == SubscribePlan.FULL ? 14 : 28));
        assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(nextPaymentDate.plusDays(plan == SubscribePlan.FULL ? 14 : 28));
        assertThat(findSubscribe.getCountSkipOneTime()).isEqualTo(countSkipOneTime + 1);
        assertThat(findSubscribe.getCountSkipOneWeek()).isEqualTo(countSkipOneWeek);

        String nextOrderMerchant_uid = findSubscribe.getNextOrderMerchantUid();
        SubscribeOrder findOrder = orderRepository.findByMerchantUid(nextOrderMerchant_uid).get();
        assertThat(findOrder.getDelivery().getNextDeliveryDate()).isEqualTo(nextDeliveryDate.plusDays(plan == SubscribePlan.FULL ? 14 : 28));
    }


    @Test
    @DisplayName("구독 취소 테스트")
    public void stopSubscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcUseCoupon(member, 1, OrderStatus.BEFORE_PAYMENT);
        MemberCoupon memberCoupon = subscribeOrder.getMemberCoupon();
        int remaining = memberCoupon.getRemaining();
        Delivery delivery = subscribeOrder.getDelivery();
        Subscribe subscribe = subscribeOrder.getSubscribe();

        List<String> reasonList = new ArrayList<>();
        String aaa = "구독 취소 사유1";
        reasonList.add(aaa);
        String bbb = "구독 취소 사유 2";
        reasonList.add(bbb);
        String ccc = "구독 취소 사유 3";
        reasonList.add(ccc);

        StopSubscribeDto requestDto = StopSubscribeDto.builder()
                .reasonList(reasonList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/subscribes/{id}/stop", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("stop_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_subscribes").description("구독 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("reasonList").description("구독 취소 사유 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_subscribes.href").description("구독 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getCancelReason()).isEqualTo(aaa+","+bbb+","+ccc);
        assertThat(findSubscribe.getDiscountCoupon()).isEqualTo(0);
        assertThat(findSubscribe.getNextPaymentDate()).isNull();
        assertThat(findSubscribe.getNextDeliveryDate()).isNull();
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.BEFORE_PAYMENT);
        assertThat(findSubscribe.getNextOrderMerchantUid()).isNull();
        assertThat(findSubscribe.getCountSkipOneTime()).isEqualTo(0);
        assertThat(findSubscribe.getCountSkipOneWeek()).isEqualTo(0);

        Optional<Order> optionalOrder = orderRepository.findById(subscribeOrder.getId());
        assertThat(optionalOrder.isPresent()).isFalse();

        Optional<Delivery> optionalDelivery = deliveryRepository.findById(delivery.getId());
        assertThat(optionalDelivery.isPresent()).isFalse();

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId()).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining + 1);

    }








    private List<Long> getRecipeIdList() {
        List<Long> recipeIdList = new ArrayList<>();
        List<Recipe> recipes = recipeRepository.findAll();
        recipeIdList.add(recipes.get(0).getId());
        recipeIdList.add(recipes.get(1).getId());
        return recipeIdList;
    }


    private Subscribe generateSubscribe(Dog dog) {
        Subscribe subscribe = Subscribe.builder()
                .plan(SubscribePlan.FULL)
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .build();
        dog.setSubscribe(subscribe);
        subscribeRepository.save(subscribe);

        List<Recipe> recipes = recipeRepository.findAll();
        Recipe recipe1 = recipes.get(0);
        Recipe recipe2 = recipes.get(1);

        generateSubscribeRecipe(subscribe, recipe1);
        generateSubscribeRecipe(subscribe, recipe2);


        return subscribe;
    }

    private void generateSubscribeRecipe(Subscribe subscribe, Recipe recipe) {
        SubscribeRecipe subscribeRecipe = SubscribeRecipe.builder()
                .subscribe(subscribe)
                .recipe(recipe)
                .build();
        subscribeRecipeRepository.save(subscribeRecipe);
        subscribe.addSubscribeRecipe(subscribeRecipe);
    }


    // ============================================================================================


    private SubscribeOrder generateSubscribeOrderAndEtcNoCoupon(Member member, int i, OrderStatus orderStatus) {
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

        Subscribe subscribe = generateSubscribe(member, i);

        BeforeSubscribe beforeSubscribe = generateBeforeSubscribe(i,subscribe);
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

        DogPicture dogPicture = DogPicture.builder()
                .dog(dog)
                .folder("folder"+i)
                .filename("filename"+i)
                .build();
        dogPictureRepository.save(dogPicture);

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

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 3, CouponStatus.ACTIVE);


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
                .memberCoupon(memberCoupon1)
                .subscribeCount(subscribe.getSubscribeCount())
                .orderConfirmDate(LocalDateTime.now().minusHours(3))
                .build();
        orderRepository.save(subscribeOrder);

        return subscribeOrder;
    }


    private SubscribeOrder generateSubscribeOrderAndEtcUseCoupon(Member member, int i, OrderStatus orderStatus) {

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

        Subscribe subscribe = generateSubscribeUseCoupon(member, i);
        MemberCoupon memberCoupon = subscribe.getMemberCoupon();

        generateBeforeSubscribe(i,subscribe);

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

        DogPicture dogPicture = DogPicture.builder()
                .dog(dog)
                .folder("folder"+i)
                .filename("filename"+i)
                .build();
        dogPictureRepository.save(dogPicture);

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

        String merchantUid = "merchant_uid" + i;
        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid("imp_uid"+i)
                .merchantUid(merchantUid)
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

        subscribe.setNextOrderMerchantUid(merchantUid);

        return subscribeOrder;
    }

    private SubscribeOrder generateSubscribeOrderAndEtc_NoBeforeSubscribe_no_deliveryNumber(Member member, int i, OrderStatus orderStatus) {

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

        Delivery delivery = generateDeliveryNoNumber(member, i);

        Subscribe subscribe = generateSubscribe(member, i);

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

    private BeforeSubscribe generateBeforeSubscribe(int i,Subscribe subscribe) {
        BeforeSubscribe beforeSubscribe = BeforeSubscribe.builder()
                .subscribe(subscribe)
                .subscribeCount(i)
                .plan(SubscribePlan.HALF)
                .oneMealRecommendGram(BigDecimal.valueOf(140.0))
                .recipeName("덕램")
                .build();
        return beforeSubscribeRepository.save(beforeSubscribe);
    }



    private Subscribe generateSubscribe(Member member, int i) {

        Card card = Card.builder()
                .member(member)
                .customerUid("custom_uid" + i)
                .cardName("cardName" + i)
                .cardNumber("카드번호" + i)
                .build();
        cardRepository.save(card);

        Subscribe subscribe = Subscribe.builder()
                .subscribeCount(i+1)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .card(card)
                .status(SubscribeStatus.SUBSCRIBING)
                .build();
        subscribeRepository.save(subscribe);
        return subscribe;
    }

    private Subscribe generateSubscribeUseCoupon(Member member, int i) {
        Coupon coupon = generateGeneralCoupon(2);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 3, CouponStatus.ACTIVE);

        Card card = Card.builder()
                .member(member)
                .customerUid("custom_uid" + i)
                .cardName("cardName" + i)
                .cardNumber("카드번호" + i)
                .build();
        cardRepository.save(card);

        Subscribe subscribe = Subscribe.builder()
                .subscribeCount(i + 1)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBING)
                .memberCoupon(memberCoupon)
                .discountCoupon(3000)
                .countSkipOneTime(3)
                .card(card)
                .build();
        subscribeRepository.save(subscribe);

        return subscribe;
    }




    private SurveyReport generateSurveyReport(Member member) {

        Recipe recipe = recipeRepository.findAll().get(0);

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

        List<Dog> dogs = dogRepository.findByMember(member);
        Recipe findRecipe = recipeRepository.findById(requestDto.getRecommendRecipeId()).get();

        String birth = requestDto.getBirth();

        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .build();
        subscribeRepository.save(subscribe);

        DogSize dogSize = requestDto.getDogSize();
        Long startAgeMonth = getTerm(birth + "01");
        boolean oldDog = requestDto.isOldDog();
        boolean neutralization = requestDto.isNeutralization();
        DogStatus dogStatus = requestDto.getDogStatus();
        SnackCountLevel snackCountLevel = requestDto.getSnackCountLevel();
        BigDecimal weight = new BigDecimal(requestDto.getWeight());

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
        return surveyReport;
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

        Coupon coupon = generateGeneralCoupon(j);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, j, CouponStatus.ACTIVE);
        OrderItem orderItem = OrderItem.builder()
                .generalOrder(generalOrder)
                .item(item)
                .salePrice(item.getSalePrice())
                .amount(j)
                .memberCoupon(memberCoupon)
                .finalPrice(item.getSalePrice() * j)
                .status(orderStatus)

                .build();
        return orderItemRepository.save(orderItem);
    }



    private Item generateItem(int i) {
        Item item = Item.builder()
                .itemType(ItemType.GOODS)
                .name("굿즈 상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("NEW,BEST")
                .totalSalesAmount(i)
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
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
        Dog dog = generateDog(member, i, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = Subscribe.builder()
                .dog(dog)
                .subscribeCount(i)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBING)
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
                .status(DeliveryStatus.DELIVERY_START)
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);
        return delivery;
    }

    private Delivery generateDeliveryNoNumber(Member member, int i) {
        Delivery delivery = Delivery.builder()
                .recipient(Recipient.builder()
                        .name(member.getName())
                        .phone(member.getPhoneNumber())
                        .zipcode(member.getAddress().getZipcode())
                        .street(member.getAddress().getStreet())
                        .detailAddress(member.getAddress().getDetailAddress())
                        .build())
                .departureDate(LocalDateTime.now().minusDays(4))
                .arrivalDate(LocalDateTime.now().minusDays(1))
                .status(DeliveryStatus.PAYMENT_DONE)
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);
        return delivery;
    }

    private Dog generateDog(Member member, int i, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
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










    //==============================================

    private LocalDate getNextDeliveryDate() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();
        int i = dayOfWeekNumber - 3;
        LocalDate nextDeliveryDate = null;
        if (dayOfWeekNumber <= 5) {
            nextDeliveryDate = today.plusDays(i+7);
        } else {
            nextDeliveryDate = today.plusDays(i+14);
        }
        return nextDeliveryDate;
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


    private Subscribe generateSubscribe(Dog dog, SubscribePlan plan) {
        List<Recipe> recipes = recipeRepository.findAll();

        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .plan(plan)
                .subscribeCount(3)
                .nextPaymentPrice(100000)
                .build();

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

    private Dog generateDogRepresentative(Member admin, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
        Dog dog = Dog.builder()
                .member(admin)
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










    private String getAdminToken() throws Exception {
        return getBearerToken(appProperties.getAdminEmail(), appProperties.getAdminPassword());
    }

    private String getUserToken() throws Exception {
        return getBearerToken(appProperties.getUserEmail(), appProperties.getUserPassword());
    }

    private String getBearerToken(String email, String password) throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .email(email)
                .password(password)
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