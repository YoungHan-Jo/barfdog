package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.BlogSaveDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
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
@RequestMapping(value = "/api/blogs",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class BlogApiController {

    private final BlogRepository blogRepository;
    private final BlogService blogService;

    private WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");













    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
