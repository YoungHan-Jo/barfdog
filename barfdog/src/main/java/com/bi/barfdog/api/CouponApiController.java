package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.CouponRepository;
import com.bi.barfdog.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/coupons", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CouponApiController {

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping
    public ResponseEntity createCoupon(@RequestBody @Valid CouponSaveRequestDto requestDto,
                                       Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        couponService.createCoupon(requestDto);




        return ResponseEntity.created(null).body(null);
    }





    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
