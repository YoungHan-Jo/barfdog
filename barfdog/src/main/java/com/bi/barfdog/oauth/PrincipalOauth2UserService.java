package com.bi.barfdog.oauth;

import com.bi.barfdog.auth.PrincipalDetails;
import com.bi.barfdog.domain.member.Agreement;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.oauth.provider.NaverUserInfo;
import com.bi.barfdog.oauth.provider.OAuth2UserInfo;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    // 로그인 후 provider로 부터 받은 userRequest 데이터에 대한 후처리 되는 함수
    // 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // sns 로그인 버튼 클릭 -> sns로그인창 -> 로그인 완료 -> code를 리턴(OAuth-client라이브러리) -> AccessToken요청
        // userRequest정보 -> 회원 프로필을 받아야함(loadUser함수를 이용해서  sns로부터 회원 프로필 받을 수 있음)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }


        String email = oAuth2UserInfo.getEmail();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (!optionalMember.isPresent()) {
            Member member = Member.builder()
                    .provider("naver")
                    .name(oAuth2UserInfo.getName())
                    .email(oAuth2UserInfo.getEmail())
                    .agreement(new Agreement(true,true,true,true,true))
                    .roles("USER")
                    .build();

            memberRepository.save(member);
            return new PrincipalDetails(member, oAuth2User.getAttributes());
        }

        Member member = optionalMember.get();

        return new PrincipalDetails(member, oAuth2User.getAttributes()); // 이거 Authentication 객체로 들어감
    }
}
