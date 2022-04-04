package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.directsend.PhoneAuthRequestDto;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.service.MemberService;
import com.bi.barfdog.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.IOException;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class IndexApiController {

    private final MemberService memberService;
    private final MemberValidator memberValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/api")
    public RepresentationModel index(){
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(IndexApiController.class).withRel("banners"));
        return index;
    }

    @PostMapping("/join")
    public ResponseEntity join(@RequestBody @Valid MemberSaveRequestDto requestDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        memberValidator.validate(requestDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        memberValidator.duplicateValidate(requestDto, errors);
        if (errors.hasErrors()) {
            return conflict(errors);
        }

        Member member = memberService.join(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(IndexApiController.class).slash("join");
        URI createUri = selfLinkBuilder.toUri();

        EntityModel<Member> entityModel = EntityModel.of(member,
                selfLinkBuilder.withSelfRel(),
                linkTo(IndexApiController.class).slash("login").withRel("login"),
                profileRootUrlBuilder.slash("index.html#resources-join").withRel("profile")
        );

        return ResponseEntity.created(createUri).body(entityModel);
    }

    @PostMapping("/join/phoneAuth")
    public ResponseEntity phoneAuth(@RequestBody @Valid PhoneAuthRequestDto phoneAuthDto, Errors errors) throws IOException {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        memberValidator.duplicatePhoneNumberValidate(phoneAuthDto.getPhoneNumber(), errors);
        if (errors.hasErrors()) {
            return conflict(errors);
        }

        DirectSendResponseDto responseDto = memberService.sendPhoneAuth(phoneAuthDto.getPhoneNumber());

        if (responseDto.getResponseCode() != 200) {
            return ResponseEntity.internalServerError().build();
        }

        WebMvcLinkBuilder selfLinkBuilder = linkTo(IndexApiController.class).slash("join/phoneAuth");

        EntityModel<DirectSendResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-join-phoneAuth").withRel("profile")
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
