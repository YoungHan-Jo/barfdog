package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.orderDto.SubscribeOrderRequestDto;
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
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.orderItem.SelectOption;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardName;
import com.bi.barfdog.domain.reward.RewardStatus;
import com.bi.barfdog.domain.reward.RewardType;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.domain.surveyReport.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
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
import java.util.List;
import java.util.stream.IntStream;

import static com.bi.barfdog.config.finalVariable.StandardVar.*;
import static com.bi.barfdog.config.finalVariable.StandardVar.LACTATING;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
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
            

    @Test
    @DisplayName("구독 주문서 조회하기")
    public void getOrderSheetDto_Subscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = generateSubscribe(dogRepresentative, SubscribePlan.FULL);

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
        Subscribe subscribe = generateSubscribe(dogRepresentative, SubscribePlan.FULL);
        int subscribeCount = subscribe.getSubscribeCount();

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
                .andDo(document("order_subscribeOrder",
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("impUid").optional().description("아임포트 결제 uid"),
                                fieldWithPath("merchantUid").optional().description("바프독에 저장할 주문 고유id yyMMdd + '_' +랜덤문자열  ex) '220707_sdlfiajskd' "),
                                fieldWithPath("memberCouponId").description("사용할 보유한 쿠폰 id"),
                                fieldWithPath("deliveryDto.name").optional().description("수령자 이름"),
                                fieldWithPath("deliveryDto.phone").optional().description("수령자 전화번호"),
                                fieldWithPath("deliveryDto.zipcode").optional().description("우편번호"),
                                fieldWithPath("deliveryDto.street").optional().description("도로명주소"),
                                fieldWithPath("deliveryDto.detailAddress").optional().description("상세주소"),
                                fieldWithPath("deliveryDto.request").description("배송 요청사항"),
                                fieldWithPath("orderPrice").optional().description("주문 상품 총 가격"),
                                fieldWithPath("deliveryPrice").optional().description("배송비"),
                                fieldWithPath("discountTotal").optional().description("총 할인 합계"),
                                fieldWithPath("discountReward").optional().description("사용할 적립금"),
                                fieldWithPath("discountCoupon").optional().description("쿠폰 적용으로 할인된 금액"),
                                fieldWithPath("paymentPrice").optional().description("최종 결제 금액"),
                                fieldWithPath("paymentMethod").optional().description("결제 방법 [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]"),
                                fieldWithPath("nextDeliveryDate").optional().description("배송 예정일 'yyyy-MM-dd'"),
                                fieldWithPath("agreePrivacy").optional().description("개인정보제공 동의 true/false"),
                                fieldWithPath("brochure").optional().description("브로슈어 받을지 여부 true/false")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCouponId).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining - 1);
        assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        if (findSubscribe.getPlan() == SubscribePlan.FULL) {
            assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(nextDeliveryDate.plusDays(14 - 7));
        } else {
            assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(nextDeliveryDate.plusDays(28 - 7));
        }
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(orderPrice);
        assertThat(findSubscribe.getNextDeliveryDate()).isEqualTo(nextDeliveryDate);
        assertThat(findSubscribe.getSubscribeCount()).isEqualTo(subscribeCount + 1);
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
        assertThat(findOrder.getSubscribeCount()).isEqualTo(subscribeCount + 1);
        assertThat(findOrder.getDelivery().getId()).isEqualTo(findDelivery.getId());

    }

    @Test
    @DisplayName("정상적으로 구독 주문하기 - HALF and 쿠폰 사용 안 할 경우")
    public void orderSubscribe_HALF_no_coupon() throws Exception {
        //given
        memberCouponRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Subscribe subscribe = generateSubscribe(dogRepresentative, SubscribePlan.HALF);
        int subscribeCount = subscribe.getSubscribeCount();

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
        int discountCoupon = 0;
        int paymentPrice = 90000;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        String impUid = "imp_uid_askdfj";
        String merchantUid = "merchantUid_sdkfjals";
        SubscribeOrderRequestDto requestDto = SubscribeOrderRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
//                .memberCouponId(memberCouponId)
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
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining);
        assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getSubscribeCount()).isEqualTo(subscribeCount + 1);
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

    @Test
    @DisplayName("쿠폰 1장일 경우 구독 주문 후 0개 된 뒤 비활성화")
    public void orderSubscribe_memberCoupon_be_inactive() throws Exception {
        //given
        memberCouponRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Subscribe subscribe = generateSubscribe(dogRepresentative, SubscribePlan.FULL);

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
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);
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
        assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.INACTIVE);

    }

    @Test
    @DisplayName("정상적으로 구독 주문 리스트 조회")
    public void querySubscribeOrders() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,14).forEach(i -> {
            generateSubscribeOrderAndEtc(member, i, OrderStatus.DELIVERY_READY);
        });
        IntStream.range(1,3).forEach(i -> {
            generateSubscribeOrderAndEtc(admin, i, OrderStatus.DELIVERY_READY);
        });


        //when & then
        mockMvc.perform(get("/api/orders/subscribe")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andDo(document("query_subscribeOrders",
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
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].recipeDto.thumbnailUrl").description("대표 썸네일 url"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].recipeDto.recipeName").description("대표 레시피 이름"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.orderId").description("주문 id"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.subscribeId").description("구독 id"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.orderDate").description("주문 날짜"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.dogName").description("강아지 이름"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.subscribeCount").description("구독 회차"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.merchantUid").description("주문 번호"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.paymentPrice").description("결제 금액"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0].subscribeOrderDto.orderStatus").description("주문 상태 " +
                                        "[ALL, HOLD, PAYMENT_DONE, PRODUCING, \" +\n" +
                                        "\"DELIVERY_READY, DELIVERY_START, \" +\n" +
                                        "\"SELLING_CANCEL, CANCEL_REQUEST, CANCEL_DONE, \" +\n" +
                                        "\"RETURN_REQUEST, RETURN_DONE, \" +\n" +
                                        "\"EXCHANGE_REQUEST, EXCHANGE_DONE, \" +\n" +
                                        "\"FAILED, CONFIRM]"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0]._links.query_subscribeOrder.href").description("주문 상세보기 링크"),
                                fieldWithPath("_embedded.querySubscribeOrdersDtoList[0]._links.query_subscribe.href").description("구독 상세보기"),
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
    @DisplayName("구독주문 하나 조회")
    public void querySubscribeOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtc(member, 1, OrderStatus.DELIVERY_READY);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/{id}/subscribe", subscribeOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_subscribeOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("주문 id")
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
                                fieldWithPath("recipeDto.thumbnailUrl").description("썸네일 url"),
                                fieldWithPath("recipeDto.recipeName").description("대표 레시피"),
                                fieldWithPath("recipeNames").description("구독 레시피 이름 나열 'xxx,xxx' , 로 구분"),
                                fieldWithPath("orderDto.subscribeCount").description("구독 회차"),
                                fieldWithPath("orderDto.dogName").description("강아지 이름"),
                                fieldWithPath("orderDto.oneMealRecommendGram").description("급여량"),
                                fieldWithPath("orderDto.plan").description("구독 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("orderDto.orderPrice").description("가격 / 주문금액"),
                                fieldWithPath("orderDto.beforeSubscribeCount").description("변경 전 구독 회차, 변경 없으면 0"),
                                fieldWithPath("orderDto.beforePlan").description("변경 전 플랜 [FULL,HALF,TOPPING] , 변경 없으면 null"),
                                fieldWithPath("orderDto.beforeOneMealRecommendGram").description("변경 전 급여량 , 변경 없으면 null"),
                                fieldWithPath("orderDto.beforeRecipeName").description("변경 전 레시피 이름 , 변경 없으면 null"),
                                fieldWithPath("orderDto.beforeOrderPrice").description("변경 전 가격 , 변경 없으면 0"),
                                fieldWithPath("orderDto.merchantUid").description("주문 번호"),
                                fieldWithPath("orderDto.orderType").description("주문 타입 ['subscribe','general'"),
                                fieldWithPath("orderDto.orderDate").description("주문 날짜"),
                                fieldWithPath("orderDto.deliveryNumber").description("운송장번호 . 없으면 null"),
                                fieldWithPath("orderDto.deliveryStatus").description("배송 상태 [PAYMENT_DONE,\n" +
                                        "    PRODUCING, DELIVERY_READY,\n" +
                                        "    DELIVERY_START,\n" +
                                        "    DELIVERY_DONE]"),
                                fieldWithPath("orderDto.deliveryPrice").description("배송비"),
                                fieldWithPath("orderDto.discountTotal").description("할인 총합"),
                                fieldWithPath("orderDto.discountReward").description("사용 적립금"),
                                fieldWithPath("orderDto.discountCoupon").description("쿠폰 사용 금액"),
                                fieldWithPath("orderDto.paymentPrice").description("결제 금액"),
                                fieldWithPath("orderDto.paymentMethod").description("결제 방법 [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]"),
                                fieldWithPath("orderDto.recipientName").description("받는사람 이름"),
                                fieldWithPath("orderDto.recipientPhone").description("받는사람 휴대전화"),
                                fieldWithPath("orderDto.zipcode").description("우편번호"),
                                fieldWithPath("orderDto.street").description("도로명주소"),
                                fieldWithPath("orderDto.detailAddress").description("상세주소"),
                                fieldWithPath("orderDto.request").description("배송요청사항"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("구독주문 하나 조회시 변경사항 없고 송장번호 없는 경우")
    public void querySubscribeOrder_noBefore_NoDeliveryNumber() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtc_NoBeforeSubscribe_no_deliveryNumber(member, 1, OrderStatus.DELIVERY_READY);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/{id}/subscribe", subscribeOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("구독주문 하나 조회 404")
    public void querySubscribeOrder_404() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtc_NoBeforeSubscribe_no_deliveryNumber(member, 1, OrderStatus.DELIVERY_READY);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/999999/subscribe")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
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

        Subscribe subscribe = generateSubscribe(i);
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

        Subscribe subscribe = generateSubscribe(i);

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



    private Subscribe generateSubscribe(int i) {
        Subscribe subscribe = Subscribe.builder()
                .subscribeCount(i+1)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDate.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBING)
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
                .nextPaymentDate(LocalDate.now().plusDays(6))
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