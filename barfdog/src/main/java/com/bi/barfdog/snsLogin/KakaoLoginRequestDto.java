package com.bi.barfdog.snsLogin;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoLoginRequestDto {
    @Builder.Default
    private String grant_type = new KakaoConfiguration().getAuthorizationGrantType();
    @Builder.Default
    private String client_id = new KakaoConfiguration().getClientId();
    @Builder.Default
    private String redirect_uri = new KakaoConfiguration().getRedirectUri();
    private String code;

    @Data
    @Configuration
    @ConfigurationProperties("spring.security.oauth2.client.registration.kakao")
    public static class KakaoConfiguration {
        private String authorizationGrantType;
        private String clientId;
        private String redirectUri;
    }
}
