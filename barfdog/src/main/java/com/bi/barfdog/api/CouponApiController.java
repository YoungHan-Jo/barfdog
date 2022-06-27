package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.*;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.service.CouponService;
import com.bi.barfdog.validator.CouponValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/coupons", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CouponApiController {

    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final CouponValidator couponValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping
    public ResponseEntity queryCoupons(@CurrentUser Member member,
                                       Pageable pageable,
                                       PagedResourcesAssembler<QueryCouponsDto> assembler) {

        QueryCouponsPageDto responseDto = couponRepository.findCouponsPage(member, pageable, assembler);

        EntityModel<QueryCouponsPageDto> entityModel = EntityModel.of(responseDto,
                linkTo(CouponApiController.class).withSelfRel(),
                linkTo(CouponApiController.class).slash("code").withRel("get_code_coupon"),
                profileRootUrlBuilder.slash("index.html#resources-query-coupons").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/code")
    public ResponseEntity getCodeCoupon(@CurrentUser Member member,
                                        @RequestBody @Valid CodeCouponRequestDto requestDto,
                                        Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        couponValidator.validateCodeAndPassword(member, requestDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        couponService.getCodeCoupon(member, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(CouponApiController.class).slash("code").withSelfRel());
        representationModel.add(linkTo(CouponApiController.class).withRel("query_coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-get-code-coupon").withRel("profile"));

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
