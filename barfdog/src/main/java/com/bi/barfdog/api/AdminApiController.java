package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.QueryMemberDto;
import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.api.memberDto.QueryMembersDto;
import com.bi.barfdog.api.resource.MembersDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.MemberService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class AdminApiController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/members")
    public ResponseEntity queryMembers(Pageable pageable,
                                       PagedResourcesAssembler<QueryMembersDto> assembler,
                                       @RequestBody @Valid QueryMembersCond cond,
                                       Errors errors
                                       ){

        if (errors.hasErrors()) return badRequest(errors);

        Page<QueryMembersDto> page = memberRepository.findDtosByCond(pageable, cond);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class);

        PagedModel<EntityModel<QueryMembersDto>> entityModels = assembler.toModel(page, e -> new MembersDtoResource(e));

        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-queryMembers").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity queryMember(@PathVariable Long id) {

        Optional<Member> optionalMember = memberRepository.findById(id);
        if(!optionalMember.isPresent()) return notFound();

        QueryMemberDto responseDto = memberService.getMemberDto(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AdminApiController.class).slash("members").slash(id);

        EntityModel<QueryMemberDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-admin-queryMember").withRel("profile")
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
