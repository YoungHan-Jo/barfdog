package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.*;
import com.bi.barfdog.domain.orderItem.*;
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
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.repository.basket.BasketOptionRepository;
import com.bi.barfdog.repository.basket.BasketRepository;
import com.bi.barfdog.repository.card.CardRepository;
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
import com.bi.barfdog.repository.orderItem.SelectOptionRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.BeforeSubscribeRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import org.junit.Before;
import org.junit.Ignore;
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
    @Autowired
    CardRepository cardRepository;
    @Autowired
    ItemImageRepository itemImageRepository;
    @Autowired
    BasketRepository basketRepository;
    @Autowired
    BasketOptionRepository basketOptionRepository;

    @Before
    public void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();
        subscribeRecipeRepository.deleteAll();
        beforeSubscribeRepository.deleteAll();
        subscribeRepository.deleteAll();
        surveyReportRepository.deleteAll();
        dogRepository.deleteAll();
    }


    @Test
    @DisplayName("정상적으로 일반 주문 주문서 조회하기")
    public void queryOrderSheetDto_general() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtc(member, 1, OrderStatus.PAYMENT_DONE);

        Item item1 = generateItem(1);
        ItemOption option1 = generateOption(item1, 1, 999);
        ItemOption option2 = generateOption(item1, 2, 999);

        Item item2 = generateItem(2);
        ItemOption option3 = generateOption(item1, 3, 999);
        ItemOption option4 = generateOption(item1, 4, 999);

        List<OrderSheetGeneralRequestDto.OrderItemDto> orderItemDtoList = new ArrayList<>();
        addOrderItemDto(item1, option1, option2, orderItemDtoList, 1);
        addOrderItemDto(item2, option3, option4, orderItemDtoList, 2);

        OrderSheetGeneralRequestDto requestDto = OrderSheetGeneralRequestDto.builder()
                .orderItemDtoList(orderItemDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/orders/sheet/general")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("deliveryId").isNotEmpty())
                .andExpect(jsonPath("nextSubscribeDeliveryDate").isNotEmpty())
                .andDo(document("query_orderSheet_general",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("order_general").description("일반상품 주문하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("orderItemDtoList[0].itemDto.itemId").description("상품 id"),
                                fieldWithPath("orderItemDtoList[0].itemDto.amount").description("상품 개수"),
                                fieldWithPath("orderItemDtoList[0].itemOptionDtoList[0].itemOptionId").description("옵션 id"),
                                fieldWithPath("orderItemDtoList[0].itemOptionDtoList[0].amount").description("옵션 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("orderItemDtoList[0].itemId").description("상품 id"),
                                fieldWithPath("orderItemDtoList[0].name").description("상품 이름"),
                                fieldWithPath("orderItemDtoList[0].optionDtoList[0].optionId").description("옵션 id"),
                                fieldWithPath("orderItemDtoList[0].optionDtoList[0].name").description("옵션 이름"),
                                fieldWithPath("orderItemDtoList[0].optionDtoList[0].price").description("옵션 하나 가격"),
                                fieldWithPath("orderItemDtoList[0].optionDtoList[0].amount").description("옵션 개수"),
                                fieldWithPath("orderItemDtoList[0].amount").description("상품 개수"),
                                fieldWithPath("orderItemDtoList[0].originalOrderLinePrice").description("자체 할인 전 상품+옵션 가격 총 가격"),
                                fieldWithPath("orderItemDtoList[0].orderLinePrice").description("자체 할인 후 상품+옵션 가격 총 가격"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("phoneNumber").description("회원 휴대전화"),
                                fieldWithPath("address.zipcode").description("우편번호"),
                                fieldWithPath("address.city").description("시도"),
                                fieldWithPath("address.street").description("도로명주소"),
                                fieldWithPath("address.detailAddress").description("상세주소"),
                                fieldWithPath("deliveryId").description("묶음배송할 배송 id . 묶음배송 불가능한 경우 null"),
                                fieldWithPath("nextSubscribeDeliveryDate").description("묶음배송할 배송 예정일 . 묶음배송 불가능한 경우 null"),
                                fieldWithPath("coupons[0].memberCouponId").description("회원쿠폰 id"),
                                fieldWithPath("coupons[0].name").description("쿠폰 이름"),
                                fieldWithPath("coupons[0].discountType").description("할인 타입 ['FIXED_RATE' / 'FLAT_RATE']"),
                                fieldWithPath("coupons[0].discountDegree").description("할인 정도 ( 원 / % )"),
                                fieldWithPath("coupons[0].availableMaxDiscount").description("적용가능 최대 할인 금액"),
                                fieldWithPath("coupons[0].availableMinPrice").description("사용가능한 최소 물품 가격"),
                                fieldWithPath("coupons[0].remaining").description("쿠폰 남은 개수"),
                                fieldWithPath("coupons[0].expiredDate").description("쿠폰 유효 기한"),
                                fieldWithPath("orderPrice").description("쿠폰.적립금 적용 전 총 가격"),
                                fieldWithPath("reward").description("사용가능한 적립금"),
                                fieldWithPath("deliveryPrice").description("배송비"),
                                fieldWithPath("freeCondition").description("배송비 무료 조건, xx원 이상 무료배송"),
                                fieldWithPath("brochure").description("브로슈어 받은 적 있는지 true/false"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.order_general.href").description("일반상품 주문하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("일반 주문 주문서 조회하기, 묶음배송 없음")
    public void queryOrderSheetDto_general_noPackage() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item1 = generateItem(1);
        ItemOption option1 = generateOption(item1, 1, 999);
        ItemOption option2 = generateOption(item1, 2, 999);

        Item item2 = generateItem(2);
        ItemOption option3 = generateOption(item1, 3, 999);
        ItemOption option4 = generateOption(item1, 4, 999);

        List<OrderSheetGeneralRequestDto.OrderItemDto> orderItemDtoList = new ArrayList<>();
        addOrderItemDto(item1, option1, option2, orderItemDtoList, 1);
        addOrderItemDto(item2, option3, option4, orderItemDtoList, 2);

        OrderSheetGeneralRequestDto requestDto = OrderSheetGeneralRequestDto.builder()
                .orderItemDtoList(orderItemDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/orders/sheet/general")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("deliveryId").isEmpty())
                .andExpect(jsonPath("nextSubscribeDeliveryDate").isEmpty())
        ;

    }

    @Test
    @DisplayName("정상적으로 결제하기 버튼 눌러서 정보 저장하는 테스트")
    public void orderGeneralOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int remainReward = member.getReward();
        int accumulatedAmount = member.getAccumulatedAmount();
        boolean brochure = member.isBrochure();

        Item item1 = generateItem(1);
        int itemRemain1 = item1.getRemaining();
        ItemOption option1 = generateOption(item1, 1, 999);
        int optionRemain1 = option1.getRemaining();
        ItemOption option2 = generateOption(item1, 2, 999);
        int optionRemain2 = option2.getRemaining();

        Item item2 = generateItem(2);
        int itemRemain2 = item2.getRemaining();
        ItemOption option3 = generateOption(item1, 3, 999);
        int optionRemain3 = option3.getRemaining();
        ItemOption option4 = generateOption(item1, 4, 999);
        int optionRemain4 = option4.getRemaining();

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 1, CouponStatus.ACTIVE);
        int couponRemain1 = memberCoupon1.getRemaining();

        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 2, CouponStatus.ACTIVE);
        int couponRemain2 = memberCoupon2.getRemaining();

        List<GeneralOrderRequestDto.OrderItemDto> orderItemDtoList = new ArrayList<>();
        int itemAmount1 = 1;
        int optionAmount1 = 1;
        int optionAmount2 = 2;
        int orderLinePrice1 = addOrderItemAndOptionDto(item1, option1, option2, memberCoupon1, orderItemDtoList, itemAmount1, optionAmount1, optionAmount2);
        int itemAmount2 = 2;
        int optionAmount3 = 3;
        int optionAmount4 = 4;
        int orderLinePrice2 = addOrderItemAndOptionDto(item2, option3, option4, memberCoupon2, orderItemDtoList, itemAmount2, optionAmount3, optionAmount4);

        Delivery delivery = generateDelivery(DeliveryStatus.PAYMENT_DONE);

        int discountReward = 10000;
        int paymentPrice = orderLinePrice1 + orderLinePrice2 - discountReward;
        int discountCoupon = 4000;
        int orderPrice = orderLinePrice1 + orderLinePrice2 + discountCoupon;
        int discountTotal = discountCoupon + discountReward;
        int deliveryPrice = 0;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;



        GeneralOrderRequestDto requestDto = GeneralOrderRequestDto.builder()
                .orderItemDtoList(orderItemDtoList)
                .deliveryDto(GeneralOrderRequestDto.DeliveryDto.builder()
                        .name("수령자")
                        .phone("01012341234")
                        .zipcode("12345")
                        .street("도로명 주소")
                        .detailAddress("상세주소 1동 102호")
                        .request("배송 요청사항")
                        .build())
                .deliveryId(delivery.getId())
                .orderPrice(orderPrice)
                .deliveryPrice(deliveryPrice)
                .discountTotal(discountTotal)
                .discountReward(discountReward)
                .discountCoupon(discountCoupon)
                .paymentPrice(paymentPrice)
                .paymentMethod(paymentMethod)
                .isBrochure(true)
                .isAgreePrivacy(true)
                .isBrochure(true)
                .build();

        //when & then
        mockMvc.perform(post("/api/orders/general")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order_generalOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("success_generalOrder").description("결제 성공 링크"),
                                linkWithRel("fail_generalOrder").description("결제 실패 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("orderItemDtoList[0].itemId").description("상품 id"),
                                fieldWithPath("orderItemDtoList[0].amount").description("상품 개수"),
                                fieldWithPath("orderItemDtoList[0].selectOptionDtoList[0].itemOptionId").description("옵션 id"),
                                fieldWithPath("orderItemDtoList[0].selectOptionDtoList[0].amount").description("옵션 개수"),
                                fieldWithPath("orderItemDtoList[0].memberCouponId").description("사용한 보유쿠폰(memberCoupon) id"),
                                fieldWithPath("orderItemDtoList[0].discountAmount").description("쿠폰 할인 총계"),
                                fieldWithPath("orderItemDtoList[0].finalPrice").description("쿠폰 적용 후 주문 내역 한 줄(일반상품 + 옵션 가격) 최종 가격"),
                                fieldWithPath("deliveryDto.name").optional().description("수령자 이름 . 묶음배송일 경우 null"),
                                fieldWithPath("deliveryDto.phone").optional().description("수령자 전화번호 . 묶음배송일 경우 null"),
                                fieldWithPath("deliveryDto.zipcode").optional().description("우편번호 . 묶음배송일 경우 null"),
                                fieldWithPath("deliveryDto.street").optional().description("도로명주소 . 묶음배송일 경우 null"),
                                fieldWithPath("deliveryDto.detailAddress").optional().description("상세주소 . 묶음배송일 경우 null"),
                                fieldWithPath("deliveryDto.request").description("배송 요청사항 . 묶음배송일 경우 null"),
                                fieldWithPath("deliveryId").optional().description("묶음 배송 할 배송 id . 묶음배송 아닐 경우 null"),
                                fieldWithPath("orderPrice").optional().description("주문 상품 총 가격(할인적용 전)"),
                                fieldWithPath("deliveryPrice").optional().description("배송비"),
                                fieldWithPath("discountTotal").optional().description("총 할인 합계"),
                                fieldWithPath("discountReward").optional().description("사용할 적립금"),
                                fieldWithPath("discountCoupon").optional().description("쿠폰 적용으로 할인된 금액"),
                                fieldWithPath("paymentPrice").optional().description("최종 결제 금액"),
                                fieldWithPath("paymentMethod").optional().description("결제 방법 [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]"),
                                fieldWithPath("brochure").optional().description("브로슈어 받을지 여부 true/false"),
                                fieldWithPath("agreePrivacy").optional().description("개인정보제공 동의 true/false")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("주문 id"),
                                fieldWithPath("merchantUid").description("주문 넘버 (아임포트로 넘겨야하는 uid) "),
                                fieldWithPath("status").description("주문 상태"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.success_generalOrder.href").description("결제 성공 링크"),
                                fieldWithPath("_links.fail_generalOrder.href").description("결제 실패 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        GeneralOrder findOrder = (GeneralOrder) orderRepository.findAll().get(0);
        assertThat(findOrder.getMerchantUid()).isNotNull();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);
        assertThat(findOrder.getOrderPrice()).isEqualTo(orderPrice);
        assertThat(findOrder.getDeliveryPrice()).isEqualTo(deliveryPrice);
        assertThat(findOrder.getDiscountTotal()).isEqualTo(discountTotal);
        assertThat(findOrder.getDiscountReward()).isEqualTo(discountReward);
        assertThat(findOrder.getDiscountCoupon()).isEqualTo(discountCoupon);
        assertThat(findOrder.getPaymentPrice()).isEqualTo(paymentPrice);
        assertThat(findOrder.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(findOrder.isPackage()).isEqualTo(true);
        assertThat(findOrder.isBrochure()).isEqualTo(true);
        assertThat(findOrder.isAgreePrivacy()).isEqualTo(true);

        Delivery findDelivery = findOrder.getDelivery();
        assertThat(findDelivery.getId()).isEqualTo(delivery.getId());

        Member findMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        assertThat(findMember.getReward()).isEqualTo(remainReward - discountReward);
        assertThat(findMember.getAccumulatedAmount()).isEqualTo(accumulatedAmount);
        assertThat(findMember.isBrochure()).isEqualTo(brochure);

        List<Reward> rewardList = rewardRepository.findByMember(member);
        assertThat(rewardList.size()).isEqualTo(1);

        Reward findRewardHistory = rewardList.get(0);
        assertThat(findRewardHistory.getTradeReward()).isEqualTo(discountReward);
        assertThat(findRewardHistory.getRewardType()).isEqualTo(RewardType.ORDER);
        assertThat(findRewardHistory.getRewardStatus()).isEqualTo(RewardStatus.USED);

        double rewardPercent = getRewardPercent(member);

        OrderItem orderItem1 = orderItemRepository.findAll().get(0);
        assertThat(orderItem1.getGeneralOrder().getId()).isEqualTo(findOrder.getId());
        assertThat(orderItem1.getItem().getId()).isEqualTo(item1.getId());
        assertThat(orderItem1.getSalePrice()).isEqualTo(item1.getSalePrice());
        assertThat(orderItem1.getAmount()).isEqualTo(itemAmount1);
        assertThat(orderItem1.getMemberCoupon().getId()).isEqualTo(memberCoupon1.getId());
        assertThat(orderItem1.getDiscountAmount()).isEqualTo(2000);
        assertThat(orderItem1.getFinalPrice()).isEqualTo(orderLinePrice1);
        assertThat(orderItem1.getStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);
        assertThat(orderItem1.getSaveReward()).isEqualTo((int) Math.round(orderLinePrice1 * rewardPercent / 100.0));
        assertThat(orderItem1.isSavedReward()).isEqualTo(false);
        assertThat(orderItem1.isWriteableReview()).isEqualTo(false);
        OrderItem orderItem2 = orderItemRepository.findAll().get(1);
        assertThat(orderItem2.getGeneralOrder().getId()).isEqualTo(findOrder.getId());
        assertThat(orderItem2.getItem().getId()).isEqualTo(item2.getId());
        assertThat(orderItem2.getSalePrice()).isEqualTo(item2.getSalePrice());
        assertThat(orderItem2.getAmount()).isEqualTo(itemAmount2);
        assertThat(orderItem2.getMemberCoupon().getId()).isEqualTo(memberCoupon2.getId());
        assertThat(orderItem2.getDiscountAmount()).isEqualTo(2000);
        assertThat(orderItem2.getFinalPrice()).isEqualTo(orderLinePrice2);
        assertThat(orderItem2.getStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);
        assertThat(orderItem2.getSaveReward()).isEqualTo((int) Math.round(orderLinePrice2 * rewardPercent / 100.0));
        assertThat(orderItem2.isSavedReward()).isEqualTo(false);
        assertThat(orderItem2.isWriteableReview()).isEqualTo(false);

        List<SelectOption> selectOptionList1 = selectOptionRepository.findAllByOrderItem(orderItem1);
        SelectOption selectOption1 = selectOptionList1.get(0);
        assertThat(selectOption1.getName()).isEqualTo(option1.getName());
        assertThat(selectOption1.getPrice()).isEqualTo(option1.getOptionPrice());
        assertThat(selectOption1.getAmount()).isEqualTo(optionAmount1);

        SelectOption selectOption2 = selectOptionList1.get(1);
        assertThat(selectOption2.getName()).isEqualTo(option2.getName());
        assertThat(selectOption2.getPrice()).isEqualTo(option2.getOptionPrice());
        assertThat(selectOption2.getAmount()).isEqualTo(optionAmount2);

        List<SelectOption> selectOptionList2 = selectOptionRepository.findAllByOrderItem(orderItem2);
        SelectOption selectOption3 = selectOptionList2.get(0);
        assertThat(selectOption3.getName()).isEqualTo(option3.getName());
        assertThat(selectOption3.getPrice()).isEqualTo(option3.getOptionPrice());
        assertThat(selectOption3.getAmount()).isEqualTo(optionAmount3);

        SelectOption selectOption4 = selectOptionList2.get(1);
        assertThat(selectOption4.getName()).isEqualTo(option4.getName());
        assertThat(selectOption4.getPrice()).isEqualTo(option4.getOptionPrice());
        assertThat(selectOption4.getAmount()).isEqualTo(optionAmount4);

        Item findItem1 = itemRepository.findById(item1.getId()).get();
        assertThat(findItem1.getRemaining()).isEqualTo(itemRemain1 - itemAmount1);
        Item findItem2 = itemRepository.findById(item2.getId()).get();
        assertThat(findItem2.getRemaining()).isEqualTo(itemRemain2 - itemAmount2);

        ItemOption itemOption1 = itemOptionRepository.findById(option1.getId()).get();
        assertThat(itemOption1.getRemaining()).isEqualTo(optionRemain1 - optionAmount1);
        ItemOption itemOption2 = itemOptionRepository.findById(option2.getId()).get();
        assertThat(itemOption2.getRemaining()).isEqualTo(optionRemain2 - optionAmount2);
        ItemOption itemOption3 = itemOptionRepository.findById(option3.getId()).get();
        assertThat(itemOption3.getRemaining()).isEqualTo(optionRemain3 - optionAmount3);
        ItemOption itemOption4 = itemOptionRepository.findById(option4.getId()).get();
        assertThat(itemOption4.getRemaining()).isEqualTo(optionRemain4 - optionAmount4);

        MemberCoupon findMemberCoupon1 = memberCouponRepository.findById(memberCoupon1.getId()).get();
        assertThat(findMemberCoupon1.getRemaining()).isEqualTo(couponRemain1 - 1);
        assertThat(findMemberCoupon1.getMemberCouponStatus()).isEqualTo(CouponStatus.INACTIVE);
        MemberCoupon findMemberCoupon2 = memberCouponRepository.findById(memberCoupon2.getId()).get();
        assertThat(findMemberCoupon2.getRemaining()).isEqualTo(couponRemain2 - 1);
        assertThat(findMemberCoupon2.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);

        assertThat(findMember.getReward()).isEqualTo(remainReward - discountReward);

        Reward reward = rewardRepository.findAll().get(0);
        assertThat(reward.getMember().getId()).isEqualTo(findMember.getId());
        assertThat(reward.getName()).isEqualTo(RewardName.USE_ORDER);
        assertThat(reward.getRewardType()).isEqualTo(RewardType.ORDER);
        assertThat(reward.getRewardStatus()).isEqualTo(RewardStatus.USED);
        assertThat(reward.getTradeReward()).isEqualTo(discountReward);

        assertThat(findItem1.getRemaining()).isEqualTo(itemRemain1 - itemAmount1);
        assertThat(findItem2.getRemaining()).isEqualTo(itemRemain2 - itemAmount2);

        ItemOption findOption1 = itemOptionRepository.findById(option1.getId()).get();
        assertThat(findOption1.getRemaining()).isEqualTo(optionRemain1 - optionAmount1);
        ItemOption findOption2 = itemOptionRepository.findById(option2.getId()).get();
        assertThat(findOption2.getRemaining()).isEqualTo(optionRemain2 - optionAmount2);
        ItemOption findOption3 = itemOptionRepository.findById(option3.getId()).get();
        assertThat(findOption3.getRemaining()).isEqualTo(optionRemain3 - optionAmount3);
        ItemOption findOption4 = itemOptionRepository.findById(option4.getId()).get();
        assertThat(findOption4.getRemaining()).isEqualTo(optionRemain4 - optionAmount4);

    }

    @Test
    @DisplayName("정상적으로 결제하기 버튼 눌러서 정보 저장하는 테스트 - 묶음배송 아닐 경우")
    public void orderGeneralOrder_no_package() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        member.changeGrade(Grade.골드);
        int reward = member.getReward();

        Item item1 = generateItem(1);
        ItemOption option1 = generateOption(item1, 1, 999);
        ItemOption option2 = generateOption(item1, 2, 999);

        Item item2 = generateItem(2);
        ItemOption option3 = generateOption(item1, 3, 999);
        ItemOption option4 = generateOption(item1, 4, 999);

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 1, CouponStatus.ACTIVE);

        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 2, CouponStatus.ACTIVE);

        List<GeneralOrderRequestDto.OrderItemDto> orderItemDtoList = new ArrayList<>();
        int itemAmount1 = 1;
        int optionAmount1 = 1;
        int optionAmount2 = 2;
        int orderLinePrice1 = addOrderItemAndOptionDto(item1, option1, option2, memberCoupon1, orderItemDtoList, itemAmount1, optionAmount1, optionAmount2);
        int itemAmount2 = 2;
        int optionAmount3 = 3;
        int optionAmount4 = 4;
        int orderLinePrice2 = addOrderItemAndOptionDto(item2, option3, option4, memberCoupon2, orderItemDtoList, itemAmount2, optionAmount3, optionAmount4);

        int discountReward = 10000;
        int paymentPrice = orderLinePrice1 + orderLinePrice2 - discountReward;
        int discountCoupon = 4000;
        int orderPrice = orderLinePrice1 + orderLinePrice2 + discountCoupon;
        int discountTotal = discountCoupon + discountReward;
        int deliveryPrice = 0;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;


        String name = "수령자";
        String phone = "01012341234";
        String zipcode = "12345";
        String street = "도로명 주소";
        String detailAddress = "상세주소 1동 102호";
        String request = "배송 요청사항";
        GeneralOrderRequestDto requestDto = GeneralOrderRequestDto.builder()
                .orderItemDtoList(orderItemDtoList)
                .deliveryDto(GeneralOrderRequestDto.DeliveryDto.builder()
                        .name(name)
                        .phone(phone)
                        .zipcode(zipcode)
                        .street(street)
                        .detailAddress(detailAddress)
                        .request(request)
                        .build())
                .orderPrice(orderPrice)
                .deliveryPrice(deliveryPrice)
                .discountTotal(discountTotal)
                .discountReward(discountReward)
                .discountCoupon(discountCoupon)
                .paymentPrice(paymentPrice)
                .paymentMethod(paymentMethod)
                .isBrochure(true)
                .isAgreePrivacy(true)
                .build();

        //when & then
        mockMvc.perform(post("/api/orders/general")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Delivery findDelivery = deliveryRepository.findAll().get(0);
        assertThat(findDelivery.getRecipient().getName()).isEqualTo(name);
        assertThat(findDelivery.getRecipient().getPhone()).isEqualTo(phone);
        assertThat(findDelivery.getRecipient().getZipcode()).isEqualTo(zipcode);
        assertThat(findDelivery.getRecipient().getStreet()).isEqualTo(street);
        assertThat(findDelivery.getRecipient().getDetailAddress()).isEqualTo(detailAddress);
        assertThat(findDelivery.getStatus()).isEqualTo(DeliveryStatus.BEFORE_PAYMENT);
        assertThat(findDelivery.getRequest()).isEqualTo(request);


    }




//    @Ignore
    @Test
    @DisplayName("정상적으로 일반 주문 결제 성공")
    public void successGeneralOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int accumulatedAmount = member.getAccumulatedAmount();
        int remainRewards = member.getReward();
        Item item1 = generateItem(1);
        int item1Remaining = item1.getRemaining();
        int optionRemaining = 999;
        ItemOption option1 = generateOption(item1, 1, optionRemaining);
        ItemOption option2 = generateOption(item1, 2, optionRemaining);

        Item item2 = generateItem(2);
        int item2Remaining = item2.getRemaining();
        ItemOption option3 = generateOption(item1, 3, optionRemaining);
        ItemOption option4 = generateOption(item1, 4, optionRemaining);

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 1, CouponStatus.ACTIVE);

        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 2, CouponStatus.ACTIVE);


        Delivery delivery = generateDelivery(DeliveryStatus.BEFORE_PAYMENT);

        int discountReward = 4000;
        GeneralOrder order = GeneralOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .isBrochure(true)
                .member(member)
                .paymentPrice(100000)
                .discountReward(discountReward)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        int item1Amount = 1;
        OrderItem orderItem1 = generateOrderItem(item1, memberCoupon1, order, item1Amount);
        int option1Amount = 1;
        generateSelectOption(option1, orderItem1, option1Amount);
        int option2Amount = 2;
        generateSelectOption(option2, orderItem1, option2Amount);
        int item2Amount = 2;
        OrderItem orderItem2 = generateOrderItem(item2, memberCoupon2, order, item2Amount);
        int option3Amount = 3;
        generateSelectOption(option3, orderItem2, option3Amount);
        int option4Amount = 4;
        generateSelectOption(option4, orderItem2, option4Amount);


        String impUid = "impuid_asdlkfjsld";
        String merchantUid = "merchantUid_sldkfjsldkf";
        SuccessGeneralRequestDto requestDto = SuccessGeneralRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/success", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("success_generalOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("일반 주문 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("impUid").description("아임포트 uid"),
                                fieldWithPath("merchantUid").description("주문 uid")
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

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getImpUid()).isEqualTo(impUid);
        assertThat(findOrder.getMerchantUid()).isEqualTo(merchantUid);
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_DONE);
        assertThat(findOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.PAYMENT_DONE);
        assertThat(findOrder.getPaymentDate()).isNotNull();

        List<OrderItem> orderItemList = orderItemRepository.findAllByGeneralOrder((GeneralOrder) findOrder);
        for (OrderItem orderItem : orderItemList) {
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.PAYMENT_DONE);
            assertThat(orderItem.isWriteableReview()).isTrue();
        }

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.isBrochure()).isTrue();
        assertThat(findMember.getAccumulatedAmount()).isEqualTo(accumulatedAmount + order.getPaymentPrice());


    }

    @Test
    @DisplayName("일반주문 결제 시 장바구니에 동일한 물품 있을 경우 제거됨")
    public void successGeneralOrder_remove_basket() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int accumulatedAmount = member.getAccumulatedAmount();
        int remainRewards = member.getReward();
        Item item1 = generateItem(1);
        int optionRemaining = 999;
        ItemOption option1 = generateOption(item1, 1, optionRemaining);
        ItemOption option2 = generateOption(item1, 2, optionRemaining);

        Item item2 = generateItem(2);
        ItemOption option3 = generateOption(item1, 3, optionRemaining);
        ItemOption option4 = generateOption(item1, 4, optionRemaining);

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 1, CouponStatus.ACTIVE);

        Delivery delivery = generateDelivery(DeliveryStatus.BEFORE_PAYMENT);

        int discountReward = 4000;
        GeneralOrder order = GeneralOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .isBrochure(true)
                .member(member)
                .paymentPrice(100000)
                .discountReward(discountReward)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        OrderItem orderItem1 = generateOrderItem(item1, memberCoupon1, order, 1);
        int option1Amount = 1;
        generateSelectOption(option1, orderItem1, option1Amount);
        int option2Amount = 2;
        generateSelectOption(option2, orderItem1, option2Amount);

        Basket basket1 = generateBasket(member, item1);
        BasketOption basketOption1 = generateBasketOption(option1, basket1);
        BasketOption basketOption2 = generateBasketOption(option2, basket1);

        Basket basket2 = generateBasket(member, item2);
        BasketOption basketOption3 = generateBasketOption(option3, basket2);
        BasketOption basketOption4 = generateBasketOption(option4, basket2);


        String impUid = "impuid_asdlkfjsld";
        String merchantUid = "merchantUid_sldkfjsldkf";
        SuccessGeneralRequestDto requestDto = SuccessGeneralRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/success", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Optional<Basket> optionalBasket1 = basketRepository.findById(basket1.getId());
        assertThat(optionalBasket1.isPresent()).isFalse();

        Optional<Basket> optionalBasket2 = basketRepository.findById(basket2.getId());
        assertThat(optionalBasket2.isPresent()).isTrue();

        Optional<BasketOption> optionalBasketOption1 = basketOptionRepository.findById(basketOption1.getId());
        assertThat(optionalBasketOption1.isPresent()).isFalse();
        Optional<BasketOption> optionalBasketOption2 = basketOptionRepository.findById(basketOption2.getId());
        assertThat(optionalBasketOption2.isPresent()).isFalse();
        Optional<BasketOption> optionalBasketOption3 = basketOptionRepository.findById(basketOption3.getId());
        assertThat(optionalBasketOption3.isPresent()).isTrue();
        Optional<BasketOption> optionalBasketOption4 = basketOptionRepository.findById(basketOption4.getId());
        assertThat(optionalBasketOption4.isPresent()).isTrue();

    }

    @Test
    @DisplayName("일반주문 결제 시 장바구니에 동일한 물품 있을 경우 제거됨2")
    public void successGeneralOrder_remove_basket2() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int accumulatedAmount = member.getAccumulatedAmount();
        int remainRewards = member.getReward();
        Item item1 = generateItem(1);
        int optionRemaining = 999;
        ItemOption option1 = generateOption(item1, 1, optionRemaining);
        ItemOption option2 = generateOption(item1, 2, optionRemaining);

        Item item2 = generateItem(2);
        ItemOption option3 = generateOption(item1, 3, optionRemaining);
        ItemOption option4 = generateOption(item1, 4, optionRemaining);

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 1, CouponStatus.ACTIVE);
        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 2, CouponStatus.ACTIVE);

        Delivery delivery = generateDelivery(DeliveryStatus.BEFORE_PAYMENT);

        int discountReward = 4000;
        GeneralOrder order = GeneralOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .isBrochure(true)
                .member(member)
                .paymentPrice(100000)
                .discountReward(discountReward)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        OrderItem orderItem1 = generateOrderItem(item1, memberCoupon1, order, 1);
        int option1Amount = 1;
        generateSelectOption(option1, orderItem1, option1Amount);
        int option2Amount = 2;
        generateSelectOption(option2, orderItem1, option2Amount);
        OrderItem orderItem2 = generateOrderItem(item2, memberCoupon2, order, 2);
        int option3Amount = 3;
        generateSelectOption(option3, orderItem2, option3Amount);
        int option4Amount = 4;
        generateSelectOption(option4, orderItem2, option4Amount);

        Basket basket1 = generateBasket(member, item1);
        BasketOption basketOption1 = generateBasketOption(option1, basket1);
        BasketOption basketOption2 = generateBasketOption(option2, basket1);

        Basket basket2 = generateBasket(member, item2);
        BasketOption basketOption3 = generateBasketOption(option3, basket2);
        BasketOption basketOption4 = generateBasketOption(option4, basket2);


        String impUid = "impuid_asdlkfjsld";
        String merchantUid = "merchantUid_sldkfjsldkf";
        SuccessGeneralRequestDto requestDto = SuccessGeneralRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/success", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Optional<Basket> optionalBasket1 = basketRepository.findById(basket1.getId());
        assertThat(optionalBasket1.isPresent()).isFalse();

        Optional<Basket> optionalBasket2 = basketRepository.findById(basket2.getId());
        assertThat(optionalBasket2.isPresent()).isFalse();

        Optional<BasketOption> optionalBasketOption1 = basketOptionRepository.findById(basketOption1.getId());
        assertThat(optionalBasketOption1.isPresent()).isFalse();
        Optional<BasketOption> optionalBasketOption2 = basketOptionRepository.findById(basketOption2.getId());
        assertThat(optionalBasketOption2.isPresent()).isFalse();
        Optional<BasketOption> optionalBasketOption3 = basketOptionRepository.findById(basketOption3.getId());
        assertThat(optionalBasketOption3.isPresent()).isFalse();
        Optional<BasketOption> optionalBasketOption4 = basketOptionRepository.findById(basketOption4.getId());
        assertThat(optionalBasketOption4.isPresent()).isFalse();

    }

    private BasketOption generateBasketOption(ItemOption option, Basket basket) {
        BasketOption basketOption = BasketOption.builder()
                .basket(basket)
                .itemOption(option)
                .build();
        return basketOptionRepository.save(basketOption);
    }

    private Basket generateBasket(Member member, Item item) {
        Basket basket = Basket.builder()
                .item(item)
                .member(member)
                .amount(2)
                .build();
        return basketRepository.save(basket);
    }

    @Test
    @DisplayName("일반 주문 결제 성공 처리 시 존재하지않는 주문 notfound")
    public void successGeneralOrder_notfound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int accumulatedAmount = member.getAccumulatedAmount();
        Item item1 = generateItem(1);
        ItemOption option1 = generateOption(item1, 1, 999);
        ItemOption option2 = generateOption(item1, 2, 999);

        Item item2 = generateItem(2);
        ItemOption option3 = generateOption(item1, 3, 999);
        ItemOption option4 = generateOption(item1, 4, 999);

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 1, CouponStatus.ACTIVE);

        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 2, CouponStatus.ACTIVE);


        Delivery delivery = generateDelivery(DeliveryStatus.BEFORE_PAYMENT);

        int discountReward = 4000;
        GeneralOrder order = GeneralOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .isBrochure(true)
                .member(member)
                .paymentPrice(100000)
                .discountReward(discountReward)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        OrderItem orderItem1 = generateOrderItem(item1, memberCoupon1, order, 1);
        generateSelectOption(option1, orderItem1, 1);
        generateSelectOption(option2, orderItem1, 2);
        OrderItem orderItem2 = generateOrderItem(item2, memberCoupon2, order, 2);
        generateSelectOption(option3, orderItem2, 3);
        generateSelectOption(option4, orderItem2, 4);


        String impUid = "impuid_asdlkfjsld";
        String merchantUid = "merchantUid_sldkfjsldkf";
        SuccessGeneralRequestDto requestDto = SuccessGeneralRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/999999/general/success")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("정상적으로 일반주문 결제 실패처리")
    public void failGeneralOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        int accumulatedAmount = member.getAccumulatedAmount();

        Item item1 = generateItem(1);
        int itemRemain1 = item1.getRemaining();
        ItemOption option1 = generateOption(item1, 1, 999);
        int optionRemain1 = option1.getRemaining();
        ItemOption option2 = generateOption(item1, 2, 999);
        int optionRemain2 = option2.getRemaining();

        Item item2 = generateItem(2);
        int itemRemain2 = item2.getRemaining();
        ItemOption option3 = generateOption(item1, 3, 999);
        int optionRemain3 = option3.getRemaining();
        ItemOption option4 = generateOption(item1, 4, 999);
        int optionRemain4 = option4.getRemaining();

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 0, CouponStatus.INACTIVE);
        int couponRemain1 = memberCoupon1.getRemaining();

        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 1, CouponStatus.ACTIVE);
        int couponRemain2 = memberCoupon2.getRemaining();


        Delivery delivery = generateDelivery(DeliveryStatus.BEFORE_PAYMENT);

        int discountReward = 4000;
        GeneralOrder order = GeneralOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .isBrochure(true)
                .member(member)
                .paymentPrice(100000)
                .discountReward(discountReward)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        int orderItemAmount1 = 1;
        OrderItem orderItem1 = generateOrderItem(item1, memberCoupon1, order, orderItemAmount1);
        int optionAmount1 = 1;
        generateSelectOption(option1, orderItem1, optionAmount1);
        int optionAmount2 = 2;
        generateSelectOption(option2, orderItem1, optionAmount2);
        int orderItemAmount2 = 2;
        OrderItem orderItem2 = generateOrderItem(item2, memberCoupon2, order, orderItemAmount2);
        int optionAmount3 = 3;
        generateSelectOption(option3, orderItem2, optionAmount3);
        int optionAmount4 = 4;
        generateSelectOption(option4, orderItem2, optionAmount4);


       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/fail", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("fail_generalOrder",
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
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

        GeneralOrder findOrder = (GeneralOrder) orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(findOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.BEFORE_PAYMENT);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + discountReward);

        List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(order);
        for (OrderItem orderItem : orderItems) {
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.FAILED);
            assertThat(orderItem.isWriteableReview()).isEqualTo(false);
        }

        Item findItem1 = itemRepository.findById(item1.getId()).get();
        assertThat(findItem1.getRemaining()).isEqualTo(itemRemain1 + orderItemAmount1);
        Item findItem2 = itemRepository.findById(item2.getId()).get();
        assertThat(findItem2.getRemaining()).isEqualTo(itemRemain2 + orderItemAmount2);

        ItemOption findOption1 = itemOptionRepository.findById(option1.getId()).get();
        assertThat(findOption1.getRemaining()).isEqualTo(optionRemain1 + optionAmount1);
        ItemOption findOption2 = itemOptionRepository.findById(option2.getId()).get();
        assertThat(findOption2.getRemaining()).isEqualTo(optionRemain2 + optionAmount2);
        ItemOption findOption3 = itemOptionRepository.findById(option3.getId()).get();
        assertThat(findOption3.getRemaining()).isEqualTo(optionRemain3 + optionAmount3);
        ItemOption findOption4 = itemOptionRepository.findById(option4.getId()).get();
        assertThat(findOption4.getRemaining()).isEqualTo(optionRemain4 + optionAmount4);

        MemberCoupon findMemberCoupon1 = memberCouponRepository.findById(memberCoupon1.getId()).get();
        assertThat(findMemberCoupon1.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(findMemberCoupon1.getRemaining()).isEqualTo(couponRemain1 + 1);

        MemberCoupon findMemberCoupon2 = memberCouponRepository.findById(memberCoupon2.getId()).get();
        assertThat(findMemberCoupon2.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(findMemberCoupon2.getRemaining()).isEqualTo(couponRemain2 + 1);

    }

    @Test
    @DisplayName("일반주문 결제 실패처리 시 존재하지않는 주문 not found")
    public void failGeneralOrder_notfound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        int accumulatedAmount = member.getAccumulatedAmount();

        Item item1 = generateItem(1);
        int itemRemain1 = item1.getRemaining();
        ItemOption option1 = generateOption(item1, 1, 999);
        int optionRemain1 = option1.getRemaining();
        ItemOption option2 = generateOption(item1, 2, 999);
        int optionRemain2 = option2.getRemaining();

        Item item2 = generateItem(2);
        int itemRemain2 = item2.getRemaining();
        ItemOption option3 = generateOption(item1, 3, 999);
        int optionRemain3 = option3.getRemaining();
        ItemOption option4 = generateOption(item1, 4, 999);
        int optionRemain4 = option4.getRemaining();

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 0, CouponStatus.INACTIVE);
        int couponRemain1 = memberCoupon1.getRemaining();

        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 1, CouponStatus.ACTIVE);
        int couponRemain2 = memberCoupon2.getRemaining();


        Delivery delivery = generateDelivery(DeliveryStatus.BEFORE_PAYMENT);

        int discountReward = 4000;
        GeneralOrder order = GeneralOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .isBrochure(true)
                .member(member)
                .paymentPrice(100000)
                .discountReward(discountReward)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        int orderItemAmount1 = 1;
        OrderItem orderItem1 = generateOrderItem(item1, memberCoupon1, order, orderItemAmount1);
        int optionAmount1 = 1;
        generateSelectOption(option1, orderItem1, optionAmount1);
        int optionAmount2 = 2;
        generateSelectOption(option2, orderItem1, optionAmount2);
        int orderItemAmount2 = 2;
        OrderItem orderItem2 = generateOrderItem(item2, memberCoupon2, order, orderItemAmount2);
        int optionAmount3 = 3;
        generateSelectOption(option3, orderItem2, optionAmount3);
        int optionAmount4 = 4;
        generateSelectOption(option4, orderItem2, optionAmount4);


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/999999/general/fail")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    private OrderItem generateOrderItem(Item item1, MemberCoupon memberCoupon1, GeneralOrder order, int amount) {
        OrderItem orderItem = OrderItem.builder()
                .generalOrder(order)
                .item(item1)
                .amount(amount)
                .status(OrderStatus.BEFORE_PAYMENT)
                .memberCoupon(memberCoupon1)
                .writeableReview(false)
                .build();
        orderItemRepository.save(orderItem);
        return orderItem;
    }

    private void generateSelectOption(ItemOption option1, OrderItem orderItem, int amount) {
        SelectOption selectOption = SelectOption.builder()
                .orderItem(orderItem)
                .itemOption(option1)
                .name(option1.getName())
                .price(option1.getOptionPrice())
                .amount(amount)
                .build();
        selectOptionRepository.save(selectOption);
    }


    private double getRewardPercent(Member member) {
        double rewardPercent = 0.0;

        Grade grade = member.getGrade();
        switch (grade) {
            case 실버:
                rewardPercent = 0.5;
            case 골드:
                rewardPercent = 1.0;
            case 플래티넘:
                rewardPercent = 1.5;
            case 다이아몬드:
                rewardPercent = 2.0;
            case 더바프:
                rewardPercent = 3.0;
            default:
                rewardPercent = 0.0;
        }
        return rewardPercent;
    }

    private Delivery generateDelivery(DeliveryStatus status) {
        Delivery delivery = Delivery.builder()
                .recipient(Recipient.builder()
                        .name("수령인 이름")
                        .phone("01000000000")
                        .zipcode("12345")
                        .street("도로명주소")
                        .detailAddress("상세수조")
                        .build())
                .status(status)
                .nextDeliveryDate(LocalDate.now().plusDays(4))
                .build();
        return deliveryRepository.save(delivery);
    }

    private int addOrderItemAndOptionDto(Item item1, ItemOption option1, ItemOption option2, MemberCoupon memberCoupon1, List<GeneralOrderRequestDto.OrderItemDto> orderItemDtoList, int itemAmount, int optionAmount1, int optionAmount2) {
        List<GeneralOrderRequestDto.SelectOptionDto> selectOptionDtoList = getSelectOptionDtoList(option1, option2, optionAmount1, optionAmount2);
        int optionPriceSum = selectOptionDtoList.stream().mapToInt(i -> itemOptionRepository.findById(i.getItemOptionId()).get().getOptionPrice() * i.getAmount()).sum();
        int discountAmount = 2000;
        int finalPrice = item1.getSalePrice() * itemAmount + optionPriceSum - discountAmount;
        GeneralOrderRequestDto.OrderItemDto orderItemDto = GeneralOrderRequestDto.OrderItemDto.builder()
                .itemId(item1.getId())
                .amount(itemAmount)
                .selectOptionDtoList(selectOptionDtoList)
                .memberCouponId(memberCoupon1.getId())
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
        orderItemDtoList.add(orderItemDto);
        return finalPrice;
    }

    private List<GeneralOrderRequestDto.SelectOptionDto> getSelectOptionDtoList(ItemOption option1, ItemOption option2, int amount1, int amount2) {
        List<GeneralOrderRequestDto.SelectOptionDto> selectOptionDtoList = new ArrayList<>();
        addSelectOptionDto(option1, selectOptionDtoList, amount1);
        addSelectOptionDto(option2, selectOptionDtoList, amount2);
        return selectOptionDtoList;
    }

    private void addSelectOptionDto(ItemOption option1, List<GeneralOrderRequestDto.SelectOptionDto> selectOptionDtoList, int amount) {
        GeneralOrderRequestDto.SelectOptionDto selectOptionDto = GeneralOrderRequestDto.SelectOptionDto.builder()
                .itemOptionId(option1.getId())
                .amount(amount)
                .build();
        selectOptionDtoList.add(selectOptionDto);
    }

    @Test
    @DisplayName("정상적으로 일반주문 결제 취소처리")
    public void cancelPaymentGeneralOrder() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        int accumulatedAmount = member.getAccumulatedAmount();

        Item item1 = generateItem(1);
        int itemRemain1 = item1.getRemaining();
        ItemOption option1 = generateOption(item1, 1, 999);
        int optionRemain1 = option1.getRemaining();
        ItemOption option2 = generateOption(item1, 2, 999);
        int optionRemain2 = option2.getRemaining();

        Item item2 = generateItem(2);
        int itemRemain2 = item2.getRemaining();
        ItemOption option3 = generateOption(item1, 3, 999);
        int optionRemain3 = option3.getRemaining();
        ItemOption option4 = generateOption(item1, 4, 999);
        int optionRemain4 = option4.getRemaining();

        Coupon coupon1 = generateGeneralCoupon(1);
        MemberCoupon memberCoupon1 = generateMemberCoupon(member, coupon1, 0, CouponStatus.INACTIVE);
        int couponRemain1 = memberCoupon1.getRemaining();

        Coupon coupon2 = generateGeneralCoupon(2);
        MemberCoupon memberCoupon2 = generateMemberCoupon(member, coupon2, 1, CouponStatus.ACTIVE);
        int couponRemain2 = memberCoupon2.getRemaining();


        Delivery delivery = generateDelivery(DeliveryStatus.BEFORE_PAYMENT);

        int discountReward = 4000;
        GeneralOrder order = GeneralOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .isBrochure(true)
                .member(member)
                .paymentPrice(100000)
                .discountReward(discountReward)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        int orderItemAmount1 = 1;
        OrderItem orderItem1 = generateOrderItem(item1, memberCoupon1, order, orderItemAmount1);
        int optionAmount1 = 1;
        generateSelectOption(option1, orderItem1, optionAmount1);
        int optionAmount2 = 2;
        generateSelectOption(option2, orderItem1, optionAmount2);
        int orderItemAmount2 = 2;
        OrderItem orderItem2 = generateOrderItem(item2, memberCoupon2, order, orderItemAmount2);
        int optionAmount3 = 3;
        generateSelectOption(option3, orderItem2, optionAmount3);
        int optionAmount4 = 4;
        generateSelectOption(option4, orderItem2, optionAmount4);


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/cancel", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cancelPayment_generalOrder",
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
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

        GeneralOrder findOrder = (GeneralOrder) orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_PAYMENT);
        assertThat(findOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.DELIVERY_CANCEL);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + discountReward);

        List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(order);
        for (OrderItem orderItem : orderItems) {
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CANCEL_PAYMENT);
            assertThat(orderItem.isWriteableReview()).isEqualTo(false);
        }

        Item findItem1 = itemRepository.findById(item1.getId()).get();
        assertThat(findItem1.getRemaining()).isEqualTo(itemRemain1 + orderItemAmount1);
        Item findItem2 = itemRepository.findById(item2.getId()).get();
        assertThat(findItem2.getRemaining()).isEqualTo(itemRemain2 + orderItemAmount2);

        ItemOption findOption1 = itemOptionRepository.findById(option1.getId()).get();
        assertThat(findOption1.getRemaining()).isEqualTo(optionRemain1 + optionAmount1);
        ItemOption findOption2 = itemOptionRepository.findById(option2.getId()).get();
        assertThat(findOption2.getRemaining()).isEqualTo(optionRemain2 + optionAmount2);
        ItemOption findOption3 = itemOptionRepository.findById(option3.getId()).get();
        assertThat(findOption3.getRemaining()).isEqualTo(optionRemain3 + optionAmount3);
        ItemOption findOption4 = itemOptionRepository.findById(option4.getId()).get();
        assertThat(findOption4.getRemaining()).isEqualTo(optionRemain4 + optionAmount4);

        MemberCoupon findMemberCoupon1 = memberCouponRepository.findById(memberCoupon1.getId()).get();
        assertThat(findMemberCoupon1.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(findMemberCoupon1.getRemaining()).isEqualTo(couponRemain1 + 1);

        MemberCoupon findMemberCoupon2 = memberCouponRepository.findById(memberCoupon2.getId()).get();
        assertThat(findMemberCoupon2.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(findMemberCoupon2.getRemaining()).isEqualTo(couponRemain2 + 1);

    }




    @Test
    @DisplayName("구독 주문서 조회하기")
    public void getOrderSheetDto_Subscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dogRepresentative = generateDogRepresentativeBeforePaymentSubscribe(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = generateSubscribeBeforePayment(member, dogRepresentative, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, 100000);

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
                                fieldWithPath("subscribeDto.nextPaymentPrice").description("구독 상품 금액(쿠폰할인/등급할인 적용 전 원가)"),
                                fieldWithPath("subscribeDto.discountGrade").description("서버에서 계산한 등급할인 할인분"),
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
        Dog dogRepresentative = generateDogRepresentativeBeforePaymentSubscribe(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Subscribe subscribe = generateSubscribeBeforePayment(member, dogRepresentative, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, 100000);
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
        int discountReward = 10000;
        int discountCoupon = 10000;
        int discountGrade = 3000;
        int discountTotal = discountReward + discountCoupon + discountGrade;
        int paymentPrice = orderPrice - discountTotal + deliveryPrice;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        SubscribeOrderRequestDto requestDto = SubscribeOrderRequestDto.builder()
                .memberCouponId(memberCouponId)
                .deliveryDto(deliveryDto)
                .orderPrice(orderPrice)
                .deliveryPrice(deliveryPrice)
                .discountTotal(discountTotal)
                .discountReward(discountReward)
                .discountCoupon(discountCoupon)
                .discountGrade(discountGrade)
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
                                linkWithRel("success_subscribeOrder").description("구독 결제 성공 링크"),
                                linkWithRel("fail_subscribeOrder").description("구독 결제 실패 링크"),
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
                                fieldWithPath("memberCouponId").description("사용한 유저쿠폰(memberCoupon) id. 없으면 null"),
                                fieldWithPath("deliveryDto.name").optional().description("수령자 이름"),
                                fieldWithPath("deliveryDto.phone").optional().description("수령자 전화번호"),
                                fieldWithPath("deliveryDto.zipcode").optional().description("우편번호"),
                                fieldWithPath("deliveryDto.street").optional().description("도로명주소"),
                                fieldWithPath("deliveryDto.detailAddress").optional().description("상세주소"),
                                fieldWithPath("deliveryDto.request").description("배송 요청사항"),
                                fieldWithPath("orderPrice").optional().description("주문 상품 원가(등급 할인 적용x)"),
                                fieldWithPath("deliveryPrice").optional().description("배송비"),
                                fieldWithPath("discountTotal").optional().description("총 할인 합계"),
                                fieldWithPath("discountReward").optional().description("사용할 적립금"),
                                fieldWithPath("discountCoupon").optional().description("쿠폰 적용 할인분"),
                                fieldWithPath("discountGrade").optional().description("등급 할인분"),
                                fieldWithPath("paymentPrice").optional().description("최종 결제 금액"),
                                fieldWithPath("paymentMethod").optional().description("결제 방법 [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]"),
                                fieldWithPath("nextDeliveryDate").optional().description("배송 예정일 'yyyy-MM-dd', 첫 결제 배송날짜는 프론트에서 넘어온 값으로 저장함"),
                                fieldWithPath("agreePrivacy").optional().description("개인정보제공 동의 true/false"),
                                fieldWithPath("brochure").optional().description("브로슈어 받을지 여부 true/false")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("주문 id"),
                                fieldWithPath("merchantUid").description("주문 넘버 (아임포트로 넘겨야하는 uid) "),
                                fieldWithPath("status").description("주문 상태"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.success_subscribeOrder.href").description("구독 결제 성공 링크"),
                                fieldWithPath("_links.fail_subscribeOrder.href").description("구독 결제 실패 링크"),
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
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(orderPrice);
        assertThat(findSubscribe.getSubscribeCount()).isEqualTo(subscribeCount);
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.BEFORE_PAYMENT);

        Delivery findDelivery = deliveryRepository.findAll().get(0);
        assertThat(findDelivery.getRecipient().getName()).isEqualTo(member.getName());
        assertThat(findDelivery.getRecipient().getPhone()).isEqualTo(member.getPhoneNumber());
        assertThat(findDelivery.getRecipient().getZipcode()).isEqualTo(member.getAddress().getZipcode());
        assertThat(findDelivery.getRecipient().getStreet()).isEqualTo(member.getAddress().getStreet());
        assertThat(findDelivery.getRecipient().getDetailAddress()).isEqualTo(member.getAddress().getDetailAddress());
        assertThat(findDelivery.getStatus()).isEqualTo(DeliveryStatus.BEFORE_PAYMENT);
        assertThat(findDelivery.getNextDeliveryDate()).isEqualTo(nextDeliveryDate);
        assertThat(findDelivery.getRequest()).isEqualTo(request);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward - discountReward);

        List<Reward> rewards = rewardRepository.findByMember(member);
        assertThat(rewards.size()).isEqualTo(1);
        Reward findReward = rewards.get(0);
        assertThat(findReward.getName()).isEqualTo(RewardName.USE_ORDER);
        assertThat(findReward.getTradeReward()).isEqualTo(discountReward);

        SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findAll().get(0);
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);
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

        List<Card> cards = cardRepository.findAll();
        assertThat(cards.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("정상적으로 구독 주문하기 - HALF and 쿠폰 사용 안 할 경우")
    public void orderSubscribe_HALF_no_coupon() throws Exception {
        //given
        memberCouponRepository.deleteAll();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        Dog dogRepresentative = generateDogRepresentativeBeforePaymentSubscribe(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Subscribe subscribe = generateSubscribeBeforePayment(member,dogRepresentative, SubscribePlan.HALF, SubscribeStatus.BEFORE_PAYMENT, 100000);
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
        SubscribeOrderRequestDto requestDto = SubscribeOrderRequestDto.builder()
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
        assertThat(findSubscribe.getSubscribeCount()).isEqualTo(subscribeCount);
        assertThat(findSubscribe.getNextPaymentPrice()).isEqualTo(orderPrice);
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.BEFORE_PAYMENT);

        Delivery findDelivery = deliveryRepository.findAll().get(0);
        assertThat(findDelivery.getRecipient().getName()).isEqualTo(member.getName());
        assertThat(findDelivery.getRecipient().getPhone()).isEqualTo(member.getPhoneNumber());
        assertThat(findDelivery.getRecipient().getZipcode()).isEqualTo(member.getAddress().getZipcode());
        assertThat(findDelivery.getRecipient().getStreet()).isEqualTo(member.getAddress().getStreet());
        assertThat(findDelivery.getRecipient().getDetailAddress()).isEqualTo(member.getAddress().getDetailAddress());
        assertThat(findDelivery.getStatus()).isEqualTo(DeliveryStatus.BEFORE_PAYMENT);
        assertThat(findDelivery.getRequest()).isEqualTo(request);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward - discountReward);

        List<Reward> rewards = rewardRepository.findByMember(member);
        assertThat(rewards.size()).isEqualTo(0);

        SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findAll().get(0);
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.BEFORE_PAYMENT);
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
        Dog dogRepresentative = generateDogRepresentativeBeforePaymentSubscribe(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Subscribe subscribe = generateSubscribeBeforePayment(member,dogRepresentative, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, 100000);

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
        SubscribeOrderRequestDto requestDto = SubscribeOrderRequestDto.builder()
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


    // TODO: 2022-07-28 구독 결제 성공 알림 테스트 ignore
//    @Ignore
    @Test
    @DisplayName("구독 결제 성공")
    public void successSubscribeOrder() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int accumulatedSubscribe = member.getAccumulatedSubscribe();
        int accumulatedAmount = member.getAccumulatedAmount();

        LocalDate nextDeliveryDate = LocalDate.now().plusDays(2);

        Dog dog = generateDog(member, 1, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Delivery delivery = Delivery.builder()
                .recipient(Recipient.builder()
                        .name(member.getName())
                        .phone(member.getPhoneNumber())
                        .zipcode(member.getAddress().getZipcode())
                        .street(member.getAddress().getStreet())
                        .detailAddress(member.getAddress().getDetailAddress())
                        .build())
                .status(DeliveryStatus.BEFORE_PAYMENT)
                .nextDeliveryDate(nextDeliveryDate)
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);

        int orderPrice = 1000;
        Subscribe subscribe = generateSubscribeBeforePayment(member,dog, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, orderPrice);
        int subscribeCount = subscribe.getSubscribeCount();

        Coupon coupon = generateGeneralCoupon(1);

        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 3, CouponStatus.ACTIVE);

        int paymentPrice = 850;
        int discountReward = 100;
        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .member(member)
                .orderPrice(orderPrice)
                .deliveryPrice(0)
                .discountTotal(150)
                .discountReward(discountReward)
                .discountCoupon(50)
                .paymentPrice(paymentPrice)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isBrochure(true)
                .isAgreePrivacy(true)
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon)
                .subscribeCount(0)
                .build();
        orderRepository.save(subscribeOrder);


        String impUid = "imp_uid_askdfj";
        String merchantUid = "merchantUid_sdkfjals";
        String customerUid = "customer_Uid_l6d6evqi";
        SuccessSubscribeRequestDto requestDto = SuccessSubscribeRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .customerUid(customerUid)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/subscribe/success",subscribeOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("success_subscribeOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 주문 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("impUid").description("아임포트 uid"),
                                fieldWithPath("merchantUid").description("주문 uid"),
                                fieldWithPath("customerUid").description("카드 결제 uid")
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

        Member findMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        assertThat(findMember.getAccumulatedSubscribe()).isEqualTo(accumulatedSubscribe + 1);
        assertThat(findMember.getAccumulatedAmount()).isEqualTo(accumulatedAmount + paymentPrice);
        assertThat(findMember.isBrochure()).isEqualTo(true);
        assertThat(findMember.isSubscribe()).isEqualTo(true);
        assertThat(findMember.getRoles()).isEqualTo("USER,SUBSCRIBER");

        Card card = cardRepository.findAll().get(0);
        assertThat(card.getMember().getId()).isEqualTo(findMember.getId());
        assertThat(card.getCustomerUid()).isEqualTo(customerUid);

        SubscribeOrder findOrder = (SubscribeOrder)orderRepository.findById(subscribeOrder.getId()).get();
        assertThat(findOrder.getImpUid()).isEqualTo(impUid);
        assertThat(findOrder.getMerchantUid()).isEqualTo(merchantUid);
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_DONE);

        Delivery findDelivery = findOrder.getDelivery();
        assertThat(findDelivery.getNextDeliveryDate()).isEqualTo(nextDeliveryDate);

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getCard().getId()).isEqualTo(card.getId());
        assertThat(findSubscribe.getSubscribeCount()).isEqualTo(subscribeCount + 1);
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.SUBSCRIBING);
        if (findSubscribe.getPlan() == SubscribePlan.FULL) {
            assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(subscribeOrder.getPaymentDate().plusDays(14));
        } else {
            assertThat(findSubscribe.getNextPaymentDate()).isEqualTo(subscribeOrder.getPaymentDate().plusDays(28));
        }
        assertThat(findSubscribe.getNextDeliveryDate()).isEqualTo(nextDeliveryDate.plusDays(findSubscribe.getPlan() == SubscribePlan.FULL ? 14 : 28));

        // 다음 회차 예약 주문 생성 확인
        Optional<SubscribeOrder> optionalSubscribeOrder = orderRepository.findByMerchantUid(findSubscribe.getNextOrderMerchantUid());
        assertThat(optionalSubscribeOrder.isPresent()).isTrue();
        if (optionalSubscribeOrder.isPresent()) {
            SubscribeOrder nextOrder = optionalSubscribeOrder.get();
            assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.RESERVED_PAYMENT);
            Delivery nextDelivery = nextOrder.getDelivery();
            SubscribePlan plan = nextOrder.getSubscribe().getPlan();
            if (plan == SubscribePlan.FULL) {
                assertThat(nextDelivery.getNextDeliveryDate()).isEqualTo(findDelivery.getNextDeliveryDate().plusDays(14));
            } else {
                assertThat(nextDelivery.getNextDeliveryDate()).isEqualTo(findDelivery.getNextDeliveryDate().plusDays(28));
            }
        }
    }

    @Test
    @DisplayName("구독 결제 성공 처리 시 존재하지않는 주문 not found")
    public void successSubscribeOrder_notfound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int accumulatedSubscribe = member.getAccumulatedSubscribe();
        int accumulatedAmount = member.getAccumulatedAmount();

        LocalDate nextDeliveryDate = LocalDate.now().plusDays(2);

        Dog dog = generateDog(member, 1, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);


        Delivery delivery = Delivery.builder()
                .recipient(Recipient.builder()
                        .name(member.getName())
                        .phone(member.getPhoneNumber())
                        .zipcode(member.getAddress().getZipcode())
                        .street(member.getAddress().getStreet())
                        .detailAddress(member.getAddress().getDetailAddress())
                        .build())
                .status(DeliveryStatus.BEFORE_PAYMENT)
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);

        int orderPrice = 100000;
        Subscribe subscribe = generateSubscribeBeforePayment(member,dog, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, orderPrice);
        int subscribeCount = subscribe.getSubscribeCount();

        Coupon coupon = generateGeneralCoupon(1);

        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 3, CouponStatus.ACTIVE);

        int paymentPrice = 85000;
        int discountReward = 10000;
        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .member(member)
                .orderPrice(orderPrice)
                .deliveryPrice(0)
                .discountTotal(15000)
                .discountReward(discountReward)
                .discountCoupon(5000)
                .paymentPrice(paymentPrice)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isBrochure(true)
                .isAgreePrivacy(true)
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon)
                .subscribeCount(0)
                .build();
        orderRepository.save(subscribeOrder);


        String impUid = "imp_uid_askdfj";
        String merchantUid = "merchantUid_sdkfjals";
        String customerUid = "customer_Uid_sldkfj";
        String cardName = "신한 카드";
        String cardNumber = "2134********2344";
        SuccessSubscribeRequestDto requestDto = SuccessSubscribeRequestDto.builder()
                .impUid(impUid)
                .merchantUid(merchantUid)
                .customerUid(customerUid)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/999999/subscribe/success")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private LocalDate calculateFirstDeliveryDate() {
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

    @Test
    @DisplayName("구독 주문 결제 실패")
    public void failSubscribeOrder() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        int accumulatedSubscribe = member.getAccumulatedSubscribe();
        int accumulatedAmount = member.getAccumulatedAmount();

        LocalDate nextDeliveryDate = LocalDate.now().plusDays(2);

        Dog dog = generateDog(member, 1, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);


        Delivery delivery = Delivery.builder()
                .recipient(Recipient.builder()
                        .name(member.getName())
                        .phone(member.getPhoneNumber())
                        .zipcode(member.getAddress().getZipcode())
                        .street(member.getAddress().getStreet())
                        .detailAddress(member.getAddress().getDetailAddress())
                        .build())
                .status(DeliveryStatus.BEFORE_PAYMENT)
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);

        int orderPrice = 100000;
        Subscribe subscribe = generateSubscribeBeforePayment(member,dog, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, orderPrice);
        int subscribeCount = subscribe.getSubscribeCount();

        Coupon coupon = generateGeneralCoupon(1);

        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 0, CouponStatus.INACTIVE);
        int remaining = memberCoupon.getRemaining();

        int paymentPrice = 85000;
        int discountReward = 10000;
        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .member(member)
                .orderPrice(orderPrice)
                .deliveryPrice(0)
                .discountTotal(15000)
                .discountReward(discountReward)
                .discountCoupon(5000)
                .paymentPrice(paymentPrice)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isBrochure(true)
                .isAgreePrivacy(true)
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon)
                .subscribeCount(0)
                .build();
        orderRepository.save(subscribeOrder);

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/subscribe/fail", subscribeOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("fail_subscribeOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 주문 id")
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
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + discountReward);

        SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findById(subscribeOrder.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(findOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.BEFORE_PAYMENT);
        Subscribe findSubscribe = findOrder.getSubscribe();
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.BEFORE_PAYMENT);
        assertThat(findSubscribe.getNextPaymentDate()).isNull();
        assertThat(findSubscribe.getNextDeliveryDate()).isNull();

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId()).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining + 1);
        assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);

    }

    @Test
    @DisplayName("구독 주문 결제 실패 not found")
    public void failSubscribeOrder_notFound() throws Exception {
        //given

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/999999/subscribe/fail")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }

    @Test
    @DisplayName("구독 주문 결제 취소")
    public void cancelPaymentSubscribeOrder() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        int accumulatedSubscribe = member.getAccumulatedSubscribe();
        int accumulatedAmount = member.getAccumulatedAmount();

        LocalDate nextDeliveryDate = LocalDate.now().plusDays(2);

        Dog dog = generateDog(member, 1, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);


        Delivery delivery = Delivery.builder()
                .recipient(Recipient.builder()
                        .name(member.getName())
                        .phone(member.getPhoneNumber())
                        .zipcode(member.getAddress().getZipcode())
                        .street(member.getAddress().getStreet())
                        .detailAddress(member.getAddress().getDetailAddress())
                        .build())
                .status(DeliveryStatus.BEFORE_PAYMENT)
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);

        int orderPrice = 100000;
        Subscribe subscribe = generateSubscribeBeforePayment(member,dog, SubscribePlan.FULL, SubscribeStatus.BEFORE_PAYMENT, orderPrice);
        int subscribeCount = subscribe.getSubscribeCount();

        Coupon coupon = generateGeneralCoupon(1);

        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 0, CouponStatus.INACTIVE);
        int remaining = memberCoupon.getRemaining();

        int paymentPrice = 85000;
        int discountReward = 10000;
        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .member(member)
                .orderPrice(orderPrice)
                .deliveryPrice(0)
                .discountTotal(15000)
                .discountReward(discountReward)
                .discountCoupon(5000)
                .paymentPrice(paymentPrice)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isBrochure(true)
                .isAgreePrivacy(true)
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon)
                .subscribeCount(0)
                .build();
        orderRepository.save(subscribeOrder);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/subscribe/cancel", subscribeOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cancelPayment_subscribeOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 주문 id")
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
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + discountReward);

        SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findById(subscribeOrder.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_PAYMENT);
        assertThat(findOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.DELIVERY_CANCEL);
        Subscribe findSubscribe = findOrder.getSubscribe();
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.BEFORE_PAYMENT);
        assertThat(findSubscribe.getNextPaymentDate()).isNull();
        assertThat(findSubscribe.getNextDeliveryDate()).isNull();

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId()).get();
        assertThat(findMemberCoupon.getRemaining()).isEqualTo(remaining + 1);
        assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);

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
                                        "[ALL,\n" +
                                        "    BEFORE_PAYMENT,\n" +
                                        "    HOLD, FAILED,\n" +
                                        "    PAYMENT_DONE,\n" +
                                        "    PRODUCING, DELIVERY_READY,\n" +
                                        "    DELIVERY_START,\n" +
                                        "    SELLING_CANCEL,\n" +
                                        "    CANCEL_REQUEST, CANCEL_DONE,\n" +
                                        "    RETURN_REQUEST, RETURN_DONE,\n" +
                                        "    EXCHANGE_REQUEST, EXCHANGE_DONE,\n" +
                                        "    CONFIRM]"),
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

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtc(member, 1, OrderStatus.CANCEL_REQUEST);

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
                                fieldWithPath("orderDto.orderStatus").description("주문 상태"),
                                fieldWithPath("orderDto.cancelRequestDate").description("취소요청날짜"),
                                fieldWithPath("orderDto.cancelConfirmDate").description("취소요청 관리자컴펌날짜"),
                                fieldWithPath("orderDto.cancelReason").description("취소요청 사유"),
                                fieldWithPath("orderDto.cancelDetailReason").description("취소요청 상세 사유"),
                                fieldWithPath("orderDto.merchantUid").description("주문 번호"),
                                fieldWithPath("orderDto.orderType").description("주문 타입 ['subscribe','general'"),
                                fieldWithPath("orderDto.orderDate").description("주문 날짜"),
                                fieldWithPath("orderDto.deliveryNumber").description("운송장번호 . 없으면 null"),
                                fieldWithPath("orderDto.deliveryStatus").description("배송 상태"),
                                fieldWithPath("orderDto.deliveryPrice").description("배송비"),
                                fieldWithPath("orderDto.discountTotal").description("할인 총합"),
                                fieldWithPath("orderDto.discountReward").description("사용 적립금"),
                                fieldWithPath("orderDto.discountCoupon").description("쿠폰 사용 금액"),
                                fieldWithPath("orderDto.discountGrade").description("등급 할인 금액"),
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



    @Test
    @DisplayName("정상적으로 일반 주문 리스트 조회")
    public void queryGeneralOrders() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();


        IntStream.range(1,14).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.BEFORE_PAYMENT);
        });

        IntStream.range(1,3).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.BEFORE_PAYMENT);

        });


        //when & then
        mockMvc.perform(get("/api/orders/general")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andDo(document("query_generalOrders",
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
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0].thumbnailUrl").description("썸네일 url"),
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0].orderDto.id").description("주문 id"),
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0].orderDto.merchantUid").description("주문 번호. 없으면 null"),
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0].orderDto.orderDate").description("주문 시간"),
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0].orderDto.paymentPrice").description("결제 금액"),
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0].orderDto.orderStatus").description("주문 상태"),
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0].itemNameList").description("주문 상품 이름 리스트"),
                                fieldWithPath("_embedded.queryGeneralOrdersDtoList[0]._links.query_order.href").description("주문 상세보기 링크"),
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
    @DisplayName("일반 주문 하나 조회")
    public void queryGeneralOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CANCEL_REQUEST);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/{id}/general", generalOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_generalOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("generalOrder_cancel_request").description("주문취소 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("일반 주문 id")
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
                                fieldWithPath("orderItemDtoList[0].orderItemId").description("주문한 상품 id"),
                                fieldWithPath("orderItemDtoList[0].thumbnailUrl").description("상품 썸네일 url"),
                                fieldWithPath("orderItemDtoList[0].selectOptionDtoList[0].optionName").description("옵션 이름"),
                                fieldWithPath("orderItemDtoList[0].selectOptionDtoList[0].optionAmount").description("옵션 수"),
                                fieldWithPath("orderItemDtoList[0].itemName").description("상품 이름"),
                                fieldWithPath("orderItemDtoList[0].amount").description("수량"),
                                fieldWithPath("orderItemDtoList[0].finalPrice").description("쿠폰적용 후 주문 금액"),
                                fieldWithPath("orderItemDtoList[0].discountAmount").description("쿠폰 할인 금액"),
                                fieldWithPath("orderItemDtoList[0].status").description("주문 상태"),
                                fieldWithPath("orderItemDtoList[0].saveReward").description("적립예정금액"),
                                fieldWithPath("orderItemDtoList[0].orderCancel.cancelReason").description("취소 이유, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderCancel.cancelDetailReason").description("취소 상세 이유, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderCancel.cancelRequestDate").description("취소 요청 날짜, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderCancel.cancelConfirmDate").description("취소 컨펌 날짜, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderReturn.returnReason").description("반품 이유, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderReturn.returnDetailReason").description("반품 상세 이유, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderReturn.returnRequestDate").description("반품 요청 날짜, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderReturn.returnConfirmDate").description("반품 컨펌 날짜, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderExchange.exchangeReason").description("교환 이유, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderExchange.exchangeDetailReason").description("교환 상세 이유, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderExchange.exchangeRequestDate").description("교환 요청 날짜, 없으면 null"),
                                fieldWithPath("orderItemDtoList[0].orderExchange.exchangeConfirmDate").description("교환 컨펌 날짜, 없으면 null"),
                                fieldWithPath("orderDto.orderId").description("주문 id"),
                                fieldWithPath("orderDto.merchantUid").description("주문 번호 . 결제실패 시 null"),
                                fieldWithPath("orderDto.paymentDate").description("결제 날짜 . 결제 실패 시 null"),
                                fieldWithPath("orderDto.deliveryNumber").description("운송장 번호. 아직 존재하지 않으면 null"),
                                fieldWithPath("orderDto.arrivalDate").description("도착날짜, 아직 도착하지않았으면 null"),
                                fieldWithPath("orderDto.orderPrice").description("할인 전 총 주문 금액"),
                                fieldWithPath("orderDto.deliveryPrice").description("배송비"),
                                fieldWithPath("orderDto.discountTotal").description("할인 총합"),
                                fieldWithPath("orderDto.discountReward").description("사용 적립금"),
                                fieldWithPath("orderDto.discountCoupon").description("쿠폰 사용 금액"),
                                fieldWithPath("orderDto.paymentPrice").description("결제 금액"),
                                fieldWithPath("orderDto.paymentMethod").description("결제 방법 [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]"),
                                fieldWithPath("orderDto.name").description("받는사람 이름"),
                                fieldWithPath("orderDto.phone").description("받는사람 휴대전화"),
                                fieldWithPath("orderDto.zipcode").description("우편번호"),
                                fieldWithPath("orderDto.street").description("도로명주소"),
                                fieldWithPath("orderDto.detailAddress").description("상세주소"),
                                fieldWithPath("orderDto.request").description("배송요청사항"),
                                fieldWithPath("orderDto.orderStatus").description("주문단위 주문상태"),
                                fieldWithPath("orderDto.cancelRequestDate").description("주문단위 취소요청날짜"),
                                fieldWithPath("orderDto.cancelConfirmDate").description("주문단위 취소요청 관리자컴펌날짜"),
                                fieldWithPath("orderDto.cancelReason").description("주문단위 취소요청 사유"),
                                fieldWithPath("orderDto.cancelDetailReason").description("주문단위 취소요청 상세 사유"),
                                fieldWithPath("orderDto.package").description("묶음 배송 여부 true/false"),
                                fieldWithPath("savedRewardTotal").description("총 적립 예정 적립금"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.generalOrder_cancel_request.href").description("주문취소요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("일반 주문 하나 조회 시 존재하지않음")
    public void queryGeneralOrder_not_found() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.DELIVERY_START);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/orders/999999/general")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }



//    @Ignore
    @Test
    @DisplayName("구독 주문 주문 취소 요청 - 상품 생산 전")
    public void cancelRequestSubscribeOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        member.subscribe();
        int accumulatedSubscribe = member.getAccumulatedSubscribe();
        int accumulatedAmount = member.getAccumulatedAmount();
        SubscribeOrder order = generateSubscribeOrderAndEtc_NoBeforeSubscribe_no_deliveryNumber(member, 1, OrderStatus.PAYMENT_DONE);
        int paymentPrice = order.getPaymentPrice();
        Subscribe subscribe = order.getSubscribe();
        int subscribeCount = subscribe.getSubscribeCount();
        String merchantUid = subscribe.getNextOrderMerchantUid();

        String reason = "취소 요청 사유";
        String detailReason = "취소 요청 상세 사유";
        OrderCancelRequestDto requestDto = OrderCancelRequestDto.builder()
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/subscribe/cancelRequest", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cancelRequest_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("구독 주문 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("reason").description("취소 사유, 없으면 -> 빈문자열"),
                                fieldWithPath("detailReason").description("취소 상세 사유, 없으면 -> 빈문자열")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getAccumulatedSubscribe()).isEqualTo(accumulatedSubscribe - 1);
        assertThat(findMember.getAccumulatedAmount()).isEqualTo(accumulatedAmount - paymentPrice);
        assertThat(findMember.isSubscribe()).isFalse();
        assertThat(findMember.getRoles()).isEqualTo("USER");

        SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_DONE_BUYER);
        assertThat(findOrder.getOrderCancel().getCancelConfirmDate()).isNotNull();
        assertThat(findOrder.getOrderCancel().getCancelRequestDate()).isNotNull();

        Delivery delivery = findOrder.getDelivery();
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERY_CANCEL);

        Optional<SubscribeOrder> optionalSubscribeOrder = orderRepository.findByMerchantUid(merchantUid);
        assertThat(optionalSubscribeOrder.isPresent()).isFalse();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.getStatus()).isEqualTo(SubscribeStatus.BEFORE_PAYMENT);

    }

    @Test
    @DisplayName("구독 주문 주문 취소 요청 - 주문확인 상태")
    public void cancelRequestSubscribeOrder_Producing() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        SubscribeOrder order = generateSubscribeOrderAndEtc_NoBeforeSubscribe_no_deliveryNumber(member, 1, OrderStatus.PRODUCING);

        String reason = "취소 요청 사유";
        String detailReason = "취소 요청 상세 사유";
        OrderCancelRequestDto requestDto = OrderCancelRequestDto.builder()
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/subscribe/cancelRequest", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_REQUEST);
        assertThat(findOrder.getOrderCancel().getCancelRequestDate()).isNotNull();
        assertThat(findOrder.getOrderCancel().getCancelReason()).isEqualTo(reason);
        assertThat(findOrder.getOrderCancel().getCancelDetailReason()).isEqualTo(detailReason);

    }




    @Test
    @DisplayName("일반 주문 주문 취소 요청 - 상품 준비 전")
    public void cancelRequestGeneralOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int rewardAmount = member.getReward();
        int accumulatedAmount = member.getAccumulatedAmount();

        GeneralOrder order = generateGeneralOrder(member, 1, OrderStatus.PAYMENT_DONE);
        int discountReward = order.getDiscountReward();
        int paymentPrice = order.getPaymentPrice();

        List<Integer> remainingList = new ArrayList<>();
        List<MemberCoupon> memberCouponList = new ArrayList<>();

        List<OrderItem> orderItemList = orderItemRepository.findAllByGeneralOrder(order);
        for (OrderItem orderItem : orderItemList) {
            MemberCoupon memberCoupon = orderItem.getMemberCoupon();
            remainingList.add(memberCoupon.getRemaining());
            memberCouponList.add(memberCoupon);
        }

        String reason = "취소 요청 사유";
        String detailReason = "취소 요청 상세 사유";
        OrderCancelRequestDto requestDto = OrderCancelRequestDto.builder()
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/cancelRequest", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("cancelRequest_general",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("일반 주문 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("reason").description("취소 사유, 없으면 -> 빈문자열"),
                                fieldWithPath("detailReason").description("취소 상세 사유, 없으면 -> 빈문자열")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(rewardAmount + discountReward);
        assertThat(findMember.getAccumulatedAmount()).isEqualTo(accumulatedAmount - paymentPrice);

        GeneralOrder findOrder = (GeneralOrder) orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_DONE_BUYER);
        assertThat(findOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.DELIVERY_CANCEL);
        List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(findOrder);
        for (OrderItem orderItem : orderItems) {
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CANCEL_DONE_BUYER);
            assertThat(orderItem.getOrderCancel().getCancelRequestDate()).isNotNull();
            assertThat(orderItem.getOrderCancel().getCancelConfirmDate()).isNotNull();
        }


        for (int i = 0; i < memberCouponList.size(); i++) {
            assertThat(memberCouponList.get(i).getRemaining()).isEqualTo(remainingList.get(i) + 1);
            assertThat(memberCouponList.get(i).getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        }

    }

    @Test
    @DisplayName("일반 주문 주문 취소 요청 - 상품 준비 전, 사유 null")
    public void cancelRequestGeneralOrder_reason_null() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int rewardAmount = member.getReward();
        int accumulatedAmount = member.getAccumulatedAmount();

        GeneralOrder order = generateGeneralOrder(member, 1, OrderStatus.PAYMENT_DONE);
        int discountReward = order.getDiscountReward();
        int paymentPrice = order.getPaymentPrice();

        List<Integer> remainingList = new ArrayList<>();
        List<MemberCoupon> memberCouponList = new ArrayList<>();

        List<OrderItem> orderItemList = orderItemRepository.findAllByGeneralOrder(order);
        for (OrderItem orderItem : orderItemList) {
            MemberCoupon memberCoupon = orderItem.getMemberCoupon();
            remainingList.add(memberCoupon.getRemaining());
            memberCouponList.add(memberCoupon);
        }

        String reason = "취소 요청 사유";
        String detailReason = "취소 요청 상세 사유";
        OrderCancelRequestDto requestDto = OrderCancelRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/cancelRequest", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("일반 주문 주문 취소 요청 - 상품 준비 중")
    public void cancelRequestGeneralOrder_DELIVERYREADY() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder order = generateGeneralOrder(member, 1, OrderStatus.DELIVERY_READY);

        String reason = "취소 요청 사유";
        String detailReason = "취소 요청 상세 사유";
        OrderCancelRequestDto requestDto = OrderCancelRequestDto.builder()
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/{id}/general/cancelRequest", order.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        GeneralOrder findOrder = (GeneralOrder) orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_REQUEST);
        assertThat(findOrder.getDelivery().getStatus()).isNotEqualTo(DeliveryStatus.DELIVERY_CANCEL);
        assertThat(findOrder.getOrderCancel().getCancelRequestDate()).isNotNull();
        assertThat(findOrder.getOrderCancel().getCancelReason()).isEqualTo(reason);
        assertThat(findOrder.getOrderCancel().getCancelDetailReason()).isEqualTo(detailReason);

        List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(findOrder);
        for (OrderItem orderItem : orderItems) {
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUEST);
            assertThat(orderItem.getOrderCancel().getCancelRequestDate()).isNotNull();
            assertThat(orderItem.getOrderCancel().getCancelConfirmDate()).isNull();
        }

    }

    @Test
    @DisplayName("일반 주문 주문 취소 요청할 주문 없음")
    public void cancelRequestGeneralOrder_not_found() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.DELIVERY_START);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/orders/999999/general/cancelRequest")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }


    @Test
    @DisplayName("구매확정하는 테스트")
    public void confirmOrders() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        GeneralOrder order = generateGeneralOrder(member, 1, OrderStatus.PAYMENT_DONE);

        List<Long> orderItemIdList = new ArrayList<>();
        List<OrderItem> orderItemList = order.getOrderItemList();

        int saveRewardTotal = 0;
        int rewardCount = 0;
        for (OrderItem orderItem : orderItemList) {
            orderItemIdList.add(orderItem.getId());
            if (orderItem.getStatus() != OrderStatus.CONFIRM) {
                saveRewardTotal += orderItem.getSaveReward();
                rewardCount++;
            }
        }

        ConfirmOrderItemsDto requestDto = ConfirmOrderItemsDto.builder()
                .orderItemIdList(orderItemIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/orders/general/confirm")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("confirm_generalOrders",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderItemIdList").description("구매확정 처리 할 주문한 상품 id (orderItem id) 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + saveRewardTotal);

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CONFIRM);
        assertThat(findOrder.getOrderConfirmDate()).isNotNull();

        List<Long> orderItemIds = requestDto.getOrderItemIdList();
        for (Long orderItemId : orderItemIds) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CONFIRM);
            assertThat(orderItem.isSavedReward()).isEqualTo(true);
        }

        List<Reward> rewards = rewardRepository.findByMember(member);
        assertThat(rewards.size()).isEqualTo(rewardCount);
        if (rewards.size() > 0) {
            Reward findReward = rewards.get(0);
            assertThat(findReward.getMember().getId()).isEqualTo(findMember.getId());
            assertThat(findReward.getName()).isEqualTo(RewardName.CONFIRM_ORDER);
            assertThat(findReward.getRewardType()).isEqualTo(RewardType.ORDER);
            assertThat(findReward.getRewardStatus()).isEqualTo(RewardStatus.SAVED);
        }


    }


    @Test
    @DisplayName("반품 요청 하기")
    public void requestReturnOrders() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        GeneralOrder order = generateGeneralOrder(member, 1, OrderStatus.PAYMENT_DONE);

        List<Long> orderItemIdList = new ArrayList<>();
        List<OrderItem> orderItemList = order.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            orderItemIdList.add(orderItem.getId());
        }

        String reason = "사유";
        String detailReason = "상세 사유";
        RequestReturnExchangeOrdersDto requestDto = RequestReturnExchangeOrdersDto.builder()
                .orderItemIdList(orderItemIdList)
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(post("/api/orders/general/return")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("request_returnOrders",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderItemIdList").description("주문한 상품 orderItem id 리스트"),
                                fieldWithPath("reason").description("사유"),
                                fieldWithPath("detailReason").description("상세 사유")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));


        em.flush();
        em.clear();

        assertThat(orderItemIdList.size()).isEqualTo(2);
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.RETURN_REQUEST);
            assertThat(orderItem.getOrderReturn().getReturnReason()).isEqualTo(reason);
            assertThat(orderItem.getOrderReturn().getReturnDetailReason()).isEqualTo(detailReason);
        }

    }

    @Test
    @DisplayName("교환 요청 하기")
    public void requestExchangeOrders() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        GeneralOrder order = generateGeneralOrder(member, 1, OrderStatus.PAYMENT_DONE);

        List<Long> orderItemIdList = new ArrayList<>();
        List<OrderItem> orderItemList = order.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            orderItemIdList.add(orderItem.getId());
        }

        String reason = "사유";
        String detailReason = "상세 사유";
        RequestReturnExchangeOrdersDto requestDto = RequestReturnExchangeOrdersDto.builder()
                .orderItemIdList(orderItemIdList)
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(post("/api/orders/general/exchange")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("request_exchangeOrders",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderItemIdList").description("주문한 상품 orderItem id 리스트"),
                                fieldWithPath("reason").description("사유"),
                                fieldWithPath("detailReason").description("상세 사유")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        assertThat(orderItemIdList.size()).isEqualTo(2);
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.EXCHANGE_REQUEST);
            assertThat(orderItem.getOrderExchange().getExchangeReason()).isEqualTo(reason);
            assertThat(orderItem.getOrderExchange().getExchangeDetailReason()).isEqualTo(detailReason);
            assertThat(orderItem.getOrderExchange().getExchangeConfirmDate()).isNull();
        }

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


        OrderCancel orderCancel = null;
        if (orderStatus == OrderStatus.CANCEL_REQUEST) {
            orderCancel = OrderCancel.builder()
                    .cancelReason("취소 사유")
                    .cancelDetailReason("취소 상세 사유")
                    .cancelRequestDate(LocalDateTime.now())
                    .build();
        }

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
                .orderCancel(orderCancel)
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

        Subscribe subscribe = generateSubscribeBeforePayment(member, i);

        Delivery nextDelivery = Delivery.builder()
                .status(DeliveryStatus.BEFORE_PAYMENT)
                .build();
        deliveryRepository.save(nextDelivery);

        SubscribeOrder nextOrder = SubscribeOrder.builder()
                .merchantUid(subscribe.getNextOrderMerchantUid())
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .member(member)
                .delivery(nextDelivery)
                .subscribe(subscribe)
                .orderPrice(subscribe.getNextPaymentPrice())
                .build();
        orderRepository.save(nextOrder);

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



    private Subscribe generateSubscribeBeforePayment(int i) {



        Subscribe subscribe = Subscribe.builder()
                .subscribeCount(i + 1)
                .plan(SubscribePlan.FULL)
                .nextOrderMerchantUid("merchantUid__" + i)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBING)
                .build();
        subscribeRepository.save(subscribe);
        return subscribe;
    }

    private Subscribe generateSubscribeBeforePayment(Member member, int i) {

        Card card = Card.builder()
                .member(member)
                .customerUid("customuid" + i)
                .cardName("cardName")
                .cardNumber("cardnumber")
                .build();
        cardRepository.save(card);

        Subscribe subscribe = Subscribe.builder()
                .subscribeCount(i + 1)
                .plan(SubscribePlan.FULL)
                .nextOrderMerchantUid("merchantUid__" + i)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBING)
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

        OrderCancel orderCancel = null;
        if (orderstatus == OrderStatus.CANCEL_REQUEST) {
            orderCancel = OrderCancel.builder()
                    .cancelReason("취소 사유")
                    .cancelDetailReason("취소 상세 사유")
                    .cancelRequestDate(LocalDateTime.now())
                    .build();
        }

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
                .paymentDate(LocalDateTime.now().minusDays(2))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .orderConfirmDate(LocalDateTime.now().minusHours(3))
                .orderCancel(orderCancel)
                .build();

        IntStream.range(1,3).forEach(j -> {
            Item item = generateItem(j);
            generateOption(item, j, 999);

            IntStream.range(1, 4).forEach(k ->{
                ItemImage itemImage = ItemImage.builder()
                        .item(item)
                        .leakOrder(k)
                        .folder("folder" + j)
                        .filename("filename" + k + j)
                        .build();
                itemImageRepository.save(itemImage);
            });

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

            generalOrder.addOrderItemList(orderItem);

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
                .isSavedReward(false)
                .saveReward(500*j)
                .orderCancel(OrderCancel.builder()
                        .cancelConfirmDate(LocalDateTime.now().minusDays(1))
                        .cancelRequestDate(LocalDateTime.now().minusDays(3))
                        .cancelDetailReason("상세이유")
                        .cancelReason("이유")
                        .build())
                .orderReturn(OrderReturn.builder()
                        .returnConfirmDate(LocalDateTime.now().minusDays(1))
                        .returnRequestDate(LocalDateTime.now().minusDays(3))
                        .returnDetailReason("상세이유")
                        .returnReason("이유")
                        .build())
                .orderExchange(OrderExchange.builder()
                        .exchangeConfirmDate(LocalDateTime.now().minusDays(1))
                        .exchangeRequestDate(LocalDateTime.now().minusDays(3))
                        .exchangeDetailReason("상세이유")
                        .exchangeReason("이유")
                        .build())
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

    private ItemOption generateOption(Item item, int i, int remaining) {
        ItemOption itemOption = ItemOption.builder()
                .item(item)
                .name("옵션" + i)
                .optionPrice(i * 1000)
                .remaining(remaining)
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
                .nextDeliveryDate(LocalDate.now().plusDays(2))
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


    private Subscribe generateSubscribeBeforePayment(Member member, Dog dog, SubscribePlan plan, SubscribeStatus status, int nextPaymentPrice) {
        List<Recipe> recipes = recipeRepository.findAll();

        int discountGrade = calculateDiscountGrade(nextPaymentPrice, member);

        Subscribe subscribe = Subscribe.builder()
                .status(status)
                .plan(plan)
                .subscribeCount(0)
                .nextPaymentPrice(nextPaymentPrice)
                .discountGrade(discountGrade)
                .build();

        SubscribeRecipe subscribeRecipe1 = generateSubscribeRecipe(recipes.get(0), subscribe);
        SubscribeRecipe subscribeRecipe2 = generateSubscribeRecipe(recipes.get(1), subscribe);
        subscribe.addSubscribeRecipe(subscribeRecipe1);
        subscribe.addSubscribeRecipe(subscribeRecipe2);

        dog.setSubscribe(subscribe);

        return subscribeRepository.save(subscribe);
    }

    private int calculateDiscountGrade(int originalPrice, Member member) {
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
            discountGrade = (int) Math.round(originalPrice * percent / 100.0);
        }

        return discountGrade;
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

    private Dog generateDogRepresentativeBeforePaymentSubscribe(Member admin, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
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