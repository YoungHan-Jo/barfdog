package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.FindEmailResponseDto;
import com.bi.barfdog.api.memberDto.FindPasswordRequestDto;
import com.bi.barfdog.api.memberDto.MemberInfoResponseDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.auth.PrincipalDetails;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/members", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class MemberApiController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;


    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/{id}")
    public ResponseEntity queryMember(@PathVariable Long id) {

        return null;
    }

    @GetMapping
    public ResponseEntity queryMember(@CurrentUser Member member) {

        MemberInfoResponseDto responseDto = modelMapper.map(member, MemberInfoResponseDto.class);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class);

        EntityModel<MemberInfoResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update-member"),
                profileRootUrlBuilder.slash("index.html#resources-query-member").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);

    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }



}
