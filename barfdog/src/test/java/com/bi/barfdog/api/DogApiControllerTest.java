package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.config.AppConfig;
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

import java.math.BigDecimal;
import java.nio.charset.Charset;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
                .weight("5.4")
                .neutralization(false)
                .activityLevel(ActivityLevel.LITTLE)
                .walkingCountPerWeek("5")
                .walkingTimePerOneTime("0.5")
                .dogStatus(DogStatus.HEALTHY)
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
                .andExpect(status().isCreated())
        ;
      
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
        ;

    }
    
    @Test
    public void pow() throws Exception {
       //given

        BigDecimal add = BigDecimal.valueOf(100.0).subtract(BigDecimal.valueOf(1.50));
        System.out.println("add = " + add);


        //when & then
      
    }
    





    private String getAdminToken() throws Exception {
        return getBearerToken(appProperties.getAdminEmail(), appProperties.getAdminPassword());
    }

    private String getUserToken() throws Exception {
        return getBearerToken(appProperties.getUserEmail(), appProperties.getUserPassword());
    }

    private String getBearerToken(String appProperties, String appProperties1) throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .username(appProperties)
                .password(appProperties1)
                .build();

        //when & then
        ResultActions perform = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        MockHttpServletResponse response = perform.andReturn().getResponse();
        return response.getHeaders("Authorization").get(0);
    }

}