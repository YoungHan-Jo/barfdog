package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.ArticlesDto;
import com.bi.barfdog.api.blogDto.QueryBlogDto;
import com.bi.barfdog.api.blogDto.QueryBlogsDto;
import com.bi.barfdog.api.resource.BlogsDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.repository.ArticleRepository;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/blogs",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class BlogApiController {

    private final ArticleRepository articleRepository;
    private final BlogRepository blogRepository;
    private final BlogService blogService;

    private WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping("/articles")
    public ResponseEntity queryArticles() {
        List<ArticlesDto> responseDto = articleRepository.findArticlesDto();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogApiController.class).slash("articles");

        List<EntityModel<ArticlesDto>> entityModels = new ArrayList<>();

        for (ArticlesDto articlesDto : responseDto) {
            EntityModel<ArticlesDto> entityModel = EntityModel.of(articlesDto,
                    linkTo(BlogApiController.class).slash(articlesDto.getId()).withRel("query_blog")
            );
            entityModels.add(entityModel);
        }

        CollectionModel<EntityModel<ArticlesDto>> collectionModel = CollectionModel.of(entityModels,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-articles").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping
    public ResponseEntity queryBlogs(Pageable pageable,
                                     PagedResourcesAssembler<QueryBlogsDto> assembler) {
        Page<QueryBlogsDto> page = blogRepository.findBlogsDto(pageable);

        PagedModel<EntityModel<QueryBlogsDto>> pagedModel = assembler.toModel(page, e -> new BlogsDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-blogs").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity queryBlogsByCategory(@PathVariable String category,
                                               Pageable pageable,
                                               PagedResourcesAssembler<QueryBlogsDto> assembler) {
        try {

            BlogCategory blogCategory = null;
            blogCategory = BlogCategory.valueOf(category.toUpperCase());
            Page<QueryBlogsDto> page = blogRepository.findBlogsDtoByCategory(pageable, blogCategory);

            PagedModel<EntityModel<QueryBlogsDto>> pagedModel = assembler.toModel(page, e -> new BlogsDtoResource(e));
            pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-blogs-category").withRel("profile"));
            return ResponseEntity.ok(pagedModel);

        } catch (IllegalArgumentException e) {
            return notFound();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity queryBlog(@PathVariable Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if(!optionalBlog.isPresent()) return notFound();

        QueryBlogDto responseDto = blogRepository.findBlogDtoById(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogApiController.class).slash(id);

        EntityModel<QueryBlogDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(BlogApiController.class).withRel("query_blogs"),
                profileRootUrlBuilder.slash("index.html#resources-query-blog").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }












    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
