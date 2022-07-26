package com.bi.barfdog.api;

import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.api.resource.AdminOrdersDtoResource;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
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
@RequestMapping(value = "/api/admin/orders", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class OrderAdminController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping("/search")
    public ResponseEntity queryOrders(Pageable pageable,
                                      PagedResourcesAssembler<QueryAdminOrdersDto> assembler,
                                      @RequestBody @Valid OrderAdminCond cond,
                                      Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        Page<QueryAdminOrdersDto> page = orderRepository.findAdminOrdersDto(pageable, cond);

        PagedModel<AdminOrdersDtoResource> pagedModel = assembler.toModel(page, e -> new AdminOrdersDtoResource(e));

        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-admin-orders").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}/general")
    public ResponseEntity queryGeneralOrder(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent()) return notFound();

        QueryAdminGeneralOrderDto responseDto = orderRepository.findAdminGeneralOrderDto(id);

        EntityModel<QueryAdminGeneralOrderDto> entityModel = EntityModel.of(responseDto,
                linkTo(OrderAdminController.class).slash(id).slash("general").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-admin-order-general").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/orderItem/{id}")
    public ResponseEntity queryOrderItem(@PathVariable Long id) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(id);
        if (!optionalOrderItem.isPresent()) return notFound();

        QueryAdminOrderItemDto responseDto = orderItemRepository.findAdminOrderItemDto(id);

        EntityModel<QueryAdminOrderItemDto> entityModel = EntityModel.of(responseDto,
                linkTo(OrderAdminController.class).slash("orderItem").slash(id).withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-admin-order-orderItem").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }


    @GetMapping("/{id}/subscribe")
    public ResponseEntity querySubscribeOrder(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (!optionalOrder.isPresent()) return notFound();

        QueryAdminSubscribeOrderDto responseDto = orderRepository.findAdminSubscribeOrderDto(id);

        EntityModel<QueryAdminSubscribeOrderDto> entityModel = EntityModel.of(responseDto,
                linkTo(OrderAdminController.class).slash(id).slash("subscribe").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-admin-order-subscribe").withRel("profile")
        );


        return ResponseEntity.ok(entityModel);
    }


    @PostMapping("/general/orderConfirm")
    public ResponseEntity orderConfirmGeneral(@RequestBody OrderConfirmGeneralDto requestDto) {

        orderService.orderConfirmGeneral(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/orderConfirm").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-orderConfirm-general").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/subscribe/orderConfirm")
    public ResponseEntity orderConfirmSubscribe(@RequestBody OrderConfirmSubscribeDto requestDto) {

        orderService.orderConfirmSubscribe(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("subscribe/orderConfirm").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-orderConfirm-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }


    @PostMapping("/general/cancelConfirm")
    public ResponseEntity cancelConfirmGeneral(@RequestBody CancelConfirmGeneralDto requestDto) {

        orderService.cancelConfirmGeneral(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/cancelConfirm").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-cancelConfirm-general").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/subscribe/cancelConfirm")
    public ResponseEntity cancelConfirmSubscribe(@RequestBody CancelConfirmSubscribeDto requestDto) {

        orderService.cancelConfirmSubscribe(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("subscribe/cancelConfirm").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-cancelConfirm-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/orderCancel")
    public ResponseEntity orderCancelGeneral(@RequestBody OrderCancelGeneralDto requestDto) {

        orderService.orderCancelGeneral(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/orderCancel").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-orderCancel-general").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/subscribe/orderCancel")
    public ResponseEntity orderCancelGeneral(@RequestBody OrderCancelSubscribeDto requestDto) {

        orderService.orderCancelSubscribe(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("subscribe/orderCancel").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-orderCancel-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/denyReturn")
    public ResponseEntity denyReturn(@RequestBody OrderItemIdListDto requestDto) {

        orderService.denyReturn(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/denyReturn").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-denyReturn").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/denyExchange")
    public ResponseEntity denyExchange(@RequestBody OrderItemIdListDto requestDto) {

        orderService.denyExchange(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/denyExchange").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-denyExchange").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/confirmExchange/buyer")
    public ResponseEntity confirmExchangeBuyer(@RequestBody OrderItemIdListDto requestDto) {

        orderService.confirmExchange(requestDto, OrderStatus.EXCHANGE_DONE_BUYER);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/confirmExchange/buyer").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-confirmExchange-buyer").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/confirmExchange/seller")
    public ResponseEntity confirmExchangeSeller(@RequestBody OrderItemIdListDto requestDto) {

        orderService.confirmExchange(requestDto, OrderStatus.EXCHANGE_DONE_SELLER);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/confirmExchange/seller").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-confirmExchange-seller").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/confirmReturn/seller")
    public ResponseEntity confirmReturnSeller(@RequestBody OrderItemIdListDto requestDto) {

        orderService.confirmReturn(requestDto, OrderStatus.RETURN_DONE_SELLER);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/confirmReturn/seller").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-confirmReturn-seller").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/general/confirmReturn/buyer")
    public ResponseEntity confirmReturnBuyer(@RequestBody OrderItemIdListDto requestDto) {

        orderService.confirmReturn(requestDto, OrderStatus.RETURN_DONE_BUYER);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/confirmReturn/buyer").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-confirmReturn-buyer").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }




    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }


}
