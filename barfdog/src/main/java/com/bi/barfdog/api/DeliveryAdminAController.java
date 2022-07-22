package com.bi.barfdog.api;

import com.bi.barfdog.api.deliveryDto.OrderIdListDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/deliveries",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class DeliveryAdminAController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping("/info")
    public ResponseEntity queryInfo(@RequestBody OrderIdListDto requestDto) {

        deliveryService.queryInfoForGoodsFlow(requestDto);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/deliveryNumber")
    public ResponseEntity updateDeliveryNumber() {



        return ResponseEntity.ok(null);
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
