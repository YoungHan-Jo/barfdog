package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.CouponListResponseDto;
import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.CouponRepository;
import com.bi.barfdog.service.CouponService;
import com.bi.barfdog.validator.CouponValidator;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/coupons", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CouponApiController {

    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final CouponValidator couponValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping
    public ResponseEntity createCoupon(@RequestBody @Valid CouponSaveRequestDto requestDto,
                                       Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        couponValidator.validateDto(requestDto, errors);
        if (errors.hasErrors()) return badRequest(errors);
        couponValidator.validateDuplicate(requestDto, errors);
        if (errors.hasErrors()) return conflict(errors);

        couponService.createCoupon(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponApiController.class);
        WebMvcLinkBuilder locationLinkBuilder = selfLinkBuilder.slash("direct");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(locationLinkBuilder.withRel("query-direct-coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-coupon").withRel("profile"));

        return ResponseEntity.created(locationLinkBuilder.toUri()).body(representationModel);
    }

    @GetMapping("/direct")
    public ResponseEntity queryCouponsDirect(@RequestParam String keyword) {
        List<CouponListResponseDto> responseDtoList = couponRepository.findRedirectCouponsByKeyword(keyword);

        List<EntityModel> entityModelList = new ArrayList<>();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponApiController.class).slash("direct");

        for (CouponListResponseDto responseDto : responseDtoList) {

            EntityModel<CouponListResponseDto> entityModel = EntityModel.of(responseDto,
                    selfLinkBuilder.withSelfRel(),
                    linkTo(CouponApiController.class).slash(responseDto.getId()).withRel("update-coupon"),
                    profileRootUrlBuilder.slash("index.html#resources-query-direct-coupon").withRel("profile")
                    );
        }


        CollectionModel<EntityModel> collectionModel = CollectionModel.of(entityModelList);

        return ResponseEntity.ok(collectionModel);
    }





    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }

}
