package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class AdminApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("정상적으로 관리자 멤버 리스트 조회하는 테스트")
    public void queryMembers() throws Exception {
       //given

        IntStream.range(1,29).forEach(i ->{
            generateMember(i);
        });

        QueryMembersCond cond = QueryMembersCond.builder()
                .from(LocalDate.of(2020, 01, 01))
                .to(LocalDate.now())
                .build();

        //when & then
        mockMvc.perform(get("/api/admin/members")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .content(objectMapper.writeValueAsString(cond)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_members",
                        links(
                                linkWithRel("first").description("self 링크"),
                                linkWithRel("prev").description("메인 배너 리스트 호출 링크"),
                                linkWithRel("self").description("해당 배너 호출 링크"),
                                linkWithRel("next").description("배너 업데이트하기"),
                                linkWithRel("last").description("해당 API 관련 문서 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        requestFields(
                                fieldWithPath("email").description("검색할 유저 email"),
                                fieldWithPath("name").description("검색할 유저 이름"),
                                fieldWithPath("from").description("가입날짜 'yyyy.MM.dd' 부터 "),
                                fieldWithPath("to").description("가입날짜 'yyyy.MM.dd' 까지 ")

                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryMembersDtoList[0].id").description("유저 인덱스 번호"),
                                fieldWithPath("_embedded.queryMembersDtoList[0].grade").description("유저 등급 [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]"),
                                fieldWithPath("_embedded.queryMembersDtoList[0].name").description("유저 이름"),
                                fieldWithPath("_embedded.queryMembersDtoList[0].email").description("유저 이메일"),
                                fieldWithPath("_embedded.queryMembersDtoList[0].phoneNumber").description("전화번호"),
                                fieldWithPath("_embedded.queryMembersDtoList[0].dogName").description("대표 강아지 이름 [없으면 null]"),
                                fieldWithPath("_embedded.queryMembersDtoList[0].accumulatedAmount").description("누적 결제 금액"),
                                fieldWithPath("_embedded.queryMembersDtoList[0].subscribe").description("구독 여부 [true/false]"),
                                fieldWithPath("_embedded.queryMembersDtoList[0]._links.query_member.href").description("유저 상세보기 링크"),
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
    @DisplayName("페이징 정보 없을 경우 0페이지 20개 조회하는 테스트")
    public void queryMembers_pageable_empty() throws Exception {
        //given

        IntStream.range(1,29).forEach(i ->{
            generateMember(i);
        });

        QueryMembersCond cond = QueryMembersCond.builder()
                .from(LocalDate.of(2020, 01, 01))
                .to(LocalDate.now())
                .build();

        //when & then
        mockMvc.perform(get("/api/admin/members")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(cond)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryMembersDtoList",hasSize(20)))
                .andExpect(jsonPath("page.number").value(0))
        ;
    }

    @Test
    @DisplayName("회원 리스트 조회 시 파라미터 값이 부족할 경우 bad request")
    public void queryMembers_bad_request() throws Exception {
        //given

        IntStream.range(1,29).forEach(i ->{
            generateMember(i);
        });

        QueryMembersCond cond = QueryMembersCond.builder()
                .build();

        //when & then
        mockMvc.perform(get("/api/admin/members")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(cond)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("정상적으로 회원 정보 조회하는 테스트")
    public void queryMember() throws Exception {
       //given

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        //when & then
        mockMvc.perform(get("/api/admin/members/{id}", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

    }








    private void generateMember(int i) {
        Member member = Member.builder()
                .email("email@gmail.com" + i)
                .name("일반 회원" + i)
                .password("1234")
                .phoneNumber("010123455"+i)
                .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                .birthday("19991201")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(Grade.BRONZE)
                .reward(0)
                .accumulatedAmount(0)
                .firstReward(new FirstReward(false, false))
                .roles("USER")
                .build();

        memberRepository.save(member);
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