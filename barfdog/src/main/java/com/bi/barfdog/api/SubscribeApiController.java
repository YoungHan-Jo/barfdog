package com.bi.barfdog.api;

import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
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
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-query-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/{id}/orderSheet")
    public ResponseEntity queryOrderSheet(@PathVariable Long id) {


        return ResponseEntity.ok(null);
    }






    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
