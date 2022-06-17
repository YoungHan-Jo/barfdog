package com.bi.barfdog.api;

import com.bi.barfdog.api.eventDto.EventSaveDto;
import com.bi.barfdog.api.eventDto.UpdateEventRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.event.Event;
import com.bi.barfdog.domain.event.EventImage;
import com.bi.barfdog.domain.event.EventStatus;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.event.EventImageRepository;
import com.bi.barfdog.repository.event.EventRepository;
import com.bi.barfdog.repository.event.EventThumbnailRepository;
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                .andDo(document("admin_query_event",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_events").description("이벤트 리스트 조회 링크"),
                                linkWithRel("update_event").description("이벤트 수정 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 이벤트 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("eventAdminDto.eventId").description("이벤트 인덱스 id"),
                                fieldWithPath("eventAdminDto.status").description("노출 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("eventAdminDto.title").description("이벤트 제목"),
                                fieldWithPath("eventAdminDto.thumbnailId").description("이벤트 썸네일 인덱스 id"),
                                fieldWithPath("eventAdminDto.filename").description("이벤트 썸네일 파일이름"),
                                fieldWithPath("eventAdminDto.url").description("이벤트 썸네일 url"),
                                fieldWithPath("eventImageDtoList[0].id").description("이벤트 내용 이미지 인덱스 id"),
                                fieldWithPath("eventImageDtoList[0].leakOrder").description("이미지 내용 노출 순서"),
                                fieldWithPath("eventImageDtoList[0].filename").description("이미지 내용 파일 이름"),
                                fieldWithPath("eventImageDtoList[0].url").description("이벤트 내용 이미지 url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_events.href").description("이벤트 리스트 조회 링크"),
                                fieldWithPath("_links.update_event.href").description("이벤트 수정 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("수정할 이벤트가 존재하지 않을경우 404")
    public void queryEvent_notfound() throws Exception {
        //given

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/events/9999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("정상적으로 이벤트 수정하는 테스트")
    public void updateEvent() throws Exception {
       //given

        Event event = generateEventAndThumbnail(1);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);
        EventImage deleteImg1 = generateEventImage(4,event);
        EventImage deleteImg2 = generateEventImage(5,event);

        EventImage addImg1 = generateEventImage(0);
        EventImage addImg2 = generateEventImage(0);
        EventImage addImg3 = generateEventImage(0);

        EventThumbnail newThumbnail = generateEventThumbnail(100);

        List<Long> addImageIdList = new ArrayList<>();
        addImageIdList.add(addImg1.getId());
        addImageIdList.add(addImg2.getId());
        addImageIdList.add(addImg3.getId());
        List<Long> deleteImageIdList = new ArrayList<>();
        deleteImageIdList.add(deleteImg1.getId());
        deleteImageIdList.add(deleteImg2.getId());

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = getImageOrderDtoList(eventImage1, eventImage2, eventImage3, addImg1, addImg2, addImg3);

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .thumbnailId(newThumbnail.getId())
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_update_event",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_events").description("이벤트 리스트 조회 링크"),
                                linkWithRel("admin_query_event").description("이벤트 하나 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("수정할 이벤트 인덱스 id")
                        ),
                        requestFields(
                                fieldWithPath("status").description("수정할 상태 [LEAKED, HIDDEN]"),
                                fieldWithPath("title").description("수정할 제목"),
                                fieldWithPath("thumbnailId").description("수정할 썸네일 id, 변경 없으면 기존 썸네일 id 입력"),
                                fieldWithPath("addImageIdList").description("추가할 이미지 인덱스 id 리스트"),
                                fieldWithPath("deleteImageIdList").description("삭제할 이미지 인덱스 id 리스트"),
                                fieldWithPath("imageOrderDtoList[0].id").description("이미지 id"),
                                fieldWithPath("imageOrderDtoList[0].leakOrder").description("id에 해당하는 노출 순서")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_events.href").description("이벤트 리스트 조회 링크"),
                                fieldWithPath("_links.admin_query_event.href").description("이벤트 하나 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Event findEvent = eventRepository.findById(event.getId()).get();
        assertThat(findEvent.getTitle()).isEqualTo(title);
        assertThat(findEvent.getStatus()).isEqualTo(status);

        EventThumbnail findThumbnail = eventThumbnailRepository.findByEventId(event.getId()).get();
        assertThat(findThumbnail.getId()).isEqualTo(newThumbnail.getId());

        List<EventImage> eventImageList = eventImageRepository.findByEventId(event.getId());
        assertThat(eventImageList.size()).isEqualTo(6);
        assertThat(eventImageList.get(5).getId()).isEqualTo(addImg3.getId());

    }

    @Test
    @DisplayName("이벤트 수정 시 이미지 파일 변화 없어도 정상적으로 성공")
    public void updateEvent_image_unchanged() throws Exception {
        //given

        Event event = generateEventAndThumbnail(1);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        EventThumbnail newThumbnail = generateEventThumbnail(100);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .thumbnailId(newThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        EventThumbnail findThumbnail = eventThumbnailRepository.findByEventId(event.getId()).get();
        assertThat(findThumbnail.getId()).isEqualTo(newThumbnail.getId());

        List<EventImage> eventImageList = eventImageRepository.findByEventId(event.getId());
        assertThat(eventImageList.size()).isEqualTo(3);
        assertThat(eventImageList.get(2).getId()).isEqualTo(eventImage3.getId());

    }

    @Test
    @DisplayName("이벤트 수정 시 썸네일 변화 없어도 정상적으로 성공")
    public void updateEvent_thumbnail_unchanged() throws Exception {
        //given

        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .thumbnailId(eventThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        EventThumbnail findThumbnail = eventThumbnailRepository.findByEventId(event.getId()).get();
        assertThat(findThumbnail.getId()).isEqualTo(eventThumbnail.getId());

        List<EventImage> eventImageList = eventImageRepository.findByEventId(event.getId());
        assertThat(eventImageList.size()).isEqualTo(3);
        assertThat(eventImageList.get(2).getId()).isEqualTo(eventImage3.getId());

    }

    @Test
    @DisplayName("이벤트 수정 시 파라미터 값 부족 시 400")
    public void updateEvent_bad_request() throws Exception {
        //given

        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .thumbnailId(eventThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이벤트 수정 시 썸네일 id가 없을 경우 400")
    public void updateEvent_thumbnail_notExist_400() throws Exception {
        //given

        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("이벤트 수정 시 파일 순서정보 없을 시 400")
    public void updateEvent_orderInfo_none_400() throws Exception {
        //given

        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .thumbnailId(eventThumbnail.getId())
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("수정할 이벤트가 존재하지 않을 경우 not found")
    public void updateEvent_not_found() throws Exception {
        //given

        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .thumbnailId(eventThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/9999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이벤트 수정 시 추가할 이미지가 존재하지 않는 이미지일 경우 400")
    public void updateEvent_doesntExist_addimage_400() throws Exception {
        //given
        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<Long> addImageIdList = new ArrayList<>();
        addImageIdList.add(100L);
        addImageIdList.add(101L);
        addImageIdList.add(102L);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .addImageIdList(addImageIdList)
                .thumbnailId(eventThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이벤트 수정 시 삭제할 이미지가 다른 이벤트의 이미지일 경우 400")
    public void updateEvent_wrong_deleteImage_400() throws Exception {
        //given
        Event event = generateEvent(1);

        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(event);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        Event event2 = generateEvent(2);

        EventImage wrongImage1 = generateEventImage(4, event2);
        EventImage wrongImage2 = generateEventImage(5, event2);
        EventImage wrongImage3 = generateEventImage(6, event2);

        List<Long> deleteImageIdList = new ArrayList<>();
        deleteImageIdList.add(wrongImage1.getId());
        deleteImageIdList.add(wrongImage2.getId());
        deleteImageIdList.add(wrongImage3.getId());

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .deleteImageIdList(deleteImageIdList)
                .thumbnailId(eventThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이벤트 수정 시 삭제할 이미지가 아무곳에도 소속되지 않은 이미지일 경우 400")
    public void updateEvent_wrong_deleteImage_null_400() throws Exception {
        //given
        Event event = generateEvent(1);

        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(event);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        EventImage wrongImage1 = generateEventImage(4);
        EventImage wrongImage2 = generateEventImage(5);
        EventImage wrongImage3 = generateEventImage(6);

        List<Long> deleteImageIdList = new ArrayList<>();
        deleteImageIdList.add(wrongImage1.getId());
        deleteImageIdList.add(wrongImage2.getId());
        deleteImageIdList.add(wrongImage3.getId());

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .deleteImageIdList(deleteImageIdList)
                .thumbnailId(eventThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }



    @Test
    @DisplayName("이벤트 수정 시 수정할 썸네일이 존재하지 않는 썸네일일 경우 400")
    public void updateEvent_doesntExist_thumbnail_400() throws Exception {
        //given
        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .thumbnailId(9999L)
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이벤트 수정 시 순서정보리스트에 있는 이미지 id의 이미지가 존재하지 않는 이미지일 경우 400")
    public void updateEvent_doesntExist_Image_inOrderInfoList_400() throws Exception {
        //given
        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목")
                .build();
        Event savedEvent = eventRepository.save(event);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(savedEvent);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(100L, 1));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(101L, 2));
        imageOrderDtoList.add(new UpdateEventRequestDto.ImageOrderDto(102L, 3));

        String title = "수정된 제목";
        EventStatus status = EventStatus.HIDDEN;
        UpdateEventRequestDto requestDto = UpdateEventRequestDto.builder()
                .status(status)
                .title(title)
                .thumbnailId(eventThumbnail.getId())
                .imageOrderDtoList(imageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("정상적으로 이벤트 삭제하는 테스트")
    public void deleteEvent() throws Exception {
       //given
        Event event = generateEvent(1);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(event);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
       
       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_delete_event",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_events").description("이벤트 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 이벤트 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_events.href").description("이벤트 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Optional<EventImage> optionalEventImage1 = eventImageRepository.findById(eventImage1.getId());
        Optional<EventImage> optionalEventImage2 = eventImageRepository.findById(eventImage2.getId());
        assertThat(optionalEventImage1.isPresent()).isFalse();
        assertThat(optionalEventImage2.isPresent()).isFalse();

        Optional<EventThumbnail> optionalEventThumbnail = eventThumbnailRepository.findById(eventThumbnail.getId());
        assertThat(optionalEventThumbnail.isPresent()).isFalse();

        Optional<Event> optionalEvent = eventRepository.findById(event.getId());
        assertThat(optionalEvent.isPresent()).isFalse();


    }

    @Test
    @DisplayName("삭제할 이벤트가 존재하지않을 경우 404")
    public void deleteEvent_notFound() throws Exception {
        //given
        Event event = generateEvent(1);
        EventThumbnail eventThumbnail = generateEventThumbnail(1);
        eventThumbnail.setEvent(event);

        EventImage eventImage1 = generateEventImage(1,event);
        EventImage eventImage2 = generateEventImage(2,event);
        EventImage eventImage3 = generateEventImage(3,event);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/events/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }
    













    private List<UpdateEventRequestDto.ImageOrderDto> getImageOrderDtoList(EventImage eventImage1, EventImage eventImage2, EventImage eventImage3, EventImage addImg1, EventImage addImg2, EventImage addImg3) {
        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        UpdateEventRequestDto.ImageOrderDto imageOrderDto1 = new UpdateEventRequestDto.ImageOrderDto(eventImage1.getId(), 1);
        UpdateEventRequestDto.ImageOrderDto imageOrderDto2 = new UpdateEventRequestDto.ImageOrderDto(addImg1.getId(), 2);
        UpdateEventRequestDto.ImageOrderDto imageOrderDto3 = new UpdateEventRequestDto.ImageOrderDto(eventImage2.getId(), 3);
        UpdateEventRequestDto.ImageOrderDto imageOrderDto4 = new UpdateEventRequestDto.ImageOrderDto(addImg2.getId(), 4);
        UpdateEventRequestDto.ImageOrderDto imageOrderDto5 = new UpdateEventRequestDto.ImageOrderDto(eventImage3.getId(), 5);
        UpdateEventRequestDto.ImageOrderDto imageOrderDto6 = new UpdateEventRequestDto.ImageOrderDto(addImg3.getId(), 6);
        imageOrderDtoList.add(imageOrderDto1);
        imageOrderDtoList.add(imageOrderDto2);
        imageOrderDtoList.add(imageOrderDto3);
        imageOrderDtoList.add(imageOrderDto4);
        imageOrderDtoList.add(imageOrderDto5);
        imageOrderDtoList.add(imageOrderDto6);
        return imageOrderDtoList;
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

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .status(EventStatus.LEAKED)
                .title("제목" + i)
                .build();
        return eventRepository.save(event);
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
                .leakOrder(i)
                .build();
        return eventImageRepository.save(eventImage);
    }

    private EventImage generateEventImage(int i, Event event) {
        EventImage eventImage = EventImage.builder()
                .folder("/folder/events")
                .filename("filename" + i + ".jpg")
                .leakOrder(i)
                .build();
        EventImage savedEventImage = eventImageRepository.save(eventImage);
        savedEventImage.setEvent(event);
        return savedEventImage;
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