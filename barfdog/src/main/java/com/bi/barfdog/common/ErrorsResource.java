package com.bi.barfdog.common;

import com.bi.barfdog.api.IndexApiController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public ErrorsResource(Errors content, Link... links) {
        super(content, Arrays.asList(links));
        add(linkTo(methodOn(IndexApiController.class).index()).withRel("index"));
    }
}
