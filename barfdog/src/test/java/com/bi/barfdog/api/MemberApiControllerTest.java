package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.FindPasswordRequestDto;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Agreement;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class MemberApiControllerTest extends BaseTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("정상적으로 아이디 찾는 테스트")
    public void findEmail() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("name", sampleMember.getName());
        params.add("phoneNumber", sampleMember.getPhoneNumber());

        //when & then
        mockMvc.perform(get("/api/members/email")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(sampleMember.getEmail()))
                .andDo(document("find_email",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("login").description("로그인 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParameters(
                                parameterWithName("name").description("이름"),
                                parameterWithName("phoneNumber").description("휴대폰 번호 '010xxxxxxxx' -없는 문자열")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("provider").description("sns 로그인 제공사 / 없으면 Null"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.login.href").description("로그인 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("찾을 아이디가 없을 경우 not found 나오는 테스트")
    public void findEmail_Not_Found() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("name", "jo young han ");
        params.add("phoneNumber", sampleMember.getPhoneNumber());

        //when & then
        mockMvc.perform(get("/api/members/email")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("정상적으로 비밀번호 찾는 테스트")
    public void findPassword() throws Exception {
       //Given
        Member sampleMember = generateSampleMember();

        FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder()
                .email(sampleMember.getEmail())
                .name(sampleMember.getName())
                .phoneNumber(sampleMember.getPhoneNumber())
                .build();

        //when & then
        mockMvc.perform(put("/api/members/temporaryPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find_password",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("login").description("로그인 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("이메일 주소"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("phoneNumber").description("휴대전화 번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("responseCode").description("응답코드 (200 이외의 값이면 다이렉트센드 내부 에러)"),
                                fieldWithPath("status").description("다이렉트 센드 상태 코드"),
                                fieldWithPath("msg").description("다이렉트 센드 상태 메시지"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.login.href").description("로그인 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 찾기 중 입력값이 부족할때 bad request 나오는 테스트")
    public void findPassword_bad_request() throws Exception {
        //Given
        FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder().build();

        //when & then
        mockMvc.perform(put("/api/members/temporaryPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 찾는 중 일치하는 회원이 존재하지 않을 경우 not found 나오는 테스트")
    public void findPassword_not_found() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        FindPasswordRequestDto requestDto = FindPasswordRequestDto.builder()
                .email("jyh@gmail.com")
                .name(sampleMember.getName())
                .phoneNumber(sampleMember.getPhoneNumber())
                .build();

        //when & then
        mockMvc.perform(put("/api/members/temporaryPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }
    







    private Member generateSampleMember() {
        Member member = Member.builder()
                .name("바프독")
                .email("sample4494@gmail.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .phoneNumber("01099038544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("20000521")
                .gender(Gender.FEMALE)
                .agreement(new Agreement(true, true, true, true, true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .roles("USER")
                .provider("naver")
                .build();

        return memberRepository.save(member);
    }

}