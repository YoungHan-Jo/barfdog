package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.NoticeSaveDto;
import com.bi.barfdog.api.blogDto.QueryAdminNoticeDto;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import com.bi.barfdog.api.blogDto.UpdateNoticeRequestDto;
import com.bi.barfdog.api.resource.NoticeAdminDtoResource;
import com.bi.barfdog.common.ErrorMessageDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
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

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/notices", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class NoticeAdminController {


    private final BlogRepository blogRepository;
    private final BlogService blogService;

    private final BlogValidator blogValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping
    public ResponseEntity createNotice(@RequestBody @Valid NoticeSaveDto requestDto,
                                       Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        blogValidator.validateWrongImgId(requestDto.getNoticeImageIdList(),errors);
        if (errors.hasErrors()) return badRequest(errors);

        blogService.saveNotice(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(NoticeAdminController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(NoticeAdminController.class).withRel("query_notices"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-notice").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(representationModel);
    }

    @GetMapping
    public ResponseEntity queryNotices(Pageable pageable,
                                       PagedResourcesAssembler<QueryBlogsAdminDto> assembler) {
        Page<QueryBlogsAdminDto> page = blogRepository.findAdminNoticeListDtos(pageable);

        PagedModel<EntityModel<QueryBlogsAdminDto>> entityModels = assembler.toModel(page, e -> new NoticeAdminDtoResource(e));

        entityModels.add(linkTo(NoticeAdminController.class).withRel("create_notice"));
        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-notices").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryNotice(@PathVariable Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (!optionalBlog.isPresent()) return notFound();

        QueryAdminNoticeDto responseDto = blogService.findQueryAdminNoticeDtoById(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(NoticeAdminController.class).slash(id);

        EntityModel<QueryAdminNoticeDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("admin_update_notice"),
                linkTo(AdminApiController.class).slash("blogImage").slash("upload").withRel("upload_blogImage"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-notice").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateNotice(@PathVariable Long id,
                                       @RequestBody @Valid UpdateNoticeRequestDto requestDto,
                                       Errors errors) {

        if(errors.hasErrors()) return badRequest(errors);
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if(!optionalBlog.isPresent()) return notFound();

        blogService.updateNotice(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(NoticeAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("admin_query_notice"));
        representationModel.add(linkTo(NoticeAdminController.class).withRel("admin_query_notices"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-notice").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteNotice(@PathVariable Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (!optionalBlog.isPresent()) return notFound();

        Blog blog = blogRepository.findById(id).get();
        if (blog.getCategory() != BlogCategory.NOTICE) {
            return ResponseEntity.badRequest().body(new ErrorMessageDto(400, "해당 인덱스의 글은 공지사항 유형이 아닙니다."));
        }

        blogService.deleteBlog(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(NoticeAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(NoticeAdminController.class).withRel("admin_query_notices"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-notice").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
