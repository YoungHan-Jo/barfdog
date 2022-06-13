package com.bi.barfdog.api;

import com.bi.barfdog.api.eventDto.QueryEventDto;
import com.bi.barfdog.api.eventDto.QueryEventsDto;
import com.bi.barfdog.api.resource.EventDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.event.Event;
import com.bi.barfdog.repository.event.EventRepository;
import com.bi.barfdog.repository.event.EventThumbnailRepository;
import com.bi.barfdog.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class EventApiController {

    private final EventService eventService;

    private final EventRepository eventRepository;

    private final EventThumbnailRepository eventThumbnailRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<QueryEventsDto> assembler) {
        Page<QueryEventsDto> page = eventThumbnailRepository.findEventDtos(pageable);

        PagedModel<EntityModel<QueryEventsDto>> pagedModel = assembler.toModel(page, e -> new EventDtoResource(e));

        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-events").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryEvent(@PathVariable Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(!optionalEvent.isPresent()) return notFound();
        QueryEventDto responseDto = eventRepository.findEventDto(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventApiController.class).slash(id);

        EntityModel<QueryEventDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(EventApiController.class).withRel("query_events"),
                profileRootUrlBuilder.slash("index.html#resources-query-event").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }









    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }



}
