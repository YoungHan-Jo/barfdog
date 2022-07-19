package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.*;
import com.bi.barfdog.domain.orderItem.*;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardName;
import com.bi.barfdog.domain.reward.RewardStatus;
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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
public class OrderAdminControllerTest extends BaseTest {

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
    ItemImageRepository itemImageRepository;
    @Autowired
    RewardRepository rewardRepository;


    @Before
    public void setUp() {
        memberCouponRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();
    }

    @Test
    @DisplayName("mock 테스트")
    public void mockTest() throws Exception {
       //given
        String impUid = "impUid_1";
        GeneralOrder order = GeneralOrder.builder()
                .impUid(impUid)
                .build();

        OrderRepository stubRepo = mock(OrderRepository.class);
        when(stubRepo.findById(1L)).thenReturn(Optional.ofNullable(order));
        when(stubRepo.findById(2L)).thenReturn(Optional.ofNullable(null));

       //when & then
        Order findOrder = stubRepo.findById(1L).get();
        assertThat(findOrder.getImpUid()).isEqualTo(impUid);
        Optional<Order> orderOptional = stubRepo.findById(2L);
        assertThat(orderOptional.isPresent()).isFalse();

    }



    @Test
    @DisplayName("정상적으로 일반 주문 리스트 조회")
    public void queryOrders_general() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(6,9).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(9,13).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(13,17).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });


        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .orderType(OrderType.GENERAL)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(14))
                .andDo(document("query_admin_orders",
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
                        requestFields(
                                fieldWithPath("from").description("검색 날짜 from 'yyyy-MM-dd'"),
                                fieldWithPath("to").description("검색 날짜 to 'yyyy-MM-dd'"),
                                fieldWithPath("merchantUid").description("주문 번호 - 검색조건 없으면 null or 빈문자열"),
                                fieldWithPath("memberName").description("주문자 이름 - 검색조건 없으면 null or 빈문자열"),
                                fieldWithPath("memberEmail").description("주문자 email - 검색조건 없으면 null or 빈문자열"),
                                fieldWithPath("recipientName").description("수령자 이름 - 검색조건 없으면 null or 빈문자열"),
                                fieldWithPath("statusList").description("주문 상태 리스트. 전체 상태 검색 시 null or 빈 배열 [HOLD, PAYMENT_DONE, PRODUCING, " +
                                        "DELIVERY_READY, DELIVERY_START, " +
                                        "SELLING_CANCEL, CANCEL_REQUEST, CANCEL_DONE, " +
                                        "RETURN_REQUEST, RETURN_DONE, " +
                                        "EXCHANGE_REQUEST, EXCHANGE_DONE, " +
                                        "FAILED, CONFIRM] "),
                                fieldWithPath("orderType").description("주문 타입 [GENERAL,SUBSCRIBE] ")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].id").description("주문 id"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].orderType").description("주문 타입 ['general' or 'subscribe']"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].merchantUid").description("상품 주문 번호"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].orderItemId").description("주문한 상품 id, 구독상품일 경우 구독 id"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].orderStatus").description("주문 상태"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].deliveryNumber").description("운송장 번호, 없으면 null"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].memberEmail").description("구매자 email"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].memberName").description("구매자 이름"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].memberPhoneNumber").description("구매자 휴대전화"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].recipientName").description("수령자 이름"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].recipientPhoneNumber").description("수령자 휴대전화"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].orderDate").description("주문 날짜"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0].packageDelivery").description("묶음배송 여부"),
                                fieldWithPath("_embedded.queryAdminOrdersDtoList[0]._links.query_order.href").description("주문 하나 조회 링크"),
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
    @DisplayName("정상적으로 구독 주문 리스트 조회")
    public void queryOrders_subscribeOrder() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(6,9).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(9,13).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(13,17).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });

        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .orderType(OrderType.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(8))
        ;
    }

    @Test
    @DisplayName("정상적으로 구매자 이름로 검색한 구독 주문 리스트 조회")
    public void queryOrders_subscribeOrder_member() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(6,15).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(15,18).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(18,25).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });

        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .memberName(member.getName())
                .orderType(OrderType.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(5))
        ;
    }

    @Test
    @DisplayName("정상적으로 구매자 이름으로 검색한 일반 주문 리스트 조회")
    public void queryOrders_generalOrder_admin() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(6,15).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(15,18).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(18,25).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .memberName(member.getName())
                .orderType(OrderType.GENERAL)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(18))
        ;
    }

    @Test
    @DisplayName("정상적으로 주문번호로 검색한 일반 주문 리스트 조회")
    public void queryOrders_generalOrder_byMerchantUid() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(6,15).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(15,18).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(18,25).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });

        GeneralOrder generalOrder = generateGeneralOrder(admin, 26, OrderStatus.PAYMENT_DONE);


        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .merchantUid(generalOrder.getMerchantUid())
                .orderType(OrderType.GENERAL)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(2)) // orderitem이 2개 라서
                .andExpect(jsonPath("_embedded.queryAdminOrdersDtoList[0].merchantUid").value(generalOrder.getMerchantUid()))
        ;
    }

    @Test
    @DisplayName("정상적으로 수령자 이름으로 검색한 구독 주문 리스트 조회")
    public void queryOrders_SubscribeOrder_byRecipientName() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(6,15).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(15,18).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(18,25).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });


        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .recipientName(admin.getName())
                .orderType(OrderType.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(3))
        ;
    }

    @Test
    @DisplayName("정상적으로 수령자 이름으로 검색한 일반 주문 리스트 조회")
    public void queryOrders_generalOrder_byRecipientName() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(6,15).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(15,18).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(18,25).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });


        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .recipientName(member.getName())
                .orderType(OrderType.GENERAL)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(18)) // orderitem 2개씩 생성
        ;
    }

    @Test
    @DisplayName("정상적으로 구매자 이름로 검색한 구독 주문 반품요청 리스트 조회")
    public void queryOrders_subscribeOrder_member_returnRequest() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrder(member,i, OrderStatus.DELIVERY_READY);
            generateSubscribeOrder(member,i, OrderStatus.RETURN_REQUEST);
        });

        IntStream.range(6,15).forEach(i -> {
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(15,18).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(18,25).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(25,35).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.RETURN_REQUEST);
        });

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.RETURN_REQUEST);

        OrderAdminCond request = OrderAdminCond.builder()
                .from(LocalDate.now().minusMonths(1))
                .to(LocalDate.now())
                .memberName(admin.getName())
                .statusList(orderStatusList)
                .orderType(OrderType.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/search")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(10))
        ;
    }

    @Test
    @DisplayName("정상적으로 일반 주문 하나 조회")
    public void queryGeneralOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/orders/{id}/general", generalOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("orderItemAndOptionDtoList", hasSize(2)))
                .andExpect(jsonPath("orderItemAndOptionDtoList[0].selectOptionDtoList", hasSize(1)))
                .andExpect(jsonPath("orderItemAndOptionDtoList[1].selectOptionDtoList", hasSize(2)))
                .andDo(document("query_admin_general_order",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("일반 주문 주문 id")
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
                                fieldWithPath("orderInfoDto.id").description("주문 id"),
                                fieldWithPath("orderInfoDto.merchantUid").description("주문 번호"),
                                fieldWithPath("orderInfoDto.orderDate").description("주문 날짜"),
                                fieldWithPath("orderInfoDto.orderType").description("주문 유형 ['general','subscribe']"),
                                fieldWithPath("orderInfoDto.memberName").description("구매자 이름"),
                                fieldWithPath("orderInfoDto.phoneNumber").description("구매자 연락처"),
                                fieldWithPath("orderInfoDto.package").description("묶음배송 여부 true/false"),
                                fieldWithPath("orderInfoDto.email").description("구매자 email"),
                                fieldWithPath("orderInfoDto.subscribe").description("구독 여부 true/false"),
                                fieldWithPath("orderItemAndOptionDtoList[0].orderItemDto.orderItemId").description("상품주문번호 id"),
                                fieldWithPath("orderItemAndOptionDtoList[0].orderItemDto.itemName").description("상품 이름"),
                                fieldWithPath("orderItemAndOptionDtoList[0].orderItemDto.amount").description("상품 수량"),
                                fieldWithPath("orderItemAndOptionDtoList[0].orderItemDto.finalPrice").description("총 상품 금액"),
                                fieldWithPath("orderItemAndOptionDtoList[0].orderItemDto.couponName").description("사용한 쿠폰 이름"),
                                fieldWithPath("orderItemAndOptionDtoList[0].orderItemDto.discountAmount").description("쿠폰 할인 금액"),
                                fieldWithPath("orderItemAndOptionDtoList[0].orderItemDto.status").description("주문한 상품 목록 하나 상태"),
                                fieldWithPath("orderItemAndOptionDtoList[0].selectOptionDtoList[0].optionName").description("옵션 이름"),
                                fieldWithPath("orderItemAndOptionDtoList[0].selectOptionDtoList[0].price").description("옵션 금액"),
                                fieldWithPath("orderItemAndOptionDtoList[0].selectOptionDtoList[0].amount").description("옵션 개수"),
                                fieldWithPath("paymentDto.orderPrice").description("상품 총 금액"),
                                fieldWithPath("paymentDto.deliveryPrice").description("배달 요금"),
                                fieldWithPath("paymentDto.discountReward").description("사용한 적립금"),
                                fieldWithPath("paymentDto.paymentPrice").description("결제 금액"),
                                fieldWithPath("paymentDto.orderStatus").description("주문 상태"),
                                fieldWithPath("paymentDto.orderConfirmDate").description("구매 확정일"),
                                fieldWithPath("deliveryDto.recipientName").description("수령자 이름"),
                                fieldWithPath("deliveryDto.recipientPhone").description("수령자 휴대전화"),
                                fieldWithPath("deliveryDto.zipcode").description("우편번호"),
                                fieldWithPath("deliveryDto.street").description("도로명 주소"),
                                fieldWithPath("deliveryDto.detailAddress").description("상세 주소"),
                                fieldWithPath("deliveryDto.departureDate").description("배송 출발 시각"),
                                fieldWithPath("deliveryDto.arrivalDate").description("배송 도착 시각"),
                                fieldWithPath("deliveryDto.deliveryNumber").description("운송장 번호"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 일반주문이 없음")
    public void queryGeneralOrder_notfound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/orders/999999/general")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("취소 반품 환불 상세보기 주문한 상품 하나 조회하기")
    public void queryOrderItem() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);
        OrderItem orderItem = generalOrder.getOrderItemList().get(1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/orders/orderItem/{id}", orderItem.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_admin_order_orderItem",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("일반 주문 주문 id")
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
                                fieldWithPath("orderInfoDto.orderId").description("주문 id"),
                                fieldWithPath("orderInfoDto.merchantUid").description("주문 번호"),
                                fieldWithPath("orderInfoDto.orderDate").description("주문 날짜"),
                                fieldWithPath("orderInfoDto.orderType").description("주문 유형 ['general','subscribe']"),
                                fieldWithPath("orderInfoDto.memberName").description("구매자 이름"),
                                fieldWithPath("orderInfoDto.phoneNumber").description("구매자 연락처"),
                                fieldWithPath("orderInfoDto.package").description("묶음배송 여부 true/false"),
                                fieldWithPath("orderInfoDto.email").description("구매자 email"),
                                fieldWithPath("orderInfoDto.subscribe").description("구독 여부 true/false"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.orderItemId").description("상품주문번호 id"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.itemName").description("상품 이름"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.amount").description("상품 수량"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.finalPrice").description("총 상품 금액"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.couponName").description("사용한 쿠폰 이름"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.discountAmount").description("쿠폰 할인 금액"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.status").description("주문한 상품 목록 하나 상태"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.cancelReason").description("취소 이유, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.cancelDetailReason").description("취소 상세 이유, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.cancelRequestDate").description("취소 요청 날짜, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.cancelConfirmDate").description("취소 컨펌 날짜, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.returnReason").description("반품 이유, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.returnDetailReason").description("반품 상세 이유, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.returnRequestDate").description("반품 요청 날짜, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.returnConfirmDate").description("반품 컨펌 날짜, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.exchangeReason").description("교환 이유, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.exchangeDetailReason").description("교환 상세 이유, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.exchangeRequestDate").description("교환 요청 날짜, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.orderItemDto.exchangeConfirmDate").description("교환 컨펌 날짜, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.selectOptionDtoList[0].optionName").description("옵션 이름, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.selectOptionDtoList[0].price").description("옵션 금액, 없으면 null"),
                                fieldWithPath("orderItemAndOptionDto.selectOptionDtoList[0].amount").description("옵션 개수, 없으면 null"),
                                fieldWithPath("paymentDto.orderPrice").description("상품 총 금액"),
                                fieldWithPath("paymentDto.deliveryPrice").description("배달 요금"),
                                fieldWithPath("paymentDto.discountReward").description("사용한 적립금"),
                                fieldWithPath("paymentDto.paymentPrice").description("결제 금액"),
                                fieldWithPath("paymentDto.orderStatus").description("주문 상태"),
                                fieldWithPath("paymentDto.orderConfirmDate").description("구매 확정일"),
                                fieldWithPath("deliveryDto.recipientName").description("수령자 이름"),
                                fieldWithPath("deliveryDto.recipientPhone").description("수령자 휴대전화"),
                                fieldWithPath("deliveryDto.zipcode").description("우편번호"),
                                fieldWithPath("deliveryDto.street").description("도로명 주소"),
                                fieldWithPath("deliveryDto.detailAddress").description("상세 주소"),
                                fieldWithPath("deliveryDto.departureDate").description("배송 출발 시각"),
                                fieldWithPath("deliveryDto.arrivalDate").description("배송 도착 시각"),
                                fieldWithPath("deliveryDto.deliveryNumber").description("운송장 번호"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("취소 반품 환불 상세보기 주문한 상품 하나 조회하기 not found")
    public void queryOrderItem_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);
        OrderItem orderItem = generalOrder.getOrderItemList().get(1);


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/orders/orderItem/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("정상적으로 구독 주문 하나 조회")
    public void querySubscribeOrder() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtcCancelDone(member, 1, OrderStatus.CANCEL_DONE_SELLER);

        List<Order> orders = orderRepository.findAll();
        assertThat(orders.size()).isEqualTo(1);


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/orders/{id}/subscribe", subscribeOrder.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_admin_subscribe_order",
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
                                fieldWithPath("subscribeOrderInfoDto.id").description("주문 id"),
                                fieldWithPath("subscribeOrderInfoDto.merchantUid").description("주문 번호"),
                                fieldWithPath("subscribeOrderInfoDto.orderDate").description("주문 날짜"),
                                fieldWithPath("subscribeOrderInfoDto.orderType").description("주문 유형 ['general','subscribe']"),
                                fieldWithPath("subscribeOrderInfoDto.memberName").description("구매자 이름"),
                                fieldWithPath("subscribeOrderInfoDto.phoneNumber").description("구매자 연락처"),
                                fieldWithPath("subscribeOrderInfoDto.cancelReason").description("취소 이유 , 없으면 null"),
                                fieldWithPath("subscribeOrderInfoDto.cancelDetailReason").description("취소 상세 이유, 없으면 null"),
                                fieldWithPath("subscribeOrderInfoDto.cancelRequestDate").description("취소 신청 날짜 , 없으면 null"),
                                fieldWithPath("subscribeOrderInfoDto.cancelConfirmDate").description("취소 컨펌 날짜, 없으면 null"),
                                fieldWithPath("subscribeOrderInfoDto.package").description("묶음배송 여부 true/false"),
                                fieldWithPath("subscribeOrderInfoDto.subscribe").description("구독 여부 true/false"),
                                fieldWithPath("subscribeOrderInfoDto.email").description("구매자 email"),
                                fieldWithPath("dogDto.name").description("강아지 이름"),
                                fieldWithPath("dogDto.inedibleFood").description("못먹는 음식"),
                                fieldWithPath("dogDto.inedibleFoodEtc").description("못먹는 음식 기타"),
                                fieldWithPath("dogDto.caution").description("특이사항"),
                                fieldWithPath("subscribeDto.id").description("구독 id"),
                                fieldWithPath("subscribeDto.subscribeCount").description("변경 전 구독 회차"),
                                fieldWithPath("subscribeDto.plan").description("구독 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("subscribeDto.oneMealRecommendGram").description("한끼 권장량 g"),
                                fieldWithPath("subscribeDto.recipeName").description("구독 레시피 이름 'xxx,xxx' "),
                                fieldWithPath("beforeSubscribeDto.id").description("구독 id , 구독 바꾼 적 없으면 beforeSubscribeDto 값 null"),
                                fieldWithPath("beforeSubscribeDto.subscribeCount").description("구독 회차"),
                                fieldWithPath("beforeSubscribeDto.plan").description("구독 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("beforeSubscribeDto.oneMealRecommendGram").description("한끼 권장 량 g"),
                                fieldWithPath("beforeSubscribeDto.recipeName").description("구독 레시피 이름"),
                                fieldWithPath("subscribePaymentDto.orderPrice").description("상품 총 금액"),
                                fieldWithPath("subscribePaymentDto.deliveryPrice").description("배달 요금"),
                                fieldWithPath("subscribePaymentDto.discountReward").description("사용한 적립금"),
                                fieldWithPath("subscribePaymentDto.couponName").description("사용한 쿠폰 이름"),
                                fieldWithPath("subscribePaymentDto.discountCoupon").description("쿠폰 할인 금액"),
                                fieldWithPath("subscribePaymentDto.paymentPrice").description("결제 금액"),
                                fieldWithPath("subscribePaymentDto.orderStatus").description("주문 상태"),
                                fieldWithPath("subscribePaymentDto.orderConfirmDate").description("구매 확정일"),
                                fieldWithPath("subscribeDeliveryDto.recipientName").description("수령자 이름"),
                                fieldWithPath("subscribeDeliveryDto.recipientPhone").description("수령자 휴대전화"),
                                fieldWithPath("subscribeDeliveryDto.zipcode").description("우편번호"),
                                fieldWithPath("subscribeDeliveryDto.street").description("도로명 주소"),
                                fieldWithPath("subscribeDeliveryDto.detailAddress").description("상세 주소"),
                                fieldWithPath("subscribeDeliveryDto.departureDate").description("배송 출발 시각"),
                                fieldWithPath("subscribeDeliveryDto.arrivalDate").description("배송 도착 시각"),
                                fieldWithPath("subscribeDeliveryDto.deliveryNumber").description("운송장 번호"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }



    @Test
    @DisplayName("일반 주문 주문확인 처리")
    public void orderConfirmGeneral() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);

        List<Long> orderItemIdList = new ArrayList<>();

        List<OrderItem> orderItemList = generalOrder.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            orderItemIdList.add(orderItem.getId());
        }

        OrderConfirmGeneralDto requestDto = OrderConfirmGeneralDto.builder()
                .orderItemIdList(orderItemIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/general/orderConfirm")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_orderConfirm_general",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orders").description("주문 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderItemIdList").description("주문확인 처리 할 주문한상품(orderItem) id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orders.href").description("주문 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));


        em.flush();
        em.clear();

        assertThat(orderItemIdList.size()).isEqualTo(2);
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            assertThat(orderItem.getGeneralOrder().getOrderStatus()).isEqualTo(OrderStatus.DELIVERY_READY);
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.DELIVERY_READY);
        }

    }

    @Test
    @DisplayName("구독 주문 주문확인 처리")
    public void orderConfirmSubscribe() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> orderIdList = new ArrayList<>();

        IntStream.range(1,6).forEach(i -> {
            SubscribeOrder subscribeOrder = generateSubscribeOrder(member, i, OrderStatus.PAYMENT_DONE);
            orderIdList.add(subscribeOrder.getId());
        });

        OrderConfirmSubscribeDto requestDto = OrderConfirmSubscribeDto.builder()
                .orderIdList(orderIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/subscribe/orderConfirm")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_orderConfirm_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orders").description("주문 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderIdList").description("주문확인 처리 할 구독주문(order) id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orders.href").description("주문 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        assertThat(orderIdList.size()).isEqualTo(5);
        for (Long orderId : orderIdList) {
            Order order = orderRepository.findById(orderId).get();
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PRODUCING);
        }

    }


    @Test
    @DisplayName("일반 주문 관리자 주문취소하기")
    public void orderCancelGeneral() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int rewardAmount = member.getReward();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);

        List<Long> orderItemIdList = new ArrayList<>();
        List<MemberCoupon> memberCouponList = new ArrayList<>();
        List<Integer> remainingList = new ArrayList<>();

        List<OrderItem> orderItemList = generalOrder.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            orderItemIdList.add(orderItem.getId());
            MemberCoupon memberCoupon = orderItem.getMemberCoupon();
            memberCouponList.add(memberCoupon);
            remainingList.add(memberCoupon.getRemaining());
        }


        String reason = "취소 이유";
        String detailReason = "취소 상세 이유";
        OrderCancelGeneralDto requestDto = OrderCancelGeneralDto.builder()
                .orderItemIdList(orderItemIdList)
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/general/orderCancel")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_orderCancel_general",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orders").description("주문 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderItemIdList").description("취소 컨펌 처리 할 주문한상품(orderItem) id 리스트"),
                                fieldWithPath("reason").description("취소 이유"),
                                fieldWithPath("detailReason").description("취소 상세 이유")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orders.href").description("주문 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CANCEL_DONE_SELLER);
            assertThat(orderItem.getOrderCancel().getCancelReason()).isEqualTo(reason);
            assertThat(orderItem.getOrderCancel().getCancelDetailReason()).isEqualTo(detailReason);
            assertThat(orderItem.getOrderCancel().getCancelConfirmDate()).isNotNull();
            assertThat(orderItem.getOrderCancel().getCancelRequestDate()).isNotNull();
            assertThat(orderItem.getCancelReward()).isNotEqualTo(0);
            assertThat(orderItem.getCancelPrice()).isEqualTo(orderItem.getFinalPrice() - orderItem.getCancelReward());
        }

        Order findOrder = orderRepository.findById(generalOrder.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_DONE_SELLER);

        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList.size()).isEqualTo(orderItemIdList.size());

        int cancelRewards = 0;

        for (Reward reward : rewardList) {
            assertThat(reward.getRewardStatus()).isEqualTo(RewardStatus.SAVED);
            assertThat(reward.getName()).isEqualTo(RewardName.CANCEL_ORDER);
            assertThat(reward.getTradeReward()).isNotEqualTo(0);
            cancelRewards += reward.getTradeReward();
        }

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isNotEqualTo(rewardAmount);
        assertThat(findMember.getReward()).isEqualTo(rewardAmount + cancelRewards);

        assertThat(memberCouponList.size()).isNotEqualTo(0);
        for (int i = 0; i < memberCouponList.size(); i++) {
            assertThat(memberCouponList.get(i).getRemaining()).isEqualTo(remainingList.get(i) + 1);
            assertThat(memberCouponList.get(i).getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        }
    }


    @Test
    @DisplayName("구독주문 관리자 판매취소")
    public void orderCancelSubscribe() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> orderIdList = new ArrayList<>();
        List<MemberCoupon> memberCouponList = new ArrayList<>();
        List<Integer> remainingList = new ArrayList<>();

        IntStream.range(1,6).forEach(i -> {
            SubscribeOrder subscribeOrder = generateSubscribeOrder(member, i, OrderStatus.PAYMENT_DONE);
            orderIdList.add(subscribeOrder.getId());
            MemberCoupon memberCoupon = subscribeOrder.getMemberCoupon();
            memberCouponList.add(memberCoupon);
            remainingList.add(memberCoupon.getRemaining());
        });

        String reason = "취소 사유";
        String detailReason = "취소 상세 사유";
        OrderCancelSubscribeDto requestDto = OrderCancelSubscribeDto.builder()
                .orderIdList(orderIdList)
                .reason(reason)
                .detailReason(detailReason)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/subscribe/orderCancel")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_orderCancel_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orders").description("주문 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderIdList").description("판매 취소 처리 할 구독 주문(order) id 리스트"),
                                fieldWithPath("reason").description("취소 이유"),
                                fieldWithPath("detailReason").description("취소 상세 이유")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orders.href").description("주문 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        for (Long orderId : orderIdList) {
            SubscribeOrder findOrder = (SubscribeOrder) orderRepository.findById(orderId).get();
            assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_DONE_SELLER);
            assertThat(findOrder.getOrderCancel().getCancelReason()).isEqualTo(reason);
            assertThat(findOrder.getOrderCancel().getCancelDetailReason()).isEqualTo(detailReason);
            assertThat(findOrder.getOrderCancel().getCancelConfirmDate()).isNotNull();
        }

        List<Reward> allRewards = rewardRepository.findAll();
        assertThat(allRewards.size()).isNotEqualTo(0);
        assertThat(allRewards.size()).isEqualTo(orderIdList.size());

        assertThat(memberCouponList.size()).isNotEqualTo(0);
        for (int i = 0; i < memberCouponList.size(); i++) {
            assertThat(memberCouponList.get(i).getRemaining()).isEqualTo(remainingList.get(i) + 1);
        }



    }






    @Test
    @DisplayName("일반 주문 주문취소 컨펌")
    public void cancelConfirmGeneral() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int rewardAmount = member.getReward();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);

        List<Long> orderItemIdList = new ArrayList<>();
        List<MemberCoupon> memberCouponList = new ArrayList<>();
        List<Integer> remainingList = new ArrayList<>();

        List<OrderItem> orderItemList = generalOrder.getOrderItemList();
        for (OrderItem orderItem : orderItemList) {
            orderItemIdList.add(orderItem.getId());
            MemberCoupon memberCoupon = orderItem.getMemberCoupon();
            memberCouponList.add(memberCoupon);
            remainingList.add(memberCoupon.getRemaining());
        }


        CancelConfirmGeneralDto requestDto = CancelConfirmGeneralDto.builder()
                .orderItemIdList(orderItemIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/general/cancelConfirm")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_cancelConfirm_general",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orders").description("주문 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderItemIdList").description("취소 컨펌 처리 할 주문한상품(orderItem) id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orders.href").description("주문 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CANCEL_DONE_BUYER);
            assertThat(orderItem.getCancelReward()).isNotEqualTo(0);
            assertThat(orderItem.getCancelPrice()).isEqualTo(orderItem.getFinalPrice() - orderItem.getCancelReward());
        }

        Order findOrder = orderRepository.findById(generalOrder.getId()).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_DONE_SELLER);

        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList.size()).isEqualTo(orderItemIdList.size());

        int cancelRewards = 0;

        for (Reward reward : rewardList) {
            assertThat(reward.getRewardStatus()).isEqualTo(RewardStatus.SAVED);
            assertThat(reward.getName()).isEqualTo(RewardName.CANCEL_ORDER);
            assertThat(reward.getTradeReward()).isNotEqualTo(0);
            cancelRewards += reward.getTradeReward();
        }

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isNotEqualTo(rewardAmount);
        assertThat(findMember.getReward()).isEqualTo(rewardAmount + cancelRewards);

        assertThat(memberCouponList.size()).isNotEqualTo(0);
        for (int i = 0; i < memberCouponList.size(); i++) {
            assertThat(memberCouponList.get(i).getRemaining()).isEqualTo(remainingList.get(i) + 1);
            assertThat(memberCouponList.get(i).getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
        }
    }

    @Test
    @DisplayName("일반 주문 취소 컨펌 . 일부 orderitem만 취소")
    public void cancelConfirmGeneral_일부만() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        GeneralOrder generalOrder = generateGeneralOrder(member, 1, OrderStatus.CONFIRM);

        List<Long> orderItemIdList = new ArrayList<>();

        List<OrderItem> orderItemList = generalOrder.getOrderItemList();
        orderItemIdList.add(orderItemList.get(0).getId());

        CancelConfirmGeneralDto requestDto = CancelConfirmGeneralDto.builder()
                .orderItemIdList(orderItemIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/general/cancelConfirm")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.CANCEL_DONE_BUYER);
        }

        Order findOrder = orderRepository.findById(generalOrder.getId()).get();
        assertThat(findOrder.getOrderStatus()).isNotEqualTo(OrderStatus.CANCEL_DONE_SELLER);

    }

    @Test
    @DisplayName("구독주문 주문취소 컨펌")
    public void cancelConfirmSubscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> orderIdList = new ArrayList<>();
        List<MemberCoupon> memberCouponList = new ArrayList<>();
        List<Integer> remainingList = new ArrayList<>();

        IntStream.range(1,6).forEach(i -> {
            SubscribeOrder subscribeOrder = generateSubscribeOrder(member, i, OrderStatus.PAYMENT_DONE);
            orderIdList.add(subscribeOrder.getId());
            MemberCoupon memberCoupon = subscribeOrder.getMemberCoupon();
            memberCouponList.add(memberCoupon);
            remainingList.add(memberCoupon.getRemaining());
        });

        CancelConfirmSubscribeDto requestDto = CancelConfirmSubscribeDto.builder()
                .orderIdList(orderIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/orders/subscribe/cancelConfirm")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_cancelConfirm_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_orders").description("주문 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("orderIdList").description("취소 컨펌 처리 할 구독 주문(order) id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_orders.href").description("주문 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        for (Long orderId : orderIdList) {
            Order findOrder = orderRepository.findById(orderId).get();
            assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL_DONE_BUYER);
        }

        List<Reward> allRewards = rewardRepository.findAll();
        assertThat(allRewards.size()).isNotEqualTo(0);
        assertThat(allRewards.size()).isEqualTo(orderIdList.size());

        assertThat(memberCouponList.size()).isNotEqualTo(0);
        for (int i = 0; i < memberCouponList.size(); i++) {
            assertThat(memberCouponList.get(i).getRemaining()).isEqualTo(remainingList.get(i) + 1);
        }



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
                .orderConfirmDate(LocalDateTime.now().minusHours(3))
                .build();
        orderRepository.save(subscribeOrder);

        return subscribeOrder;
    }

    private SubscribeOrder generateSubscribeOrderAndEtcCancelDone(Member member, int i, OrderStatus orderStatus) {

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

        OrderCancel ordercancel = OrderCancel.builder()
                .cancelReason("취소 사유")
                .cancelDetailReason("취소 상세 사유")
                .cancelRequestDate(LocalDateTime.now().minusDays(3))
                .cancelConfirmDate(LocalDateTime.now().minusDays(1))
                .build();

        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid("imp_uid"+i)
                .merchantUid("merchant_uid"+i)
                .orderStatus(orderStatus)
                .orderCancel(ordercancel)
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

    private void generateSubscribeRecipe(Recipe recipe, Subscribe subscribe) {
        SubscribeRecipe subscribeRecipe = SubscribeRecipe.builder()
                .subscribe(subscribe)
                .recipe(recipe)
                .build();
        subscribeRecipeRepository.save(subscribeRecipe);
    }

    private Subscribe generateSubscribe(int i) {
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

        Coupon coupon = generateGeneralCoupon(i);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 3, CouponStatus.ACTIVE);

        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid("imp_uid" + i)
                .merchantUid("merchant_uid" + i)
                .orderStatus(orderStatus)
                .member(member)
                .orderPrice(120000)
                .deliveryPrice(0)
                .discountTotal(0)
                .discountReward(5000)
                .discountCoupon(4000)
                .paymentPrice(115000)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon)
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