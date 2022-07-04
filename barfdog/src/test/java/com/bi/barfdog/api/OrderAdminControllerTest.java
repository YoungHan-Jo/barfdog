package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class OrderAdminControllerTest extends BaseTest {

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

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("merchantUid", "")
                        .param("memberName", "")
                        .param("memberEmail", "")
                        .param("recipientName", "")
                        .param("status", "ALL")
                        .param("orderType", "GENERAL")
                )
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
                                parameterWithName("size").description("한 페이지 당 조회 개수"),
                                parameterWithName("from").description("검색 날짜 from 'yyyy-MM-dd'"),
                                parameterWithName("to").description("검색 날짜 to 'yyyy-MM-dd'"),
                                parameterWithName("merchantUid").description("주문 번호 - 검색조건 없으면 null or 빈문자열"),
                                parameterWithName("memberName").description("주문자 이름 - 검색조건 없으면 null or 빈문자열"),
                                parameterWithName("memberEmail").description("주문자 email - 검색조건 없으면 null or 빈문자열"),
                                parameterWithName("recipientName").description("수령자 이름 - 검색조건 없으면 null or 빈문자열"),
                                parameterWithName("status").description("주문 상태 반드시 대문자 [ALL, HOLD, PAYMENT_DONE, PRODUCING, " +
                                        "DELIVERY_READY, DELIVERY_START, " +
                                        "SELLING_CANCEL, CANCEL_REQUEST, CANCEL_DONE, " +
                                        "RETURN_REQUEST, RETURN_DONE, " +
                                        "EXCHANGE_REQUEST, EXCHANGE_DONE, " +
                                        "FAILED, CONFIRM] "),
                                parameterWithName("orderType").description("주문 타입 반드시 대문자 [ALL,GENERAL,SUBSCRIBE] ")
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
            generateGeneralOrder(member, i, OrderStatus.PAYMENT_DONE);
        });

        IntStream.range(9,13).forEach(i -> {
            generateSubscribeOrder(admin,i, OrderStatus.DELIVERY_READY);
        });

        IntStream.range(13,17).forEach(i -> {
            generateGeneralOrder(admin, i, OrderStatus.PAYMENT_DONE);
        });

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("merchantUid", "")
                        .param("memberName", "")
                        .param("recipientName", "")
                        .param("memberEmail", appProperties.getUserEmail())
                        .param("status", "ALL")
                        .param("orderType", "SUBSCRIBE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(5))
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

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("merchantUid", "")
                        .param("memberEmail", "")
                        .param("recipientName", "")
                        .param("memberName", member.getName())
                        .param("status", "ALL")
                        .param("orderType", "SUBSCRIBE"))
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

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("merchantUid", "")
                        .param("memberEmail", "")
                        .param("recipientName", "")
                        .param("memberName", admin.getName())
                        .param("status", "ALL")
                        .param("orderType", "GENERAL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(14))
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


        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("memberName", "")
                        .param("memberEmail", "")
                        .param("recipientName", "")
                        .param("merchantUid", generalOrder.getMerchantUid())
                        .param("status", "ALL")
                        .param("orderType", "GENERAL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(2))
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


        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("status", "ALL")
                        .param("recipientName", admin.getName())
                        .param("orderType", "SUBSCRIBE"))
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


        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("status", "ALL")
                        .param("recipientName", member.getName())
                        .param("orderType", "GENERAL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(18))
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

        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", today)
                        .param("merchantUid", "")
                        .param("memberEmail", "")
                        .param("recipientName", "")
                        .param("memberName", admin.getName())
                        .param("status", "RETURN_REQUEST")
                        .param("orderType", "SUBSCRIBE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(10))
        ;
    }

    @Test
    @DisplayName("정상적으로 일반 주문 하나 조회")
    public void queryGeneralOrder() throws Exception {
       //given

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/orders/{id}/general")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }


    @Test
    @DisplayName("정상적으로 구독 주문 하나 조회")
    public void querySubscribeOrder() throws Exception {
        //given

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/orders/{id}/subscribe")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }















    private GeneralOrder generateGeneralOrder(Member member, int i, OrderStatus orderstatus) {
        Delivery delivery = generateDelivery(member, i);
        GeneralOrder generalOrder = GeneralOrder.builder()
                .impUid("imp_uid" + i)
                .merchantUid("merchant_uid" + i)
                .orderStatus(com.bi.barfdog.domain.order.OrderStatus.DELIVERY_READY)
                .member(member)
                .orderPrice(120000)
                .deliveryPrice(0)
                .discountTotal(0)
                .discountReward(0)
                .discountCoupon(0)
                .paymentPrice(120000)
                .saveReward(1200)
                .isSavedReward(false)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .build();

        IntStream.range(1,3).forEach(j -> {
            Item item = generateItem(j);

            generateOrderItem(generalOrder, j, item, orderstatus);

        });


        return orderRepository.save(generalOrder);
    }

    private void generateOrderItem(GeneralOrder generalOrder, int j, Item item, OrderStatus orderStatus) {
        OrderItem orderItem = OrderItem.builder()
                .generalOrder(generalOrder)
                .item(item)
                .salePrice(item.getSalePrice())
                .amount(j)
                .finalPrice(item.getSalePrice() * j)
                .status(orderStatus)
                .build();
        orderItemRepository.save(orderItem);
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

    private void generateSubscribeOrder(Member member, int i, OrderStatus orderStatus) {
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
                .saveReward(1200)
                .isSavedReward(false)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .subscribe(subscribe)
                .build();
        orderRepository.save(subscribeOrder);


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