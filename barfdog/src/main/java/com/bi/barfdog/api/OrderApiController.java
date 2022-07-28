package com.bi.barfdog.api;

import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.api.resource.GeneralOrdersDtoResource;
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

        OrderResponseDto responseDto = orderService.orderGeneralOrder(member, requestDto);

        EntityModel<OrderResponseDto> entityModel = EntityModel.of(responseDto);
        entityModel.add(linkTo(OrderApiController.class).slash("general").withSelfRel());
        entityModel.add(linkTo(OrderApiController.class).slash(responseDto.getId()).slash("general/success").withRel("success_generalOrder"));
        entityModel.add(linkTo(OrderApiController.class).slash(responseDto.getId()).slash("general/fail").withRel("fail_generalOrder"));
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-order-generalOrder").withRel("profile"));

        return ResponseEntity.ok(entityModel);
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

        EntityModel<OrderSheetSubscribeResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(OrderApiController.class).slash("sheet/subscribe").slash(id).withSelfRel(),
                linkTo(OrderApiController.class).slash("subscribe").slash(id).withRel("order_subscribe"),
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

        OrderResponseDto responseDto = orderService.orderSubscribeOrder(member, id, requestDto);

        EntityModel<OrderResponseDto> entityModel = EntityModel.of(responseDto);
        entityModel.add(linkTo(OrderApiController.class).slash("subscribe").slash(id).withSelfRel());
        entityModel.add(linkTo(OrderApiController.class).slash(responseDto.getId()).slash("subscribe/success").withRel("success_subscribeOrder"));
        entityModel.add(linkTo(OrderApiController.class).slash(responseDto.getId()).slash("subscribe/fail").withRel("fail_subscribeOrder"));
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-order-subscribeOrder").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/{id}/subscribe/success")
    public ResponseEntity successSubscribeOrder(@CurrentUser Member member,
                                                @PathVariable Long id,
                                                @RequestBody @Valid SuccessSubscribeRequestDto requestDto,
                                                Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent()) return notFound();

        orderService.successSubscribeOrder(id, member, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash(id).slash("subscribe/success").slash(id).withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-success-subscribeOrder").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/{id}/subscribe/fail")
    public ResponseEntity failSubscribeOrder(@CurrentUser Member member,
                                             @PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent()) return notFound();

        orderService.failSubscribeOrder(id, member);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash(id).slash("subscribe/fail").slash(id).withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-fail-subscribeOrder").withRel("profile"));

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

    @GetMapping("/general")
    public ResponseEntity queryGeneralOrders(@CurrentUser Member member,
                                             Pageable pageable,
                                             PagedResourcesAssembler<QueryGeneralOrdersDto> assembler) {

        Page<QueryGeneralOrdersDto> page = orderRepository.findGeneralOrdersDto(member, pageable);

        PagedModel<GeneralOrdersDtoResource> pagedModel = assembler.toModel(page, e -> new GeneralOrdersDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-generalOrders").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }


    @GetMapping("/{id}/general")
    public ResponseEntity queryGeneralOrder(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent()) return notFound();

        QueryGeneralOrderDto responseDto = orderRepository.findGeneralOrderDto(id);

        EntityModel<QueryGeneralOrderDto> entityModel = EntityModel.of(responseDto,
                linkTo(OrderApiController.class).slash(id).slash("general").withSelfRel(),
                linkTo(OrderApiController.class).slash(id).slash("general/cancelRequest").withRel("generalOrder_cancel_request"),
                profileRootUrlBuilder.slash("index.html#resources-query-generalOrder").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/{id}/general/cancelRequest")
    public ResponseEntity generalOrderCancelRequest(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(!optionalOrder.isPresent()) return notFound();

        orderService.cancelRequestGeneral(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash(id).slash("general/cancelRequest").slash(id).withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-cancelRequest-general").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/{id}/subscribe/cancelRequest")
    public ResponseEntity subscribeOrderCancelRequest(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(!optionalOrder.isPresent()) return notFound();

        orderService.cancelRequestSubscribe(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash(id).slash("subscribe/cancelRequest").slash(id).withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-cancelRequest-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/confirm")
    public ResponseEntity confirmOrders(@CurrentUser Member member,
                                        @RequestBody ConfirmOrderItemsDto requestDto) {

        orderService.confirmOrders(member, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash("general/confirm").withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-confirm-generalOrders").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/return")
    public ResponseEntity requestReturnOrders(@RequestBody @Valid RequestReturnExchangeOrdersDto requestDto,
                                              Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        orderService.requestReturn(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash("general/return").withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-return-generalOrders").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/exchange")
    public ResponseEntity requestExchangeOrders(@RequestBody @Valid RequestReturnExchangeOrdersDto requestDto,
                                              Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        orderService.requestExchange(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderApiController.class).slash("general/exchange").withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-exchange-generalOrders").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }






    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }



}
