package com.bi.barfdog.api;

import com.bi.barfdog.api.eventDto.EventSaveDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.event.Event;
import com.bi.barfdog.domain.event.EventImage;
import com.bi.barfdog.domain.event.EventStatus;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.EventImageRepository;
import com.bi.barfdog.repository.EventRepository;
import com.bi.barfdog.repository.EventThumbnailRepository;
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
import java.util.stream.IntStream;

import static com.bi.barfdog.api.eventDto.EventSaveDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class EventAdminControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;
    @Autowired
    EntityManager em;

    @Autowired
    EventThumbnailRepository eventThumbnailRepository;
    @Autowired
    EventImageRepository eventImageRepository;
    @Autowired
    EventRepository eventRepository;


    @Test
    @DisplayName("정상적으로 썸네일 업로드하는 테스트")
    public void uploadThumbnail() throws Exception {
       //given
        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
       //when & then
        mockMvc.perform(multipart("/api/admin/events/thumbnail")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("url").exists())
                .andDo(document("upload_eventThumbnail",
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
                                partWithName("file").description("업로드할 이벤트 썸네일 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("썸네일 이미지 인덱스 id"),
                                fieldWithPath("url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<EventThumbnail> all = eventThumbnailRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("업로드할 썸네일 첨부파일이 없을 경우 400")
    public void uploadThumbnail_noFile() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        //when & then
        mockMvc.perform(multipart("/api/admin/events/thumbnail")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 이미지 업로드하는 테스트")
    public void uploadImage() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        //when & then
        mockMvc.perform(multipart("/api/admin/events/image")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("url").exists())
                .andDo(document("upload_eventImage",
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
                                partWithName("file").description("업로드할 이벤트 이미지 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("이벤트 이미지 인덱스 id"),
                                fieldWithPath("url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<EventImage> all = eventImageRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("업로드할 이벤트 이미지가 없을 경우 400")
    public void uploadImage_noFile_400() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));
        //when & then
        mockMvc.perform(multipart("/api/admin/events/image")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 이벤트 등록")
    public void createEvent() throws Exception {
       //given
        EventThumbnail eventThumbnail = generateEventThumbnail(1);

        List<EventImageRequestDto> eventImageRequestDtoList = getEventImageRequestDtos();

        EventSaveDto requestDto = builder()
                .status(EventStatus.LEAKED)
                .title("제목 1")
                .thumbnailId(eventThumbnail.getId())
                .eventImageRequestDtoList(eventImageRequestDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_event",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_events").description("이벤트 리스트 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("status").description("이벤트 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("title").description("이벤트 제목"),
                                fieldWithPath("thumbnailId").description("썸네일 인덱스 id"),
                                fieldWithPath("eventImageRequestDtoList[0].id").description("이미지 인덱스 id"),
                                fieldWithPath("eventImageRequestDtoList[0].leakOrder").description("이미지 노출 순서")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_events.href").description("이벤트 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        List<Event> events = eventRepository.findAll();

        EventThumbnail findThumbnail = eventThumbnailRepository.findById(eventThumbnail.getId()).get();
        assertThat(findThumbnail.getEvent().getId()).isEqualTo(events.get(0).getId());

        for (EventImageRequestDto eventImageRequestDto : eventImageRequestDtoList) {
            EventImage eventImage = eventImageRepository.findById(eventImageRequestDto.getId()).get();
            assertThat(eventImage.getLeakOrder()).isEqualTo(eventImageRequestDto.getLeakOrder());
        }

    }

    @Test
    @DisplayName("이벤트 등록 시 썸네일 없을경우 400")
    public void createEvent_no_thumbnail() throws Exception {
        //given

        List<EventImageRequestDto> eventImageRequestDtoList = getEventImageRequestDtos();

        EventSaveDto requestDto = builder()
                .status(EventStatus.LEAKED)
                .title("제목 1")
                .eventImageRequestDtoList(eventImageRequestDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이벤트 등록시 이미지 id 리스트 없을 경우")
    public void createEvent_no_Image() throws Exception {
        //given
        EventThumbnail eventThumbnail = generateEventThumbnail(1);

        List<EventImageRequestDto> eventImageRequestDtoList = new ArrayList<>();

        EventSaveDto requestDto = builder()
                .status(EventStatus.LEAKED)
                .title("제목 1")
                .thumbnailId(eventThumbnail.getId())
                .eventImageRequestDtoList(eventImageRequestDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("이벤트 생성 시 값 부족일 경우 400")
    public void createEvent_badRequest() throws Exception {
        //given

        EventSaveDto requestDto = builder()
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이벤트 등록시 존재하지 않는 이미지 id일 경우 400")
    public void createEvent_not_exist_400() throws Exception {
        //given
        EventThumbnail eventThumbnail = generateEventThumbnail(1);

        List<EventImageRequestDto> eventImageRequestDtoList = getEventImageRequestDtos();

        eventImageRequestDtoList
                .add(EventImageRequestDto.builder()
                        .id(100L)
                        .leakOrder(100)
                        .build());

        EventSaveDto requestDto = builder()
                .status(EventStatus.LEAKED)
                .title("제목 1")
                .thumbnailId(eventThumbnail.getId())
                .eventImageRequestDtoList(eventImageRequestDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("이벤트 등록시 존재하지 않는 썸네일 id일 경우 400")
    public void createEvent_thumbnail_not_exist_400() throws Exception {
        //given

        List<EventImageRequestDto> eventImageRequestDtoList = getEventImageRequestDtos();

        EventSaveDto requestDto = builder()
                .status(EventStatus.LEAKED)
                .title("제목 1")
                .thumbnailId(9999L)
                .eventImageRequestDtoList(eventImageRequestDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 이벤트 리스트 조회하는 테스트")
    public void queryEvents() throws Exception {
       //given

        IntStream.range(1,18).forEach(i -> {
            generateEventAndThumbnail(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_events",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("create_event").description("마지막 페이지 링크"),
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
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0].eventsAdminDto.id").description("이벤트 인덱스 id"),
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0].eventsAdminDto.title").description("제목"),
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0].eventsAdminDto.createdDate").description("작성일"),
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0].eventsAdminDto.status").description("노출여부 [LEAKED,HIDDEN]"),
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0].imageUrl").description("썸네일 url"),
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0]._links.query_event.href").description("이벤트 하나 조회 링크"),
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0]._links.update_event.href").description("이벤트 수정 요청 링크"),
                                fieldWithPath("_embedded.queryEventsAdminDtoList[0]._links.delete_event.href").description("이벤트 삭제 요청 링크"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.create_event.href").description("이벤트 생성 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));


    }

    @Test
    @DisplayName("이벤트 리스트 조회 시 페이징 정보 없을 시 0페이지 20개 조회")
    public void queryEvents_no_paging() throws Exception {
        //given

        IntStream.range(1,24).forEach(i -> {
            generateEventAndThumbnail(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/events")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryEventsAdminDtoList", hasSize(20)))
        ;

    }

    @Test
    @DisplayName("정상적으로 수정할 이벤트 하나 정보 조회하는 테스트")
    public void queryEvent() throws Exception {
       //given

        Event event = generateEventAndThumbnail(1);

        IntStream.range(1,4).forEach(i -> {
            EventImage eventImage = generateEventImage(i);
            eventImage.setEvent(event);
        });



        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

    }











    private Event generateEventAndThumbnail(int i) {
        EventThumbnail eventThumbnail = generateEventThumbnail(i);

        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목" + i)
                .build();

        Event savedEvent = eventRepository.save(event);

        eventThumbnail.setEvent(savedEvent);

        return event;
    }


    private List<EventImageRequestDto> getEventImageRequestDtos() {
        List<EventImageRequestDto> eventImageRequestDtoList = new ArrayList<>();

        IntStream.range(1,4).forEach(i -> {
            generateEventImageAndAddRequestDto(eventImageRequestDtoList, i);
        });

        return eventImageRequestDtoList;
    }

    private void generateEventImageAndAddRequestDto(List<EventImageRequestDto> eventImageRequestDtoList,
                                                    int i) {
        EventImage eventImage = EventImage.builder()
                .folder("folder" + i)
                .filename("filename" + i +".jpg")
                .build();
        EventImage savedEventImage = eventImageRepository.save(eventImage);

        EventImageRequestDto eventImageRequestDto = EventImageRequestDto.builder()
                .id(savedEventImage.getId())
                .leakOrder(i)
                .build();
        eventImageRequestDtoList.add(eventImageRequestDto);
    }


    private EventImage generateEventImage(int i) {
        EventImage eventImage = EventImage.builder()
                .folder("/folder/events")
                .filename("filename" + i + ".jpg")
                .build();
        return eventImageRepository.save(eventImage);
    }

    private EventThumbnail generateEventThumbnail(int i) {
        EventThumbnail eventThumbnail = EventThumbnail.builder()
                .folder("/folder/events")
                .filename("filename" + i +".jpg")
                .build();
        return eventThumbnailRepository.save(eventThumbnail);
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