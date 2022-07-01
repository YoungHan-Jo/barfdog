package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    @DisplayName("정상적으로 주문 리스트 조회")
    public void queryOrders() throws Exception {

        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Delivery delivery = generateDelivery(member, 1);
        Dog dog = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = Subscribe.builder()
                .dog(dog)
                .subscribeCount(1)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDate.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBING)
                .build();
        subscribeRepository.save(subscribe);

        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid("imp_uid")
                .merchantUid("merchant_uid")
                .orderStatus(OrderStatus.DELIVERY_READY)
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


        //when & then
        mockMvc.perform(get("/api/admin/orders")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("from", "2022-06-01")
                        .param("to", "2022-07-01")
                        .param("memberEmail", appProperties.getUserEmail())
                        .param("status", "ALL")
                        .param("orderType", "ALL")
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;

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

    private Dog generateDogRepresentative(Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
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