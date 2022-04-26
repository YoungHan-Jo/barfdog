package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.dog.ActivityLevel;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.dog.DogStatus;
import com.bi.barfdog.domain.dog.SnackCountLevel;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.RecipeRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class DogApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    RecipeRepository recipeRepository;

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    
    @Test
    @DisplayName("정상적으로 강아지 등록하는 테스트")
    public void create_dog() throws Exception {
        //Given
        Recipe recipe = recipeRepository.findByName("스타트").get();

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

        //when & then
        mockMvc.perform(post("/api/dogs")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_dog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-surveyReport").description("설문조사 레포트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("강아지 이름"),
                                fieldWithPath("gender").description("강아지 성별 [MALE, FEMALE]"),
                                fieldWithPath("birth").description("강아지 생월 'yyyyMM'"),
                                fieldWithPath("oldDog").description("노견 여부 true/false"),
                                fieldWithPath("dogType").description("강아지 종"),
                                fieldWithPath("dogSize").description("강아지 체급 [LARGE, MIDDLE, SMALL]"),
                                fieldWithPath("weight").description("강아지 몸무게"),
                                fieldWithPath("neutralization").description("중성화 여부 true/false"),
                                fieldWithPath("activityLevel").description("활동량 레벨 [VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH]"),
                                fieldWithPath("walkingCountPerWeek").description("주 당 산책 횟수"),
                                fieldWithPath("walkingTimePerOneTime").description("한 번 산책 할 때 산책 시간"),
                                fieldWithPath("dogStatus").description("강아지 상태 [HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING]"),
                                fieldWithPath("snackCountLevel").description("간식 먹는 정도 [LITTLE, NORMAL, MUCH]"),
                                fieldWithPath("inedibleFood").description("못 먹는 음식 [없으면 'NONE', 기타일 경우 'ETC']"),
                                fieldWithPath("inedibleFoodEtc").description("기타('ETC') 일 경우 못 먹는 음식 입력 [없으면 'NONE']"),
                                fieldWithPath("recommendRecipeId").description("특별히 챙겨주고싶은 부분에 해당하는 레시피 id"),
                                fieldWithPath("caution").description("기타 특이사항 [없으면 'NONE']")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-surveyReport.href").description("설문조사 레포트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("강아지 등록 시 파라미터 값 부족하면 bad request 나오는 테스트")
    public void create_dog_bad_request() throws Exception {
        //Given
        Recipe recipe = recipeRepository.findByName("스타트").get();

        DogSaveRequestDto requestDto = DogSaveRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(post("/api/dogs")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("강아지 등록 시 파라미터값이 숫자가 아닌경우 bad request 나오는 테스트")
    public void create_dog_not_number() throws Exception {
        //Given
        Recipe recipe = recipeRepository.findByName("스타트").get();

        DogSaveRequestDto requestDto = DogSaveRequestDto.builder()
                .name("김바프")
                .gender(Gender.MALE)
                .birth("202102")
                .oldDog(false)
                .dogType("포메라니안")
                .dogSize(DogSize.SMALL)
                .weight("3.5x")
                .neutralization(false)
                .activityLevel(ActivityLevel.LITTLE)
                .walkingCountPerWeek("5.")
                .walkingTimePerOneTime(".")
                .dogStatus(DogStatus.NEED_DIET)
                .snackCountLevel(SnackCountLevel.MUCH)
                .inedibleFood("NONE")
                .inedibleFoodEtc("NONE")
                .recommendRecipeId(recipe.getId())
                .caution("NONE")
                .build();

        //when & then
        mockMvc.perform(post("/api/dogs")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors",hasSize(3)))
        ;

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