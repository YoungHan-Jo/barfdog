package com.bi.barfdog.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bi.barfdog.api.barfDto.*;
import com.bi.barfdog.api.deliveryDto.SaveDeliveryNumDto;
import com.bi.barfdog.api.deliveryDto.UpdateDeliveryNumberDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.directsend.PhoneAuthRequestDto;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.api.memberDto.jwt.JwtProperties;
import com.bi.barfdog.api.memberDto.jwt.JwtTokenProvider;
import com.bi.barfdog.goodsFlow.GoodsFlowResponseDto;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.service.BarfService;
import com.bi.barfdog.service.DeliveryService;
import com.bi.barfdog.service.MemberService;
import com.bi.barfdog.snsLogin.*;
import com.bi.barfdog.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BarfService barfService;
    private final OrderRepository orderRepository;
    private final DeliveryService deliveryService;


    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping("/api/goodsFlow/postTraceResult")
    public ResponseEntity saveDeliveryNumber(@RequestBody @Valid SaveDeliveryNumDto requestDto,
                                             Errors errors) {

        GoodsFlowResponseDto responseDto = new GoodsFlowResponseDto();

        if (errors.hasErrors()){
            responseDto.fail();
            return ResponseEntity.ok(responseDto);
        }


        List<UpdateDeliveryNumberDto.DeliveryNumberDto> deliveryNumberDtoList = new ArrayList<>();

        for (SaveDeliveryNumDto.ItemDto item : requestDto.getData().getItems()) {
            UpdateDeliveryNumberDto.DeliveryNumberDto deliveryNumberDto = UpdateDeliveryNumberDto.DeliveryNumberDto.builder()
                    .transUniqueCd(item.getTransUniqueCd())
                    .deliveryNumber(item.getSheetNo())
                    .build();
            deliveryNumberDtoList.add(deliveryNumberDto);
        }

        UpdateDeliveryNumberDto updateDeliveryNumberDto = UpdateDeliveryNumberDto.builder()
                .deliveryNumberDtoList(deliveryNumberDtoList)
                .build();

        deliveryService.setDeliveryNumber(updateDeliveryNumberDto);

        responseDto.success();

        return ResponseEntity.ok(responseDto);
    }


    @GetMapping("/api/home")
    public ResponseEntity home(@CurrentUser Member member) {

        HomePageDto responseDto = barfService.getHome(member);

        EntityModel<HomePageDto> entityModel = EntityModel.of(responseDto,
                linkTo(IndexApiController.class).slash("api/home").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-home-page").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/api/mypage")
    public ResponseEntity mypage(@CurrentUser Member member) {

        MypageDto responseDto = memberRepository.findMypageDtoByMember(member);

        EntityModel<MypageDto> entityModel = EntityModel.of(responseDto,
                linkTo(IndexApiController.class).slash("api/mypage").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-my-page").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/api/mypage/inviteSms")
    public ResponseEntity sendInviteSms(@CurrentUser Member member,
                                        @RequestBody @Valid SendInviteSmsDto requestDto,
                                        Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        try {
            DirectSendResponseDto responseDto = memberService.sendInviteSms(member, requestDto);

            EntityModel<DirectSendResponseDto> entityModel = EntityModel.of(responseDto,
                    linkTo(IndexApiController.class).slash("api/mypage/inviteSms").withSelfRel(),
                    profileRootUrlBuilder.slash("index.html#resources-send-inviteSms").withRel("profile")
            );

            return ResponseEntity.ok(entityModel);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/admin/dashBoard")
    public ResponseEntity queryDashBoard(@ModelAttribute @Valid AdminDashBoardRequestDto requestDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        AdminDashBoardResponseDto responseDto = orderRepository.findAdminDashBoard(requestDto);

        EntityModel<AdminDashBoardResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(IndexApiController.class).slash("api/admin/dashBoard").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-admin-dashBoard").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }






    @GetMapping("/api")
    public RepresentationModel index(){
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(IndexApiController.class).withRel("banners"));
        return index;
    }

    @GetMapping("/api/unauthorized")
    public ResponseEntity unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/api/forbidden")
    public ResponseEntity forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/api/join")
    public ResponseEntity join(@RequestBody @Valid MemberSaveRequestDto requestDto, Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.validatePasswordConfirm(requestDto.getConfirmPassword(), requestDto.getPassword(), errors);
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.duplicateValidate(requestDto, errors);
        if (errors.hasErrors()) return conflict(errors);

        JoinResponseDto responseDto = memberService.join(requestDto);

        EntityModel<JoinResponseDto> entityModel = EntityModel.of(responseDto);

        entityModel.add(linkTo(IndexApiController.class).slash("join").withSelfRel());
        entityModel.add(linkTo(IndexApiController.class).slash("login").withRel("login"));
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-join").withRel("profile"));

        return ResponseEntity.created(linkTo(IndexApiController.class).slash("api/login").toUri()).body(entityModel);
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

        EntityModel<DirectSendResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(IndexApiController.class).slash("join/phoneAuth").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-join-phoneAuth").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/api/email/duplication")
    public ResponseEntity validateEmail(@RequestParam String email) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isPresent()) return new ResponseEntity(HttpStatus.CONFLICT);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(IndexApiController.class).slash("api/email/duplication");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-email-duplication").withRel("profile"));

        return ResponseEntity.ok(representationModel);
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

    @PostMapping("/api/adminPasswordEmailAuth")
    public ResponseEntity sendAdminPasswordEmailAuth(@RequestBody @Valid EmailAuthDto requestDto, Errors errors) throws Exception {
        if(errors.hasErrors()) return badRequest(errors);
        Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
        if(!optionalMember.isPresent()) return ResponseEntity.notFound().build();

        memberValidator.validateAdmin(optionalMember.get(),errors);
        if(errors.hasErrors()) return badRequest(errors);

        DirectSendResponseDto responseDto = memberService.sendAdminPasswordEmailAuth(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(IndexApiController.class).slash("api").slash("adminPasswordEmailAuth");

        EntityModel<DirectSendResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(IndexApiController.class).slash("api/admin/password").withRel("changeAdminPassword"),
                profileRootUrlBuilder.slash("index.html#resources-admin-password-email-auth").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/api/admin/password")
    public ResponseEntity updateAdminPassword(@RequestBody @Valid UpdateAdminPasswordRequestDto requestDto,
                                              Errors errors) {
        if(errors.hasErrors()) badRequest(errors);
        Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
        if(!optionalMember.isPresent()) return ResponseEntity.notFound().build();
        memberValidator.validateAdmin(optionalMember.get(), errors);
        memberValidator.validatePasswordConfirm(requestDto.getPassword(), requestDto.getPasswordConfirm(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        memberService.updateAdminPassword(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(IndexApiController.class).slash("api/admin/password");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(IndexApiController.class).slash("api/login").withRel("login"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-change-admin-password").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/api/login")
    public ResponseEntity login(HttpServletResponse response,
                                @RequestBody @Valid LoginDto loginDto,
                                Errors errors) {
        Optional<Member> optional = memberRepository.findByEmail(loginDto.getEmail());
        if (!optional.isPresent()) return ResponseEntity.notFound().build();
        Member member = optional.get();
        memberValidator.isWithdrawalMember(member, errors);
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.validatePassword(member, loginDto.getPassword(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        memberService.login(member);
        LocalDateTime expiresAt = generateAccessToken(response, member, loginDto.getTokenValidDays());

        List<String> roleList = member.getRoleList();

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .roleList(roleList)
                .expiresAt(expiresAt)
                .isTemporaryPassword(member.isTemporaryPassword())
                .build();

        EntityModel<LoginResponseDto> entityModel = EntityModel.of(responseDto);

        return ResponseEntity.ok(entityModel);
    }

    private LocalDateTime generateAccessToken(HttpServletResponse response, Member member, int tokenValidDays){

        int expirationTime = tokenValidDays != 0 ? 1000 * 60 * 60 * 24 * tokenValidDays : JwtProperties.EXPIRATION_TIME;

        Date expiresAt = new Date(System.currentTimeMillis() + expirationTime);

        String jwtToken = JwtProperties.TOKEN_PREFIX + JWT.create()
                .withSubject("토큰 이름")
                .withExpiresAt(expiresAt)
                .withClaim("email", member.getEmail())
                .withClaim("id", member.getId())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, jwtToken);

        LocalDateTime localDateTime = expiresAt.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime;
    }

    @PostMapping("/api/login/naver")
    public ResponseEntity loginNaver(HttpServletResponse response,
                                     @RequestBody @Valid NaverLoginDto requestDto,
                                     Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        NaverResponseDto responseDto = loginService.naver(requestDto);
        EntityModel<NaverResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(IndexApiController.class).slash("api/login/naver").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-login-naver").withRel("profile")
        );

        String resultCode = responseDto.getResultcode();
        if (resultCode.equals(SnsResponse.NEED_TO_CONNECT_NEW_SNS_CODE)) {
            entityModel.add(linkTo(IndexApiController.class).slash("api/connectSns").withRel("connect_sns"));
        }
        if (resultCode.equals(SnsResponse.SUCCESS_CODE)) {
            // 네이버로 부터 받은 고유id 가 받을 때 마다 달라져서(테스트 api라서?) provider로 구분할 수 없음
//            String providerId = responseDto.getResponse().getId();
//            Member member = memberRepository.findByProviderAndProviderId(SnsProvider.NAVER, providerId).get();
            String phoneNumber = responseDto.getResponse().getMobile().replace("-","");
            Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phoneNumber);
            if (!optionalMember.isPresent()) return notfound();
            Member member = optionalMember.get();
            generateAccessToken(response, member, requestDto.getTokenValidDays());
        }

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/api/login/kakao")
    public ResponseEntity loginKakao(HttpServletResponse response,
                                     @RequestBody @Valid KakaoLoginDto requestDto,
                                     Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        KakaoResponseDto responseDto = loginService.kakao(requestDto);
        EntityModel<KakaoResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(IndexApiController.class).slash("api/login/kakao").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-login-kakao").withRel("profile")
        );

        String resultCode = responseDto.getResultcode();
        if (resultCode.equals(SnsResponse.NEED_TO_CONNECT_NEW_SNS_CODE)) {
            entityModel.add(linkTo(IndexApiController.class).slash("api/connectSns").withRel("connect_sns"));
        }
        if (resultCode.equals(SnsResponse.SUCCESS_CODE)) {
            // 네이버로 부터 받은 고유id 가 받을 때 마다 달라져서(테스트 api라서?) provider로 구분할 수 없음
//            String providerId = responseDto.getResponse().getId();
//            Member member = memberRepository.findByProviderAndProviderId(SnsProvider.KAKAO, providerId).get();

            KakaoAccountDto.KakaoPhone_number kakaoPhone_number = responseDto.getResponse().getKakao_accountDto().getKakaoPhone_number();
            String phone_number = kakaoPhone_number.getPhone_number();

            phone_number = "0" + phone_number.substring(phone_number.indexOf(" ") + 1).replace("-", "");

            Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phone_number);
            if (!optionalMember.isPresent()) return notfound();
            Member member = optionalMember.get();
            generateAccessToken(response, member, requestDto.getTokenValidDays());
        }

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/api/connectSns")
    public ResponseEntity connectSns(HttpServletResponse response,
                                     @RequestBody @Valid ConnectSnsRequestDto requestDto,
                                     Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(requestDto.getPhoneNumber());
        if (!optionalMember.isPresent()) return notfound();
        Member member = optionalMember.get();
        memberValidator.validatePassword(member, requestDto.getPassword(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        ConnectSnsResponseDto responseDto = memberService.connectSns(requestDto);
        generateAccessToken(response, member, requestDto.getTokenValidDays());

        EntityModel<ConnectSnsResponseDto> entityModel = EntityModel.of(responseDto);
        entityModel.add(linkTo(IndexApiController.class).slash("api/connectSns").withSelfRel());
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-connect-sns").withRel("profile"));

        return ResponseEntity.ok(entityModel);

    }

    private ResponseEntity notfound() {
        return ResponseEntity.notFound().build();
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }
}