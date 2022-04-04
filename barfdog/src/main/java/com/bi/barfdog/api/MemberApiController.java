package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.FindEmailResponseDto;
import com.bi.barfdog.api.memberDto.FindPasswordRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/{id}")
    public ResponseEntity queryMember(@PathVariable Long id) {

        return null;
    }

    @GetMapping("/email")
    public ResponseEntity findEmail(@RequestParam String name, @RequestParam String phoneNumber) {
        Optional<FindEmailResponseDto> optionalFindEmailResponseDto = memberRepository.findByNameAndPhoneNumber(name, phoneNumber);
        if (!optionalFindEmailResponseDto.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        FindEmailResponseDto responseDto = optionalFindEmailResponseDto.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class).slash("email");

        EntityModel<FindEmailResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(IndexApiController.class).slash("login").withRel("login"),
                profileRootUrlBuilder.slash("index.html#resources-find-email").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/temporaryPassword")
    public ResponseEntity sendTemporaryPassword(@RequestBody @Valid FindPasswordRequestDto requestDto, Errors errors) throws IOException {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Optional<Member> optionalMember = memberRepository.findByEmailAndNameAndPhoneNumber(requestDto.getEmail(), requestDto.getName(), requestDto.getPhoneNumber());
        if (!optionalMember.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        DirectSendResponseDto responseDto = memberService.temporaryPassword(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberApiController.class).slash("temporaryPassword");

        EntityModel<DirectSendResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(IndexApiController.class).slash("login").withRel("login"),
                profileRootUrlBuilder.slash("index.html#resources-find-password").withRel("profile")
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
