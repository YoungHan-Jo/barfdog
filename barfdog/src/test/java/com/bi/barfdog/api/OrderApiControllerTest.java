package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class OrderApiControllerTest extends BaseTest {

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

    @Test
    @DisplayName("구독 주문서 조회하기")
    public void getOrderSheetDto_Subscribe() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = generateSubscribe(dogRepresentative);

       //when & then
        mockMvc.perform(get("/api/orders/sheet/subscribe/{id}",subscribe.getId())
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
                                fieldWithPath("email").description("회원 이메일 주소"),
                                fieldWithPath("phoneNumber").description("휴대전화 번호"),
                                fieldWithPath("address.zipcode").description("우편번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명 주소"),
                                fieldWithPath("address.detailAddress").description("상세 주소"),
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