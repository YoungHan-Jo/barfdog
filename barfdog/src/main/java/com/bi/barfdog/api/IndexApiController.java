package com.bi.barfdog.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bi.barfdog.api.memberDto.FindEmailResponseDto;
import com.bi.barfdog.api.memberDto.FindPasswordRequestDto;
import com.bi.barfdog.api.memberDto.LoginDto;
import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.directsend.PhoneAuthRequestDto;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.jwt.JwtProperties;
import com.bi.barfdog.jwt.JwtTokenProvider;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.MemberService;
import com.bi.barfdog.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class IndexApiController {

    private final MemberService memberService;
    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/api")
    public RepresentationModel index(){
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(IndexApiController.class).withRel("banners"));
        return index;
    }

    @PostMapping("/api/join")
    public ResponseEntity join(@RequestBody @Valid MemberSaveRequestDto requestDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        memberValidator.validatePasswordConfirm(requestDto, errors);
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

    @PostMapping("/api/join/phoneAuth")
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


    @GetMapping("/api/email")
    public ResponseEntity findEmail(@RequestParam String name, @RequestParam String phoneNumber) {
        Optional<FindEmailResponseDto> optionalFindEmailResponseDto = memberRepository.findByNameAndPhoneNumber(name, phoneNumber);
        if (!optionalFindEmailResponseDto.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        FindEmailResponseDto responseDto = optionalFindEmailResponseDto.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(IndexApiController.class).slash("email");

        EntityModel<FindEmailResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(IndexApiController.class).slash("login").withRel("login"),
                profileRootUrlBuilder.slash("index.html#resources-find-email").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/api/temporaryPassword")
    public ResponseEntity sendTemporaryPassword(@RequestBody @Valid FindPasswordRequestDto requestDto, Errors errors) throws IOException {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Optional<Member> optionalMember = memberRepository.findByEmailAndNameAndPhoneNumber(requestDto.getEmail(), requestDto.getName(), requestDto.getPhoneNumber());
        if (!optionalMember.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        DirectSendResponseDto responseDto = memberService.temporaryPassword(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(IndexApiController.class).slash("temporaryPassword");

        EntityModel<DirectSendResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(IndexApiController.class).slash("login").withRel("login"),
                profileRootUrlBuilder.slash("index.html#resources-find-password").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/api/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        Optional<Member> optional = memberRepository.findByEmail(loginDto.getEmail());
        if (!optional.isPresent()) return ResponseEntity.notFound().build();

        Member member = optional.get();

        if (!bCryptPasswordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            return ResponseEntity.status(401).body(null);
        }

        String jwtToken = JwtProperties.TOKEN_PREFIX + JWT.create()
                .withSubject("토큰 이름")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("email", member.getEmail())
                .withClaim("id", member.getId())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, jwtToken);
        return ResponseEntity.ok(null);
    }





    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }

}
