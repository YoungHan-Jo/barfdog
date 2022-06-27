package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.UploadedImageDto;
import com.bi.barfdog.api.eventDto.EventSaveDto;
import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.api.eventDto.QueryEventDto;
import com.bi.barfdog.api.eventDto.UpdateEventRequestDto;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.event.Event;
import com.bi.barfdog.domain.event.EventImage;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.repository.event.EventImageRepository;
import com.bi.barfdog.repository.event.EventRepository;
import com.bi.barfdog.repository.event.EventThumbnailRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    public UploadedImageDto uploadThumbnailFile(MultipartFile file) {

        ImgFilenamePath path = storageService.storeEventImg(file);

        String filename = path.getFilename();
        EventThumbnail eventThumbnail = EventThumbnail.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        EventThumbnail savedEventThumbnail = eventThumbnailRepository.save(eventThumbnail);

        String url = linkTo(InfoController.class).slash("display").slash("events?filename=" + filename).toString();

        UploadedImageDto uploadedImageDto = UploadedImageDto.builder()
                .id(savedEventThumbnail.getId())
                .url(url)
                .build();
        return uploadedImageDto;
    }

    @Transactional
    public UploadedImageDto uploadImage(MultipartFile file) {
        ImgFilenamePath path = storageService.storeEventImg(file);

        String filename = path.getFilename();

        EventImage eventImage = EventImage.builder()
                .leakOrder(0)
                .folder(path.getFolder())
                .filename(filename)
                .build();

        EventImage savedEventImage = eventImageRepository.save(eventImage);

        String url = linkTo(InfoController.class).slash("display").slash("events?filename=" + filename).toString();

        UploadedImageDto uploadedImageDto = UploadedImageDto.builder()
                .id(savedEventImage.getId())
                .url(url)
                .build();

        return uploadedImageDto;
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

    @Transactional
    public void updateEvent(Long id, UpdateEventRequestDto requestDto) {
        Event event = eventRepository.findById(id).get();
        event.update(requestDto);
        updateThumbnail(id, requestDto, event);
        updateEventImages(requestDto, event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        deleteThumbnail(id);
        deleteImages(id);
        Event event = eventRepository.findById(id).get();
        eventRepository.delete(event);
    }

    private void deleteImages(Long id) {
        List<EventImage> eventImageList = eventImageRepository.findByEventId(id);
        for (EventImage eventImage : eventImageList) {
            eventImageRepository.delete(eventImage);
        }
    }

    private void deleteThumbnail(Long id) {
        Optional<EventThumbnail> optionalEventThumbnail = eventThumbnailRepository.findByEventId(id);
        if (optionalEventThumbnail.isPresent()) {
            EventThumbnail eventThumbnail = optionalEventThumbnail.get();
            eventThumbnailRepository.delete(eventThumbnail);
        }
    }

    private void updateEventImages(UpdateEventRequestDto requestDto, Event event) {
        eventImageRepository.deleteByIdList(requestDto.getDeleteImageIdList());

        for (Long imageId : requestDto.getAddImageIdList()) {
            EventImage eventImage = eventImageRepository.findById(imageId).get();
            eventImage.setEvent(event);
        }

        for (UpdateEventRequestDto.ImageOrderDto imageOrderDto : requestDto.getImageOrderDtoList()) {
            EventImage eventImage = eventImageRepository.findById(imageOrderDto.getId()).get();
            eventImage.setLeakOrder(imageOrderDto.getLeakOrder());
        }
    }

    private void updateThumbnail(Long id, UpdateEventRequestDto requestDto, Event event) {
        EventThumbnail eventThumbnail = eventThumbnailRepository.findByEventId(id).get();

        EventThumbnail newThumbnail = eventThumbnailRepository.findById(requestDto.getThumbnailId()).get();

        if (eventThumbnail.getId() != newThumbnail.getId()) {
            eventThumbnailRepository.delete(eventThumbnail);
            newThumbnail.setEvent(event);
        }
    }


    private void setEventToEventImages(EventSaveDto requestDto, Event savedEvent) {
        for (EventImageRequestDto eventImageRequestDto : requestDto.getEventImageRequestDtoList()) {
            EventImage savedEventImage = eventImageRepository.findById(eventImageRequestDto.getId()).get();
            savedEventImage.setEvent(savedEvent);
            savedEventImage.setLeakOrder(eventImageRequestDto.getLeakOrder());
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


    public QueryEventDto queryEvent(Long id) {
        Event event = eventRepository.findById(id).get();

        return null;
    }
}
