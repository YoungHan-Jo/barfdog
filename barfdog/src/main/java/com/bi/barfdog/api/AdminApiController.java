package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.BlogImageDto;
import com.bi.barfdog.api.blogDto.BlogSaveDto;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.api.resource.BlogsAdminDtoResource;
import com.bi.barfdog.api.resource.MembersDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.BlogService;
import com.bi.barfdog.service.MemberService;
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

    private final MemberService memberService;
    private final BlogService blogService;

    private final MemberValidator memberValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/members")
    public ResponseEntity queryMembers(Pageable pageable,
                                       PagedResourcesAssembler<QueryMembersDto> assembler,
                                       @RequestBody @Valid QueryMembersCond cond,
                                       Errors errors
                                       ){
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.wrongTerm(cond.getFrom(), cond.getTo(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        Page<QueryMembersDto> page = memberRepository.findDtosByCond(pageable, cond);


        PagedModel<EntityModel<QueryMembersDto>> entityModels = assembler.toModel(page, e -> new MembersDtoResource(e));

        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-queryMembers").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity queryMember(@PathVariable Long id) {

        Optional<Member> optionalMember = memberRepository.findById(id);
        if(!optionalMember.isPresent()) return notFound();

        QueryMemberAndDogsDto responseDto = memberService.getMemberDto(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("members").slash(id);

        EntityModel<QueryMemberAndDogsDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(AdminApiController.class).slash("members").slash(id).slash("birthday").withRel("update_member_birth"),
                linkTo(AdminApiController.class).slash("members").slash(id).slash("grade").withRel("update_member_grade"),
                linkTo(AdminApiController.class).slash("members").slash(id).slash("subscribes").withRel("query_member_subscribes"),
                profileRootUrlBuilder.slash("index.html#resources-admin-queryMember").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/members/{id}/birthday")
    public ResponseEntity updateMemberBirthday(@PathVariable Long id,
                                               @RequestBody @Valid UpdateBirthdayRequestDto requestDto,
                                               Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(!optionalMember.isPresent()) return notFound();

        memberService.updateBirthday(id,requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("members").slash(id).slash("birthday");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(AdminApiController.class).slash("members").slash(id).withRel("query_member"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-updateBirthday").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("/members/{id}/grade")
    public ResponseEntity updateMemberGrade(@PathVariable Long id,
                                            @RequestBody @Valid UpdateGradeRequestDto requestDto,
                                            Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) return notFound();

        memberService.updateGrade(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("members").slash(id).slash("grade");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(AdminApiController.class).slash("members").slash(id).withRel("query_member"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-updateGrade").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }



















    @PostMapping("/blogImage/upload")
    public ResponseEntity uploadBlogImage(@RequestPart MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();

        BlogImageDto responseDto = blogService.uploadFile(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("blogImage").slash("upload");

        EntityModel<BlogImageDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-blogImage").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/blogs")
    public ResponseEntity createBlog(@RequestBody @Valid BlogSaveDto requestDto,
                                     Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);

        blogService.saveBlog(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("blogs");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(AdminApiController.class).slash("blogs").withRel("query_blogs"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-blog").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(representationModel);
    }

    @GetMapping("/blogs")
    public ResponseEntity queryBlogs(Pageable pageable,
                                     PagedResourcesAssembler<QueryBlogsAdminDto> assembler) {

        Page<QueryBlogsAdminDto> page = blogRepository.findAdminListDtos(pageable);

        PagedModel<EntityModel<QueryBlogsAdminDto>> entityModels = assembler.toModel(page, e -> new BlogsAdminDtoResource(e));

        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-blogs").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/articles")
    public ResponseEntity queryArticles() {

        blogService.getArticlesAdmin();

        return null;
    }











    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
