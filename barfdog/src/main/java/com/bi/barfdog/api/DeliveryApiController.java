package com.bi.barfdog.api;

import com.bi.barfdog.api.deliveryDto.QueryDeliveriesDto;
import com.bi.barfdog.api.resource.DeliveriesDtoResource;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/deliveries", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class DeliveryApiController {

    private final DeliveryRepository deliveryRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping("/subscribe")
    public ResponseEntity querySubscribeDeliveries(@CurrentUser Member member,
                                                   Pageable pageable,
                                                   PagedResourcesAssembler<QueryDeliveriesDto> assembler) {

        Page<QueryDeliveriesDto> page = deliveryRepository.findDeliveriesDto(member, pageable);

        PagedModel<DeliveriesDtoResource> pagedModel = assembler.toModel(page, e -> new DeliveriesDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-deliveries-subscribe").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
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
