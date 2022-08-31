package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.service.MemberService;
import com.bi.barfdog.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/members", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class MemberApiController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private final ModelMapper modelMapper;
    private final MemberValidator memberValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping
    public ResponseEntity queryMember(@CurrentUser Member member) {

        Optional<MemberInfoResponseDto> memberInfoResponseDtoOptional = memberRepository.findMemberInfoDto(member);
        if (!memberInfoResponseDtoOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);

        MemberInfoResponseDto responseDto = memberInfoResponseDtoOptional.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class);

        EntityModel<MemberInfoResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_member"),
                profileRootUrlBuilder.slash("index.html#resources-query-member").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping
    public ResponseEntity updateMember(@CurrentUser Member member,
                                       @RequestBody @Valid MemberUpdateRequestDto requestDto,
                                       Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        memberValidator.validatePassword(member, requestDto.getPassword(), errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        String phoneNumber = member.getPhoneNumber();
        String newPhoneNumber = requestDto.getPhoneNumber();
        if (!phoneNumber.equals(newPhoneNumber)) {
            memberValidator.duplicateValidate(requestDto, errors);
            if (errors.hasErrors()) {
                return conflict(errors);
            }
        }

        memberService.updateMember(member.getId(), requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("query_member"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-member").withRel("profile"));


        return ResponseEntity.ok(representationModel);
    }

    @DeleteMapping
    public ResponseEntity deleteMember(@CurrentUser Member member,
                                       @RequestBody @Valid DeleteMemberDto requestDto,
                                       Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.validatePassword(member, requestDto.getPassword(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        memberService.deleteMember(member.getId());

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(MemberApiController.class).withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-withdrawal").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("/password")
    public ResponseEntity updatePassword(@CurrentUser Member member,
                                         @RequestBody @Valid UpdatePasswordRequestDto requestDto,
                                         Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        memberValidator.validatePassword(member, requestDto.getPassword(), errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        memberValidator.validatePasswordConfirm(requestDto.getNewPassword(), requestDto.getNewPasswordConfirm(), errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        memberService.updatePassword(member.getId(), requestDto.getNewPassword());

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(MemberApiController.class).slash("password").withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-password").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/sns/password")
    public ResponseEntity checkSnsPassword(@CurrentUser Member member) {

        IsUnknownPasswordDto responseDto = memberService.checkUnknownPassword(member.getId());

        EntityModel<IsUnknownPasswordDto> entityModel = EntityModel.of(responseDto,
                linkTo(MemberApiController.class).slash("sns/password").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-isKnownPassword").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }


    @PostMapping("/sns/password")
    public ResponseEntity setPasswordSnsMember(@CurrentUser Member member,
                                               @RequestBody @Valid SnsLoginSetPasswordDto requestDto,
                                               Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.validatePasswordConfirm(requestDto.getConfirmPassword(), requestDto.getPassword(), errors);

        memberService.setPasswordSnsMember(member.getId(), requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(MemberApiController.class).slash("sns/password").withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-password-snsMember").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }


    @GetMapping("/sns")
    public ResponseEntity querySnsProvider(@CurrentUser Member member) {

        QuerySnsDto responseDto = memberRepository.findProviderByMember(member);

        EntityModel<QuerySnsDto> entityModel = EntityModel.of(responseDto,
                linkTo(MemberApiController.class).slash("sns").withSelfRel(),
                linkTo(MemberApiController.class).slash("sns").withRel("unconnect_sns"),
                profileRootUrlBuilder.slash("index.html#resources-query-snsProvider").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/sns")
    public ResponseEntity unconnectSns(@CurrentUser Member member) {

        memberService.unconnectSns(member);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(MemberApiController.class).slash("sns").withSelfRel());
        representationModel.add(linkTo(MemberApiController.class).slash("sns").withRel("query_sns"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-unconnect-sns").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/publication")
    public ResponseEntity queryMemberDtosInPublication(@ModelAttribute @Valid MemberConditionToPublish condition,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return badRequest(bindingResult);
        memberValidator.validateConditionInPublication(condition, bindingResult);
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        List<MemberPublishResponseDto> responseDto = memberRepository.searchMemberDtosInPublication(condition);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class).slash("publication");

        CollectionModel<MemberPublishResponseDto> collectionModel = CollectionModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(CouponApiController.class).slash("personal").withRel("publish_coupon_personal"),
                profileRootUrlBuilder.slash("index.html#resources-query-members-in-publication").withRel("profile")
                );


        return ResponseEntity.ok(collectionModel);
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }



}
