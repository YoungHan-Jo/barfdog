package com.bi.barfdog.api;

import com.bi.barfdog.api.resource.SubscribesDtoResource;
import com.bi.barfdog.api.subscribeDto.QuerySubscribeDto;
import com.bi.barfdog.api.subscribeDto.QuerySubscribesDto;
import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.service.SubscribeService;
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

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/subscribes",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class SubscribeApiController {

    private final SubscribeRepository subscribeRepository;
    private final SubscribeService subscribeService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PutMapping("/{id}")
    public ResponseEntity updateSubscribe(@CurrentUser Member member,
                                          @PathVariable Long id,
                                          @RequestBody @Valid UpdateSubscribeDto requestDto,
                                          Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if (!optionalSubscribe.isPresent()) return notFound();

        subscribeService.updateSubscribe(id, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(SubscribeApiController.class).slash(id).withSelfRel());
        representationModel.add(linkTo(OrderApiController.class).slash("sheet/subscribe").slash(id).withRel("query_orderSheet_subscribe"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping
    public ResponseEntity querySubscribes(@CurrentUser Member member,
                                          Pageable pageable,
                                          PagedResourcesAssembler<QuerySubscribesDto> assembler) {

        Page<QuerySubscribesDto> page = subscribeRepository.findSubscribesDto(member, pageable);

        PagedModel<SubscribesDtoResource> pagedModel = assembler.toModel(page, e -> new SubscribesDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-subscribes").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity querySubscribe(@CurrentUser Member member,
                                         @PathVariable Long id) {
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if (!optionalSubscribe.isPresent()) return notFound();

        QuerySubscribeDto responseDto = subscribeRepository.findSubscribeDto(member, id);

        EntityModel<QuerySubscribeDto> entityModel = EntityModel.of(responseDto,
                linkTo(SubscribeApiController.class).slash(id).withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-subscribe").withRel("profile")
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
