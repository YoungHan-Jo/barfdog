package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.AdminBlogImageDto;
import com.bi.barfdog.api.blogDto.BlogSaveDto;
import com.bi.barfdog.api.blogDto.UpdateBlogRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.blog.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.*;
import com.bi.barfdog.repository.article.ArticleRepository;
import com.bi.barfdog.repository.blog.BlogImageRepository;
import com.bi.barfdog.repository.blog.BlogRepository;
import com.bi.barfdog.repository.blog.BlogThumbnailRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.service.DogService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class BlogAdminControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BlogImageRepository blogImageRepository;

    @Autowired
    BlogThumbnailRepository blogThumbnailRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    DogRepository dogRepository;

    @Autowired
    SurveyReportRepository surveyReportRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    SubscribeRecipeRepository subscribeRecipeRepository;
    @Autowired
    SubscribeRepository subscribeRepository;

    @Autowired
    DogService dogService;

    @Autowired
    EntityManager em;



    @Test
    @DisplayName("정상적으로 블로그이미지 업로드하는 테스트")
    public void uploadBlogThumbnail() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/blogs/thumbnail/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("url").exists())
                .andDo(document("upload_blogThumbnail",
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
                                partWithName("file").description("업로드할 블로그 썸네일 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("블로그 썸네일 인덱스 id"),
                                fieldWithPath("url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("업로드할 블로그썸네일 파일이 없을 경우 400")
    public void uploadBlogThumbnail_noFile() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/blogs/thumbnail/upload")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    @DisplayName("정상적으로 블로그이미지 업로드하는 테스트")
    public void uploadBlogImage() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/blogs/image/upload")
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
        mockMvc.perform(multipart("/api/admin/blogs/image/upload")
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

        BlogThumbnail blogThumbnail = generateBlogThumbnail(1);


        String title = "건강 블로그 제목";
        BlogStatus status = BlogStatus.LEAKED;
        BlogCategory category = BlogCategory.HEALTH;
        String contents = "컨텐츠 내용";
        BlogSaveDto requestDto = BlogSaveDto.builder()
                .status(status)
                .title(title)
                .category(category)
                .contents(contents)
                .blogThumbnailId(blogThumbnail.getId())
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
                                fieldWithPath("blogThumbnailId").description("블로그 썸네일 id"),
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

        BlogThumbnail findThumbnail = blogThumbnailRepository.findById(blogThumbnail.getId()).get();
        assertThat(findThumbnail.getBlog().getId()).isEqualTo(findBlog.getId());

    }

    @Test
    @DisplayName("블로그 생성 시 이미지 없어도 등록하는 테스트")
    public void createBlog_no_arrays() throws Exception {
        //given
        List<Long> blogImageIdList = getBlogImgIdList();

        BlogThumbnail blogThumbnail = generateBlogThumbnail(1);

        String title = "건강 블로그 제목";
        BlogStatus status = BlogStatus.LEAKED;
        BlogCategory category = BlogCategory.HEALTH;
        String contents = "컨텐츠 내용";
        BlogSaveDto requestDto = BlogSaveDto.builder()
                .status(status)
                .title(title)
                .category(category)
                .contents(contents)
                .blogThumbnailId(blogThumbnail.getId())
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
    @DisplayName("블로그 생성 시 썸네일 id 없으면 400")
    public void createBlog_no_thumbnail_400() throws Exception {
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
                .andExpect(status().isBadRequest());

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
    @DisplayName("존재하지 않는 이미지 id일 경우")
    public void createBlog_wrong_image_id() throws Exception {
        //given

        List<Long> blogImageIdList = getBlogImgIdList();
        blogImageIdList.add(100L);

        BlogThumbnail blogThumbnail = generateBlogThumbnail(1);

        String title = "건강 블로그 제목";
        BlogStatus status = BlogStatus.LEAKED;
        BlogCategory category = BlogCategory.HEALTH;
        String contents = "컨텐츠 내용";
        BlogSaveDto requestDto = BlogSaveDto.builder()
                .status(status)
                .title(title)
                .category(category)
                .contents(contents)
                .blogThumbnailId(blogThumbnail.getId())
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

        IntStream.range(1,3).forEach(i -> {
            generateNoticeLeaked(i);
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
                .andExpect(jsonPath("page.totalElements").value(17))
                .andDo(document("admin_query_blogs",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("admin_query_articles").description("수정할 아티클 리스트 조회 링크"),
                                linkWithRel("create_blog").description("블로그 생성 링크"),
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
                                fieldWithPath("_links.admin_query_articles.href").description("수정할 아티클 리스트 조회 링크"),
                                fieldWithPath("_links.create_blog.href").description("블로그 생성 링크"),
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
    @DisplayName("정상적으로 수정할 블로그 정보 조회하는 테스트")
    public void queryBlog() throws Exception {
        //given
        Blog blog = generateBlog(1);

        IntStream.range(1,6).forEach(i -> {
            generateBlogImage(i, blog);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/blogs/{id}", blog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_blog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_update_blog").description("블로그 수정 요청 링크"),
                                linkWithRel("upload_blogImage").description("이미지 업로드 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 블로그 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("blogAdminDto.id").description("블로그 인덱스 id"),
                                fieldWithPath("blogAdminDto.status").description("블로그 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("blogAdminDto.title").description("블로그 제목"),
                                fieldWithPath("blogAdminDto.category").description("블로그 카테고리 [NUTRITION,HEALTH,LIFE]"),
                                fieldWithPath("blogAdminDto.thumbnailId").description("썸네일 인덱스 id"),
                                fieldWithPath("blogAdminDto.filename").description("썸네일 파일 이름"),
                                fieldWithPath("blogAdminDto.thumbnailUrl").description("썸네일 url"),
                                fieldWithPath("blogAdminDto.contents").description("블로그 내용"),
                                fieldWithPath("adminBlogImageDtos[0].blogImageId").description("블로그 이미지 인덱스 id"),
                                fieldWithPath("adminBlogImageDtos[0].filename").description("파일 이름"),
                                fieldWithPath("adminBlogImageDtos[0].url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_update_blog.href").description("블로그 수정 요청 링크"),
                                fieldWithPath("_links.upload_blogImage.href").description("이미지 업로드 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("수정할 블로그가 존재하지 않을 경우 404")
    public void queryBlog_notFound() throws Exception {
        //given

        Blog blog = generateBlog(1);

        IntStream.range(1,6).forEach(i -> {
            generateBlogImage(i, blog);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/blogs/9999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 블로그를 수정하는 테스트")
    public void updateBlog() throws Exception {
        //given
        Blog blog = generateBlog(1);
        BlogThumbnail newThumbnail = generateBlogThumbnail(1);

        BlogImage original1 = generateBlogImage(1, blog);
        BlogImage original2 = generateBlogImage(2, blog);
        BlogImage original3 = generateBlogImage(3, blog);
        BlogImage delete1 = generateBlogImage(4, blog);
        BlogImage delete2 = generateBlogImage(5, blog);

        BlogImage add1 = generateBlogImage(1);
        BlogImage add2 = generateBlogImage(2);
        BlogImage add3 = generateBlogImage(3);

        List<Long> addImageIdList = new ArrayList<>();
        List<Long> deleteImageIdList = new ArrayList<>();

        addImageIdList.add(add1.getId());
        addImageIdList.add(add2.getId());
        addImageIdList.add(add3.getId());

        deleteImageIdList.add(delete1.getId());
        deleteImageIdList.add(delete2.getId());

        UpdateBlogRequestDto requestDto = UpdateBlogRequestDto.builder()
                .status(BlogStatus.LEAKED)
                .title("업데이트 블로그")
                .category(BlogCategory.NUTRITION)
                .contents("업데이트 컨텐츠 내용")
                .thumbnailId(newThumbnail.getId())
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/blogs/{id}",blog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_update_blog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_blog").description("수정할 블로그 하나 조회하는 링크"),
                                linkWithRel("admin_query_blogs").description("블로그 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("수정할 블로그 인덱스 id")
                        ),
                        requestFields(
                                fieldWithPath("status").description("수정할 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("title").description("수정할 제목"),
                                fieldWithPath("category").description("수정할 카테고리 [NUTRITION,HEALTH,LIFE]"),
                                fieldWithPath("contents").description("수정할 내용"),
                                fieldWithPath("thumbnailId").description("수정할 썸네일 id"),
                                fieldWithPath("addImageIdList").description("추가할 블로그 이미지 인덱스 id 리스트"),
                                fieldWithPath("deleteImageIdList").description("삭제할 블로그 이미지 인덱스 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_blog.href").description("수정할 블로그 하나 조회하는 링크"),
                                fieldWithPath("_links.admin_query_blogs.href").description("블로그 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Optional<BlogImage> optional1 = blogImageRepository.findById(delete1.getId());
        Optional<BlogImage> optional2 = blogImageRepository.findById(delete2.getId());

        assertThat(optional1.isPresent()).isFalse();
        assertThat(optional2.isPresent()).isFalse();

        BlogImage addedImage1 = blogImageRepository.findById(add1.getId()).get();
        BlogImage addedImage2 = blogImageRepository.findById(add2.getId()).get();
        BlogImage addedImage3 = blogImageRepository.findById(add3.getId()).get();

        assertThat(addedImage1.getBlog().getId()).isEqualTo(blog.getId());
        assertThat(addedImage2.getBlog().getId()).isEqualTo(blog.getId());
        assertThat(addedImage3.getBlog().getId()).isEqualTo(blog.getId());

        Blog findBlog = blogRepository.findById(blog.getId()).get();
        assertThat(findBlog.getBlogThumbnail().getId()).isEqualTo(newThumbnail.getId());


    }

    @Test
    @DisplayName("블로그 수정 시 파일 변화 없어도 성공하는 테스트")
    public void updateBlog_image() throws Exception {
        //given
        Blog blog = generateBlog(1);

        BlogImage original1 = generateBlogImage(1, blog);
        BlogImage original2 = generateBlogImage(2, blog);
        BlogImage original3 = generateBlogImage(3, blog);
        BlogImage original4 = generateBlogImage(4, blog);
        BlogImage original5 = generateBlogImage(5, blog);

        List<Long> addImageIdList = new ArrayList<>();
        List<Long> deleteImageIdList = new ArrayList<>();

        String title = "업데이트 블로그";
        String contents = "업데이트 컨텐츠 내용";
        UpdateBlogRequestDto requestDto = UpdateBlogRequestDto.builder()
                .status(BlogStatus.LEAKED)
                .title(title)
                .category(BlogCategory.NUTRITION)
                .contents(contents)
                .thumbnailId(blog.getBlogThumbnail().getId())
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/blogs/{id}",blog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        List<AdminBlogImageDto> adminDtoByBlogId = blogImageRepository.findAdminDtoByBlogId(blog.getId());
        assertThat(adminDtoByBlogId.size()).isEqualTo(5);

        Blog findBlog = blogRepository.findById(blog.getId()).get();
        assertThat(findBlog.getTitle()).isEqualTo(title);
        assertThat(findBlog.getContents()).isEqualTo(contents);


    }

    @Test
    @DisplayName("블로그 수정시 값 부족할 경우 400 나오는 테스트")
    public void updateBlog_400() throws Exception {
        //given
        Blog blog = generateBlog(1);

        BlogImage original1 = generateBlogImage(1, blog);
        BlogImage original2 = generateBlogImage(2, blog);
        BlogImage original3 = generateBlogImage(3, blog);
        BlogImage delete1 = generateBlogImage(4, blog);
        BlogImage delete2 = generateBlogImage(5, blog);

        BlogImage add1 = generateBlogImage(1);
        BlogImage add2 = generateBlogImage(2);
        BlogImage add3 = generateBlogImage(3);

        List<Long> addImageIdList = new ArrayList<>();
        List<Long> deleteImageIdList = new ArrayList<>();

        addImageIdList.add(add1.getId());
        addImageIdList.add(add2.getId());
        addImageIdList.add(add3.getId());

        deleteImageIdList.add(delete1.getId());
        deleteImageIdList.add(delete2.getId());

        UpdateBlogRequestDto requestDto = UpdateBlogRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/blogs/{id}",blog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정할 블로그가 존재하지 않을 경우 404")
    public void updateBlog_not_found() throws Exception {
        //given
        BlogImage add1 = generateBlogImage(1);
        BlogImage add2 = generateBlogImage(2);
        BlogImage add3 = generateBlogImage(3);

        List<Long> addImageIdList = new ArrayList<>();
        List<Long> deleteImageIdList = new ArrayList<>();

        addImageIdList.add(add1.getId());
        addImageIdList.add(add2.getId());
        addImageIdList.add(add3.getId());

        BlogThumbnail blogThumbnail = generateBlogThumbnail(1);

        UpdateBlogRequestDto requestDto = UpdateBlogRequestDto.builder()
                .status(BlogStatus.LEAKED)
                .title("업데이트 블로그")
                .category(BlogCategory.NUTRITION)
                .contents("업데이트 컨텐츠 내용")
                .thumbnailId(blogThumbnail.getId())
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/blogs/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 블로그 삭제하는 테스트")
    public void deleteBlog() throws Exception {
        //given
        Blog blog = generateBlog(1);

        BlogThumbnail thumbnail = blog.getBlogThumbnail();

        BlogImage original1 = generateBlogImage(1, blog);
        BlogImage original2 = generateBlogImage(2, blog);
        BlogImage original3 = generateBlogImage(3, blog);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/blogs/{id}", blog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_delete_blog",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_blogs").description("블로그 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 블로그 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_blogs.href").description("블로그 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Optional<Blog> optionalBlog = blogRepository.findById(blog.getId());
        assertThat(optionalBlog.isPresent()).isFalse();

        Optional<BlogImage> optionalBlogImage1 = blogImageRepository.findById(original1.getId());
        Optional<BlogImage> optionalBlogImage2 = blogImageRepository.findById(original2.getId());
        Optional<BlogImage> optionalBlogImage3 = blogImageRepository.findById(original3.getId());
        assertThat(optionalBlogImage1.isPresent()).isFalse();
        assertThat(optionalBlogImage2.isPresent()).isFalse();
        assertThat(optionalBlogImage3.isPresent()).isFalse();

        Optional<BlogThumbnail> optionalBlogThumbnail = blogThumbnailRepository.findById(thumbnail.getId());
        assertThat(optionalBlogThumbnail.isPresent()).isFalse();

    }

    @Test
    @DisplayName("삭제할 블로그가 존재하지 않을 경우 404")
    public void deleteBlog_not_found() throws Exception {
        //given

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/blogs/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }

    @Test
    @DisplayName("삭제할 블로그가 아티클로 설정되어있을 경우 400")
    public void deleteBlog_isArticle_400() throws Exception {
        //given
        createBlogsAndArticles();

        Article article = articleRepository.findByNumber(1).get();

        Blog blog = article.getBlog();

        BlogImage original1 = generateBlogImage(1, blog);
        BlogImage original2 = generateBlogImage(2, blog);
        BlogImage original3 = generateBlogImage(3, blog);


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/blogs/{id}", blog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }


    private void createBlogsAndArticles() {
        Blog blog1 = makeBlog(1);
        Blog blog2 = makeBlog(2);


        Article article1 = Article.builder()
                .number(1)
                .blog(blog1)
                .build();

        Article article2 = Article.builder()
                .number(2)
                .blog(blog2)
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);
    }

    private Blog makeBlog(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.LEAKED)
                .title("블로그" + i)
                .category(BlogCategory.HEALTH)
                .contents("블로그 내용" + i)
                .build();

        return blogRepository.save(blog);
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

    private Blog generateBlogHidden(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.HIDDEN)
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

    private BlogImage generateBlogImage(int i, Blog blog) {
        BlogImage blogImage = BlogImage.builder()
                .blog(blog)
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

    private BlogThumbnail generateBlogThumbnail(int i) {
        BlogThumbnail blogThumbnail = BlogThumbnail.builder()
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();

        return blogThumbnailRepository.save(blogThumbnail);
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