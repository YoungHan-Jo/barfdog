package com.bi.barfdog.api;

import com.bi.barfdog.api.recipeDto.RecipeRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.domain.recipe.ThumbnailImage;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.RecipeRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
public class RecipeApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    RecipeRepository recipeRepository;

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    @Test
    @DisplayName("정상적으로 레시피를 등록하는 테스트")
    public void create_recipe() throws Exception {
       //Given
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        String name = "스타터 프리미엄";
        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name(name)
                .description("스타터 프리미엄 설명")
                .uiNameKorean("스타터 프리미엄")
                .uiNameEnglish("STARTER PREMIUM")
                .pricePerGram("58.586")
                .gramPerKcal("1.19462")
                .ingredients("닭,칠면조")
                .descriptionForSurvey("첫 스타트 서베이 설명")
                .leaked(Leaked.LEAKED)
                .inStock(true)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));


        //when & then
        mockMvc.perform(multipart("/api/recipes")
                        .file(file1)
                        .file(file2)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("create_recipe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-recipes").description("레시피 리스트 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestParts(
                                partWithName("file1").description("썸네일1 이미지 파일"),
                                partWithName("file2").description("썸네일2 이미지 파일"),
                                partWithName("requestDto").description("레시피 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("레시피 이름"),
                                fieldWithPath("description").description("레시피 설명"),
                                fieldWithPath("uiNameKorean").description("UI용 한국어 레시피 이름"),
                                fieldWithPath("uiNameEnglish").description("UI용 영어 레시피 이름"),
                                fieldWithPath("pricePerGram").description("그램 당 가격"),
                                fieldWithPath("gramPerKcal").description("칼로리 당 그램"),
                                fieldWithPath("ingredients").description("레시피 재료 [띄워쓰기없이 쉼표(,)로 연결] ex) 닭,칠면조,오리"),
                                fieldWithPath("descriptionForSurvey").description("설문조사 시 노출시킬 특별히 챙겨주고 싶은 부분"),
                                fieldWithPath("leaked").description("레시피 노출 상태 [LEAKED/HIDDEN]"),
                                fieldWithPath("inStock").description("재고 여부 true/false")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-recipes.href").description("레시피 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        Recipe findRecipe = recipeRepository.findByName(name).get();

        assertThat(findRecipe.getStatus()).isEqualTo(RecipeStatus.ACTIVE);
    }

    @Test
    @DisplayName("레시피 등록 중 요청 값 부족으로 bad request 나오는 테스트")
    public void create_recipe_bad_request() throws Exception {
        //Given
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        RecipeRequestDto requestDto = RecipeRequestDto.builder().build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));


        //when & then
        mockMvc.perform(multipart("/api/recipes")
                        .file(file1)
                        .file(file2)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("숫자가 아닌값이 들어왔을때 bad request 나오는 테스트")
    public void create_recipe_not_number() throws Exception {
        //Given
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name("스타터 프리미엄")
                .description("스타터 프리미엄 설명")
                .uiNameKorean("스타터 프리미엄")
                .uiNameEnglish("STARTER PREMIUM")
                .pricePerGram("...")
                .gramPerKcal("1.1946xx")
                .ingredients("닭,칠면조")
                .descriptionForSurvey("첫 스타트 서베이 설명")
                .leaked(Leaked.LEAKED)
                .inStock(true)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));


        //when & then
        mockMvc.perform(multipart("/api/recipes")
                        .file(file1)
                        .file(file2)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("레시피 등록 중 파일 없으면 bad request 나오는 테스트")
    public void create_recipe_file_empty() throws Exception {
        //Given
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name("스타터 프리미엄")
                .description("스타터 프리미엄 설명")
                .uiNameKorean("스타터 프리미엄")
                .uiNameEnglish("STARTER PREMIUM")
                .pricePerGram("58.586")
                .gramPerKcal("1.19462")
                .ingredients("닭,칠면조")
                .descriptionForSurvey("첫 스타트 서베이 설명")
                .leaked(Leaked.LEAKED)
                .inStock(true)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));


        //when & then
        mockMvc.perform(multipart("/api/recipes")
                        .file(file1)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("정상적으로 레시피 리스트 조회 테스트")
    public void query_recipes() throws Exception {
       //given
        IntStream.range(1,5).forEach(i -> {
            generateRecipe(i);
        });

        List<Recipe> recipeList = recipeRepository.findByStatus(RecipeStatus.ACTIVE);

        //when & then
        mockMvc.perform(get("/api/recipes")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.recipeListResponseDtoList", hasSize(recipeList.size())))
                .andDo(document("query_recipes",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("create-recipe").description("레시피 생성 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
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
                                fieldWithPath("_embedded.recipeListResponseDtoList[0].id").description("레시피 id"),
                                fieldWithPath("_embedded.recipeListResponseDtoList[0].name").description("레시피 이름"),
                                fieldWithPath("_embedded.recipeListResponseDtoList[0].description").description("레시피 설명"),
                                fieldWithPath("_embedded.recipeListResponseDtoList[0].leaked").description("레시피 노출 상태 [LEAKED/HIDDEN]"),
                                fieldWithPath("_embedded.recipeListResponseDtoList[0].inStock").description("레시피 재고 유무"),
                                fieldWithPath("_embedded.recipeListResponseDtoList[0].modifiedDate").description("최종 수정 일자"),
                                fieldWithPath("_embedded.recipeListResponseDtoList[0]._links.update-recipe.href").description("해당 레시피 수정 링크"),
                                fieldWithPath("_embedded.recipeListResponseDtoList[0]._links.inactive-recipe.href").description("해당 레시피 비활성화 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.create-recipe.href").description("레시피 생성 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 레시피 하나 호출하는 테스트")
    public void query_recipe() throws Exception {
        //given
        Recipe recipe = generateRecipe(1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipes/{id}", recipe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_recipe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update-recipe").description("레시피 수정 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("해당 레시피 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("레시피 id"),
                                fieldWithPath("name").description("레시피 이름"),
                                fieldWithPath("description").description("레시피 설명"),
                                fieldWithPath("uiNameKorean").description("UI용 한국어 레시피 이름"),
                                fieldWithPath("uiNameEnglish").description("UI용 영어 레시피 이름"),
                                fieldWithPath("pricePerGram").description("그램 당 가격"),
                                fieldWithPath("gramPerKcal").description("칼로리 당 그램"),
                                fieldWithPath("ingredientList").description("레시피 재료 리스트"),
                                fieldWithPath("descriptionForSurvey").description("설문조사 시 노출시킬 특별히 챙겨주고 싶은 부분"),
                                fieldWithPath("thumbnailUri1").description("썸네일1 URI 주소"),
                                fieldWithPath("thumbnailUri2").description("썸네일2 URI 주소"),
                                fieldWithPath("leaked").description("레시피 노출 상태 [LEAKED/HIDDEN]"),
                                fieldWithPath("inStock").description("재고 여부 true/false"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update-recipe.href").description("레시피 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 레시피가 존재하지 않으면 not found 나오는 테스트")
    public void query_recipe_not_found() throws Exception {
        //given
        Recipe recipe = generateRecipe(1);

        //when & then
        mockMvc.perform(get("/api/recipes/99999")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("정상적으로 레시피 수정하는 테스트")
    public void update_recipe() throws Exception {
       //Given
        Recipe recipe = generateRecipe(1);

        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        String name = "수정한 이름";

        String description = "스타터 프리미엄 설명";
        String uiNameKorean = "스타터 프리미엄";
        String uiNameEnglish = "STARTER PREMIUM";
        String pricePerGram = "58.586";
        String gramPerKcal = "1.19462";
        String ingredients = "닭,칠면조";
        String descriptionForSurvey = "첫 스타트 서베이 설명";
        boolean leaked = true;
        boolean inStock = true;

        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name(name)
                .description(description)
                .uiNameKorean(uiNameKorean)
                .uiNameEnglish(uiNameEnglish)
                .pricePerGram(pricePerGram)
                .gramPerKcal(gramPerKcal)
                .ingredients(ingredients)
                .descriptionForSurvey(descriptionForSurvey)
                .leaked(Leaked.LEAKED)
                .inStock(inStock)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/recipes/{id}", recipe.getId())
                        .file(file1)
                        .file(file2)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_recipe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-recipes").description("레시피 리스트 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("해당 레시피 id")
                        ),
                        requestParts(
                                partWithName("file1").description("썸네일1 이미지 파일"),
                                partWithName("file2").description("썸네일2 이미지 파일"),
                                partWithName("requestDto").description("레시피 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("레시피 이름"),
                                fieldWithPath("description").description("레시피 설명"),
                                fieldWithPath("uiNameKorean").description("UI용 한국어 레시피 이름"),
                                fieldWithPath("uiNameEnglish").description("UI용 영어 레시피 이름"),
                                fieldWithPath("pricePerGram").description("그램 당 가격"),
                                fieldWithPath("gramPerKcal").description("칼로리 당 그램"),
                                fieldWithPath("ingredients").description("레시피 재료 [띄워쓰기없이 쉼표(,)로 연결] ex) 닭,칠면조,오리"),
                                fieldWithPath("descriptionForSurvey").description("설문조사 시 노출시킬 특별히 챙겨주고 싶은 부분"),
                                fieldWithPath("leaked").description("레시피 노출 상태 [LEAKED/HIDDEN]"),
                                fieldWithPath("inStock").description("재고 여부 true/false")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-recipes.href").description("레시피 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        Recipe findRecipe = recipeRepository.findById(recipe.getId()).get();

        assertThat(findRecipe.getName()).isEqualTo(name);
        assertThat(findRecipe.getDescription()).isEqualTo(description);
        assertThat(findRecipe.getUiNameKorean()).isEqualTo(uiNameKorean);
        assertThat(findRecipe.getUiNameEnglish()).isEqualTo(uiNameEnglish);
        assertThat(findRecipe.getPricePerGram()).isEqualTo(pricePerGram);
        assertThat(findRecipe.getGramPerKcal()).isEqualTo(gramPerKcal);
        assertThat(findRecipe.getIngredients()).isEqualTo(ingredients);
        assertThat(findRecipe.getDescriptionForSurvey()).isEqualTo(descriptionForSurvey);
        assertThat(findRecipe.getLeaked()).isEqualTo(Leaked.LEAKED);
        assertThat(findRecipe.isInStock()).isEqualTo(inStock);
    }

    @Test
    @DisplayName("첨부파일 하나 없어도 레시피 수정하는 테스트")
    public void update_recipe_one_file() throws Exception {
        //Given
        Recipe recipe = generateRecipe(1);

        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name("수정한 이름")
                .description("스타터 프리미엄 설명")
                .uiNameKorean("스타터 프리미엄")
                .uiNameEnglish("STARTER PREMIUM")
                .pricePerGram("58.586")
                .gramPerKcal("1.19462")
                .ingredients("닭,칠면조")
                .descriptionForSurvey("첫 스타트 서베이 설명")
                .leaked(Leaked.LEAKED)
                .inStock(true)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/recipes/{id}", recipe.getId())
                        .file(file1)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        Recipe findRecipe = recipeRepository.findById(recipe.getId()).get();

        assertThat(findRecipe.getThumbnailImage().getFilename2()).isEqualTo(recipe.getThumbnailImage().getFilename2());
    }

    @Test
    @DisplayName("첨부파일 없어도 정상적으로 레시피 수정하는 테스트")
    public void update_recipe_no_file() throws Exception {
        //Given
        Recipe recipe = generateRecipe(1);

        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name("수정한 이름")
                .description("스타터 프리미엄 설명")
                .uiNameKorean("스타터 프리미엄")
                .uiNameEnglish("STARTER PREMIUM")
                .pricePerGram("58.586")
                .gramPerKcal("1.19462")
                .ingredients("닭,칠면조")
                .descriptionForSurvey("첫 스타트 서베이 설명")
                .leaked(Leaked.LEAKED)
                .inStock(true)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/recipes/{id}", recipe.getId())
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Recipe findRecipe = recipeRepository.findById(recipe.getId()).get();

        assertThat(findRecipe.getThumbnailImage().getFilename1()).isEqualTo(recipe.getThumbnailImage().getFilename1());
        assertThat(findRecipe.getThumbnailImage().getFilename2()).isEqualTo(recipe.getThumbnailImage().getFilename2());
    }

    @Test
    @DisplayName("수정할 값이 숫자가 아닌 경우 bad request 나오는 테스트")
    public void update_recipe_not_number() throws Exception {
        //Given
        Recipe recipe = generateRecipe(1);

        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name("수정한 이름")
                .description("스타터 프리미엄 설명")
                .uiNameKorean("스타터 프리미엄")
                .uiNameEnglish("STARTER PREMIUM")
                .pricePerGram("...")
                .gramPerKcal("1.19462x")
                .ingredients("닭,칠면조")
                .descriptionForSurvey("첫 스타트 서베이 설명")
                .leaked(Leaked.LEAKED)
                .inStock(true)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/recipes/{id}", recipe.getId())
                        .file(file1)
                        .file(file2)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("레시피 수정 중 파라미터 값이 부족하면 bad request 나오는 테스트")
    public void update_recipe_bad_request() throws Exception {
        //Given
        Recipe recipe = generateRecipe(1);

        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        RecipeRequestDto requestDto = RecipeRequestDto.builder().build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/recipes/{id}", recipe.getId())
                        .file(file1)
                        .file(file2)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정할 레시피가 없을 경우 not found 나오는 테스트")
    public void update_recipe_not_found() throws Exception {
        //Given
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        RecipeRequestDto requestDto = RecipeRequestDto.builder()
                .name("수정한 이름")
                .description("스타터 프리미엄 설명")
                .uiNameKorean("스타터 프리미엄")
                .uiNameEnglish("STARTER PREMIUM")
                .pricePerGram("58.586")
                .gramPerKcal("1.19462")
                .ingredients("닭,칠면조")
                .descriptionForSurvey("첫 스타트 서베이 설명")
                .leaked(Leaked.LEAKED)
                .inStock(true)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/recipes/999999")
                        .file(file1)
                        .file(file2)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("정상적으로 레시피를 비활성화 시키는 테스트")
    public void inactive_recipe() throws Exception {
       //given
        Recipe recipe = generateRecipe(1);

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/recipes/{id}/inactive", recipe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("inactive_recipe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-recipes").description("레시피 리스트 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("해당 레시피 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-recipes.href").description("레시피 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        Recipe findRecipe = recipeRepository.findById(recipe.getId()).get();

        assertThat(findRecipe.getStatus()).isEqualTo(RecipeStatus.INACTIVE);
    }

    @Test
    @DisplayName("비활성화시킬 레시피가 없을 경우 not found 나오는 테스트")
    public void inactive_recipe_not_found() throws Exception {
        //given

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/recipes/999999/inactive")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("정상적으로 재료 리스트 호출하는 테스트")
    public void queryIngredients() throws Exception {
       //given
       IntStream.range(1,10).forEach(i -> {
           generateRecipe(i);
       });

       //when & then
        mockMvc.perform(get("/api/recipes/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;


      
    }
    
    
    




    






    private Recipe generateRecipe(int i) {
        Recipe recipe = Recipe.builder()
                .name("레시피" + i)
                .description("레시피 설명" + i)
                .uiNameKorean("레시피 한글")
                .uiNameEnglish("RECIPE ENGLISH")
                .pricePerGram(new BigDecimal("48.234"))
                .gramPerKcal(new BigDecimal("1.23456"))
                .ingredients(i==1?"닭,소":"칠면조,양")
                .descriptionForSurvey("설문조사용 설명")
                .thumbnailImage(new ThumbnailImage("http://xxxx.com/recipe", "file1.jpg", "file2.jpg"))
                .leaked(Leaked.LEAKED)
                .inStock(false)
                .status(RecipeStatus.ACTIVE)
                .build();

        if (i%2 == 0) {
            recipe.inactive();
        }

        return recipeRepository.save(recipe);
    }

    private String getBearerToken() throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .username(appProperties.getAdminEmail())
                .password(appProperties.getAdminPassword())
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