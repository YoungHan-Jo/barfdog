package com.bi.barfdog.api.webhookDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelTalkRequestDto {

    private RefersDto refers;

    @Data
    @AllArgsConstructor
    @Builder
    public static class RefersDto {
        private UsersDto user;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class UsersDto {
        private ProfileDto profile;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class ProfileDto {
        private String name;
        private String mobileNumber;
        private String email;
    }




}
