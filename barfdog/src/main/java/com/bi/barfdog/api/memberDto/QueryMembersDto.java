package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class QueryMembersDto {

    private Long id;

    private Grade grade;

    private String name;

    private String email;

    private String phoneNumber;

    private String dogName;

    private int accumulatedAmount;

    private boolean subscribe;

    private boolean longUnconnected;

}
