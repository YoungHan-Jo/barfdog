package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.BlogSaveDto;
import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.api.memberDto.UpdateBirthdayRequestDto;
import com.bi.barfdog.api.memberDto.UpdateGradeRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogImage;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.BlogImageRepository;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.repository.MemberRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
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
public class AdminApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BlogImageRepository blogImageRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    EntityManager em;

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
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
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
                                fieldWithPath("email").type("String").description("검색할 유저 email"),
                                fieldWithPath("name").type("String").description("검색할 유저 이름"),
                                fieldWithPath("from").description("가입날짜 'yyyy-MM-dd' 부터 "),
                                fieldWithPath("to").description("가입날짜 'yyyy-MM-dd' 까지 ")

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
    @DisplayName("설정한 기간이 미래일 경우 400")
    public void queryMembers_future_400() throws Exception {
        //given

        IntStream.range(1,29).forEach(i ->{
            generateMember(i);
        });

        QueryMembersCond cond = QueryMembersCond.builder()
                .from(LocalDate.of(2020, 01, 01))
                .to(LocalDate.now().plusDays(1))
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
    @DisplayName("기간 설정이 잘못되었을 경우")
    public void queryMembers_wrong_term_400() throws Exception {
        //given

        IntStream.range(1,29).forEach(i ->{
            generateMember(i);
        });

        QueryMembersCond cond = QueryMembersCond.builder()
                .to(LocalDate.of(2020, 01, 01))
                .from(LocalDate.now())
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
    @DisplayName("정상적으로 회원 한명 정보 조회하는 테스트")
    public void queryMember() throws Exception {
       //given

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/members/{id}", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_member",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_member_birth").description("회원 생일 변경 링크"),
                                linkWithRel("update_member_grade").description("회원 등급 변경 링크"),
                                linkWithRel("query_member_subscribes").description("회원 구독 정보 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("유저 인덱스")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("memberDto.id").description("회원 인덱스 id"),
                                fieldWithPath("memberDto.name").description("회원 이름"),
                                fieldWithPath("memberDto.email").description("회원 이메일"),
                                fieldWithPath("memberDto.address.zipcode").description("우편 번호"),
                                fieldWithPath("memberDto.address.city").description("시/도"),
                                fieldWithPath("memberDto.address.street").description("도로명주소"),
                                fieldWithPath("memberDto.address.detailAddress").description("상세 주소"),
                                fieldWithPath("memberDto.phoneNumber").description("휴대전화 번호"),
                                fieldWithPath("memberDto.birthday").description("생일"),
                                fieldWithPath("memberDto.accumulatedAmount").description("누적 결제 금액"),
                                fieldWithPath("memberDto.grade").description("등급 [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]"),
                                fieldWithPath("memberDto.subscribe").description("정기구독 여부 [true/false]"),
                                fieldWithPath("memberDto.accumulatedSubscribe").description("누적 구독횟수"),
                                fieldWithPath("memberDto.lastLoginDate").description("마지막 로그인 날짜"),
                                fieldWithPath("memberDto.longUnconnected").description("장기 미접속 여부 [true/false]"),
                                fieldWithPath("memberDto.withdrawal").description("탈퇴 여부 [true/false]"),
                                fieldWithPath("dogNames").description("반려견 이름 리스트 [0번 인덱스가 대표견 이름]"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_member_birth.href").description("회원 생일 변경 링크"),
                                fieldWithPath("_links.update_member_grade.href").description("회원 등급 변경 링크"),
                                fieldWithPath("_links.query_member_subscribes.href").description("회원 구독 정보 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 회원이 없을 경우 404")
    public void queryMember_notFound() throws Exception {
        //given

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/members/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("정상적으로 회원 생일 변경")
    public void update_member_birthday() throws Exception {
       //given

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        UpdateBirthdayRequestDto requestDto = UpdateBirthdayRequestDto.builder()
                .birthday(LocalDate.of(1993, 5, 21))
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/members/{id}/birthday", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_memberBirthday",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_member").description("회원 정보 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("유저 인덱스")
                        ),
                        requestFields(
                                fieldWithPath("birthday").description("수정할 생일 'yyyy-MM-dd'")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_member.href").description("회원 정보 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(admin.getId()).get();
        String newBirthday = requestDto.getBirthday().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(findMember.getBirthday()).isEqualTo(newBirthday);


    }

    @Test
    @DisplayName("생일 변경할 유저가 존재하지 않을 경우 404")
    public void update_member_birthday_404() throws Exception {
        //given

        UpdateBirthdayRequestDto requestDto = UpdateBirthdayRequestDto.builder()
                .birthday(LocalDate.of(1993, 5, 21))
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/members/999999/birthday")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("회원 생일 변경 시 요청 파라미터 없음")
    public void update_member_birthday_empty_request_400() throws Exception {
        //given

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        UpdateBirthdayRequestDto requestDto = UpdateBirthdayRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/members/{id}/birthday", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("회원 생일 변경 시 생일이 미래일 경우")
    public void update_member_birthday_future_birthday_400() throws Exception {
        //given

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        UpdateBirthdayRequestDto requestDto = UpdateBirthdayRequestDto.builder()
                .birthday(LocalDate.now().plusDays(1))
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/members/{id}/birthday", admin.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
    
    @Test
    @DisplayName("정상적으로 회원 등급 변경하는 테스트")
    public void update_grade() throws Exception {
       //given

        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Grade newGrade = Grade.GOLD;
        UpdateGradeRequestDto requestDto = UpdateGradeRequestDto.builder()
                .grade(newGrade)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/members/{id}/grade", user.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_memberGrade",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_member").description("회원 정보 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("유저 인덱스")
                        ),
                        requestFields(
                                fieldWithPath("grade").description("수정할 등급 [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_member.href").description("회원 정보 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(user.getId()).get();
        assertThat(findMember.getGrade()).isEqualTo(newGrade);

    }

    @Test
    @DisplayName("등급 변경할 회원이 없음")
    public void update_grade_user_notFound() throws Exception {
        //given

        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Grade newGrade = Grade.GOLD;
        UpdateGradeRequestDto requestDto = UpdateGradeRequestDto.builder()
                .grade(newGrade)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/members/999999/grade")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원 등급 변경 시 요청정보 없음 400")
    public void update_grade_emptyRequest() throws Exception {
        //given

        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        UpdateGradeRequestDto requestDto = UpdateGradeRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/members/{id}/grade",user.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    
    
    
    
    
    
    


    
    
    
    
    
    
    
    
    @Test
    @DisplayName("정상적으로 블로그이미지 업로드하는 테스트")
    public void uploadBlogImage() throws Exception {
       //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
       
       //when & then
        mockMvc.perform(multipart("/api/admin/blogImage/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("url").exists())
                .andDo(document("upload_blogImage",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParts(
                                partWithName("file").description("업로드할 블로그 이미지 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("블로그 이미지 인덱스 id"),
                                fieldWithPath("url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("업로드할 블로그이미지 파일이 없을 경우 400")
    public void uploadBlogImage_noFile() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/blogImage/upload")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
    
    @Test
    @DisplayName("정상적으로 블로그 생성하는 테스트")
    public void createBlog() throws Exception {
       //given

        List<Long> blogImageIdList = getBlogImgIdList();

        String title = "건강 블로그 제목";
        BlogStatus status = BlogStatus.LEAKED;
        BlogCategory category = BlogCategory.HEALTH;
        String contents = "컨텐츠 내용";
        BlogSaveDto requestDto = BlogSaveDto.builder()
                .status(status)
                .title(title)
                .category(category)
                .contents(contents)
                .blogImageIdList(blogImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/blogs")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_blog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_blogs").description("블로그 리스트 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("status").description("블로그 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("title").description("블로그 제목"),
                                fieldWithPath("category").description("블로그 카테고리 [NUTRITION,HEALTH,LIFE]"),
                                fieldWithPath("contents").description("블로그 컨텐츠 html"),
                                fieldWithPath("blogImageIdList").description("블로그 이미지 ID 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_blogs.href").description("블로그 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Blog findBlog = blogRepository.findByTitle(title).get();

        assertThat(findBlog.getStatus()).isEqualTo(status);
        assertThat(findBlog.getTitle()).isEqualTo(title);
        assertThat(findBlog.getCategory()).isEqualTo(category);
        assertThat(findBlog.getContents()).isEqualTo(contents);

        for (Long id : blogImageIdList) {
            BlogImage blogImage = blogImageRepository.findById(id).get();
            assertThat(blogImage.getBlog().getId()).isEqualTo(findBlog.getId());
        }
        
    }

    @Test
    @DisplayName("블로그 생성 시 이미지 없어도 등록하는 테스트")
    public void createBlog_no_arrays() throws Exception {
        //given
        List<Long> blogImageIdList = getBlogImgIdList();

        String title = "건강 블로그 제목";
        BlogStatus status = BlogStatus.LEAKED;
        BlogCategory category = BlogCategory.HEALTH;
        String contents = "컨텐츠 내용";
        BlogSaveDto requestDto = BlogSaveDto.builder()
                .status(status)
                .title(title)
                .category(category)
                .contents(contents)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/blogs")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        em.flush();
        em.clear();

        Blog findBlog = blogRepository.findByTitle(title).get();

        assertThat(findBlog.getStatus()).isEqualTo(status);
        assertThat(findBlog.getTitle()).isEqualTo(title);
        assertThat(findBlog.getCategory()).isEqualTo(category);
        assertThat(findBlog.getContents()).isEqualTo(contents);

    }

    @Test
    @DisplayName("블로그 생성 시 값 부족할 경우 400")
    public void createBlog_emptyParams_400() throws Exception {
        //given
        List<Long> blogImageIdList = getBlogImgIdList();

        String title = "건강 블로그 제목";
        BlogStatus status = BlogStatus.LEAKED;
        BlogCategory category = BlogCategory.HEALTH;
        String contents = "컨텐츠 내용";
        BlogSaveDto requestDto = BlogSaveDto.builder()
                .status(status)
                .title(title)
                .blogImageIdList(blogImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/blogs")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("정상적으로 블로그 리스트 조회")
    public void queryBlogs() throws Exception {
       //given

        IntStream.range(1,18).forEach(i -> {
            generateBlog(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/blogs")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_blogs",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
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
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].id").description("블로그 인덱스 id"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].title").description("제목"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].createdDate").description("작성일"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].status").description("노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0]._links.query_blog.href").description("수정할 블로그 하나 조회하는 링크"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0]._links.update_blog.href").description("블로그 수정 요청하는 링크"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0]._links.delete_blog.href").description("해당 블로그 삭제 링크"),
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
    @DisplayName("페이징 정보 없어도 0페이지 20개 정상적으로 블로그 리스트 조회")
    public void queryBlogs_no_Pageable() throws Exception {
        //given

        IntStream.range(1,30).forEach(i -> {
            generateBlog(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/blogs")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryBlogsAdminDtoList", hasSize(20)))
                .andExpect(jsonPath("page.number").value(0))
        ;

    }
    
    @Test
    @DisplayName("정상적으로 아티클 정보 조회하는 테스트")
    public void queryArticles() throws Exception {
       //given
       
       //when & then
        mockMvc.perform(get("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
      
    }
    















    private Blog generateBlog(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.LEAKED)
                .title("제목" + i)
                .category(BlogCategory.HEALTH)
                .contents("컨텐츠 내용")
                .build();
        return blogRepository.save(blog);
    }


    private List<Long> getBlogImgIdList() {
        BlogImage blogImage1 = generateBlogImage(1);
        BlogImage blogImage2 = generateBlogImage(2);

        List<Long> blogImageIdList = new ArrayList<>();
        blogImageIdList.add(blogImage1.getId());
        blogImageIdList.add(blogImage2.getId());
        return blogImageIdList;
    }

    private BlogImage generateBlogImage(int i) {
        BlogImage blogImage = BlogImage.builder()
                .folder("folder")
                .filename("filename" + i)
                .build();

        return blogImageRepository.save(blogImage);
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