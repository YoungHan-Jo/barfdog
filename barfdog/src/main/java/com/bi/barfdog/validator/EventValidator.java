package com.bi.barfdog.validator;

import com.bi.barfdog.api.eventDto.EventSaveDto;
import com.bi.barfdog.api.eventDto.UpdateEventRequestDto;
import com.bi.barfdog.domain.event.EventImage;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.repository.event.EventImageRepository;
import com.bi.barfdog.repository.event.EventThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

import static com.bi.barfdog.api.eventDto.EventSaveDto.*;

@RequiredArgsConstructor
@Component
public class EventValidator {

    private final EventThumbnailRepository eventThumbnailRepository;
    private final EventImageRepository eventImageRepository;

    public void validateImages(EventSaveDto requestDto, Errors errors) {
        validateThumbnail(requestDto.getThumbnailId(), errors);

        List<EventImageRequestDto> eventImageRequestDtoList = requestDto.getEventImageRequestDtoList();
        for (EventImageRequestDto eventImageRequestDto : eventImageRequestDtoList) {
            validateImage(eventImageRequestDto.getId(), errors);
        }
    }

    public void validateImages(UpdateEventRequestDto requestDto, Errors errors) {
        validateThumbnail(requestDto.getThumbnailId(), errors);

        List<Long> addImageIdList = requestDto.getAddImageIdList();
        for (Long id : addImageIdList) {
            validateImage(id, errors);
        }

        List<Long> deleteImageIdList = requestDto.getDeleteImageIdList();
        for (Long id : deleteImageIdList) {
            validateImage(id, errors);
        }

        List<UpdateEventRequestDto.ImageOrderDto> imageOrderDtoList = requestDto.getImageOrderDtoList();
        for (UpdateEventRequestDto.ImageOrderDto imageOrderDto : imageOrderDtoList) {
            validateImage(imageOrderDto.getId(),errors);
        }
    }

    public void validateWrongImages(Long eventId, UpdateEventRequestDto requestDto, Errors errors) {
        List<Long> deleteImageIdList = requestDto.getDeleteImageIdList();
        for (Long id : deleteImageIdList) {
            EventImage eventImage = eventImageRepository.findById(id).get();
            if ( eventImage.getEvent() == null || eventImage.getEvent().getId() != eventId) {
                errors.reject("wrong event","다른 이벤트의 이미지 입니다.");
            }
        }
    }

    private void validateImage(Long id, Errors errors) {
        Optional<EventImage> optionalEventImage = eventImageRepository.findById(id);
        if (!optionalEventImage.isPresent()) {
            errors.reject("eventImage doesn't exist","존재하지 않는 이벤트 이미지 id 입니다.");
        }
    }

    private void validateThumbnail(Long thumbnailId, Errors errors) {
        Optional<EventThumbnail> optionalEventThumbnail = eventThumbnailRepository.findById(thumbnailId);
        if (!optionalEventThumbnail.isPresent()) {
            errors.reject("eventThumbnail doesn't exist","존재하지 않는 썸네일 id 입니다.");
        }
    }
}
