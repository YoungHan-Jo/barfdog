package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.util.StringUtils.*;

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

        MemberInfoResponseDto responseDto = modelMapper.map(member, MemberInfoResponseDto.class);

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

        memberValidator.validatePassword(member, requestDto, errors);
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

    @PutMapping("/password")
    public ResponseEntity updatePassword(@CurrentUser Member member,
                                         @RequestBody @Valid UpdatePasswordRequestDto requestDto,
                                         Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        memberValidator.validatePassword(member, requestDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        memberValidator.validatePasswordConfirm(requestDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class).slash("password");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-password").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/publicationCoupon")
    public ResponseEntity queryMemberDtosInPublication(@RequestBody @Valid MemberConditionPublishCoupon condition, Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.validateConditionInPublication(condition, errors);
        if (errors.hasErrors()) return badRequest(errors);

        List<MemberPublishCouponResponseDto> responseDto = memberRepository.searchMemberDtosInPublication(condition);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class).slash("publicationCoupon");

        CollectionModel<MemberPublishCouponResponseDto> collectionModel = CollectionModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(CouponApiController.class).slash("personal").withRel("publish_coupon_personal"),
                profileRootUrlBuilder.slash("index.html#resources-query-members-in-publishCoupon").withRel("profile")
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
