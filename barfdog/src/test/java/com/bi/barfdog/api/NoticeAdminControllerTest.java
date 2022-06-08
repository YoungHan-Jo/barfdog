package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.NoticeSaveDto;
import com.bi.barfdog.api.blogDto.UpdateNoticeRequestDto;
import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogImage;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.domain.dog.ActivityLevel;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.dog.DogStatus;
import com.bi.barfdog.domain.dog.SnackCountLevel;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.*;
import com.bi.barfdog.service.DogService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class NoticeAdminControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BlogImageRepository blogImageRepository;

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
    @DisplayName("정상적으로 공지사항 등록")
    public void createNotice() throws Exception {
        //given
        List<Long> blogImageIdList = getBlogImgIdList();

        NoticeSaveDto requestDto = NoticeSaveDto.builder()
                .status(BlogStatus.LEAKED)
                .title("제목")
                .contents("내용")
                .noticeImageIdList(blogImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/notices")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_notice",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_notices").description("공지사항 리스트 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("status").description("공지사항 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("title").description("공지사항 제목"),
                                fieldWithPath("contents").description("공지사항 컨텐츠 html"),
                                fieldWithPath("noticeImageIdList").description("공지사항 이미지 ID 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_notices.href").description("공지사항 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<Blog> allNotices = blogRepository.findAllNotices();

        assertThat(allNotices.size()).isEqualTo(1);


    }

    @Test
    @DisplayName("공지사항 등록 시 값 부족하면 400에러")
    public void createNotice_bad_request() throws Exception {
        //given
        List<Long> blogImageIdList = getBlogImgIdList();

        NoticeSaveDto requestDto = NoticeSaveDto.builder()
                .status(BlogStatus.LEAKED)
                .noticeImageIdList(blogImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/notices")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("정상적으로 공지사항 등록")
    public void createNotice_() throws Exception {
        //given
        List<Long> blogImageIdList = getBlogImgIdList();
        blogImageIdList.add(100L);

        NoticeSaveDto requestDto = NoticeSaveDto.builder()
                .status(BlogStatus.LEAKED)
                .title("제목")
                .contents("내용")
                .noticeImageIdList(blogImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/notices")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("정상적으로 공지사항 리스트 조회하는 테스트")
    public void queryNotices() throws Exception {
        //given
        IntStream.range(1,13).forEach(i -> {
            generateNoticeLeaked(i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateNoticeHidden(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/notices")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_notices",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("create_notice").description("공지사항 생성 링크"),
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
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].id").description("공지사항 인덱스 id"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].title").description("제목"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].createdDate").description("작성일"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0].status").description("노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0]._links.query_notice.href").description("수정할 공지사항 하나 조회하는 링크"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0]._links.update_notice.href").description("공지사항 수정 요청하는 링크"),
                                fieldWithPath("_embedded.queryBlogsAdminDtoList[0]._links.delete_notice.href").description("해당 공지사항 삭제 링크"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.create_notice.href").description("공지사항 생성 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<Blog> leakedNotices = blogRepository.findLeakedNotices();
        assertThat(leakedNotices.size()).isEqualTo(12);
    }

    @Test
    @DisplayName("공지사항 리스트 조회 시 페이지 정보 없을 경우 0페이지 20개 조회 테스트")
    public void queryNotices_noPageable() throws Exception {
        //given
        IntStream.range(1,24).forEach(i -> {
            generateNoticeLeaked(i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateNoticeHidden(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/notices")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.size").value(20))
                .andExpect(jsonPath("page.number").value(0))
        ;

        em.flush();
        em.clear();

        List<Blog> leakedNotices = blogRepository.findLeakedNotices();
        assertThat(leakedNotices.size()).isEqualTo(23);
    }

    @Test
    @DisplayName("공지사항 하나만 조회하기")
    public void queryNotice() throws Exception {
        //given
        Blog notice = generateNoticeLeaked(1);

        IntStream.range(1,4).forEach(i -> {
            generateBlogImage(i, notice);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/notices/{id}", notice.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_notice",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_update_notice").description("공지사항 수정 요청 링크"),
                                linkWithRel("upload_blogImage").description("이미지 업로드 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 공지사항 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("noticeAdminDto.id").description("공지사항 인덱스 id"),
                                fieldWithPath("noticeAdminDto.status").description("공지사항 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("noticeAdminDto.title").description("공지사항 제목"),
                                fieldWithPath("noticeAdminDto.contents").description("공지사항 내용"),
                                fieldWithPath("adminBlogImageDtos[0].blogImageId").description("공지사항 이미지 인덱스 id"),
                                fieldWithPath("adminBlogImageDtos[0].filename").description("파일 이름"),
                                fieldWithPath("adminBlogImageDtos[0].url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_update_notice.href").description("공지사항 수정 요청 링크"),
                                fieldWithPath("_links.upload_blogImage.href").description("이미지 업로드 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 공지사항이 없을 경우 404")
    public void queryNotice_not_exist_404() throws Exception {
        //given

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/notices/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    @Test
    @DisplayName("정상적으로 공지사항 수정하는 테스트")
    public void updateNotice_() throws Exception {
        //given

        Blog notice = generateNoticeLeaked(1);

        BlogImage original1 = generateBlogImage(1, notice);
        BlogImage original2 = generateBlogImage(2, notice);
        BlogImage original3 = generateBlogImage(3, notice);
        BlogImage delete1 = generateBlogImage(4, notice);
        BlogImage delete2 = generateBlogImage(5, notice);

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

        String title = "공지사항 제목 수정";
        String contents = "공지사항 내용 수정";
        UpdateNoticeRequestDto requestDto = UpdateNoticeRequestDto.builder()
                .status(BlogStatus.LEAKED)
                .title(title)
                .contents(contents)
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/notices/{id}",notice.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_update_notice",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_notice").description("수정할 공지사항 하나 조회하는 링크"),
                                linkWithRel("admin_query_notices").description("공지사항 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("수정할 공지사항 인덱스 id")
                        ),
                        requestFields(
                                fieldWithPath("status").description("수정할 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("title").description("수정할 제목"),
                                fieldWithPath("contents").description("수정할 내용"),
                                fieldWithPath("addImageIdList").description("추가할 공지사항 이미지 인덱스 id 리스트"),
                                fieldWithPath("deleteImageIdList").description("삭제할 공지사항 이미지 인덱스 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_notice.href").description("수정할 공지사항 하나 조회하는 링크"),
                                fieldWithPath("_links.admin_query_notices.href").description("공지사항 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        Optional<BlogImage> optional1 = blogImageRepository.findById(delete1.getId());
        Optional<BlogImage> optional2 = blogImageRepository.findById(delete2.getId());

        assertThat(optional1.isPresent()).isFalse();
        assertThat(optional2.isPresent()).isFalse();

        BlogImage addedImage1 = blogImageRepository.findById(add1.getId()).get();
        BlogImage addedImage2 = blogImageRepository.findById(add2.getId()).get();
        BlogImage addedImage3 = blogImageRepository.findById(add3.getId()).get();

        assertThat(addedImage1.getBlog().getId()).isEqualTo(notice.getId());
        assertThat(addedImage2.getBlog().getId()).isEqualTo(notice.getId());
        assertThat(addedImage3.getBlog().getId()).isEqualTo(notice.getId());

        Blog savedBlog = blogRepository.findById(notice.getId()).get();
        assertThat(savedBlog.getContents()).isEqualTo(contents);
        assertThat(savedBlog.getTitle()).isEqualTo(title);


    }

    @Test
    @DisplayName("파일 변화 없어도 정상적으로 공지사항 수정하는 테스트")
    public void updateNotice_noFiles() throws Exception {
        //given

        Blog notice = generateNoticeLeaked(1);

        String title = "공지사항 제목 수정";
        String contents = "공지사항 내용 수정";
        UpdateNoticeRequestDto requestDto = UpdateNoticeRequestDto.builder()
                .status(BlogStatus.LEAKED)
                .title(title)
                .contents(contents)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/notices/{id}",notice.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();


        Blog savedBlog = blogRepository.findById(notice.getId()).get();
        assertThat(savedBlog.getContents()).isEqualTo(contents);
        assertThat(savedBlog.getTitle()).isEqualTo(title);

    }

    @Test
    @DisplayName("공지사항 수정시 요청 값 부족 시 400")
    public void updateNotice_badRequest() throws Exception {
        //given

        Blog notice = generateNoticeLeaked(1);

        String title = "공지사항 제목 수정";
        String contents = "공지사항 내용 수정";
        UpdateNoticeRequestDto requestDto = UpdateNoticeRequestDto.builder()
                .status(BlogStatus.LEAKED)
                .contents(contents)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/notices/{id}",notice.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("수정할 공지사항이 존재하지 않음 404")
    public void updateNotice_notFound() throws Exception {
        //given

        Blog notice = generateNoticeLeaked(1);

        String title = "공지사항 제목 수정";
        String contents = "공지사항 내용 수정";
        UpdateNoticeRequestDto requestDto = UpdateNoticeRequestDto.builder()
                .status(BlogStatus.LEAKED)
                .title(title)
                .contents(contents)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/notices/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 공지사항 삭제하는 테스트")
    public void deleteNotice() throws Exception {
        //given
        Blog notice = generateNoticeLeaked(1);

        BlogImage original1 = generateBlogImage(1, notice);
        BlogImage original2 = generateBlogImage(2, notice);
        BlogImage original3 = generateBlogImage(3, notice);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/notices/{id}", notice.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_delete_notice",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_notices").description("공지사항 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 공지사항 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_notices.href").description("공지사항 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Optional<Blog> optionalBlog = blogRepository.findById(notice.getId());
        assertThat(optionalBlog.isPresent()).isFalse();

        Optional<BlogImage> optionalBlogImage1 = blogImageRepository.findById(original1.getId());
        Optional<BlogImage> optionalBlogImage2 = blogImageRepository.findById(original2.getId());
        Optional<BlogImage> optionalBlogImage3 = blogImageRepository.findById(original3.getId());
        assertThat(optionalBlogImage1.isPresent()).isFalse();
        assertThat(optionalBlogImage2.isPresent()).isFalse();
        assertThat(optionalBlogImage3.isPresent()).isFalse();

    }

    @Test
    @DisplayName("삭제할 공지사항이 존재하지 않을 경우 404")
    public void deleteNotice_404() throws Exception {
        //given
        Blog notice = generateBlog(1);

        BlogImage original1 = generateBlogImage(1, notice);
        BlogImage original2 = generateBlogImage(2, notice);
        BlogImage original3 = generateBlogImage(3, notice);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/notices/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("삭제할 공지사항이 공지사항 유형이 아닐경우 bad request")
    public void deleteNotice_not_notice() throws Exception {
        //given
        Blog blog = generateBlog(1);

        BlogImage original1 = generateBlogImage(1, blog);
        BlogImage original2 = generateBlogImage(2, blog);
        BlogImage original3 = generateBlogImage(3, blog);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/notices/{id}", blog.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
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


    private void generateNoticeHidden(int i) {
        Blog blog = Blog.builder()
                .status(BlogStatus.HIDDEN)
                .title("공지사항" + i)
                .category(BlogCategory.NOTICE)
                .contents("컨텐츠 내용")
                .build();
        blogRepository.save(blog);
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

    private BlogImage generateBlogImage(int i, Blog blog) {
        BlogImage blogImage = BlogImage.builder()
                .blog(blog)
                .folder("folder")
                .filename("filename" + i)
                .build();

        return blogImageRepository.save(blogImage);
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