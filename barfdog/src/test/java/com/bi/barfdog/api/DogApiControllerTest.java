package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.dogDto.UpdateDogPictureDto;
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
import com.bi.barfdog.repository.card.CardRepository;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.orderItem.SelectOptionRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.subscribe.BeforeSubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import com.bi.barfdog.repository.dog.DogPictureRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import org.junit.Before;
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

import javax.persistence.EntityManager;
import java.io.FileInputStream;
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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class DogApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;
    @Autowired
    AppProperties appProperties;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    DogRepository dogRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    DogPictureRepository dogPictureRepository;
    @Autowired
    SubscribeRepository subscribeRepository;
    @Autowired
    SurveyReportRepository surveyReportRepository;
    @Autowired
    SettingRepository settingRepository;
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

    @Before
    public void setUp() {

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();
        surveyReportRepository.deleteAll();
        dogRepository.deleteAll();

    }

    @Test
    @DisplayName("정상적으로 강아지 사진 업로드")
    public void uploadPicture() throws Exception {
       //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/dogs/picture/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("upload_dogPicture",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestParts(
                                partWithName("file").description("업로드할 강아지 사진 이미지 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("강아지 프로필사진 id"),
                                fieldWithPath("url").description("프로필 사진 url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("업로드할 강아지 사진이 없음")
    public void uploadPicture_noFile() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/dogs/picture/upload")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("정상적으로 강아지 사진 수정")
    public void updateDogPicture() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        DogPicture dogPicture = generateDogPicture(1);
        dogPicture.setDog(dog);

        List<DogPicture> dogPictures = dogPictureRepository.findByDog(dog);
        assertThat(dogPictures.size()).isEqualTo(1);
        assertThat(dogPictures.get(0).getId()).isEqualTo(dogPicture.getId());

        DogPicture newDogPicture = generateDogPicture(2);

        UpdateDogPictureDto requestDto = UpdateDogPictureDto.builder()
                .dogPictureId(newDogPicture.getId())
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/dogs/{id}/picture",dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_dogPicture",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_dogs").description("강아지 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("프로필사진 변경할 강아지 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("dogPictureId").description("강아지 사진 id, null 일 경우 사진 삭제 기능")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_dogs.href").description("강아지 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        List<DogPicture> dogPictureList = dogPictureRepository.findByDog(dog);
        assertThat(dogPictureList.size()).isEqualTo(1);
        assertThat(dogPictureList.get(0).getId()).isEqualTo(newDogPicture.getId());

        DogPicture findPicture = dogPictureRepository.findById(newDogPicture.getId()).get();
        assertThat(findPicture.getDog().getId()).isEqualTo(dog.getId());
    }

    @Test
    @DisplayName("강아지 사진 수정 시 파일 없으면 강아지 사진 삭제")
    public void updateDogPicture_noFile() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        DogPicture dogPicture = generateDogPicture(1);
        dogPicture.setDog(dog);

        List<DogPicture> dogPictures = dogPictureRepository.findByDog(dog);
        assertThat(dogPictures.size()).isEqualTo(1);
        assertThat(dogPictures.get(0).getId()).isEqualTo(dogPicture.getId());


        UpdateDogPictureDto requestDto = UpdateDogPictureDto.builder()
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/dogs/{id}/picture",dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        List<DogPicture> dogPictureList = dogPictureRepository.findByDog(dog);
        assertThat(dogPictureList.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("수정하려는 개가 내 개가 아닐경우")
    public void updateDogPicture_notMyDog() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Dog dog = generateDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        DogPicture dogPicture = generateDogPicture(1);
        dogPicture.setDog(dog);

        List<DogPicture> dogPictures = dogPictureRepository.findByDog(dog);
        assertThat(dogPictures.size()).isEqualTo(1);
        assertThat(dogPictures.get(0).getId()).isEqualTo(dogPicture.getId());

        DogPicture newDogPicture = generateDogPicture(2);

        UpdateDogPictureDto requestDto = UpdateDogPictureDto.builder()
                .dogPictureId(newDogPicture.getId())
                .build();

        //when & then
        mockMvc.perform(put("/api/dogs/{id}/picture",dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정하려는 개가 존재하지않음")
    public void updateDogPicture_dog_notfound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        DogPicture dogPicture = generateDogPicture(1);
        dogPicture.setDog(dog);

        List<DogPicture> dogPictures = dogPictureRepository.findByDog(dog);
        assertThat(dogPictures.size()).isEqualTo(1);
        assertThat(dogPictures.get(0).getId()).isEqualTo(dogPicture.getId());

        DogPicture newDogPicture = generateDogPicture(2);

        UpdateDogPictureDto requestDto = UpdateDogPictureDto.builder()
                .dogPictureId(newDogPicture.getId())
                .build();

        //when & then
        mockMvc.perform(put("/api/dogs/999999/picture")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("수정하려는 사진이 존재하지않음")
    public void updateDogPicture_pic_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        DogPicture dogPicture = generateDogPicture(1);
        dogPicture.setDog(dog);

        DogPicture newDogPicture = generateDogPicture(2);

        UpdateDogPictureDto requestDto = UpdateDogPictureDto.builder()
                .dogPictureId(999999L)
                .build();

        //when & then
        mockMvc.perform(put("/api/dogs/{id}/picture",dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 대표견 설정")
    public void setRepresentativeDog() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog representativeDog = generateDogRepresentative(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Dog dog = generateDog(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Dog newDog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/dogs/{id}/representative",newDog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_representative_dog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_dogs").description("강아지 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("대표견으로 설정할 강아지 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_dogs.href").description("강아지 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Dog findDog = dogRepository.findById(newDog.getId()).get();
        assertThat(findDog.isRepresentative()).isTrue();

        Dog originalRepresentativeDog = dogRepository.findById(representativeDog.getId()).get();
        assertThat(originalRepresentativeDog.isRepresentative()).isFalse();
        Dog falseDog = dogRepository.findById(dog.getId()).get();
        assertThat(falseDog.isRepresentative()).isFalse();


    }

    @Test
    @DisplayName("대표견으로 설정할 강아지가 존재하지 않음")
    public void setRepresentativeDog_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog representativeDog = generateDogRepresentative(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Dog dog = generateDog(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        Dog newDog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/dogs/99999/representative")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("정상적으로 강아지 하나 정보 조회")
    public void queryDog() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/dogs/{id}", dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_dog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_dog").description("강아지 정보 수정 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 강아지 id")
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
                                fieldWithPath("dogDto.id").description("강아지 id"),
                                fieldWithPath("dogDto.name").description("강아지 이름"),
                                fieldWithPath("dogDto.gender").description("강아지 성별"),
                                fieldWithPath("dogDto.birth").description("강아지 생일 'yyyyMM'"),
                                fieldWithPath("dogDto.oldDog").description("노견 여부 true/false"),
                                fieldWithPath("dogDto.dogType").description("견종"),
                                fieldWithPath("dogDto.dogSize").description("강아지 크기 [LARGE, MIDDLE, SMALL]"),
                                fieldWithPath("dogDto.weight").description("강아지 무게 "),
                                fieldWithPath("dogDto.neutralization").description("중성화 여부 true/false"),
                                fieldWithPath("dogDto.activityLevel").description("활동량 레벨 [VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH]"),
                                fieldWithPath("dogDto.walkingCountPerWeek").description("주 당 산책 횟수"),
                                fieldWithPath("dogDto.walkingTimePerOneTime").description("한 번 산책 할 때 산책 시간"),
                                fieldWithPath("dogDto.dogStatus").description("강아지 상태 [HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING]"),
                                fieldWithPath("dogDto.snackCountLevel").description("간식 먹는 정도 [LITTLE, NORMAL, MUCH]"),
                                fieldWithPath("dogDto.inedibleFood").description("못 먹는 음식 [없으면 'NONE', 기타일 경우 'ETC']"),
                                fieldWithPath("dogDto.inedibleFoodEtc").description("기타('ETC') 일 경우 못 먹는 음식 입력 [없으면 'NONE']"),
                                fieldWithPath("dogDto.recommendRecipeId").description("특별히 챙겨주고싶은 부분에 해당하는 레시피 id"),
                                fieldWithPath("dogDto.caution").description("기타 특이사항 [없으면 'NONE']"),
                                fieldWithPath("recipeDtoList[0].id").description("레시피 id"),
                                fieldWithPath("recipeDtoList[0].descriptionForSurvey").description("설문조사용 레시피 설명"),
                                fieldWithPath("recipeDtoList[0].ingredients").description("레시피에 들어간 재료 리스트"),
                                fieldWithPath("ingredients").description("전체 재료 리스트"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_dog.href").description("강아지 정보 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회한 강아지가 존재하지않음")
    public void queryDog_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/dogs/9999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 설문조사 레포트 조회")
    public void querySurveyReport() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SurveyReport surveyReport = generateSurveyReport(member);
        Dog dog = surveyReport.getDog();

        List<SurveyReport> reports = surveyReportRepository.findAll();
        SurveyReport findReport = reports.get(0);
        assertThat(findReport.getId()).isEqualTo(surveyReport.getId());
        assertThat(findReport.getDog().getId()).isEqualTo(dog.getId());

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/dogs/{id}/surveyReport", dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_dog_surveyReport",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_surveyReportResult").description("설문조사 리포트 결과 화면 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("해당 강아지 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("lastSurveyDate").description("마지막 설문조사 날짜"),
                                fieldWithPath("myDogName").description("설문 강아지 이름"),
                                fieldWithPath("dogSize").description("강아지 크기 [LARGE, MIDDLE, SMALL]"),
                                fieldWithPath("dogActivity.activityLevel").description("활동량 레벨 [VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH]"),
                                fieldWithPath("dogActivity.walkingCountPerWeek").description("주 당 산책 회수"),
                                fieldWithPath("dogActivity.walkingTimePerOneTime").description("한 번 당 산책 시간"),
                                fieldWithPath("ageAnalysis.avgAgeMonth").description("바프독을 시작한 평균 나이"),
                                fieldWithPath("ageAnalysis.ageGroupOneCount").description("1그룹(가장어린)에 포함된 강아지 수"),
                                fieldWithPath("ageAnalysis.ageGroupTwoCount").description("2그룹에 포함된 강아지 수"),
                                fieldWithPath("ageAnalysis.ageGroupThreeCount").description("3그룹에 포함된 강아지 수"),
                                fieldWithPath("ageAnalysis.ageGroupFourCount").description("4그룹에 포함된 강아지 수"),
                                fieldWithPath("ageAnalysis.ageGroupFiveCount").description("5그룹에 포함된 강아지 수"),
                                fieldWithPath("ageAnalysis.myAgeGroup").description("내 강아지가 속한 그룹"),
                                fieldWithPath("ageAnalysis.myStartAgeMonth").description("내 강아지가 바프독을 시작한 나이"),
                                fieldWithPath("weightAnalysis.avgWeight").description("해당 체급 평균 체중"),
                                fieldWithPath("weightAnalysis.weightGroupOneCount").description("해당 체급 중 1그룹(가장가벼운)에 포함된 강아지 수"),
                                fieldWithPath("weightAnalysis.weightGroupTwoCount").description("해당 체급 중 2그룹에 포함된 강아지 수"),
                                fieldWithPath("weightAnalysis.weightGroupThreeCount").description("해당 체급 중 3그룹에 포함된 강아지 수"),
                                fieldWithPath("weightAnalysis.weightGroupFourCount").description("해당 체급 중 4그룹에 포함된 강아지 수"),
                                fieldWithPath("weightAnalysis.weightGroupFiveCount").description("해당 체급 중 5그룹에 포함된 강아지 수"),
                                fieldWithPath("weightAnalysis.myWeightGroup").description("내 강아지가 속한 그룹"),
                                fieldWithPath("weightAnalysis.weightInLastReport").description("마지막으로 설문조사 했을 때 강아지 체중"),
                                fieldWithPath("activityAnalysis.avgActivityLevel").description("해당 체급의 평균 활동량"),
                                fieldWithPath("activityAnalysis.activityGroupOneCount").description("해당 체급 중 1그룹(활동량 가장 낮은)에 포함된 강아지 수"),
                                fieldWithPath("activityAnalysis.activityGroupTwoCount").description("해당 체급 중 2그룹에 포함된 강아지 수"),
                                fieldWithPath("activityAnalysis.activityGroupThreeCount").description("해당 체급 중 3그룹에 포함된 강아지 수"),
                                fieldWithPath("activityAnalysis.activityGroupFourCount").description("해당 체급 중 4그룹에 포함된 강아지 수"),
                                fieldWithPath("activityAnalysis.activityGroupFiveCount").description("해당 체급 중 5그룹에 포함된 강아지 수"),
                                fieldWithPath("activityAnalysis.myActivityGroup").description("내 강아지가 속한 그룹 [1:VERY_LITTLE, 2:LITTLE, 3:NORMAL, 4:MUCH, 5:VERY_MUCH]"),
                                fieldWithPath("walkingAnalysis.highRankPercent").description("산책량 상위 퍼센트"),
                                fieldWithPath("walkingAnalysis.walkingCountPerWeek").description("일주일 산책 횟수"),
                                fieldWithPath("walkingAnalysis.totalWalingTime").description("일주일 총 산책 시간"),
                                fieldWithPath("walkingAnalysis.avgWalkingTimeInCity").description("같은 지역 평균 산책 시간"),
                                fieldWithPath("walkingAnalysis.avgWalkingTimeInAge").description("또래 평균 산책 시간"),
                                fieldWithPath("walkingAnalysis.avgWalkingTimeInDogSize").description("같은 체급 평균 산책 시간"),
                                fieldWithPath("snackAnalysis.avgSnackCountInLargeDog").description("대형견 평균 간식 레벨 [1~3] 숫자가 높을수록 간식량 많음"),
                                fieldWithPath("snackAnalysis.avgSnackCountInMiddleDog").description("중형견 평균 간식 레벨 [1~3]"),
                                fieldWithPath("snackAnalysis.avgSnackCountInSmallDog").description("소형견 평균 간식 레벨 [1~3]"),
                                fieldWithPath("snackAnalysis.mySnackCount").description("내 강아지 간식량 [1,2,3]"),
                                fieldWithPath("foodAnalysis.oneDayRecommendKcal").description("하루 권장 칼로리"),
                                fieldWithPath("foodAnalysis.oneDayRecommendGram").description("하루 권장 식사랑"),
                                fieldWithPath("foodAnalysis.oneMealRecommendGram").description("한끼 권장 식사량"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_surveyReportResult.href").description("설문조사 리포트 결과 화면 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("설문조사 레포트 조회할 강아지가 존재하지않음")
    public void querySurveyReport_dog_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SurveyReport surveyReport = generateSurveyReport(member);
        Dog dog = surveyReport.getDog();

        List<SurveyReport> reports = surveyReportRepository.findAll();
        SurveyReport findReport = reports.get(0);
        assertThat(findReport.getId()).isEqualTo(surveyReport.getId());
        assertThat(findReport.getDog().getId()).isEqualTo(dog.getId());

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/dogs/99999/surveyReport")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("설문조사 리포트의 결과 조회 하는 테스트")
    public void querySurveyReportResult() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder subscribeOrder = generateSubscribeOrderAndEtc(member, 1, OrderStatus.PAYMENT_DONE);
        Dog dog = subscribeOrder.getSubscribe().getDog();

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/dogs/{id}/surveyReportResult",dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_dog_surveyReportResult",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-orderSheet-subscribe").description("구독 주문서 조회 링크 - 구독중이지 않을 경우 사용"),
                                linkWithRel("update_subscribe").description("구독 정보 업데이트 링크 - 구독중(status = SUBSCRIBING)일 경우 사용"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("강아지 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("dogId").description("강아지 id"),
                                fieldWithPath("dogName").description("강아지 이름"),
                                fieldWithPath("subscribeId").description("구독 id"),
                                fieldWithPath("subscribeStatus").description("구독 상태 [BEFORE_PAYMENT, SUBSCRIBING, SUBSCRIBE_PENDING, ADMIN]"),
                                fieldWithPath("recommendRecipeId").description("추천 레시피 id"),
                                fieldWithPath("recommendRecipeName").description("추천 레시피 이름"),
                                fieldWithPath("recommendRecipeDescription").description("추천 레시피 설명"),
                                fieldWithPath("recommendRecipeImgUrl").description("추천 레시피 썸네일"),
                                fieldWithPath("uiNameKorean").description("한글 ui 이름"),
                                fieldWithPath("uiNameEnglish").description("영어 ui 이름"),
                                fieldWithPath("foodAnalysis.oneDayRecommendKcal").description("하루 권장 칼로리"),
                                fieldWithPath("foodAnalysis.oneDayRecommendGram").description("하루 권장 식사량"),
                                fieldWithPath("foodAnalysis.oneMealRecommendGram").description("한끼 권장 식사량"),
                                fieldWithPath("recipeDtoList[0].id").description("레시피 id"),
                                fieldWithPath("recipeDtoList[0].name").description("레시피 이름"),
                                fieldWithPath("recipeDtoList[0].description").description("레시피 설명"),
                                fieldWithPath("recipeDtoList[0].pricePerGram").description("그램 당 가격"),
                                fieldWithPath("recipeDtoList[0].gramPerKcal").description("칼로리 당 그램"),
                                fieldWithPath("recipeDtoList[0].inStock").description("재고 여부 true/false"),
                                fieldWithPath("recipeDtoList[0].imgUrl").description("썸네일 url"),
                                fieldWithPath("plan").description("기존에 설정된 플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("recipeName").description("기존에 설정된 레시피 이름 ','로 구분 ex) '스타트,터키비프'"),
                                fieldWithPath("oneMealRecommendGram").description("기존에 설정된 권장량"),
                                fieldWithPath("nextPaymentDate").description("기존에 설정된 결제 예정 날짜, 정보 없으면 null"),
                                fieldWithPath("nextPaymentPrice").description("기존에 설정된 결제 예정 금액, 정보 없으면 null"),
                                fieldWithPath("nextDeliveryDate").description("기존에 설정된 배송 예정 날짜, 정보 없으면 null"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-orderSheet-subscribe.href").description("결제를 위한 구독 주문서 조회 링크 - 구독중이지 않을 경우 사용"),
                                fieldWithPath("_links.update_subscribe.href").description("구독 정보 업데이트 링크 - 구독중(status = SUBSCRIBING)일 경우 사용"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("설문조사리포트 결과를 조회할 강아지가 없음 404")
    public void querySurveyReportResult_dog_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SurveyReport surveyReport = generateSurveyReport(member);
        Dog dog = surveyReport.getDog();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/dogs/999999/surveyReportResult")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("정상적으로 강아지 삭제")
    public void deleteDog() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        assertThat(dog.isDeleted()).isFalse();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/dogs/{id}",dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete_dog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_dogs").description("강아지 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 강아지 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_dogs.href").description("강아지 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Dog findDog = dogRepository.findById(dog.getId()).get();
        assertThat(findDog.isDeleted()).isTrue();

    }

    @Test
    @DisplayName("삭제할 강아지가 없음")
    public void deleteDog_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDog(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        assertThat(dog.isDeleted()).isFalse();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/dogs/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 강아지 삭제")
    public void deleteDog_representativeDog() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        assertThat(dog.isRepresentative()).isTrue();
        assertThat(dog.isDeleted()).isFalse();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/dogs/{id}",dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 강아지 리스트 조회")
    public void queryDogs() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Dog dogRepresentative = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
        DogPicture dogPicture = generateDogPicture(1);
        dogPicture.setDog(dogRepresentative);
        generateSubscribe(dogRepresentative);

        IntStream.range(1,4).forEach(i -> {
            Dog dog = generateDog(i, member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
            generateSubscribe(dog);
            DogPicture dogPic = generateDogPicture(i);
            dogPic.setDog(dog);
            Dog adminDog = generateDog(i, admin, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);
            generateSubscribe(adminDog);
        });

        em.flush();
        em.clear();

        //when & then
        mockMvc.perform(get("/api/dogs")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryDogsDtoList", hasSize(4)))
                .andDo(document("query_dogs",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryDogsDtoList[0].id").description("강아지 id"),
                                fieldWithPath("_embedded.queryDogsDtoList[0].pictureUrl").description("강아지 프로필사진 url, 사진없으면 null"),
                                fieldWithPath("_embedded.queryDogsDtoList[0].name").description("강아지 이름"),
                                fieldWithPath("_embedded.queryDogsDtoList[0].birth").description("강아지 생일 'yyyyMM'"),
                                fieldWithPath("_embedded.queryDogsDtoList[0].gender").description("강아지 성별 [MALE, FEMALE]"),
                                fieldWithPath("_embedded.queryDogsDtoList[0].representative").description("대표견 여부 true/false"),
                                fieldWithPath("_embedded.queryDogsDtoList[0].subscribeStatus").description("구독 상태 [BEFORE_PAYMENT, SUBSCRIBING, SUBSCRIBE_PENDING, ADMIN] 각 결제 전/ 구독 중/ 구독 보류 / 관리자구독"),
                                fieldWithPath("_embedded.queryDogsDtoList[0]._links.update_picture.href").description("강아지 사진 수정 링크"),
                                fieldWithPath("_embedded.queryDogsDtoList[0]._links.set_representative_dog.href").description("대표견 설정 링크"),
                                fieldWithPath("_embedded.queryDogsDtoList[0]._links.query_dog.href").description("강아지 정보 조회 링크"),
                                fieldWithPath("_embedded.queryDogsDtoList[0]._links.query_surveyReport.href").description("설문조사 리포트 조회 링크"),
                                fieldWithPath("_embedded.queryDogsDtoList[0]._links.delete_dog.href").description("강아지 삭제 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 강아지 수정")
    public void updateDog() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SurveyReport surveyReport = generateSurveyReport(member);

        Recipe recipe = recipeRepository.findAll().get(0);
        String name = "수정된이름";
        String birth = "202205";
        String dogType = "불독";
        String weight = "3.70";
        String walkingCountPerWeek = "5";
        DogSaveRequestDto requestDto = DogSaveRequestDto.builder()
                .name(name)
                .gender(Gender.MALE)
                .birth(birth)
                .oldDog(false)
                .dogType(dogType)
                .dogSize(DogSize.SMALL)
                .weight(weight)
                .neutralization(true)
                .activityLevel(ActivityLevel.NORMAL)
                .walkingCountPerWeek(walkingCountPerWeek)
                .walkingTimePerOneTime("1.1")
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(SnackCountLevel.NORMAL)
                .inedibleFood("NONE")
                .inedibleFoodEtc("NONE")
                .recommendRecipeId(recipe.getId())
                .caution("NONE")
                .build();

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/dogs/{id}", surveyReport.getDog().getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_dog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_surveyReport").description("설문조사 레포트 조회 링크"),
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_surveyReport.href").description("설문조사 레포트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));


        em.flush();
        em.clear();

        Dog findDog = dogRepository.findById(surveyReport.getDog().getId()).get();
        assertThat(findDog.getRecommendRecipe().getId()).isEqualTo(recipe.getId());
        assertThat(findDog.getName()).isEqualTo(name);
        assertThat(findDog.getBirth()).isEqualTo(birth);
        assertThat(findDog.getDogType()).isEqualTo(dogType);
        assertThat(findDog.getWeight()).isEqualTo(new BigDecimal(weight));
        assertThat(findDog.getDogActivity().getWalkingCountPerWeek()).isEqualTo(Integer.valueOf(walkingCountPerWeek));

        AgeAnalysis ageAnalysis = getAgeAnalysis(findDog.getStartAgeMonth());
        SnackAnalysis snackAnalysis = getSnackAnalysis(findDog);
        ActivityAnalysis activityAnalysis = getActivityAnalysis(findDog.getDogSize(), findDog);
        WeightAnalysis weightAnalysis = getWeightAnalysis(findDog.getDogSize(), findDog.getWeight());

        Long surveyReportId = surveyReport.getId();

        SurveyReport findSurveyReport = surveyReportRepository.findById(surveyReportId).get();
        assertThat(findSurveyReport.getAgeAnalysis().getAvgAgeMonth()).isEqualTo(ageAnalysis.getAvgAgeMonth());
        assertThat(findSurveyReport.getAgeAnalysis().getMyAgeGroup()).isEqualTo(ageAnalysis.getMyAgeGroup());
        assertThat(findSurveyReport.getAgeAnalysis().getMyStartAgeMonth()).isEqualTo(ageAnalysis.getMyStartAgeMonth());

        assertThat(findSurveyReport.getSnackAnalysis().getAvgSnackCountInLargeDog()).isEqualTo(snackAnalysis.getAvgSnackCountInLargeDog());
        assertThat(findSurveyReport.getSnackAnalysis().getMySnackCount()).isEqualTo(snackAnalysis.getMySnackCount());

        assertThat(findSurveyReport.getActivityAnalysis().getMyActivityGroup()).isEqualTo(activityAnalysis.getMyActivityGroup());
        assertThat(findSurveyReport.getActivityAnalysis().getMyActivityGroup()).isEqualTo(activityAnalysis.getMyActivityGroup());

        assertThat(findSurveyReport.getWeightAnalysis().getAvgWeight()).isEqualTo(weightAnalysis.getAvgWeight());
        assertThat(findSurveyReport.getWeightAnalysis().getMyWeightGroup()).isEqualTo(weightAnalysis.getMyWeightGroup());
        assertThat(findSurveyReport.getWeightAnalysis().getWeightInLastReport()).isEqualTo(weightAnalysis.getWeightInLastReport());


    }

    @Test
    @DisplayName("수정할 강아지가 내 강아지가 아닐 경우 400")
    public void updateDog_isNotMyDog() throws Exception {
        //given

        Recipe recipe = recipeRepository.findAll().get(0);

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

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
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/dogs/{id}", dog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("수정할 강아지가 존재하지않음 404")
    public void updateDog_dog_notFound() throws Exception {
        //given

        Recipe recipe = recipeRepository.findAll().get(0);

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Dog dog = generateDogRepresentative(member, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

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
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/dogs/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }





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
                                linkWithRel("query_surveyReport").description("설문조사 레포트 조회 링크"),
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
                                fieldWithPath("_links.query_surveyReport.href").description("설문조사 레포트 조회 링크"),
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




    private void generateSubscribe(Dog dog) {
        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .build();
        subscribeRepository.save(subscribe);

        dog.setSubscribe(subscribe);
    }




    private DogPicture generateDogPicture(int i) {
        DogPicture dogPicture = DogPicture.builder()
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return dogPictureRepository.save(dogPicture);
    }


    private Dog generateDog(int i, Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
        List<Recipe> recipes = recipeRepository.findAll();
        Dog dog = Dog.builder()
                .member(member)
                .name("샘플독" + i)
                .birth("202005")
                .startAgeMonth(startAgeMonth)
                .gender(Gender.MALE)
                .oldDog(false)
                .dogType("포메라니안")
                .dogSize(dogSize)
                .weight(new BigDecimal(weight))
                .dogActivity(new DogActivity(activitylevel, walkingCountPerWeek, walkingTimePerOneTime))
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(snackCountLevel)
                .recommendRecipe(recipes.get(0))
                .inedibleFood("NONE")
                .inedibleFoodEtc("NONE")
                .caution("NONE")
                .build();
        return dogRepository.save(dog);
    }

    private Dog generateDog(Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
        List<Recipe> recipes = recipeRepository.findAll();
        Dog dog = Dog.builder()
                .member(member)
                .name("샘플독")
                .birth("202005")
                .startAgeMonth(startAgeMonth)
                .gender(Gender.MALE)
                .oldDog(false)
                .dogType("포메라니안")
                .dogSize(dogSize)
                .weight(new BigDecimal(weight))
                .dogActivity(new DogActivity(activitylevel, walkingCountPerWeek, walkingTimePerOneTime))
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(snackCountLevel)
                .recommendRecipe(recipes.get(0))
                .inedibleFood("NONE")
                .inedibleFoodEtc("NONE")
                .caution("NONE")
                .build();
        return dogRepository.save(dog);
    }





    //===================================================================================================

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

        SubscribeRecipe subscribeRecipe = generateSubscribeRecipe(recipe1, subscribe);
        SubscribeRecipe subscribeRecipe1 = generateSubscribeRecipe(recipe2, subscribe);
//        subscribe.addSubscribeRecipe(subscribeRecipe);
//        subscribe.addSubscribeRecipe(subscribeRecipe1);

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
        dog.setSurveyReport(surveyReport);
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