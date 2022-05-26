package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPublishResponseDto {

    private Long memberId;

    private Grade grade;

    private String name;

    private String email;
    private String phoneNumber;

    private String dogName;

    private int accumulatedAmount;

    private boolean isSubscribe;

}
