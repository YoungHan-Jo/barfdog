package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.UploadedImageAdminDto;
import com.bi.barfdog.api.eventDto.EventSaveDto;
import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.event.Event;
import com.bi.barfdog.domain.event.EventImage;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.repository.EventImageRepository;
import com.bi.barfdog.repository.EventRepository;
import com.bi.barfdog.repository.EventThumbnailRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.bi.barfdog.api.eventDto.EventSaveDto.*;
import static com.bi.barfdog.api.eventDto.QueryEventAdminDto.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EventService {

    private final EventThumbnailRepository eventThumbnailRepository;
    private final EventImageRepository eventImageRepository;

    private final EventRepository eventRepository;

    private final StorageService storageService;

    @Transactional
    public UploadedImageAdminDto uploadThumbnailFile(MultipartFile file) {

        ImgFilenamePath path = storageService.storeEventImg(file);

        String filename = path.getFilename();
        EventThumbnail eventThumbnail = EventThumbnail.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        EventThumbnail savedEventThumbnail = eventThumbnailRepository.save(eventThumbnail);

        String url = linkTo(InfoController.class).slash("display").slash("events?filename=" + filename).toString();

        UploadedImageAdminDto uploadedImageAdminDto = UploadedImageAdminDto.builder()
                .id(savedEventThumbnail.getId())
                .url(url)
                .build();
        return uploadedImageAdminDto;
    }

    @Transactional
    public UploadedImageAdminDto uploadImage(MultipartFile file) {
        ImgFilenamePath path = storageService.storeEventImg(file);

        String filename = path.getFilename();

        EventImage eventImage = EventImage.builder()
                .leakOrder(0)
                .folder(path.getFolder())
                .filename(filename)
                .build();

        EventImage savedEventImage = eventImageRepository.save(eventImage);

        String url = linkTo(InfoController.class).slash("display").slash("events?filename=" + filename).toString();

        UploadedImageAdminDto uploadedImageAdminDto = UploadedImageAdminDto.builder()
                .id(savedEventImage.getId())
                .url(url)
                .build();

        return uploadedImageAdminDto;
    }

    @Transactional
    public void saveEvent(EventSaveDto requestDto) {
        Event savedEvent = saveEventAndReturn(requestDto);
        setEventToEventThumbnail(requestDto, savedEvent);
        setEventToEventImages(requestDto, savedEvent);
    }

    public QueryEventAdminDto findAdminEvent(Long id) {

        EventAdminDto eventAdminDto = eventRepository.findEventAdminDto(id);
        eventAdminDto.changeUrl();

        List<EventImageDto> eventImageDtoList = eventImageRepository.findEventImageDtoByEventId(id);
        for (EventImageDto eventImageDto : eventImageDtoList) {
            eventImageDto.changeUrl();
        }

        QueryEventAdminDto queryEventAdminDto = QueryEventAdminDto.builder()
                .eventAdminDto(eventAdminDto)
                .eventImageDtoList(eventImageDtoList)
                .build();

        return queryEventAdminDto;
    }

    private void setEventToEventImages(EventSaveDto requestDto, Event savedEvent) {
        for (EventImageRequestDto eventImageRequestDto : requestDto.getEventImageRequestDtoList()) {
            EventImage savedEventImage = eventImageRepository.findById(eventImageRequestDto.getId()).get();
            savedEventImage.setEvent(savedEvent);
            savedEventImage.setOrder(eventImageRequestDto.getLeakOrder());
        }
    }

    private void setEventToEventThumbnail(EventSaveDto requestDto, Event savedEvent) {
        EventThumbnail eventThumbnail = eventThumbnailRepository.findById(requestDto.getThumbnailId()).get();
        eventThumbnail.setEvent(savedEvent);
    }


    private Event saveEventAndReturn(EventSaveDto requestDto) {
        Event event = Event.builder()
                .status(requestDto.getStatus())
                .title(requestDto.getTitle())
                .build();

        return eventRepository.save(event);
    }


}
