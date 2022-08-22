package com.bi.barfdog.api;

import com.bi.barfdog.api.deliveryDto.OrderIdListDto;
import com.bi.barfdog.api.deliveryDto.QueryOrderInfoForDelivery;
import com.bi.barfdog.api.deliveryDto.UpdateDeliveryNumberDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/deliveries",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class DeliveryAdminController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping("/info")
    public ResponseEntity queryInfo(@RequestBody OrderIdListDto requestDto) {

        List<QueryOrderInfoForDelivery> responseDto = deliveryService.queryInfoForGoodsFlow(requestDto);

        CollectionModel<QueryOrderInfoForDelivery> collectionModel = CollectionModel.of(responseDto,
                linkTo(DeliveryAdminController.class).slash("info").withSelfRel(),
                linkTo(DeliveryAdminController.class).slash("deliveryNumber").withRel("update_deliveryNumber"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-deliveries-info").withRel("profile")
                );

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping("/deliveryNumber")
    public ResponseEntity updateDeliveryNumber(@RequestBody @Valid UpdateDeliveryNumberDto requestDto,
                                               Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        deliveryService.setDeliveryNumber(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(DeliveryAdminController.class).slash("deliveryNumber").withSelfRel());
        representationModel.add(linkTo(OrderAdminController.class).slash("search").withRel("query_orders"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-update-deliveryNumber").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {

        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }


}
