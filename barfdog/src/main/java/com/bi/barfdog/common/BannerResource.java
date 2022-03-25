package com.bi.barfdog.common;

import com.bi.barfdog.api.BannerApiController;
import com.bi.barfdog.domain.banner.Banner;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BannerResource extends EntityModel<Banner> {
    public BannerResource(Banner banner, Link... links) {
        super(banner, Arrays.asList(links));
        add(linkTo(BannerApiController.class).slash(banner.getId()).withSelfRel());
    }
}
