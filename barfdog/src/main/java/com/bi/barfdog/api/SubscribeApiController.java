package com.bi.barfdog.api;

import com.bi.barfdog.api.resource.SubscribesDtoResource;
import com.bi.barfdog.api.subscribeDto.*;
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
import retrofit2.http.Path;

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

    // 플랜 변경 적용하기
    @PutMapping("/{id}")
    public ResponseEntity updateSubscribe(@CurrentUser Member member,
                                          @PathVariable Long id,
                                          @RequestBody @Valid UpdatePlanDto requestDto,
                                          Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if (!optionalSubscribe.isPresent()) return notFound();

        subscribeService.updatePlan(id, requestDto);

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

    @PostMapping("/{id}/coupon")
    public ResponseEntity useCouponToSubscribe(@PathVariable Long id,
                                               @RequestBody @Valid UseCouponDto requestDto,
                                               Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if (!optionalSubscribe.isPresent()) return notFound();

        subscribeService.useCoupon(id,requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(SubscribeApiController.class).slash(id).slash("coupon").withSelfRel());
        representationModel.add(linkTo(SubscribeApiController.class).slash(id).withRel("query_subscribe"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-use-coupon-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/{id}/gram")
    public ResponseEntity updateGramSubscribe(@PathVariable Long id,
                                              @RequestBody @Valid UpdateGramDto requestDto,
                                              Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if (!optionalSubscribe.isPresent()) return notFound();

        subscribeService.updateGram(id, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(SubscribeApiController.class).slash(id).slash("gram").withSelfRel());
        representationModel.add(linkTo(SubscribeApiController.class).slash(id).withRel("query_subscribe"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-gram-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/{id}/plan")
    public ResponseEntity updatePlanSubscribe(@PathVariable Long id,
                                              @RequestBody @Valid UpdatePlanDto requestDto,
                                              Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if (!optionalSubscribe.isPresent()) return notFound();

        subscribeService.updatePlan(id, requestDto);


        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(SubscribeApiController.class).slash(id).slash("plan").withSelfRel());
        representationModel.add(linkTo(SubscribeApiController.class).slash(id).withRel("query_subscribe"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-plan-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/{id}/skip/{count}")
    public ResponseEntity skipSubscribe(@PathVariable Long id,
                                        @PathVariable int count) {


    }







    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
