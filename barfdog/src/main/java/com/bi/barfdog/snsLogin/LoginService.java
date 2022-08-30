package com.bi.barfdog.snsLogin;

import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    public NaverResponseDto naver(NaverLoginDto requestDto) {
        String str = SnsLogin.Naver(requestDto.getAccessToken());
        NaverResponseDto naverResponseDto = new NaverResponseDto();

        try {
            naverResponseDto = objectMapper.readValue(str, NaverResponseDto.class);
            if (naverResponseDto.getResultcode().equals("024") ||
                    naverResponseDto.getResultcode().equals("028") ||
                    naverResponseDto.getResultcode().equals("403") ||
                    naverResponseDto.getResultcode().equals("404") ||
                    naverResponseDto.getResultcode().equals("500")) {
                return naverResponseDto;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return naverResponseDto.internalServerError();
        }

        String providerId = naverResponseDto.getResponse().getId();
        if (isJoinedMember(naverResponseDto, providerId)) return naverResponseDto.success();

        String phoneNumber = naverResponseDto.getResponse().getMobile().replace("-","");

        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phoneNumber);

        if (isNewMember(optionalMember)) return naverResponseDto.newMember();

        Member member = optionalMember.get();

        String provider = member.getProvider();
        if (isNewSns(provider)) return naverResponseDto.connectNewSns();
        if (provider.equals(SnsProvider.KAKAO)) return naverResponseDto.kakao();
        if (provider.equals(SnsProvider.NAVER)) return naverResponseDto.success();

        return naverResponseDto;
    }

    private boolean isJoinedMember(NaverResponseDto naverResponseDto, String providerId) {
        Optional<Member> optionalMemberByProvider = memberRepository.findByProviderAndProviderId(SnsProvider.NAVER, providerId);
        if (optionalMemberByProvider.isPresent()) {
            return true;
        }
        return false;
    }

    private boolean isNewSns(String provider) {
        return provider == null || provider.length() == 0;
    }

    private boolean isNewMember(Optional<Member> optionalMember) {
        return !optionalMember.isPresent();
    }
}
