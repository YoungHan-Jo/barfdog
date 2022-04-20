package com.bi.barfdog.api;

import com.bi.barfdog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/coupons", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CouponApiController {

    private final CouponRepository couponRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping
    public ResponseEntity createCoupon() {


    }

}
