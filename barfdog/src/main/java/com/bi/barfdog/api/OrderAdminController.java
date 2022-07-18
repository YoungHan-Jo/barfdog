package com.bi.barfdog.api;

import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.api.resource.AdminOrdersDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.order.Order;
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
    public ResponseEntity orderConfirmGeneral(@RequestBody OrderConfirmGeneralRequestDto requestDto) {

        orderService.orderConfirmGeneral(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("general/orderConfirm").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-query-admin-orderConfirm-general").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/subscribe/orderConfirm")
    public ResponseEntity orderConfirmSubscribe(@RequestBody OrderConfirmSubscribeRequestDto requestDto) {

        orderService.orderConfirmSubscribe(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(OrderAdminController.class).slash("subscribe/orderConfirm").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-query-admin-orderConfirm-subscribe").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }






    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }


}
