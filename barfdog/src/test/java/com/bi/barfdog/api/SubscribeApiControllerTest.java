package com.bi.barfdog.api;

import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
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
import org.assertj.core.api.Assertions;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    RecipeRepository recipeRepository;
    @Autowired
    SubscribeRepository subscribeRepository;
    @Autowired
    DogRepository dogRepository;
    @Autowired
    SubscribeRecipeRepository subscribeRecipeRepository;

    @Test
    @DisplayName("정상적으로 구독 업데이트하는 테스트")
    public void updateSubscribe() throws Exception {
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
    @DisplayName("정상적으로 구독 업데이트하는 테스트")
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




    private List<Long> getRecipeIdList() {
        List<Long> recipeIdList = new ArrayList<>();
        List<Recipe> recipes = recipeRepository.findAll();
        recipeIdList.add(recipes.get(0).getId());
        recipeIdList.add(recipes.get(1).getId());
        return recipeIdList;
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

    private Subscribe generateSubscribe(Dog dog) {
        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .build();

        dog.setSubscribe(subscribe);

        return subscribeRepository.save(subscribe);
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