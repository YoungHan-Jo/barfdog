package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.*;
import com.bi.barfdog.api.resource.BlogsAdminDtoResource;
import com.bi.barfdog.common.ErrorMessageDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.repository.ArticleRepository;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.service.BlogService;
import com.bi.barfdog.validator.BlogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/blogs", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class BlogAdminController {

    private final BlogRepository blogRepository;
    private final ArticleRepository articleRepository;
    private final BlogService blogService;
    private final BlogValidator blogValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping("/thumbnail/upload")
    public ResponseEntity uploadBlogThumbnail(@RequestPart MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageAdminDto responseDto = blogService.uploadThumbnail(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogAdminController.class).slash("thumbnail").slash("upload");

        EntityModel<UploadedImageAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-blogThumbnail").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/image/upload")
    public ResponseEntity uploadBlogImage(@RequestPart MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageAdminDto responseDto = blogService.uploadImage(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogAdminController.class).slash("image").slash("upload");

        EntityModel<UploadedImageAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-blogImage").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }




    @PostMapping
    public ResponseEntity createBlog(@RequestBody @Valid BlogSaveDto requestDto,
                                     Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);

        blogValidator.validateWrongImgId(requestDto.getBlogImageIdList(),errors);
        if (errors.hasErrors()) return badRequest(errors);


        blogService.saveBlog(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogAdminController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BlogAdminController.class).withRel("query_blogs"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-blog").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(representationModel);
    }

    @GetMapping
    public ResponseEntity queryBlogs(Pageable pageable,
                                     PagedResourcesAssembler<QueryBlogsAdminDto> assembler) {

        Page<QueryBlogsAdminDto> page = blogRepository.findAdminBlogListDtos(pageable);

        PagedModel<EntityModel<QueryBlogsAdminDto>> entityModels = assembler.toModel(page, e -> new BlogsAdminDtoResource(e));

        entityModels.add(linkTo(AdminApiController.class).slash("articles").withRel("admin_query_articles"));
        entityModels.add(linkTo(BlogAdminController.class).withRel("create_blog"));
        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-blogs").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryBlog(@PathVariable Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (!optionalBlog.isPresent()) return notFound();

        QueryAdminBlogDto responseDto = blogService.findQueryAdminBlogDtoById(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogAdminController.class).slash(id);

        EntityModel<QueryAdminBlogDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("admin_update_blog"),
                linkTo(AdminApiController.class).slash("blogImage").slash("upload").withRel("upload_blogImage"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-blog").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBlog(@PathVariable Long id,
                                     @RequestBody @Valid UpdateBlogRequestDto requestDto,
                                     Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if(!optionalBlog.isPresent()) return notFound();

        blogService.updateBlog(id,requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("admin_query_blog"));
        representationModel.add(linkTo(BlogAdminController.class).withRel("admin_query_blogs"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-blog").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBlog(@PathVariable Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (!optionalBlog.isPresent()) return notFound();

        Long count = articleRepository.findCountByBlogId(id);
        if (count > 0L) {
            return ResponseEntity.badRequest().body(new ErrorMessageDto(400, "아티클로 설정된 블로그는 삭제할 수 없습니다."));
        }

        blogService.deleteBlog(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BlogAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BlogAdminController.class).withRel("admin_query_blogs"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-blog").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
