package com.bi.barfdog.api;

import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.api.resource.SubscribeOrdersDtoResource;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.service.OrderService;
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
@RequestMapping(value = "/api/orders", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class OrderApiController {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final SubscribeRepository subscribeRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping("/sheet/general")
    public ResponseEntity queryOrderSheetGeneral(@CurrentUser Member member,
                                                 @RequestBody @Valid OrderSheetGeneralRequestDto requestDto,
                                                 Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        OrderSheetGeneralResponseDto responseDto = orderService.getOrderSheetGeneralDto(member, requestDto);

        EntityModel<OrderSheetGeneralResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(OrderApiController.class).slash("sheet/general").withSelfRel(),
                linkTo(OrderApiController.class).slash("general").withRel("order_general"),
                profileRootUrlBuilder.slash("index.html#resources-query-orderSheet-general").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/general")
    public ResponseEntity orderGeneralOrder(@CurrentUser Member member,
                                            @RequestBody @Valid GeneralOrderRequestDto requestDto,
                                            Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        Order order = orderService.orderGeneralOrder(member, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash("general").withSelfRel());
        representationModel.add(linkTo(OrderApiController.class).slash(order.getId()).slash("general/success").withRel("success_generalOrder"));
        representationModel.add(linkTo(OrderApiController.class).slash(order.getId()).slash("general/fail").withRel("fail_generalOrder"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-order-generalOrder").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/{id}/general/success")
    public ResponseEntity successGeneralOrder(@CurrentUser Member member,
                                              @PathVariable Long id,
                                              @RequestBody @Valid SuccessGeneralRequestDto requestDto,
                                              Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(!optionalOrder.isPresent()) return notFound();

        orderService.successGeneralOrder(id, member, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash(id).slash("general/success").withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-success-generalOrder").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/{id}/general/fail")
    public ResponseEntity failGeneralOrder(@CurrentUser Member member,
                                           @PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent()) return notFound();

        orderService.failGeneralOrder(id, member);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash(id).slash("general/fail").withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-fail-generalOrder").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/sheet/subscribe/{id}")
    public ResponseEntity queryOrderSheetSubscribe(@CurrentUser Member member,
                                                   @PathVariable Long id) {

        OrderSheetSubscribeResponseDto responseDto = orderService.getOrderSheetSubsDto(member,id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(OrderApiController.class).slash("sheet").slash("subscribe");

        EntityModel<OrderSheetSubscribeResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(OrderApiController.class).slash("subscribe").withRel("order_subscribe"),
                profileRootUrlBuilder.slash("index.html#resources-query-orderSheet-subscribe").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/subscribe/{id}")
    public ResponseEntity orderSubscribe(@CurrentUser Member member,
                                         @PathVariable Long id,
                                         @RequestBody @Valid SubscribeOrderRequestDto requestDto,
                                         Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if(!optionalSubscribe.isPresent()) return notFound();

        Order order = orderService.orderSubscribeOrder(member, id, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash("subscribe").slash(id).withSelfRel());
        representationModel.add(linkTo(OrderApiController.class).slash(order.getId()).slash("subscribe/success").withRel("success_subscribeOrder"));
        representationModel.add(linkTo(OrderApiController.class).slash(order.getId()).slash("subscribe/fail").withRel("fail_subscribeOrder"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-order-subscribeOrder").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }


    @GetMapping("/subscribe")
    public ResponseEntity querySubscribeOrders(@CurrentUser Member member,
                                               Pageable pageable,
                                               PagedResourcesAssembler<QuerySubscribeOrdersDto> assembler) {

        Page<QuerySubscribeOrdersDto> page = orderRepository.findSubscribeOrdersDto(member, pageable);

        PagedModel<SubscribeOrdersDtoResource> pagedModel = assembler.toModel(page, e -> new SubscribeOrdersDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-subscribeOrders").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}/subscribe")
    public ResponseEntity querySubscribeOrder(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent()) return notFound();

        QuerySubscribeOrderDto responseDto = orderRepository.findSubscribeOrderDto(id);

        EntityModel<QuerySubscribeOrderDto> entityModel = EntityModel.of(responseDto,
                linkTo(OrderApiController.class).slash(id).slash("subscribe").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-subscribeOrder").withRel("profile")
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
