package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.api.resource.AdminMemberSubscribesDtoRessource;
import com.bi.barfdog.api.resource.MembersDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/members", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class MemberAdminController {

    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;
    private final MemberService memberService;

    private final MemberValidator memberValidator;


    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping
    public ResponseEntity queryMembers(Pageable pageable,
                                       PagedResourcesAssembler<QueryMembersDto> assembler,
                                       @ModelAttribute @Valid QueryMembersCond cond,
                                       BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) return badRequest(bindingResult);
        memberValidator.wrongTerm(cond.getFrom(), cond.getTo(), bindingResult);
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        Page<QueryMembersDto> page = memberRepository.findDtosByCond(pageable, cond);


        PagedModel<EntityModel<QueryMembersDto>> entityModels = assembler.toModel(page, e -> new MembersDtoResource(e));

        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-queryMembers").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
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

    @PutMapping("/{id}/birthday")
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

    @PutMapping("/{id}/grade")
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

    @GetMapping("/{id}/subscribes")
    public ResponseEntity queryMemberSubscribes(@PathVariable Long id,
                                                Pageable pageable,
                                                PagedResourcesAssembler<MemberSubscribeAdminDto> assembler) {

        Optional<Member> optionalMember = memberRepository.findById(id);
        if(!optionalMember.isPresent()) return notFound();

        Page<MemberSubscribeAdminDto> page = subscribeRepository.findSubscribeAdminDtoByMemberId(id, pageable);

        PagedModel<AdminMemberSubscribesDtoRessource> entityModels = assembler.toModel(page, e -> new AdminMemberSubscribesDtoRessource(e));

        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-memberSubscribes").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }



    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }


}
