package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.UpdateArticlesRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.ArticleRepository;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ArticleAdminControllerTest extends BaseTest {


    @Autowired
    AppProperties appProperties;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    EntityManager em;


    @Test
    @DisplayName("정상적으로 아티클 정보 조회하는 테스트")
    public void queryArticles() throws Exception {
        //given
        IntStream.range(1,5).forEach(i -> {
            generateBlog(i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateNoticeLeaked(i);
        });


        //when & then
        mockMvc.perform(get("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("blogTitlesDtos",hasSize(6)))
                .andDo(document("admin_query_articles",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_article").description("아티클 수정 요청 링크"),
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
                                fieldWithPath("articlesAdminDtos[0].articleId").description("아티클 인덱스 id"),
                                fieldWithPath("articlesAdminDtos[0].articleNumber").description("아티클 번호 [1 or 2]"),
                                fieldWithPath("articlesAdminDtos[0].blogId").description("블로그 인덱스 id"),
                                fieldWithPath("articlesAdminDtos[0].blogTitle").description("블로그 제목"),
                                fieldWithPath("blogTitlesDtos[0].blogId").description("블로그 인덱스 id"),
                                fieldWithPath("blogTitlesDtos[0].title").description("블로그 제목"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_article.href").description("아티클 수정 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("아티클 정보 조회 시 블로그 LEAKED 만 조회하는지 테스트")
    public void queryArticles_onlyLEKAED_Blog() throws Exception {
        //given

        IntStream.range(1,5).forEach(i -> {
            generateBlogHidden(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("blogTitlesDtos", hasSize(2)))
        ;
    }

    @Test
    @DisplayName("정상적으로 아티클을 업데이트 하는 테스트")
    public void updateArticles() throws Exception {
        //given
        Blog blog1 = generateBlog(1);
        Blog blog2 = generateBlog(2);

        UpdateArticlesRequestDto requestDto = UpdateArticlesRequestDto.builder()
                .firstBlogId(blog1.getId())
                .secondBlogId(blog2.getId())
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_update_articles",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_articles").description("수정할 아티클 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("firstBlogId").description("1번 아티클로 설정할 블로그 id"),
                                fieldWithPath("secondBlogId").description("2번 아티클로 설정할 블로그 id")

                        ), responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_articles.href").description("수정할 아티클 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("아티클로 설정할 블로그가 존재하지 않을 경우 404")
    public void updateArticles_blog_404() throws Exception {
        //given

        UpdateArticlesRequestDto requestDto = UpdateArticlesRequestDto.builder()
                .firstBlogId(9999L)
                .secondBlogId(999L)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("아티클로 설정할 블로그가 공지사항일 경우 400")
    public void updateArticles_blog_Notice_400() throws Exception {
        //given
        Blog blog1 = generateBlog(1);
        Blog notice = generateNoticeLeaked(2);

        UpdateArticlesRequestDto requestDto = UpdateArticlesRequestDto.builder()
                .firstBlogId(blog1.getId())
                .secondBlogId(notice.getId())
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아티클로 설정할 블로그가 hidden 일 경우 400")
    public void updateArticles_blog_hidden_404() throws Exception {
        //given
        Blog blog1 = generateBlogHidden(1);
        Blog blog2 = generateBlogHidden(2);

        UpdateArticlesRequestDto requestDto = UpdateArticlesRequestDto.builder()
                .firstBlogId(blog1.getId())
                .secondBlogId(blog2.getId())
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아티클로 설정할 블로그가 서로 같을 경우 400")
    public void updateArticles_duplicateBlog_400() throws Exception {
        //given

        Blog blog1 = generateBlog(1);

        UpdateArticlesRequestDto requestDto = UpdateArticlesRequestDto.builder()
                .firstBlogId(blog1.getId())
                .secondBlogId(blog1.getId())
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/articles")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
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





    private Blog generateBlog(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.LEAKED)
                .title("제목" + i)
                .category(BlogCategory.HEALTH)
                .contents("컨텐츠 내용")
                .build();
        return blogRepository.save(blog);
    }

    private Blog generateBlogHidden(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.HIDDEN)
                .title("제목" + i)
                .category(BlogCategory.HEALTH)
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