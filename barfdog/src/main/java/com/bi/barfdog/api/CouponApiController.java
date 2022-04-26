package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.CouponListResponseDto;
import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.api.couponDto.PersonalPublishRequestDto;
import com.bi.barfdog.api.couponDto.PublicationCouponDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponType;
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
import java.util.Optional;

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
                    linkTo(CouponApiController.class).slash(responseDto.getId()).slash("inactive").withRel("inactive-coupon")
                    );

            entityModel.add();

            entityModelList.add(entityModel);
        }


        CollectionModel<EntityModel> collectionModel = CollectionModel.of(entityModelList,
                selfLinkBuilder.withSelfRel(),
                linkTo(CouponApiController.class).slash("auto?keyword= ").withRel("query-auto-coupons"),
                profileRootUrlBuilder.slash("index.html#resources-query-direct-coupons").withRel("profile")
                );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/auto")
    public ResponseEntity queryCouponsAuto(@RequestParam String keyword) {
        List<CouponListResponseDto> responseDtoList = couponRepository.findAutoCouponsByKeyword(keyword);

        List<EntityModel> entityModelList = new ArrayList<>();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponApiController.class).slash("auto");

        for (CouponListResponseDto responseDto : responseDtoList) {

            EntityModel<CouponListResponseDto> entityModel = EntityModel.of(responseDto);

            entityModelList.add(entityModel);
        }

        CollectionModel<EntityModel> collectionModel = CollectionModel.of(entityModelList,
                selfLinkBuilder.withSelfRel(),
                linkTo(CouponApiController.class).slash("direct?keyword= ").withRel("query-direct-coupons"),
                profileRootUrlBuilder.slash("index.html#resources-query-auto-coupons").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}/inactive")
    public ResponseEntity inactiveCoupon(@PathVariable Long id) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(id);
        if (!optionalCoupon.isPresent()) return notFound();

        Coupon coupon = optionalCoupon.get();
        CouponType couponType = coupon.getCouponType();
        if (couponType == CouponType.AUTO_PUBLISHED) {
            return ResponseEntity.badRequest().body(null);
        }

        couponService.inactiveCoupon(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponApiController.class).slash(id).slash("inactive");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(CouponApiController.class).slash("direct?keyword= ").withRel("query-direct-coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-coupon-inactive").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/publication/general")
    public ResponseEntity queryGeneralCouponsWhenPublication() {

        List<PublicationCouponDto> responseDtoList = couponRepository.findPublicationCouponDtosByCouponType(CouponType.GENERAL_PUBLISHED);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponApiController.class).slash("publication").slash("general");

        CollectionModel<PublicationCouponDto> collectionModel = CollectionModel.of(responseDtoList,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-general-coupons-in-publication").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/publication/code")
    public ResponseEntity queryCodeCouponsWhenPublication() {

        List<PublicationCouponDto> responseDtoList = couponRepository.findPublicationCouponDtosByCouponType(CouponType.CODE_PUBLISHED);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponApiController.class).slash("publication").slash("code");

        CollectionModel<PublicationCouponDto> collectionModel = CollectionModel.of(responseDtoList,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-code-coupons-in-publication").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping("/personal")
    public ResponseEntity publishCouponsPersonal(@RequestBody @Valid PersonalPublishRequestDto requestDto,
                                                 Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        Optional<Coupon> optionalCoupon = couponRepository.findById(requestDto.getCouponId());
        if (!optionalCoupon.isPresent()) return notFound();

        couponValidator.validateCouponType(requestDto, errors);
        if(errors.hasErrors()) return badRequest(errors);

        couponService.publishCouponsToPersonal(requestDto);




        return ResponseEntity.created(null).body(null);
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
