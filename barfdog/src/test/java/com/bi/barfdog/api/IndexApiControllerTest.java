package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.FindPasswordRequestDto;
import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.directsend.PhoneAuthRequestDto;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Agreement;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.jwt.JwtProperties;
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
import java.nio.charset.Charset;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class IndexApiControllerTest extends BaseTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired BCryptPasswordEncoder bCryptPasswordEncoder;

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    @Test
    @DisplayName("추천인 코드ㅇ/수신동의ㅇ 적립금 4000원으로 회원 가입이 완료되는 테스트")
    public void join() throws Exception {
       //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .recommendCode(sampleMember.getMyRecommendationCode())
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("rewardPoint").value(4000))
                .andExpect(jsonPath("recommendCode").value(sampleMember.getMyRecommendationCode()))
                .andExpect(jsonPath("agreement.receiveSms").value(true))
                .andExpect(jsonPath("agreement.receiveEmail").value(true))
                .andDo(document("join",
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
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("email").description("회원 이메일(로그인 ID)"),
                                fieldWithPath("password").description("회원 비밀번호"),
                                fieldWithPath("confirmPassword").description("회원 비밀번호 확인"),
                                fieldWithPath("phoneNumber").description("휴대폰 번호 '010xxxxxxxx' -없는 문자열"),
                                fieldWithPath("address.zipcode").description("우편 번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명주소"),
                                fieldWithPath("address.detailAddress").description("상세주소"),
                                fieldWithPath("birthday").description("생년월일 'yyyymmdd' 길이 8 문자열"),
                                fieldWithPath("gender").description("성별 [MALE, FEMALE, NONE]"),
                                fieldWithPath("recommendCode").description("추천인 코드"),
                                fieldWithPath("agreement.servicePolicy").description("이용약관 동의 [true/false]"),
                                fieldWithPath("agreement.privacyPolicy").description("개인정보 제공 동의 [true/false]"),
                                fieldWithPath("agreement.receiveSms").description("sms 수신 여부 [true/false]"),
                                fieldWithPath("agreement.receiveEmail").description("email 수신 여부 [true/false]"),
                                fieldWithPath("agreement.over14YearsOld").description("14세 이상 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("회원 생성 날짜"),
                                fieldWithPath("modifiedDate").description("마지막으로 회원 수정한 날짜"),
                                fieldWithPath("id").description("회원 id 번호"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("email").description("회원 이메일(로그인 ID)"),
                                fieldWithPath("password").description("회원 비밀번호 암호화"),
                                fieldWithPath("phoneNumber").description("휴대폰 번호"),
                                fieldWithPath("address.zipcode").description("우편 번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명주소"),
                                fieldWithPath("address.detailAddress").description("상세주소"),
                                fieldWithPath("birthday").description("생년월일 'yyyymmdd' 길이 8 문자열 형식"),
                                fieldWithPath("gender").description("성별"),
                                fieldWithPath("agreement.servicePolicy").description("이용약관 동의"),
                                fieldWithPath("agreement.privacyPolicy").description("개인정보 제공 동의"),
                                fieldWithPath("agreement.receiveSms").description("sms 수신 여부"),
                                fieldWithPath("agreement.receiveEmail").description("email 수신 여부"),
                                fieldWithPath("agreement.over14YearsOld").description("14세 이상 여부"),
                                fieldWithPath("recommendCode").description("내가 추천한 사람 추천코드"),
                                fieldWithPath("myRecommendationCode").description("내 추천 코드"),
                                fieldWithPath("grade").description("등급"),
                                fieldWithPath("rewardPoint").description("적립금"),
                                fieldWithPath("lastLoginDate").description("마지막 로그인 날짜시간"),
                                fieldWithPath("roles").description("권한"),
                                fieldWithPath("provider").description("sns 로그인 제공사(네이버/카카오)"),
                                fieldWithPath("providerId").description("sns 로그인 제공사 고유 id"),
                                fieldWithPath("roleList").description("권한 리스트"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.login.href").description("로그인 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("추천인 코드ㅇ 적립금3000원으로 회원 가입이 완료되는 테스트")
    public void join_recommendCode() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .recommendCode(sampleMember.getMyRecommendationCode())
                .agreement(new Agreement(true,true,false,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("rewardPoint").value(3000))
                .andExpect(jsonPath("recommendCode").value(sampleMember.getMyRecommendationCode()))
                .andExpect(jsonPath("agreement.receiveSms").value(false))
                .andExpect(jsonPath("agreement.receiveEmail").value(true))
        ;
    }

    @Test
    @DisplayName("수신여부ㅇ 적립금1000원으로 회원 가입이 완료되는 테스트")
    public void join_receive_agree() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("rewardPoint").value(1000))
                .andExpect(jsonPath("agreement.receiveSms").value(true))
                .andExpect(jsonPath("agreement.receiveEmail").value(true));
    }

    @Test
    @DisplayName("수신여부x/추천인x 적립금 0원으로 회원 가입이 완료되는 테스트")
    public void join_no_point() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,false,false,true))
                .build();

        //when & then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("rewardPoint").value(0))
                .andExpect(jsonPath("agreement.receiveSms").value(false))
                .andExpect(jsonPath("agreement.receiveEmail").value(false));
    }

    @Test
    @DisplayName("부족한 입력값으로 회원 가입한 경우 bad request 나오는 테스트")
    public void join_Bad_Request() throws Exception {
       //Given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder().build();

        //when & then
        mockMvc.perform(post("/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 서로 다를 경우 404 bad request 나오는 테스트")
    public void join_Passwords_Different_Bad_Request() throws Exception {
        //Given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("43214321")
                .phoneNumber("01012341234")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("email 중복으로 가입할 경우 409Conflict 나오는 테스트")
    public void join_Email_Duplicate_Conflict() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email(sampleMember.getEmail())
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038544")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("휴대폰번호 중복으로 가입할 경우 409Conflict 나오는 테스트")
    public void join_PhoneNumber_Duplicate_Conflict() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber(sampleMember.getPhoneNumber())
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();

        //when & then
        mockMvc.perform(post("/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
    
    @Test
    @DisplayName("정상적으로 휴대폰 인증 번호 보내기")
    public void phoneAuth() throws Exception {
       //Given
        PhoneAuthRequestDto requestDto = PhoneAuthRequestDto.builder()
                .phoneNumber("01099038544")
                .build();

        //when & then
        mockMvc.perform(post("/join/phoneAuth")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("join_phoneAuth",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("phoneNumber").description("메일을 보낼 휴대폰 번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("authNumber").description("휴대폰 인증번호"),
                                fieldWithPath("responseCode").description("응답코드 (200 이외의 값이면 다이렉트센드 내부 에러)"),
                                fieldWithPath("status").description("다이렉트 센드 상태 코드"),
                                fieldWithPath("msg").description("다이렉트 센드 상태 메시지"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("폰 인증 번호 중복 시 Conflict 나오는 테스트")
    public void phoneAuth_Duplicate_Conflict() throws Exception {
       //Given
        Member member = generateSampleMember();

        PhoneAuthRequestDto requestDto = PhoneAuthRequestDto.builder()
                .phoneNumber(member.getPhoneNumber())
                .build();

        //when & then
        mockMvc.perform(post("/join/phoneAuth")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

//    @Test
//    @DisplayName("회원가입 휴대폰 인증 요청 중 내부 에러 발생시 500 응답하는 테스트")
//    public void phoneAuth_Internal_Error() throws Exception {
//        //Given
//        PhoneAuthRequestDto requestDto = PhoneAuthRequestDto.builder()
//                .phoneNumber("00000000000")
//                .build();
//
//        //when & then
//        mockMvc.perform(post("/join/phoneAuth")
//                        .accept(MediaTypes.HAL_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andDo(print())
//                .andExpect(status().isInternalServerError());
//    }


    @Test
    @DisplayName("정상적으로 로그인 성공 후 jwt 토큰 받는 테스트")
    public void login() throws Exception {
       //Given
        Member member = generateSampleMember();

        Member findMember = memberRepository.findById(member.getId()).get();

        System.out.println("findMember = " + findMember.getEmail());

        JwtLoginDto requestDto = JwtLoginDto.builder()
                .username(member.getEmail())
                .password("1234")
                .build();

        //when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtProperties.HEADER_STRING))
                .andDo(document("login",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("username").description("회원 이메일(로그인 ID)"),
                                fieldWithPath("password").description("회원 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName("Authorization").description("bearer 방식 JWT 토큰")
                        )
                ));
    }
    
    @Test
    @DisplayName("로그인 실패 시 Unauthorized 401 에러 나오는 테스트")
    public void login_Unauthorized() throws Exception {
       //Given
        Member member = generateSampleMember();

        Member findMember = memberRepository.findById(member.getId()).get();

        System.out.println("findMember = " + findMember.getEmail());

        JwtLoginDto requestDto = JwtLoginDto.builder()
                .username(member.getEmail())
                .password("5678")
                .build();

        //when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("정상적으로 아이디 찾는 테스트")
    public void findEmail() throws Exception {
        //Given
        Member sampleMember = generateSampleMember();

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("name", sampleMember.getName());
        params.add("phoneNumber", sampleMember.getPhoneNumber());

        //when & then
        mockMvc.perform(get("/email")
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
        mockMvc.perform(get("/email")
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
        mockMvc.perform(put("/temporaryPassword")
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
        mockMvc.perform(put("/temporaryPassword")
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
        mockMvc.perform(put("/temporaryPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }







    private Member generateSampleMember() {
        Member member = Member.builder()
                .name("샘플Member")
                .email("sample4494@gmail.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .phoneNumber("01088881111")
                .address(new Address("48060","부산시","해운대구 센텀2로 19","브리티시인터내셔널"))
                .birthday("20000521")
                .gender(Gender.FEMALE)
                .agreement(new Agreement(true, true, true, true, true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .roles("USER")
                .build();

        return memberRepository.save(member);
    }

}