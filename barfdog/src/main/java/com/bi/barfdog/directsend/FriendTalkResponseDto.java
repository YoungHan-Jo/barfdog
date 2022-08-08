package com.bi.barfdog.directsend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendTalkResponseDto {

    private int status;
    private String message;

    private int responseCode;
}
