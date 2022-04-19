package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.SurveyReportRepository;
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

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class SurveyReportApiControllerTest extends BaseTest {

    @Autowired
    SurveyReportRepository surveyReportRepository;

    @Autowired
    AppProperties appProperties;

    @Test
    @DisplayName("정상적으로 설문조사 리포트 조회하는 테스트")
    public void query_surveyReport() throws Exception {
       //given

        SurveyReport findSurveyReport = surveyReportRepository.findAll().get(0);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/surveyReports/{id}", findSurveyReport.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_surveyReport",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("surveyReport-result").description("설문조사 리포트 결과 화면 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("해당 설문 리포트 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("lastSurveyDate").description("마지막 설문조사 날짜"),
                                fieldWithPath("myDogName").description("설문 강아지 이름"),
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
                                fieldWithPath("_links.surveyReport-result.href").description("설문조사 리포트 결과 화면 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 설문리포트가 없는 경우 not found 나오는 테스트")
    public void query_surveyReport_not_found() throws Exception {
       //given

       //when & then
        mockMvc.perform(get("/api/surveyReports/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 설문 리포트의 결과 조회하는 테스트")
    public void query_surveyResult() throws Exception {
       //given
        SurveyReport findSurveyReport = surveyReportRepository.findAll().get(0);
       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/surveyReports/{id}/result", findSurveyReport.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_surveyResult",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-orderSheet-subscribe").description("주문서 작성에 필요한 값 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("해당 설문 리포트 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("dogName").description("강아지 이름"),
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
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-orderSheet-subscribe.href").description("주문서 작성에 필요한 값 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 리포트 결과가 없는 경우 not found 나오는 테스트")
    public void query_surveyResult_not_found() throws Exception {
       //given

       //when & then
        mockMvc.perform(get("/api/surveyReports/999999/result")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
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