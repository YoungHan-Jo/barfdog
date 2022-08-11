package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.event.Event;
import com.bi.barfdog.domain.event.EventImage;
import com.bi.barfdog.domain.event.EventStatus;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class EventApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventImageRepository eventImageRepository;

    @Autowired
    EventThumbnailRepository eventThumbnailRepository;


    @Test
    @DisplayName("정상적으로 이벤트 리스트 조회하는 테스트")
    public void queryEvents() throws Exception {
       //given

        IntStream.range(1,14).forEach(i -> {
            generateEventAndThumbnail(i);
        });

        IntStream.range(1,5).forEach(i -> {
            generateEventAndThumbnail_Hidden(i);
        });

       //when & then
        mockMvc.perform(get("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andDo(document("query_events",
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
                                fieldWithPath("_embedded.queryEventsDtoList[0].id").description("이벤트 인덱스 id"),
                                fieldWithPath("_embedded.queryEventsDtoList[0].title").description("이벤트 제목"),
                                fieldWithPath("_embedded.queryEventsDtoList[0].thumbnailUrl").description("이벤트 썸네일 url"),
                                fieldWithPath("_embedded.queryEventsDtoList[0]._links.query_event.href").description("이벤트 상세보기 조회 링크"),
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
    @DisplayName("정상적으로 이벤트 하나 조회하는 테스트")
    public void queryEvent() throws Exception {
       //given
        Event event = generateEventAndThumbnail(1);
        IntStream.range(1,6).forEach(i -> {
            generateEventImage(i, event);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_event",
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
                                parameterWithName("id").description("이벤트 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("eventDto.id").description("이벤트 인덱스 id"),
                                fieldWithPath("eventDto.title").description("이벤트 제목"),
                                fieldWithPath("eventDto.createdDate").description("이벤트 등록날짜"),
                                fieldWithPath("imageUrlList").description("이미지 url 리스트"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_events.href").description("이벤트 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;
    }

    @Test
    @DisplayName("조회할 이벤트가 존재하지 않을 경우 404")
    public void queryEvent_404() throws Exception {
        //given
        Event event = generateEventAndThumbnail(1);
        IntStream.range(1,6).forEach(i -> {
            generateEventImage(i, event);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/events/99999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

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

    private Event generateEventAndThumbnail_Hidden(int i) {
        EventThumbnail eventThumbnail = generateEventThumbnail(i);

        Event event = Event.builder()
                .status(EventStatus.HIDDEN)
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