package com.bi.barfdog.api;

import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.blog.*;
import com.bi.barfdog.repository.ArticleRepository;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.repository.BlogThumbnailRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class BlogApiControllerTest extends BaseTest {

    @Autowired
    BlogRepository blogRepository;
    @Autowired
    BlogThumbnailRepository blogThumbnailRepository;
    @Autowired
    ArticleRepository articleRepository;

    @Test
    @DisplayName("정상적으로 아티클 조회하는 테스트")
    public void queryArticles() throws Exception {
       //given

        Blog blog1 = generateBlog(1);
        Blog blog2 = generateBlog(2);

        generateArticle(1,blog1);
        generateArticle(2,blog2);

        //when & then
        mockMvc.perform(get("/api/blogs/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.articlesDtoList", hasSize(2)))
                .andDo(document("query_articles",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.articlesDtoList[0].id").description("블로그 id"),
                                fieldWithPath("_embedded.articlesDtoList[0].number").description("아티클 번호 [1,2]"),
                                fieldWithPath("_embedded.articlesDtoList[0].url").description("블로그 썸네일 url"),
                                fieldWithPath("_embedded.articlesDtoList[0].category").description("블로그 카테고리 [NUTRITION,HEALTH,LIFE]"),
                                fieldWithPath("_embedded.articlesDtoList[0].title").description("블로그 제목"),
                                fieldWithPath("_embedded.articlesDtoList[0].createdDate").description("블로그 등록날짜"),
                                fieldWithPath("_embedded.articlesDtoList[0]._links.query_blog.href").description("블로그 조회 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }
    
    @Test
    @DisplayName("정상적으로 전체 블로그 리스트 조회")
    public void queryBlogs() throws Exception {
       //given

        IntStream.range(1,6).forEach(i -> {
            generateBlog(i, BlogCategory.LIFE);
        });
        IntStream.range(6,11).forEach(i -> {
            generateBlog(i, BlogCategory.HEALTH);
        });
        IntStream.range(11,14).forEach(i -> {
            generateBlog(i, BlogCategory.NUTRITION);
            generateBlog(i, BlogCategory.NOTICE);
        });

       //when & then
        mockMvc.perform(get("/api/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andDo(document("query_blogs",
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryBlogsDtoList[0].id").description("블로그 id"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].category").description("블로그 카테고리"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].title").description("블로그 제목"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].contents").description("블로그 내용"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].createdDate").description("블로그 생성날짜"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].url").description("썸네일 url"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0]._links.query_blog.href").description("블로그 조회 링크"),
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
    @DisplayName("정상적으로 life 카테고리 리스트 조회")
    public void queryBlogs_LIFE() throws Exception {
       //given

        IntStream.range(1,14).forEach(i -> {
            generateBlog(i, BlogCategory.LIFE);
        });
        IntStream.range(6,11).forEach(i -> {
            generateBlog(i, BlogCategory.HEALTH);
        });
        IntStream.range(11,14).forEach(i -> {
            generateBlog(i, BlogCategory.NUTRITION);
            generateBlog(i, BlogCategory.NOTICE);
        });

        String category = "life";

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/blogs/category/{category}",category)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andDo(document("query_blogs_category",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("category").description("조회할 카테고리 [nutrition, health, life]")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryBlogsDtoList[0].id").description("블로그 id"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].category").description("블로그 카테고리 [NUTRITION,HEALTH,LIFE]"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].title").description("블로그 제목"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].contents").description("블로그 내용"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].createdDate").description("블로그 생성날짜"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0].url").description("썸네일 url"),
                                fieldWithPath("_embedded.queryBlogsDtoList[0]._links.query_blog.href").description("블로그 조회 링크"),
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
    @DisplayName("정상적으로 nutrition 카테고리 리스트 조회")
    public void queryBlogs_NUTRITION() throws Exception {
        //given

        IntStream.range(1,5).forEach(i -> {
            generateBlog(i, BlogCategory.LIFE);
        });
        IntStream.range(6,11).forEach(i -> {
            generateBlog(i, BlogCategory.HEALTH);
            generateBlog(i, BlogCategory.NOTICE);
        });
        IntStream.range(1,14).forEach(i -> {
            generateBlog(i, BlogCategory.NUTRITION);
        });

        String category = "nutrition";

        //when & then
        mockMvc.perform(get("/api/blogs/category/{category}",category)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13));

    }

    @Test
    @DisplayName("정상적으로 health 카테고리 리스트 조회")
    public void queryBlogs_HEALTH() throws Exception {
        //given

        IntStream.range(1,5).forEach(i -> {
            generateBlog(i, BlogCategory.LIFE);
        });
        IntStream.range(6,11).forEach(i -> {
            generateBlog(i, BlogCategory.NOTICE);
            generateBlog(i, BlogCategory.NUTRITION);
        });
        IntStream.range(1,14).forEach(i -> {
            generateBlog(i, BlogCategory.HEALTH);
        });

        String category = "health";

        //when & then
        mockMvc.perform(get("/api/blogs/category/{category}",category)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13));

    }

    @Test
    @DisplayName("존재하지않는 카테고리 리스트일 경우")
    public void queryBlogs_wrong_category() throws Exception {
        //given

        IntStream.range(1,5).forEach(i -> {
            generateBlog(i, BlogCategory.LIFE);
        });
        IntStream.range(6,11).forEach(i -> {
            generateBlog(i, BlogCategory.NOTICE);
            generateBlog(i, BlogCategory.NUTRITION);
        });
        IntStream.range(1,14).forEach(i -> {
            generateBlog(i, BlogCategory.HEALTH);
        });

        String category = "wrong";

        //when & then
        mockMvc.perform(get("/api/blogs/category/{category}", category)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 블로그 하나 조회하는 테스트")
    public void queryBlog() throws Exception {
       //given

        Blog blog = generateBlog(1, BlogCategory.LIFE);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/blogs/{id}", blog.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_blog",
                        links(
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("query_blogs").description("블로그 전체 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 블로그 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("블로그 id"),
                                fieldWithPath("createdDate").description("블로그 생성 날짜"),
                                fieldWithPath("title").description("블로그 제목"),
                                fieldWithPath("contents").description("블로그 내용"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.query_blogs.href").description("블로그 전체 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("조회할 블로그가 존재하지 않을 경우 404")
    public void queryBlog_notFound() throws Exception {
        //given

        Blog blog = generateBlog(1, BlogCategory.LIFE);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/blogs/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }


    








    private void generateArticle(int number, Blog blog1) {
        Article article = Article.builder()
                .number(number)
                .blog(blog1)
                .build();
        articleRepository.save(article);
    }


    private Blog generateBlog(int i) {
        BlogThumbnail thumbnail = BlogThumbnail.builder()
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        blogThumbnailRepository.save(thumbnail);

        Blog blog = Blog.builder()
                .status(BlogStatus.LEAKED)
                .title("제목" + i)
                .category(BlogCategory.HEALTH)
                .contents("컨텐츠 내용")
                .blogThumbnail(thumbnail)
                .build();
        return blogRepository.save(blog);
    }

    private Blog generateBlog(int i, BlogCategory category) {
        BlogThumbnail thumbnail = BlogThumbnail.builder()
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        blogThumbnailRepository.save(thumbnail);

        Blog blog = Blog.builder()
                .status(BlogStatus.LEAKED)
                .title("제목" + i)
                .category(category)
                .contents("컨텐츠 내용")
                .blogThumbnail(thumbnail)
                .build();
        return blogRepository.save(blog);
    }


}