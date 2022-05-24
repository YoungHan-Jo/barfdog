package com.bi.barfdog.validator;

import com.bi.barfdog.api.eventDto.EventSaveDto;
import com.bi.barfdog.domain.event.EventImage;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.repository.EventImageRepository;
import com.bi.barfdog.repository.EventThumbnailRepository;
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
        Long thumbnailId = requestDto.getThumbnailId();
        Optional<EventThumbnail> optionalEventThumbnail = eventThumbnailRepository.findById(thumbnailId);
        if (!optionalEventThumbnail.isPresent()) {
            errors.reject("eventThumbnail doesn't exist","존재하지 않는 썸네일 id 입니다.");
        }

        List<EventImageRequestDto> eventImageRequestDtoList = requestDto.getEventImageRequestDtoList();
        for (EventImageRequestDto eventImageRequestDto : eventImageRequestDtoList) {
            Optional<EventImage> optionalEventImage = eventImageRepository.findById(eventImageRequestDto.getId());
            if (!optionalEventImage.isPresent()) {
                errors.reject("eventImage doesn't exist","존재하지 않는 이벤트 이미지 id 입니다.");
            }
        }


    }
}
