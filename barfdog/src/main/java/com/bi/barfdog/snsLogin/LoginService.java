package com.bi.barfdog.snsLogin;

import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public KakaoResponseDto kakao(String code) {
        KakaoLoginResponseDto kakaoLoginResponseDto = getKakaoLoginResponseDto(code);

        String str = SnsLogin.Kakao(kakaoLoginResponseDto.getAccess_token());
        KakaoResponseDto kakaoResponseDto = new KakaoResponseDto();

        try {
            kakaoResponseDto = objectMapper.readValue(str, KakaoResponseDto.class);
            if (kakaoResponseDto.getResultcode().equals("-101") ||
                    kakaoResponseDto.getResultcode().equals("-102") ||
                    kakaoResponseDto.getResultcode().equals("-103") ||
                    kakaoResponseDto.getResultcode().equals("-406") ||
                    kakaoResponseDto.getResultcode().equals("500")) {
                return kakaoResponseDto;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return kakaoResponseDto.internalServerError();
        }

        Long kakaoId = kakaoResponseDto.getId();
        String providerId = kakaoId.toString();

        if (isJoinedMember(kakaoResponseDto, providerId)) return kakaoResponseDto.success();

        String phone_number =kakaoResponseDto.getKakao_account().getPhone_number();
        phone_number = "0" + phone_number.substring(phone_number.indexOf(" ") + 1).replace("-", "");

        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phone_number);

        if (isNewMember(optionalMember)) return kakaoResponseDto.newMember();

        Member member = optionalMember.get();

        String provider = member.getProvider();
        if (isNewSns(provider)) return kakaoResponseDto.connectNewSns();
        if (provider.equals(SnsProvider.KAKAO)) return kakaoResponseDto.success();
        if (provider.equals(SnsProvider.NAVER)) return kakaoResponseDto.naver();

        return kakaoResponseDto;
    }

    public KakaoLoginResponseDto getKakaoLoginResponseDto(String code) {
        String access_token = "";
        String refresh_token = "";
        KakaoLoginRequestDto kakaoLoginRequestDto;
        KakaoLoginResponseDto kakaoLoginResponseDto = null;
        String requestURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            kakaoLoginRequestDto = KakaoLoginRequestDto.builder()
                    .code(code)
                    .build();

            // post 요청
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=" + kakaoLoginRequestDto.getGrant_type());
            sb.append("&client_id=" + kakaoLoginRequestDto.getClient_id());
            sb.append("&redirect_uri" + kakaoLoginRequestDto.getRedirect_uri());
            sb.append("&code=" + kakaoLoginRequestDto.getCode());
            bw.write(sb.toString());
            bw.flush();

            // 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            // 요청을 통해 얻은 JSON 타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            // Gson 라이브러리에 포함된 클래스로 JSON 파싱 객체 생성
            kakaoLoginResponseDto = objectMapper.readValue(result, KakaoLoginResponseDto.class);

            br.close();
            bw.close();

        } catch(IOException e) {
            e.printStackTrace();
        }

        return kakaoLoginResponseDto;
    }

    private boolean isJoinedMember(ResponseDto responseDto, String providerId) {
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
