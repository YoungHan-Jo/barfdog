package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.repository.blog.BlogRepository;
import org.junit.Ignore;
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
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
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
public class NoticeApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;

    @Autowired
    AppProperties appProperties;

    @Autowired
    BlogRepository blogRepository;

    @Test
    @DisplayName("정상적으로 공지사항 목록 조회")
    public void queryNotices() throws Exception {
       //given

        IntStream.range(1,14).forEach(i -> {
            generateNoticeLeaked(i);
        });

        IntStream.range(1,2).forEach(i -> {
            generateNoticeHidden(i);
            generateBlog(i);
        });


       //when & then
        mockMvc.perform(get("/api/notices")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andDo(document("query_notices",
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
                                fieldWithPath("_embedded.queryNoticesDtoList[0].id").description("공지사항 인덱스 id"),
                                fieldWithPath("_embedded.queryNoticesDtoList[0].title").description("공지사항 제목"),
                                fieldWithPath("_embedded.queryNoticesDtoList[0].createdDate").description("작성날짜"),
                                fieldWithPath("_embedded.queryNoticesDtoList[0]._links.query_notice.href").description("공지사항 하나 조회 링크"),
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


    @Ignore
    @Test
    @DisplayName("정상적으로 공지사항 하나 조회")
    public void queryNotice() throws Exception {
       //given

        Blog notice1 = generateNoticeLeaked(1);
        Blog notice2 = generateNoticeLeaked(2);
        Blog notice3 = generateNoticeLeaked(3);

        IntStream.range(4,8).forEach(i -> {
            generateNoticeLeaked(i);
        });

        em.flush();
        em.clear();

        List<Blog> leakedNotices = blogRepository.findLeakedNotices();
        assertThat(leakedNotices.size()).isEqualTo(7);



        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices/{id}", notice2.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_notice",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_notices").description("공지사항 리스트 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("공지사항 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("noticeDto.id").description("현재 공지사항 인덱스 id"),
                                fieldWithPath("noticeDto.title").description("현재 공지사항 제목"),
                                fieldWithPath("noticeDto.createdDate").description("현재 공지사항 등록 날짜"),
                                fieldWithPath("noticeDto.contents").description("현재 공지사항 내용"),
                                fieldWithPath("previous.id").description("이전 글 id"),
                                fieldWithPath("previous.title").description("이전 글 제목"),
                                fieldWithPath("previous._link").description("이전 글 링크"),
                                fieldWithPath("next.id").description("다음 글 id"),
                                fieldWithPath("next.title").description("다음 글 제목"),
                                fieldWithPath("next._link").description("다음 글 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_notices.href").description("공지사항 리스트 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("공지사항 하나 조회 시 이전글 없을 경우 이전글 null")
    public void queryNotice_empty_Previous() throws Exception {
        //given

        Blog notice1 = generateNoticeLeaked(1);
        Blog notice2 = generateNoticeLeaked(2);
        Blog notice3 = generateNoticeLeaked(3);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices/{id}", notice3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("previous").isEmpty())
        ;

    }

    @Test
    @DisplayName("공지사항 하나 조회 시 다음글 없을 경우 다음글 null")
    public void queryNotice_empty_next() throws Exception {
        //given

        Blog notice1 = generateNoticeLeaked(1);
        Blog notice2 = generateNoticeLeaked(2);
        Blog notice3 = generateNoticeLeaked(3);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices/{id}", notice1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("next").isEmpty())
        ;

    }

    @Test
    @DisplayName("공지사항 하나 조회 시 이전글 다음글 모두 없을 경우 이전글 다음글 null")
    public void queryNotice_empty_nextPrev() throws Exception {
        //given

        Blog notice1 = generateNoticeLeaked(1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices/{id}", notice1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("next").isEmpty())
                .andExpect(jsonPath("previous").isEmpty())
        ;
    }

    @Test
    @DisplayName("공지사항 하나 조회 시 이전글 다음글 모두 없을 경우 이전글 다음글 null")
    public void queryNotice_notFound() throws Exception {
        //given

        Blog notice1 = generateNoticeLeaked(1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
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

    private void generateNoticeHidden(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.HIDDEN)
                .title("공지사항" + i)
                .category(BlogCategory.NOTICE)
                .contents("컨텐츠 내용")
                .build();
        blogRepository.save(blog);
    }

    private Blog generateNoticeLeaked(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.LEAKED)
                .title("공지사항" + i)
                .category(BlogCategory.NOTICE)
                .contents("컨텐츠 내용")
                .build();
        return blogRepository.save(blog);
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