package com.bi.barfdog.api;

import com.bi.barfdog.api.noticeDto.QueryNoticePageDto;
import com.bi.barfdog.api.noticeDto.QueryNoticesDto;
import com.bi.barfdog.api.resource.NoticeDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.repository.blog.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/notices", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class NoticeApiController {

    private final BlogRepository blogRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping
    public ResponseEntity queryNotices(Pageable pageable,
                                       PagedResourcesAssembler<QueryNoticesDto> assembler) {
        Page<QueryNoticesDto> page = blogRepository.findNoticeDtos(pageable);
        PagedModel<EntityModel<QueryNoticesDto>> pagedModel = assembler.toModel(page, e -> new NoticeDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-notices").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryNotice(@PathVariable Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if(!optionalBlog.isPresent()) return notFound();

        QueryNoticePageDto responseDto = blogRepository.findNoticePageDtoById(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(NoticeApiController.class).slash(id);

        EntityModel<QueryNoticePageDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(NoticeApiController.class).withRel("query_notices"),
                profileRootUrlBuilder.slash("index.html#resources-query-notice").withRel("profile")
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
