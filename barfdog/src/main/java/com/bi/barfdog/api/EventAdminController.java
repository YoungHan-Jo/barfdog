package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.UploadedImageAdminDto;
import com.bi.barfdog.api.eventDto.EventSaveDto;
import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.api.eventDto.QueryEventsAdminDto;
import com.bi.barfdog.api.eventDto.UpdateEventRequestDto;
import com.bi.barfdog.api.resource.EventAdminDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.event.Event;
import com.bi.barfdog.repository.event.EventRepository;
import com.bi.barfdog.service.EventService;
import com.bi.barfdog.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/events",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class EventAdminController {

    private final EventRepository eventRepository;

    private final EventService eventService;

    private final EventValidator eventValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping("/thumbnail")
    public ResponseEntity uploadThumbnail(@RequestPart MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageAdminDto responseDto = eventService.uploadThumbnailFile(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventAdminController.class).slash("thumbnail");

        EntityModel<UploadedImageAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-eventThumbnail").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/image")
    public ResponseEntity uploadImage(@RequestPart MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageAdminDto responseDto = eventService.uploadImage(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventAdminController.class).slash("image");

        EntityModel<UploadedImageAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-eventImage").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventSaveDto requestDto,
                                      Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);

        eventValidator.validateImages(requestDto, errors);
        if(errors.hasErrors()) return badRequest(errors);

        eventService.saveEvent(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(AdminApiController.class).withRel("query_events"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-event").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(representationModel);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<QueryEventsAdminDto> assembler) {

        Page<QueryEventsAdminDto> page = eventRepository.findAdminEventsDtoList(pageable);

        PagedModel<EntityModel<QueryEventsAdminDto>> entityModels = assembler.toModel(page, e -> new EventAdminDtoResource(e));

        entityModels.add(linkTo(EventAdminController.class).withRel("create_event"));
        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-events").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryEvent(@PathVariable Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(!optionalEvent.isPresent()) return notFound();

        QueryEventAdminDto responseDto = eventService.findAdminEvent(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventAdminController.class).slash(id);

        EntityModel<QueryEventAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(EventAdminController.class).withRel("query_events"),
                linkTo(EventAdminController.class).slash(id).withRel("update_event"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-event").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Long id,
                                      @RequestBody @Valid UpdateEventRequestDto requestDto,
                                      Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);

        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(!optionalEvent.isPresent()) return notFound();
        eventValidator.validateImages(requestDto, errors);
        if(errors.hasErrors()) return badRequest(errors);
        eventValidator.validateWrongImages(id,requestDto, errors);
        if(errors.hasErrors()) return badRequest(errors);

        eventService.updateEvent(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(EventAdminController.class).slash(id).withRel("admin_query_event"));
        representationModel.add(linkTo(EventAdminController.class).withRel("admin_query_events"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-update-event").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(!optionalEvent.isPresent()) return notFound();

        eventService.deleteEvent(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(EventAdminController.class).withRel("query_events"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-delete-event").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }









    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }




}
