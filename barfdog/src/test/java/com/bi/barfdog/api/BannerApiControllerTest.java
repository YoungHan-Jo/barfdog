package com.bi.barfdog.api;

import com.bi.barfdog.api.bannerDto.MainBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.MyPageBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.PopupBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.TopBannerSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.banner.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.banner.BannerRepository;
import org.junit.Before;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BannerApiControllerTest extends BaseTest {

    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    EntityManager em;

    @Autowired
    AppProperties appProperties;

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    @Before
    public void setUp() {
        bannerRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 메인 배너를 생성하는 테스트")
    public void createMainBanner() throws Exception {
       //Given
        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder()
                .name("메인배너1")
                .targets(BannerTargets.ALL)
                .status(BannerStatus.LEAKED)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/main")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("create_mainBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_mainBanners").description("메인 배너 리스트 호출 링크"),
                                linkWithRel("query_mainBanner").description("해당 배너 호출 링크"),
                                linkWithRel("update_banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParts(
                                partWithName("pcFile").description("pc용 배너 이미지 파일"),
                                partWithName("mobileFile").description("모바일 용 배너 이미지 파일"),
                                partWithName("requestDto").description("배너 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("targets").description("배너 대상 [ALL, GUEST, USER, SUBSCRIBER]"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_mainBanners.href").description("메인 배너 리스트 호출 링크"),
                                fieldWithPath("_links.query_mainBanner.href").description("해당 배너 호출 링크"),
                                fieldWithPath("_links.update_banner.href").description("해당 배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("url 링크가 빈 문자열이어도 정상적으로 메인 배너를 생성하는 테스트")
    public void createMainBanner_linkUrl_notnull() throws Exception {
        //Given
        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        String name = "메인배너 샘플 테스트 ";
        MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder()
                .name(name)
                .targets(BannerTargets.ALL)
                .status(BannerStatus.LEAKED)
                .pcLinkUrl("")
                .mobileLinkUrl("")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/main")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                ;
        List<MainBanner> mainBanners = bannerRepository.findMainBannersByName(name);
        MainBanner findBanner = mainBanners.get(0);
        assertThat(findBanner.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("업로드 파일이 없을 경우 에러가 발생하는 테스트")
    public void createMainBanner_Bad_Request_Empty_File() throws Exception {
       //Given
        MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder()
                .name("메인배너1")
                .targets(BannerTargets.ALL)
                .status(BannerStatus.LEAKED)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));

       //when

        mockMvc.perform(multipart("/api/banners/main")
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists());
    }
    
    @Test
    @DisplayName("입력값이 부족할 경우 에러가 발생하는 테스트")
    public void createMainBanner_Bad_Request_Empty_Input() throws Exception {
       //Given
        MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder()
                .targets(BannerTargets.ALL)
                .status(BannerStatus.LEAKED)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile("requestDto", "requestDto", "application/json", requestDtoJson.getBytes(StandardCharsets.UTF_8));
       //when & then
        mockMvc.perform(multipart("/api/banners/main")
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 마이페이지 배너 생성하는 테스트")
    public void createMyPageBanner() throws Exception {
       //Given
        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                .name("마이페이지 배너")
                .pcLinkUrl("pc url")
                .status(BannerStatus.LEAKED)
                .mobileLinkUrl("mobile url")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/myPage")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("create_myPageBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_myPageBanner").description("마이페이지 배너 호출 링크"),
                                linkWithRel("update_banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParts(
                                partWithName("pcFile").description("pc용 배너 이미지 파일"),
                                partWithName("mobileFile").description("모바일 용 배너 이미지 파일"),
                                partWithName("requestDto").description("배너 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("배너 생성 날짜"),
                                fieldWithPath("modifiedDate").description("마지막으로 배너 수정한 날짜"),
                                fieldWithPath("id").description("배너 id 번호"),
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("status").description("배너 노출 상태"),
                                fieldWithPath("imgFile.folder").description("파일이 저장된 폴더 경로"),
                                fieldWithPath("imgFile.filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("imgFile.filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_myPageBanner.href").description("마이페이지 배너 호출 링크"),
                                fieldWithPath("_links.update_banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("url link 빈문자열이라도 정상적으로 마이페이지 배너 생성하는 테스트")
    public void createMyPageBanner_linkUrl_notNull() throws Exception {
        //Given
        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                .name("마이페이지 배너")
                .pcLinkUrl("")
                .status(BannerStatus.LEAKED)
                .mobileLinkUrl("")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/myPage")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()));
    }

    @Test
    @DisplayName("업로드 파일이 없을 경우 에러 발생하는 테스트")
    public void createMyPageBanner_Bad_Request_Empty_File() throws Exception {
       //Given
        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                .name("마이페이지 배너")
                .pcLinkUrl("pc url")
                .status(BannerStatus.LEAKED)
                .mobileLinkUrl("mobile url")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));
       //when & then
        mockMvc.perform(multipart("/api/banners/myPage")
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists());
    }

    @Test
    @DisplayName("입력값이 부족할 경우 에러가 발생하는 테스트")
    public void createMyPageBanner_Bad_Request_Empty_Input() throws Exception {
       //Given
        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                .pcLinkUrl("pc url")
                .status(BannerStatus.LEAKED)
                .mobileLinkUrl("mobile url")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));
       //when
        mockMvc.perform(multipart("/api/banners/myPage")
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists());
    }
    
    @Test
    @DisplayName("이미 마이페이지 배너가 존재하면 conflict 나오는 테스트")
    public void createMypagebanner_duplicate_conflict() throws Exception {
        MyPageBanner mypageBanner = MyPageBanner.builder()
                .name("기존에 있던 마이페지 배너")
                .build();

        bannerRepository.save(mypageBanner);


        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                .name("마이페이지 배너")
                .pcLinkUrl("pc url")
                .status(BannerStatus.LEAKED)
                .mobileLinkUrl("mobile url")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/myPage")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
      
    }
    

    @Test
    @Transactional
    @DisplayName("정상적으로 팝업 배너 생성하는 테스트")
    public void createPopupBanner() throws Exception {
       //Given
        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        PopupBannerSaveRequestDto requestDto = PopupBannerSaveRequestDto.builder()
                .name("팝업배너1")
                .position(PopupBannerPosition.LEFT)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/popup")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("create_popupBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_popupBanners").description("팝업 배너 리스트 호출 링크"),
                                linkWithRel("update_banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParts(
                                partWithName("pcFile").description("pc용 배너 이미지 파일"),
                                partWithName("mobileFile").description("모바일 용 배너 이미지 파일"),
                                partWithName("requestDto").description("배너 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("position").description("배너 위치 [LEFT, MID, RIGHT]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_popupBanners.href").description("팝업 배너 리스트 호출 링크"),
                                fieldWithPath("_links.update_banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("url link 빈문자열이라도 정상적으로 팝업 배너 생성하는 테스트")
    public void createPopupBanner_urlLink_notNull() throws Exception {
        //Given
        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        PopupBannerSaveRequestDto requestDto = PopupBannerSaveRequestDto.builder()
                .name("팝업배너1")
                .position(PopupBannerPosition.LEFT)
                .pcLinkUrl("")
                .mobileLinkUrl("")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/popup")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()));
    }

    @Test
    @Transactional
    @DisplayName("팝업배너 생성 중 url link null 이면 bad request나오는 테스트")
    public void createPopupBanner_urlLink_nNull() throws Exception {
        //Given
        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        PopupBannerSaveRequestDto requestDto = PopupBannerSaveRequestDto.builder()
                .name("팝업배너1")
                .position(PopupBannerPosition.LEFT)
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/popup")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("업로드 파일이 없을 경우 에러 발생하는 테스트")
    public void createPopupBanner_Bad_Request_Empty_File() throws Exception {
        //Given
        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                .name("팝업 배너")
                .pcLinkUrl("pc url")
                .status(BannerStatus.LEAKED)
                .mobileLinkUrl("mobile url")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));
        //when & then
        mockMvc.perform(multipart("/api/banners/popup")
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists());
    }

    @Test
    @DisplayName("입력값이 부족할 경우 에러가 발생하는 테스트")
    public void createPopupBanner_Bad_Request_Empty_Input() throws Exception {
        //Given
        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder()
                .pcLinkUrl("pc url")
                .status(BannerStatus.LEAKED)
                .mobileLinkUrl("mobile url")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));
        //when
        mockMvc.perform(multipart("/api/banners/popup")
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 상단 띠 배너 생성하는 테스트")
    public void createTopBanner() throws Exception {
       //Given
        TopBannerSaveRequestDto requestDto = TopBannerSaveRequestDto.builder()
                .name("친구 초대하면 2천원 적립금!")
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .status(BannerStatus.HIDDEN)
                .build();

        //when & then
        mockMvc.perform(post("/api/banners/top")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andDo(document("create_topBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_topBanner").description("상단 띠 배너 호출 링크"),
                                linkWithRel("update_banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("backgroundColor").description("띠 배너 배경 색상 [기본 값 : #CA0101]"),
                                fieldWithPath("fontColor").description("띠 배너 글자 색상 [기본 값 : #fff]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소 [없을 시 '' 빈 문자열 입력]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("배너 생성 날짜"),
                                fieldWithPath("modifiedDate").description("마지막으로 배너 수정한 날짜"),
                                fieldWithPath("id").description("배너 id 번호"),
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("status").description("배너 노출 상태"),
                                fieldWithPath("backgroundColor").description("배너 배경 색상"),
                                fieldWithPath("fontColor").description("배너 글자 색상"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_topBanner.href").description("띠 배너 리스트 호출 링크"),
                                fieldWithPath("_links.update_banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("url link 빈문자열이더라도 정상적으로 상단 띠 배너 생성하는 테스트")
    public void createTopBanner_urlLink_notNull() throws Exception {
        //Given
        TopBannerSaveRequestDto requestDto = TopBannerSaveRequestDto.builder()
                .name("친구 초대하면 2천원 적립금!")
                .pcLinkUrl("")
                .mobileLinkUrl("")
                .status(BannerStatus.HIDDEN)
                .build();

        //when & then
        mockMvc.perform(post("/api/banners/top")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    @Transactional
    @DisplayName("상단 띠 배너 생성 중 url link null 이면 bad request 나오는 테스트")
    public void createTopBanner_urlLink_Null() throws Exception {
        //Given
        TopBannerSaveRequestDto requestDto = TopBannerSaveRequestDto.builder()
                .name("친구 초대하면 2천원 적립금!")
                .status(BannerStatus.HIDDEN)
                .build();

        //when & then
        mockMvc.perform(post("/api/banners/top")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 부족할 경우 에러가 발생하는 테스트")
    public void createTopBanner_Bad_Request() throws Exception {
        //Given
        TopBannerSaveRequestDto requestDto = TopBannerSaveRequestDto.builder()
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .build();

        //when & then
        mockMvc.perform(post("/api/banners/top")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists());
    }

    @Test
    @Transactional
    @DisplayName("마이페이지 배너를 정상적으로 호출하는 테스트")
    public void queryMyPageBanner() throws Exception {
       //Given

        Banner banner1 = generateMyPageBanner(1);


        //when & then
        mockMvc.perform(get("/api/banners/myPage")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(jsonPath("id").exists())
                .andDo(document("query_myPageBanner",
                        links(
                                linkWithRel("thumbnail_pc").description("pc 썸네일 링크"),
                                linkWithRel("thumbnail_mobile").description("mobile 썸네일 링크"),
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("배너 id 번호"),
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태"),
                                fieldWithPath("filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("_links.thumbnail_pc.href").description("pc 썸네일 링크"),
                                fieldWithPath("_links.thumbnail_mobile.href").description("mobile 썸네일 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("호출할 마이페이지가 없을 경우 not found 나오는 테스트")
    public void queryMyPageBanner_not_found() throws Exception {
       //given

       //when & then
        mockMvc.perform(get("/api/banners/myPage")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                ;

    }


    @Test
    @Transactional
    @DisplayName("정상적으로 마이페이지 배너 업데이트 하는 테스트")
    public void updateMyPageBanner() throws Exception {
       //Given
        Banner banner = generateMyPageBanner(1);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        MyPageBannerSaveRequestDto requestDto = modelmapper.map(banner, MyPageBannerSaveRequestDto.class);
        String name = "new My page Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/banners/myPage/{id}", banner.getId())
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andDo(document("update_myPageBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_myPageBanner").description("마이페이지 배너 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        requestParts(
                                partWithName("pcFile").description("pc용 배너 이미지 파일"),
                                partWithName("mobileFile").description("모바일 용 배너 이미지 파일"),
                                partWithName("requestDto").description("배너 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_myPageBanner.href").description("마이페이지 배너 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        MyPageBanner findBanner = bannerRepository.findMyPageBannerById(banner.getId()).get();
        assertThat(findBanner.getName()).isEqualTo(name);

    }

    @Test
    @Transactional
    @DisplayName("파일 하나만 첨부해서 마이페이지 배너 업데이트 하는 테스트")
    public void updateMyPageBanner_One_File() throws Exception {
        //Given
        Banner banner = generateMyPageBanner(1);

        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        MyPageBannerSaveRequestDto requestDto = modelmapper.map(banner, MyPageBannerSaveRequestDto.class);
        requestDto.setName("new My page Banner");
        requestDto.setStatus(BannerStatus.HIDDEN);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/myPage/{id}", banner.getId())
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @Transactional
    @DisplayName("첨부파일 없이 마이페이지 배너 업데이트 하는 테스트")
    public void updateMyPageBanner_No_Files() throws Exception {
        //Given
        Banner banner = generateMyPageBanner(1);

        MyPageBannerSaveRequestDto requestDto = modelmapper.map(banner, MyPageBannerSaveRequestDto.class);
        requestDto.setName("new My page Banner");

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/myPage/{id}", banner.getId())
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @Transactional
    @DisplayName("입력값이 비어있는 경우 이벤트 수정 실패")
    public void updateMyPageBanner400_Empty() throws Exception {
       //Given
        Banner banner = generateMyPageBanner(1);

        MyPageBannerSaveRequestDto requestDto = MyPageBannerSaveRequestDto.builder().build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));
       //when & then
        mockMvc.perform(multipart("/api/banners/myPage/{id}", banner.getId())
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists());


    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 배너일 경우 수정 실패")
    public void updateMyPageBanner404() throws Exception {
        Banner banner = generateMyPageBanner(1);

        MyPageBannerSaveRequestDto requestDto = modelmapper.map(banner, MyPageBannerSaveRequestDto.class);
        requestDto.setName("new My page Banner");

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/myPage/9999", banner.getId())
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("상단 띠 배너 정상적으로 호출 하는 테스트")
    public void queryTopBanner() throws Exception {
       //Given
        Banner banner = generateTopBanner(1);

        //when & then
        mockMvc.perform(get("/api/banners/top")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andDo(document("query_topBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_banner").description("상단 띠 배너 업데이트 링크"),
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
                                fieldWithPath("id").description("배너 id 번호"),
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태"),
                                fieldWithPath("backgroundColor").description("배너 배경 색상"),
                                fieldWithPath("fontColor").description("배너 글자 색상"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("상단 띠 배너가 존재하지 않을 경우")
    public void queryTopBanner_notFound() throws Exception {
       //given

       //when & then
        mockMvc.perform(get("/api/banners/top")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    @Test
    @Transactional
    @DisplayName("정상적으로 상단 띠 배너 업데이트 하는 테스트")
    public void updateTopBanner() throws Exception {
       //Given
        Banner banner = generateTopBanner(1);

        TopBannerSaveRequestDto requestDto = modelmapper.map(banner, TopBannerSaveRequestDto.class);
        String name = "수정된 상단 띠 배너 제목";
        requestDto.setName(name);
        
        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/banners/top/{id}", banner.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_topBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_topBanner").description("상단 띠 배너 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        requestFields(
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("backgroundColor").description("띠 배너 배경 색상"),
                                fieldWithPath("fontColor").description("띠 배너 글자 색상"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_topBanner.href").description("띠 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        TopBanner findBanner = bannerRepository.findTopBannerById(banner.getId()).get();
        assertThat(findBanner.getName()).isEqualTo(name);
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 띠 배너 업데이트 시 실패")
    public void updateTopBanner_404() throws Exception {
        //Given
        Banner banner = generateTopBanner(1);

        TopBannerSaveRequestDto requestDto = modelmapper.map(banner, TopBannerSaveRequestDto.class);
        String name = "수정된 상단 띠 배너 제목";
        requestDto.setName(name);

        //when & then
        mockMvc.perform(put("/api/banners/top/9999", banner.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("입력값이 부족해서 업데이트 실패하는 테스트")
    public void updateTopBanner_Bad_Request() throws Exception {
        //Given
        Banner banner = generateTopBanner(1);

        TopBannerSaveRequestDto requestDto = TopBannerSaveRequestDto.builder().build();

        //when & then
        mockMvc.perform(put("/api/banners/top/{id}", banner.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 메인배너 리스트 호출하는 테스트")
    public void queryMainBanners() throws Exception {
       //Given
        IntStream.range(1,4).forEach(i -> {
            generateMainBanner(i);
        });

        //when & then
        mockMvc.perform(get("/api/banners/main")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andDo(document("query_mainBanners",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("create_banner").description("배너 생성 링크"),
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
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].id").description("배너 id 번호"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].leakedOrder").description("배너 노출 순서"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].name").description("배너 이름"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].targets").description("배너 노출 대상"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].createdDate").description("배너 생성 날짜"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].modifiedDate").description("마지막으로 배너 수정한 날짜"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0].filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0]._links.thumbnail_pc.href").description("pc 썸네일 링크"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0]._links.thumbnail_mobile.href").description("mobile 썸네일 링크"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0]._links.query_banner.href").description("해당 배너 정보 조회 링크"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0]._links.update_banner.href").description("해당 배너 업데이트 링크"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0]._links.delete_banner.href").description("해당 배너 삭제 링크"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0]._links.mainBanner_leakedOrder_up.href").description("해당 배너 노출 순위 올리는 링크"),
                                fieldWithPath("_embedded.mainBannerListResponseDtoList[0]._links.mainBanner_leakedOrder_down.href").description("해당 배너 노출 순위 내리는 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.create_banner.href").description("배너 생성하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 메인 배너 하나만 조회하는 테스트")
    public void queryMainBanner() throws Exception {
        //Given
        Banner banner = generateMainBanner(1);
        generateMainBanner(2);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banners/main/{id}", banner.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andDo(document("query_mainBanner",
                        links(
                                linkWithRel("thumbnail_mobile").description("pc 용 썸네일 링크"),
                                linkWithRel("thumbnail_pc").description("mobile 용 썸네일 링크"),
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_banner").description("배너 수정 링크"),
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
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("배너 id 번호"),
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("targets").description("배너 노출 대상"),
                                fieldWithPath("status").description("배너 노출 상태"),
                                fieldWithPath("filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("_links.thumbnail_pc.href").description("pc 썸네일 링크"),
                                fieldWithPath("_links.thumbnail_mobile.href").description("mobile 썸네일 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_banner.href").description("배너 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )

                ));
    }

    @Test
    @DisplayName("없는 메인 배너를 조회 했을 때 404 응답 받기")
    public void queryMainBanner404() throws Exception {
        //Given

        //when & then
        mockMvc.perform(get("/api/banners/main/90999")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @Transactional
    @DisplayName("정상적으로 메인 배너를 수정 하는 테스트")
    public void updateMainBanner() throws Exception {
       //Given
        Banner banner = generateMainBanner(200);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        //when
        MainBannerSaveRequestDto requestDto = modelmapper.map(banner, MainBannerSaveRequestDto.class);
        String name = "new Main Banner";
        requestDto.setName(name);
        requestDto.setStatus(BannerStatus.HIDDEN);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/banners/main/{id}", banner.getId())
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_mainBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_banner").description("해당 배너 정보 조회 링크"),
                                linkWithRel("query_mainBanners").description("메인 배너 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        requestParts(
                                partWithName("pcFile").description("pc용 배너 이미지 파일"),
                                partWithName("mobileFile").description("모바일 용 배너 이미지 파일"),
                                partWithName("requestDto").description("배너 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("targets").description("배너 대상 [ALL, GUEST, USER, SUBSCRIBER]"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_banner.href").description("해당 배너 정보 조회 링크"),
                                fieldWithPath("_links.query_mainBanners.href").description("메인 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Banner findBanner = bannerRepository.findById(banner.getId()).get();
        assertThat(findBanner.getName()).isEqualTo(name);
    }

    @Test
    @Transactional
    @DisplayName("파일이 하나만 있어도 메인 배너를 수정 하는 테스트")
    public void updateMainBanner_One_File() throws Exception {
        //Given
        Banner banner = generateMainBanner(100);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when
        MainBannerSaveRequestDto requestDto = modelmapper.map(banner, MainBannerSaveRequestDto.class);
        String name = "new Main Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        MainBanner mainBanner = (MainBanner) banner;

        //then
        mockMvc.perform(multipart("/api/banners/main/{id}", banner.getId())
                        .file(pcFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        MainBanner findBanner = bannerRepository.findMainBannerById(banner.getId()).get();
        assertThat(findBanner.getName()).isEqualTo(name);
        assertThat(findBanner.getImgFile().getFilenameMobile()).isEqualTo(mainBanner.getImgFile().getFilenameMobile());
    }

    @Test
    @Transactional
    @DisplayName("파일이 없어도 메인 배너를 수정 하는 테스트")
    public void updateMainBanner_Empty_files() throws Exception {
        //Given
        Banner banner = generateMainBanner(100);

        //when
        MainBannerSaveRequestDto requestDto = modelmapper.map(banner, MainBannerSaveRequestDto.class);
        String name = "new Main Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //then
        mockMvc.perform(multipart("/api/banners/main/{id}", banner.getId())
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("부족한 요청정보로 메인배너 수정 시 400 에러 나오는 테스트")
    public void updateMainBanner_Bad_Request() throws Exception {
        //Given
        Banner banner = generateMainBanner(100);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        //when
        MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder().build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //then
        mockMvc.perform(multipart("/api/banners/main/{id}", banner.getId())
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("수정할 메인배너 정보가 없을 시 not found 에러 나오는 테스트")
    public void updateMainBanner_Not_Found() throws Exception {
        //Given
        Banner banner = generateMainBanner(100);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        //when
        MainBannerSaveRequestDto requestDto = modelmapper.map(banner, MainBannerSaveRequestDto.class);
        String name = "new Main Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //then
        mockMvc.perform(multipart("/api/banners/main/9999")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 메인배너의 노출순위를 올리는 테스트")
    public void updateMainBannerUp() throws Exception {
        //Given
        IntStream.range(1,10).forEach(i -> {
            generateMainBanner(i);
        });

        MainBanner order6 = bannerRepository.findMainBannerByOrder(6);
        MainBanner order5 = bannerRepository.findMainBannerByOrder(5);


        //when
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/banners/main/{id}/up", order6.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("update_mainBanner_up",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_mainBanners").description("메인 배너 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_mainBanners.href").description("메인 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        MainBanner was6 = (MainBanner) bannerRepository.findById(order6.getId()).get();
        MainBanner was5 = (MainBanner) bannerRepository.findById(order5.getId()).get();

        assertThat(was5.getLeakedOrder()).isEqualTo(6);
        assertThat(was6.getLeakedOrder()).isEqualTo(5);
    }

    @Test
    @Transactional
    @DisplayName("메인배너의 노출순위가 더 이상 올라갈 수 없을 경우 400 나오게 하는 테스트")
    public void updateMainBannerUp_Bad_Request() throws Exception {
        //Given
        IntStream.range(1,10).forEach(i -> {
            generateMainBanner(i);
        });

        MainBanner order1 = bannerRepository.findMainBannerByOrder(1);

        //when
        mockMvc.perform(put("/api/banners/main/{id}/up", order1.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @Transactional
    @DisplayName("노출 순위를 올릴 메인 배너가 존재하지 않을 경우 not found 나오는 테스트")
    public void updateMainBannerUp_Not_Found() throws Exception {
        //when
        mockMvc.perform(put("/api/banners/main/9999/up")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 메인 배너 노출 순위를 내리는 테스트")
    public void updateMainBannerDown() throws Exception {
       //Given
        IntStream.range(1,10).forEach(i -> {
            generateMainBanner(i);
        });

        MainBanner order5 = bannerRepository.findMainBannerByOrder(5);
        MainBanner order6 = bannerRepository.findMainBannerByOrder(6);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/banners/main/{id}/down",order5.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("update_mainBanner_down",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_mainBanners").description("메인 배너 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_mainBanners.href").description("메인 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        MainBanner was5 = (MainBanner) bannerRepository.findById(order5.getId()).get();
        MainBanner was6 = (MainBanner) bannerRepository.findById(order6.getId()).get();

        assertThat(was5.getLeakedOrder()).isEqualTo(6);
        assertThat(was6.getLeakedOrder()).isEqualTo(5);
    }
    
    @Test
    @Transactional
    @DisplayName("노출 순위를 내릴 메인 배너가 없을 경우 NOT FOUND 나오는 테스트")
    public void updateMainBannerDown_Not_Found() throws Exception {
       //Given
       
       //when & then
        mockMvc.perform(put("/api/banners/main/999999/down")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @Transactional
    @DisplayName("노출 순위를 더 이상 내릴 수 없을 경우 Bad Request 나오는 테스트")
    public void updateMainBannerDown_Bad_Request() throws Exception {
        //Given
        IntStream.range(1,10).forEach(i -> {
            generateMainBanner(i);
        });

        MainBanner order9 = bannerRepository.findMainBannerByOrder(9);

        //when & then
        mockMvc.perform(put("/api/banners/main/${id}/down", order9.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Transactional
    @DisplayName("정상적으로 메인배너를 삭제하는 테스트")
    public void deleteMainBanner() throws Exception {
       //Given
        IntStream.range(1,6).forEach(i -> {
            generateMainBanner(i);
        });

        MainBanner order3 = bannerRepository.findMainBannerByOrder(3);
        MainBanner order4 = bannerRepository.findMainBannerByOrder(4);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/banners/main/{id}",order3.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("delete_mainBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_mainBanners").description("메인 배너 리스트 조회 링크"),
                                linkWithRel("create_banner").description("배너 생성 링크"),
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
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_mainBanners.href").description("메인 배너 리스트 조회 링크"),
                                fieldWithPath("_links.create_banner.href").description("배너 생성하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        MainBanner was4 = (MainBanner) bannerRepository.findById(order4.getId()).get();
        assertThat(was4.getLeakedOrder()).isEqualTo(3);

    }

    @Test
    @Transactional
    @DisplayName("삭제할 메인 배너가 없을 경우 NOT FOUND 나오는 테스트")
    public void deleteMainBanner_Not_Found() throws Exception {
        //Given

        //when & then
        mockMvc.perform(delete("/api/banners/main/9999")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }
    
    @Test
    @Transactional
    @DisplayName("정상적으로 팝업 배너 리스트 호출하는 테스트")
    public void queryPopupBanners() throws Exception {
       //Given
        IntStream.range(1,6).forEach(i -> {
            generatePopupBanner(i);
        });
       
       //when & then
        mockMvc.perform(get("/api/banners/popup")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("query_popupBanners",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("create_banner").description("배너 생성 링크"),
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
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].id").description("배너 id 번호"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].leakedOrder").description("배너 노출 순서"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].position").description("배너 노출 위치"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].name").description("배너 이름"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].createdDate").description("배너 생성 날짜"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].modifiedDate").description("마지막으로 배너 수정한 날짜"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0].filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0]._links.thumbnail_pc.href").description("pc 용 배너 썸네일 링크"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0]._links.thumbnail_mobile.href").description("mobile 용 배너 썸네일 링크"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0]._links.query_popupBanner.href").description("배너 조회 링크"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0]._links.update_popupBanner.href").description("배너 수정 링크"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0]._links.delete_popupBanner.href").description("배너 삭제 링크"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0]._links.update_popupBanner_order_up.href").description("배너 노출 순위 올리는 링크"),
                                fieldWithPath("_embedded.popupBannerListResponseDtoList[0]._links.update_popupBanner_order_down.href").description("배너 노출 순위 내리는 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.create_banner.href").description("배너 생성하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }
    
    @Test
    @Transactional
    @DisplayName("정상적으로 팝업 배너 하나만 조회하는 테스트")
    public void queryPopupBanner() throws Exception {
       //Given
        Banner banner = generatePopupBanner(1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banners/popup/{id}",banner.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andExpect(jsonPath("id").value(banner.getId()))
                .andDo(document("query_popupBanner",
                        links(
                                linkWithRel("thumbnail_pc").description("pc 썸네일 링크"),
                                linkWithRel("thumbnail_mobile").description("mobile 썸네일 링크"),
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_banner").description("배너 수정 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("배너 id 번호"),
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("status").description("배너 노출 상태"),
                                fieldWithPath("position").description("배너 노출 위치"),
                                fieldWithPath("filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("_links.thumbnail_pc.href").description("pc 썸네일 링크"),
                                fieldWithPath("_links.thumbnail_mobile.href").description("mobile 썸네일 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_banner.href").description("배너 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("조회할 팝업 배너가 없을 경우 NOT FOUND 나오는 테스트")
    public void queryPopupBanner_Not_Found() throws Exception {
        //Given

        //when & then
        mockMvc.perform(get("/api/banners/popup/999999")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    @Transactional
    @DisplayName("정상적으로 팝업 배너 수정하는 테스트")
    public void updatePopupBanner() throws Exception {
       //Given
        Banner banner = generatePopupBanner(1);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        PopupBannerSaveRequestDto requestDto = modelmapper.map(banner, PopupBannerSaveRequestDto.class);
        String name = "new popup Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/banners/popup/{id}", banner.getId())
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("update_popupBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_banner").description("해당 배너 정보 조회 링크"),
                                linkWithRel("query_popupBanners").description("팝업 배너 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        requestParts(
                                partWithName("pcFile").description("pc용 배너 이미지 파일"),
                                partWithName("mobileFile").description("모바일 용 배너 이미지 파일"),
                                partWithName("requestDto").description("배너 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("position").description("배너 노출 위치 [LEFT, MID, RIGHT]"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_banner.href").description("해당 배너 정보 조회 링크"),
                                fieldWithPath("_links.query_popupBanners.href").description("팝업 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        PopupBanner findBanner = bannerRepository.findPopupBannerById(banner.getId()).get();
        assertThat(findBanner.getName()).isEqualTo(name);
    }

    @Test
    @Transactional
    @DisplayName("파일이 하나일때 정상적으로 수정하는 테스트")
    public void updatePopupBanner_One_File() throws Exception {
        //Given
        PopupBanner banner = (PopupBanner) generatePopupBanner(1);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        PopupBannerSaveRequestDto requestDto = modelmapper.map(banner, PopupBannerSaveRequestDto.class);
        String name = "new popup Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/popup/{id}", banner.getId())
                        .file(pcFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE));

        em.flush();
        em.clear();

        PopupBanner findBanner = bannerRepository.findPopupBannerById(banner.getId()).get();
        assertThat(findBanner.getName()).isEqualTo(name);
        assertThat(findBanner.getImgFile().getFilenameMobile()).isEqualTo(banner.getImgFile().getFilenameMobile());
    }

    @Test
    @Transactional
    @DisplayName("파일이 없어도 정상적으로 수정하는 테스트")
    public void updatePopupBanner_Empty_Files() throws Exception {
        //Given
        Banner banner = generatePopupBanner(1);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        PopupBannerSaveRequestDto requestDto = modelmapper.map(banner, PopupBannerSaveRequestDto.class);
        String name = "new popup Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/popup/{id}", banner.getId())
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE));

        PopupBanner findBanner = bannerRepository.findPopupBannerById(banner.getId()).get();
        assertThat(findBanner.getName()).isEqualTo(name);
    }

    @Test
    @Transactional
    @DisplayName("입력값이 부족핧 경우 Bad Request 나오는 테스트")
    public void updatePopupBanner_Bad_Request() throws Exception {
        //Given
        Banner banner = generatePopupBanner(1);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        PopupBannerSaveRequestDto requestDto = PopupBannerSaveRequestDto.builder().build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/popup/{id}", banner.getId())
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("수정할 팝업 배너가 없을 경우 Not Found 나오는 테스트")
    public void updatePopupBanner_Not_Found() throws Exception {
        //Given
        Banner banner = generatePopupBanner(1);

        MockMultipartFile pcFile = new MockMultipartFile("pcFile", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        MockMultipartFile mobileFile = new MockMultipartFile("mobileFile", "file2.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file2.jpg"));

        PopupBannerSaveRequestDto requestDto = modelmapper.map(banner, PopupBannerSaveRequestDto.class);
        String name = "new popup Banner";
        requestDto.setName(name);

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile request = new MockMultipartFile(
                "requestDto",
                "requestDto",
                "application/json",
                requestDtoJson.getBytes(StandardCharsets.UTF_8));

        //when & then
        mockMvc.perform(multipart("/api/banners/popup/9999")
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 팝업 배너의 노출 순서를 올리는 테스트")
    public void updatePopupBannerUp() throws Exception {
       //Given
        IntStream.range(1,9).forEach(i -> {
            generatePopupBanner(i);
        });

        PopupBanner order5 = bannerRepository.findPopupBannerByOrder(5);
        PopupBanner order6 = bannerRepository.findPopupBannerByOrder(6);
        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/banners/popup/{id}/up", order6.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_popupBanner_up",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_popupBanners").description("팝업 배너 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_popupBanners.href").description("팝업 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        PopupBanner was6 = (PopupBanner) bannerRepository.findById(order6.getId()).get();
        PopupBanner was5 = (PopupBanner) bannerRepository.findById(order5.getId()).get();

        assertThat(was5.getLeakedOrder()).isEqualTo(6);
        assertThat(was6.getLeakedOrder()).isEqualTo(5);
    }

    @Test
    @Transactional
    @DisplayName("노출 순서를 올릴 팝업 배너가 존재 하지 않을 경우 Not Found 나오는 테스트")
    public void updatePopupBannerUp_Not_Found() throws Exception {
        //Given
        IntStream.range(1,2).forEach(i -> {
            generatePopupBanner(i);
        });

        //when & then
        mockMvc.perform(put("/api/banners/popup/999999/up")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("팝업배너의 순서를 더 이상 올릴 수 없을 경우 Bad Request가 나오는 테스트")
    public void updatePopupBannerUp_Bad_Request() throws Exception {
        //Given
        IntStream.range(1,4).forEach(i -> {
            generatePopupBanner(i);
        });

        PopupBanner banner = bannerRepository.findPopupBannerByOrder(1);

        //when & then
        mockMvc.perform(put("/api/banners/popup/{id}/up", banner.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 팝업배너의 순서를 내리는 테스트")
    public void updatePopupBannerDown() throws Exception {
       //Given
        IntStream.range(1,9).forEach(i -> {
            generatePopupBanner(i);
        });

        PopupBanner order5 = bannerRepository.findPopupBannerByOrder(5);
        PopupBanner order6 = bannerRepository.findPopupBannerByOrder(6);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/banners/popup/{id}/down", order5.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("update_popupBanner_down",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_popupBanners").description("팝업 배너 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_popupBanners.href").description("팝업 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        PopupBanner was5 = (PopupBanner) bannerRepository.findById(order5.getId()).get();
        PopupBanner was6 = (PopupBanner) bannerRepository.findById(order6.getId()).get();

        assertThat(was5.getLeakedOrder()).isEqualTo(6);
        assertThat(was6.getLeakedOrder()).isEqualTo(5);
    }

    @Test
    @Transactional
    @DisplayName("순서를 내릴 팝업 배너가 없을 경우 not found 나오는 테스트")
    public void updatePopupBannerDown_Not_Found() throws Exception {
        //when & then
        mockMvc.perform(put("/api/banners/popup/999999/down")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("팝업배너의 노출 순위를 더 이상 내릴 수 없을 경우 bad request 나오는 테스트")
    public void updatePopupBannerDown_Bad_Request() throws Exception {
        //Given
        IntStream.range(1,7).forEach(i -> {
            generatePopupBanner(i);
        });

        PopupBanner order6 = bannerRepository.findPopupBannerByOrder(6);

        //when & then
        mockMvc.perform(put("/api/banners/popup/{id}/down", order6.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 팝업배너를 삭제하는 테스트")
    public void deletePopupBanner() throws Exception {
       //Given
        IntStream.range(1,9).forEach(i -> {
            generatePopupBanner(i);
        });

        PopupBanner order5 = bannerRepository.findPopupBannerByOrder(5);
        PopupBanner order6 = bannerRepository.findPopupBannerByOrder(6);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/banners/popup/{id}", order5.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("delete_popupBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_popupBanners").description("팝업 배너 리스트 조회 링크"),
                                linkWithRel("create_banner").description("배너 생성 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("배너 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_popupBanners.href").description("팝업 배너 리스트 조회 링크"),
                                fieldWithPath("_links.create_banner.href").description("배너 생성하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        PopupBanner was6 = (PopupBanner) bannerRepository.findById(order6.getId()).get();
        assertThat(was6.getLeakedOrder()).isEqualTo(5);
    }

    @Test
    @Transactional
    @DisplayName("삭제할 팝업 배너가 없을 경우 not found 나오는 테스트")
    public void deletePopupBanner_Not_Found() throws Exception {
        //Given

        //when & then
        mockMvc.perform(delete("/api/banners/popup/999999")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    
    
    
    







    private Banner generateMainBanner(int index) {
        MainBanner banner = MainBanner.builder()
                .name("메인배너" + index)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .status(BannerStatus.LEAKED)
                .leakedOrder(index)
                .imgFile(new ImgFile("C:/Users/verin/jyh/upload/test/banners", "filenamePc.jpg", "filenameMobile.jpg"))
                .targets(BannerTargets.ALL)
                .build();
        return bannerRepository.save(banner);
    }

    private Banner generatePopupBanner(int index) {
        PopupBanner banner = PopupBanner.builder()
                .name("팝업배너" + index)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .status(BannerStatus.LEAKED)
                .leakedOrder(index)
                .position(PopupBannerPosition.LEFT)
                .imgFile(new ImgFile("C:/Users/verin/jyh/upload/test/banners", "filenamePc.jpg", "filenameMobile.jpg"))
                .build();
        return bannerRepository.save(banner);
    }


    private Banner generateTopBanner(int i) {
        TopBanner topbanner = TopBanner.builder()
                .name("친구 초대하면 2천원 적립금!" + i)
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .status(BannerStatus.LEAKED)
                .backgroundColor("black")
                .fontColor("white")
                .build();
        return bannerRepository.save(topbanner);
    }

    private Banner generateMyPageBanner(int i) {
        MyPageBanner banner = MyPageBanner.builder()
                .name("마이페이지 배너" + i)
                .pcLinkUrl("pc url")
                .mobileLinkUrl("mobile url")
                .status(BannerStatus.LEAKED)
                .imgFile(new ImgFile("C:/Users/verin/jyh/upload/test/banners", "filenamePc.jpg", "filenameMobile.jpg"))
                .build();

        return bannerRepository.save(banner);
    }

    private String getBearerToken() throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .email(appProperties.getAdminEmail())
                .password(appProperties.getAdminPassword())
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