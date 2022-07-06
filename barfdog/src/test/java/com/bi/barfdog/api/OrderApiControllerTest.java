package com.bi.barfdog.api;

import com.bi.barfdog.api.orderDto.SubscribeOrderRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardName;
import com.bi.barfdog.domain.reward.RewardStatus;
import com.bi.barfdog.domain.reward.RewardType;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class OrderApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;
    @Autowired
    AppProperties appProperties;
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
    MemberCouponRepository memberCouponRepository;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("구독 주문서 조회하기")
    public void getOrderSheetDto_Subscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = generateSubscribe(dogRepresentative);

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/sheet/subscribe/{id}", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_orderSheet_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("order_subscribe").description("구독 주문하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("subscribeDto.id").description("구독 id"),
                                fieldWithPath("subscribeDto.plan").description("구독 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("subscribeDto.nextPaymentPrice").description("구독 상품 금액"),
                                fieldWithPath("recipeNameList").description("구독으로 선택한 레시피 이름 리스트"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("grade").description("회원 등급"),
                                fieldWithPath("gradeDiscountPercent").description("등급에 해당하는 할인율(%)"),
                                fieldWithPath("email").description("회원 이메일 주소"),
                                fieldWithPath("phoneNumber").description("휴대전화 번호"),
                                fieldWithPath("address.zipcode").description("우편번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명 주소"),
                                fieldWithPath("address.detailAddress").description("상세 주소"),
                                fieldWithPath("nextDeliveryDate").description("배송 예정일 'yyyy-MM-dd' "),
                                fieldWithPath("coupons[0].memberCouponId").description("멤버가 보유한 쿠폰 id"),
                                fieldWithPath("coupons[0].name").description("쿠폰 이름"),
                                fieldWithPath("coupons[0].discountType").description("할인 타입 ['FIXED_RATE' / 'FLAT_RATE']"),
                                fieldWithPath("coupons[0].discountDegree").description("할인 정도 ( 원 / % )"),
                                fieldWithPath("coupons[0].availableMaxDiscount").description("적용가능 최대 할인 금액"),
                                fieldWithPath("coupons[0].availableMinPrice").description("사용가능한 최소 물품 가격"),
                                fieldWithPath("coupons[0].remaining").description("쿠폰 남은 개수"),
                                fieldWithPath("coupons[0].expiredDate").description("쿠폰 유효 기한"),
                                fieldWithPath("reward").description("회원이 보유 적립금"),
                                fieldWithPath("brochure").description("브로슈어 받은 적 있는지 여부 true/false"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.order_subscribe.href").description("구독 주문하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 구독 주문하기")
    public void orderSubscribe() throws Exception {
       //given
        memberCouponRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Subscribe subscribe = generateSubscribe(dogRepresentative);

        String request = "안전배송 부탁드립니다.";
        SubscribeOrderRequestDto.DeliveryDto deliveryDto = SubscribeOrderRequestDto.DeliveryDto.builder()
                .name(member.getName())
                .phone(member.getPhoneNumber())
                .zipcode(member.getAddress().getZipcode())
                .street(member.getAddress().getStreet())
                .detailAddress(member.getAddress().getDetailAddress())
                .request(request)
                .build();

        LocalDate nextDeliveryDate = getNextDeliveryDate();

        Coupon coupon = generateGeneralCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 4, CouponStatus.ACTIVE);
        int remaining = memberCoupon.getRemaining();
        Long memberCouponId = memberCoupon.getId();

        int orderPrice = 100000;
        int deliveryPrice = 0;
        int discountTotal = 20000;
        int discountReward = 10000;
        int discountCoupon = 10000;
        int paymentPrice = 80000;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        String impUid = "imp_uid_askdfj";
        String merchantUid = "merchantUid_sdkfjals";
        SubscribeOrderRequestDto requestDto = SubscribeOrderRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .memberCouponId(memberCouponId)
                .deliveryDto(deliveryDto)
                .orderPrice(orderPrice)
                .deliveryPrice(deliveryPrice)
                .discountTotal(discountTotal)
                .discountReward(discountReward)
                .discountCoupon(discountCoupon)
                .paymentPrice(paymentPrice)
                .paymentMethod(paymentMethod)
                .nextDeliveryDate(nextDeliveryDate)
                .isBrochure(true)
                .isAgreePrivacy(true)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/subscribe/{id}", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCouponId).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining - 1);

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getSubscribeCount()).isEqualTo(1);
        if (findSubscribe.getPlan() == SubscribePlan.FULL) {
            assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(nextDeliveryDate.plusDays(14 - 7));
        } else {
            assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(nextDeliveryDate.plusDays(28 - 7));
        }
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(orderPrice);
        assertThat(findSubscribe.getNextDeliveryDate()).isEqualTo(nextDeliveryDate);
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.SUBSCRIBING);

        Delivery findDelivery = deliveryRepository.findAll().get(0);
        assertThat(findDelivery.getRecipient().getName()).isEqualTo(member.getName());
        assertThat(findDelivery.getRecipient().getPhone()).isEqualTo(member.getPhoneNumber());
        assertThat(findDelivery.getRecipient().getZipcode()).isEqualTo(member.getAddress().getZipcode());
        assertThat(findDelivery.getRecipient().getStreet()).isEqualTo(member.getAddress().getStreet());
        assertThat(findDelivery.getRecipient().getDetailAddress()).isEqualTo(member.getAddress().getDetailAddress());
        assertThat(findDelivery.getStatus()).isEqualTo(DeliveryStatus.PAYMENT_DONE);
        assertThat(findDelivery.getRequest()).isEqualTo(request);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward - discountReward);
        assertThat(findMember.getAccumulatedAmount()).isEqualTo(paymentPrice);
        assertThat(findMember.isSubscribe()).isTrue();
        assertThat(findMember.getAccumulatedSubscribe()).isEqualTo(1);
        assertThat(findMember.isBrochure()).isTrue();
        assertThat(findMember.getRoles()).isEqualTo("USER,SUBSCRIBER");

        Reward findReward = rewardRepository.findByMember(member).get(0);
        assertThat(findReward.getName()).isEqualTo(RewardName.USE_ORDER);
        assertThat(findReward.getRewardType()).isEqualTo(RewardType.ORDER);
        assertThat(findReward.getRewardStatus()).isEqualTo(RewardStatus.USED);
        assertThat(findReward.getTradeReward()).isEqualTo(discountReward);

        SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findAll().get(0);
        assertThat(findOrder.getImpUid()).isEqualTo(impUid);
        assertThat(findOrder.getMerchantUid()).isEqualTo(merchantUid);
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_DONE);
        assertThat(findOrder.getOrderPrice()).isEqualTo(orderPrice);
        assertThat(findOrder.getDeliveryPrice()).isEqualTo(deliveryPrice);
        assertThat(findOrder.getDiscountTotal()).isEqualTo(discountTotal);
        assertThat(findOrder.getDiscountReward()).isEqualTo(discountReward);
        assertThat(findOrder.getDiscountCoupon()).isEqualTo(discountCoupon);
        assertThat(findOrder.getPaymentPrice()).isEqualTo(paymentPrice);
        assertThat(findOrder.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(findOrder.isPackage()).isFalse();
        assertThat(findOrder.isAgreePrivacy()).isTrue();
        assertThat(findOrder.getDelivery().getId()).isEqualTo(findDelivery.getId());

    }

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


//    @Test
//    public void validateOrderSheetSubscribe() throws Exception {
//       //given
//
//       //when & then
//        mockMvc.perform(get("/api/orders/sheet/subscribe/validate")
//                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaTypes.HAL_JSON)
//                        .content(objectMapper.writeValueAsString(null)))
//                .andDo(print())
//                .andExpect(status().isOk())
//        ;
//    }





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


    private Subscribe generateSubscribe(Dog dog) {
        List<Recipe> recipes = recipeRepository.findAll();

        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .plan(SubscribePlan.FULL)
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