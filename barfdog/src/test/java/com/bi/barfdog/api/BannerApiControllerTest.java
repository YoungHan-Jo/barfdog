package com.bi.barfdog.api;

import com.bi.barfdog.api.dto.MainBannerSaveRequestDto;
import com.bi.barfdog.api.dto.MyPageBannerSaveRequestDto;
import com.bi.barfdog.api.dto.PopupBannerSaveRequestDto;
import com.bi.barfdog.api.dto.TopBannerSaveRequestDto;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.banner.*;
import com.bi.barfdog.repository.BannerRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BannerApiControllerTest extends BaseTest {

    @Autowired
    BannerRepository bannerRepository;

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    @Test
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
                                linkWithRel("query-mainBanners").description("메인 배너 리스트 호출 링크"),
                                linkWithRel("update-banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParts(
                                partWithName("pcFile").description("pc용 배너 이미지 파일"),
                                partWithName("mobileFile").description("모바일 용 배너 이미지 파일"),
                                partWithName("requestDto").description("배너 내용 / Json")
                        ),
                        requestPartFields("requestDto",
                                fieldWithPath("name").description("배너 이름"),
                                fieldWithPath("targets").description("배너 대상 [ALL, GUESTS, MEMBERS, SUBSCRIBERS]"),
                                fieldWithPath("status").description("배너 노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소")
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
                                fieldWithPath("leakedOrder").description("배너 노출 순서"),
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("status").description("배너 노출 상태"),
                                fieldWithPath("imgFile.folder").description("파일이 저장된 폴더 경로"),
                                fieldWithPath("imgFile.filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("imgFile.filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("targets").description("배너 노출 대상"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-mainBanners.href").description("메인 배너 리스트 호출 링크"),
                                fieldWithPath("_links.update-banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists());
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("create_myPageBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-myPageBanner").description("마이페이지 배너 호출 링크"),
                                linkWithRel("update-banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
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
                                fieldWithPath("_links.query-myPageBanner.href").description("마이페이지 배너 호출 링크"),
                                fieldWithPath("_links.update-banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists());
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType.toString()))
                .andDo(document("create_popupBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-popupBanners").description("팝업 배너 리스트 호출 링크"),
                                linkWithRel("update-banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
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
                                fieldWithPath("pcLinkUrl").description("pc 배너 클릭 시 이동할 url 주소"),
                                fieldWithPath("mobileLinkUrl").description("모바일 배너 클릭 시 이동할 url 주소")
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
                                fieldWithPath("leakedOrder").description("배너 노출 순서"),
                                fieldWithPath("position").description("배너 위치"),
                                fieldWithPath("imgFile.folder").description("파일이 저장된 폴더 경로"),
                                fieldWithPath("imgFile.filenamePc").description("pc 배너 파일 이름"),
                                fieldWithPath("imgFile.filenameMobile").description("mobile 배너 파일 이름"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query-popupBanners.href").description("팝업 배너 리스트 호출 링크"),
                                fieldWithPath("_links.update-banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists());
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("정상적으로 상단 띠 배너 생성하는 테스트")
    public void createTopBanner() throws Exception {
       //Given
        TopBannerSaveRequestDto requestDto = TopBannerSaveRequestDto.builder()
                .name("친구 초대하면 2천원 적립금!")
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .build();

        //when & then
        mockMvc.perform(post("/api/banners/top")
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
                                linkWithRel("query-topBanner").description("상단 띠 배너 호출 링크"),
                                linkWithRel("update-banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
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
                                fieldWithPath("_links.query-topBanner.href").description("띠 배너 리스트 호출 링크"),
                                fieldWithPath("_links.update-banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }


    @Test
    @Transactional
    @DisplayName("마이페이지 배너를 정상적으로 호출하는 테스트")
    public void getMyPageBanner() throws Exception {
       //Given
        Banner banner = generateMyPageBanner();

        //when & then
        mockMvc.perform(get("/api/banners/myPage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(jsonPath("id").exists())
                .andDo(document("query_myPageBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update-banner").description("배너 업데이트하기"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        responseHeaders(
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
                                fieldWithPath("_links.update-banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("정상적으로 마이페이지 배너 업데이트 하는 테스트")
    public void updateMyPageBanner() throws Exception {
       //Given
        Banner banner = generateMyPageBanner();

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
        mockMvc.perform(multipart("/api/banners/myPage/{id}", banner.getId())
                        .file(pcFile)
                        .file(mobileFile)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(jsonPath("name").value(name))
                .andDo(document("update_myPageBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-myPageBanner").description("마이페이지 배너 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
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
                                fieldWithPath("_links.query-myPageBanner.href").description("마이페이지 배너 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @Transactional
    @DisplayName("파일 하나만 첨부해서 마이페이지 배너 업데이트 하는 테스트")
    public void updateMyPageBanner_One_File() throws Exception {
        //Given
        Banner banner = generateMyPageBanner();

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
        Banner banner = generateMyPageBanner();

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
        Banner banner = generateMyPageBanner();

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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());


    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 배너일 경우 수정 실패")
    public void updateMyPageBanner404() throws Exception {
        Banner banner = generateMyPageBanner();

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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @DisplayName("상단 띠 배너 정상적으로 호출 하는 테스트")
    public void getTopBanner() throws Exception {
       //Given
        Banner banner = generateTopBanner();

        //when & then
        mockMvc.perform(get("/api/banners/top")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andDo(document("query_topBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update-banner").description("상단 띠 배너 업데이트 링크"),
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
                                fieldWithPath("_links.update-banner.href").description("배너 업데이트하기"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }


    @Test
    @Transactional
    @DisplayName("정상적으로 상단 띠 배너 업데이트 하는 테스트")
    public void updateTopBanner() throws Exception {
       //Given
        Banner banner = generateTopBanner();

        TopBannerSaveRequestDto requestDto = modelmapper.map(banner, TopBannerSaveRequestDto.class);
        String name = "수정된 상단 띠 배너 제목";
        requestDto.setName(name);
        
        //when & then
        mockMvc.perform(put("/api/banners/top/{id}", banner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(name))
                .andDo(document("update_topBanner",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-topBanner").description("상단 띠 배너 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
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
                                fieldWithPath("_links.query-topBanner.href").description("띠 배너 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않는 띠 배너 업데이트 시 실패")
    public void updateTopBanner_404() throws Exception {
        //Given
        Banner banner = generateTopBanner();

        TopBannerSaveRequestDto requestDto = modelmapper.map(banner, TopBannerSaveRequestDto.class);
        String name = "수정된 상단 띠 배너 제목";
        requestDto.setName(name);

        //when & then
        mockMvc.perform(put("/api/banners/top/9999", banner.getId())
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
        Banner banner = generateTopBanner();

        TopBannerSaveRequestDto requestDto = TopBannerSaveRequestDto.builder().build();

        //when & then
        mockMvc.perform(put("/api/banners/top/{id}", banner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }











    private Banner generateTopBanner() {
        TopBanner topbanner = TopBanner.builder()
                .name("친구 초대하면 2천원 적립금!")
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .status(BannerStatus.LEAKED)
                .backgroundColor("black")
                .fontColor("white")
                .build();
        return bannerRepository.save(topbanner);
    }


    private Banner generateMyPageBanner() {
        MyPageBanner banner = MyPageBanner.builder()
                .name("마이페이지 배너")
                .pcLinkUrl("pc url")
                .mobileLinkUrl("mobile url")
                .status(BannerStatus.LEAKED)
                .imgFile(new ImgFile("C:/Users/verin/jyh/upload/test/banners", "filenamePc.jpg", "filenameMobile.jpg"))
                .build();

        return bannerRepository.save(banner);
    }


}