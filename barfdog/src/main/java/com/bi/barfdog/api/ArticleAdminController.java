package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.QueryArticlesAdminDto;
import com.bi.barfdog.api.blogDto.UpdateArticlesRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.repository.blog.BlogRepository;
import com.bi.barfdog.service.BlogService;
import com.bi.barfdog.validator.BlogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/articles", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ArticleAdminController {

    private final BlogRepository blogRepository;
    private final BlogService blogService;

    private final BlogValidator blogValidator; assdfg

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping
    public ResponseEntity queryArticles() {

        QueryArticlesAdminDto responseDto = blogService.getArticlesAdmin();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ArticleAdminController.class);

        EntityModel<QueryArticlesAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_article"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-articles").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping
    public ResponseEntity updateArticles(@RequestBody @Valid UpdateArticlesRequestDto requestDto,
                                         Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Blog> optionalBlog1 = blogRepository.findById(requestDto.getFirstBlogId());
        Optional<Blog> optionalBlog2 = blogRepository.findById(requestDto.getSecondBlogId());
        if (!optionalBlog1.isPresent() || !optionalBlog2.isPresent()) return notFound();
        validateBadRequest(requestDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        blogService.updateArticles(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ArticleAdminController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("admin_query_articles"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-articles").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }




    private void validateBadRequest(UpdateArticlesRequestDto requestDto, Errors errors) {
        blogValidator.validateIsNotice(requestDto, errors);
        blogValidator.validateHiddenStatus(requestDto, errors);
        blogValidator.validateDuplicateBlogId(requestDto, errors);
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }


}
