package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.*;
import com.bi.barfdog.api.resource.AdminCouponsDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.service.CouponService;
import com.bi.barfdog.validator.CouponValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/coupons",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CouponAdminController {

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

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class);
        WebMvcLinkBuilder locationLinkBuilder = selfLinkBuilder.slash("direct");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(locationLinkBuilder.withRel("query_direct_coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-coupon").withRel("profile"));

        return ResponseEntity.created(locationLinkBuilder.toUri()).body(representationModel);
    }

    @GetMapping("/direct")
    public ResponseEntity queryCouponsDirect(Pageable pageable,
                                             PagedResourcesAssembler<CouponListResponseDto> assembler,
                                             @RequestParam(value = "keyword",required = false,defaultValue = "") String keyword) {

        Page<CouponListResponseDto> page = couponRepository.findRedirectCouponsByKeyword(keyword, pageable);

        PagedModel<AdminCouponsDtoResource> pagedModel = assembler.toModel(page, e -> new AdminCouponsDtoResource(e));
        pagedModel.add(linkTo(CouponAdminController.class).slash("auto?keyword= ").withRel("query_auto_coupons"));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-direct-coupons").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/auto")
    public ResponseEntity queryCouponsAuto(Pageable pageable,
                                           PagedResourcesAssembler<CouponListResponseDto> assembler,
                                           @RequestParam(value = "keyword",required = false,defaultValue = "") String keyword) {

        Page<CouponListResponseDto> page = couponRepository.findAutoCouponsByKeyword(keyword, pageable);

        PagedModel<AdminCouponsDtoResource> pagedModel = assembler.toModel(page, e -> new AdminCouponsDtoResource(e));

        pagedModel.add(linkTo(CouponAdminController.class).slash("direct?keyword= ").withRel("query_direct_coupons"));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-auto-coupons").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
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

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash(id).slash("inactive");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(CouponAdminController.class).slash("direct?keyword= ").withRel("query_direct_coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-coupon-inactive").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/publication/general")
    public ResponseEntity queryGeneralCouponsWhenPublication() {

        List<PublicationCouponDto> responseDtoList = couponRepository.findPublicationCouponDtosByCouponType(CouponType.GENERAL_PUBLISHED);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash("publication").slash("general");

        CollectionModel<PublicationCouponDto> collectionModel = CollectionModel.of(responseDtoList,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-general-coupons-in-publication").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/publication/code")
    public ResponseEntity queryCodeCouponsWhenPublication() {

        List<PublicationCouponDto> responseDtoList = couponRepository.findPublicationCouponDtosByCouponType(CouponType.CODE_PUBLISHED);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash("publication").slash("code");

        CollectionModel<PublicationCouponDto> collectionModel = CollectionModel.of(responseDtoList,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-code-coupons-in-publication").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping("/personal")
    public ResponseEntity publishCouponsPersonal(@RequestBody @Valid PersonalPublishRequestDto requestDto,
                                                 Errors errors) throws IOException {
        if(errors.hasErrors()) return badRequest(errors);
        Optional<Coupon> optionalCoupon = couponRepository.findById(requestDto.getCouponId());
        if (!optionalCoupon.isPresent()) return notFound();
        couponValidator.validateCouponType(requestDto.getCouponId(), requestDto.getCouponType(), errors);
        couponValidator.validateExpiredDate(requestDto.getExpiredDate(), errors);
        couponValidator.validateCouponStatus(requestDto.getCouponId(), errors);
        if(errors.hasErrors()) return badRequest(errors);

        couponService.publishCouponsToPersonal(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash("personal");
        WebMvcLinkBuilder locationUrlBuilder = linkTo(CouponAdminController.class).slash("direct?keyword= ");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(locationUrlBuilder.withRel("query_direct_coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-publish-coupon-personal").withRel("profile"));

        return ResponseEntity.created(locationUrlBuilder.toUri()).body(representationModel);
    }

    @PostMapping("/group")
    public ResponseEntity publishCouponsGroup(@RequestBody @Valid GroupPublishRequestDto requestDto,
                                              Errors errors) throws IOException {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Coupon> optionalCoupon = couponRepository.findById(requestDto.getCouponId());
        if (!optionalCoupon.isPresent()) return notFound();
        couponValidator.validateBirthYear(requestDto, errors);
        couponValidator.validateCouponType(requestDto.getCouponId(), requestDto.getCouponType(), errors);
        couponValidator.validateCouponStatus(requestDto.getCouponId(), errors);
        couponValidator.validateExpiredDate(requestDto.getExpiredDate(), errors);
        if(errors.hasErrors()) return badRequest(errors);

        couponService.publishCouponsToGroup(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash("group");
        WebMvcLinkBuilder locationUrlBuilder = linkTo(CouponAdminController.class).slash("direct?keyword= ");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(locationUrlBuilder.withRel("query_direct_coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-publish-coupon-group").withRel("profile"));

        return ResponseEntity.created(locationUrlBuilder.toUri()).body(representationModel);
    }

    @PostMapping("/all")
    public ResponseEntity publishCouponsAll(@RequestBody @Valid AllPublishRequestDto requestDto,
                                            Errors errors) throws IOException {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Coupon> optionalCoupon = couponRepository.findById(requestDto.getCouponId());
        if (!optionalCoupon.isPresent()) return notFound();
        couponValidator.validateCouponType(requestDto.getCouponId(), requestDto.getCouponType(), errors);
        couponValidator.validateExpiredDate(requestDto.getExpiredDate(), errors);
        couponValidator.validateCouponStatus(requestDto.getCouponId(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        couponService.publishCouponsToAll(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash("all");
        WebMvcLinkBuilder locationUrlBuilder = linkTo(CouponAdminController.class).slash("direct?keyword= ");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(locationUrlBuilder.withRel("query_direct_coupons"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-publish-coupon-all").withRel("profile"));

        return ResponseEntity.created(locationUrlBuilder.toUri()).body(representationModel);
    }

    @GetMapping("/auto/modification")
    public ResponseEntity queryAutoCouponsForUpdate() {
        List<AutoCouponsForUpdateDto> responseDto = couponRepository.findAutoCouponDtosForUpdate();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash("auto").slash("modification");

        CollectionModel<AutoCouponsForUpdateDto> collectionModel = CollectionModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_auto_coupons"),
                profileRootUrlBuilder.slash("index.html#resources-query-auto-coupons-modification").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/auto/modification")
    public ResponseEntity updateAutoCoupons(@RequestBody @Valid UpdateAutoCouponRequest requestDto,
                                            Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        couponService.updateAutoCoupons(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(CouponAdminController.class).slash("auto").slash("modification");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("query_auto_coupons_modification"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-auto-coupons").withRel("profile"));

        return ResponseEntity.ok(representationModel);
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
