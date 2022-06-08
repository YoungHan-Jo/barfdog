package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.*;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.api.resource.AdminMemberSubscribesDtoRessource;
import com.bi.barfdog.api.resource.BlogsAdminDtoResource;
import com.bi.barfdog.api.resource.MembersDtoResource;
import com.bi.barfdog.api.resource.NoticeAdminDtoResource;
import com.bi.barfdog.common.ErrorMessageDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.ArticleRepository;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.repository.SubscribeRepository;
import com.bi.barfdog.service.BlogService;
import com.bi.barfdog.service.MemberService;
import com.bi.barfdog.validator.BlogValidator;
import com.bi.barfdog.validator.MemberValidator;
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
@RequestMapping(value = "/api/admin",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class AdminApiController {

    private final MemberRepository memberRepository;
    private final BlogRepository blogRepository;
    private final ArticleRepository articleRepository;
    private final SubscribeRepository subscribeRepository;

    private final MemberService memberService;
    private final BlogService blogService;

    private final MemberValidator memberValidator;
    private final BlogValidator blogValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");







    @GetMapping("/articles")
    public ResponseEntity queryArticles() {

        QueryArticlesAdminDto responseDto = blogService.getArticlesAdmin();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("articles");

        EntityModel<QueryArticlesAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_article"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-articles").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/articles")
    public ResponseEntity updateArticles(@RequestBody @Valid UpdateArticlesRequestDto requestDto,
                                         Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Optional<Blog> optionalBlog1 = blogRepository.findById(requestDto.getFirstBlogId());
        Optional<Blog> optionalBlog2 = blogRepository.findById(requestDto.getSecondBlogId());
        if (!optionalBlog1.isPresent() || !optionalBlog2.isPresent()) {
            return notFound();
        }

        blogValidator.validateIsNotice(requestDto, errors);
        blogValidator.validateHiddenStatus(requestDto, errors);
        blogValidator.validateDuplicateBlogId(requestDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        blogService.updateArticles(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("articles");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("admin_query_articles"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-articles").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }














    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
