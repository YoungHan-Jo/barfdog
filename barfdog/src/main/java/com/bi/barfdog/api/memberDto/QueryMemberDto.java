package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryMemberDto {

    private String name;

    private String email;

    private Address address;

    private String phoneNumber;

    private String birthday;

    private int accumulatedAmount;

    private Grade grade;

    private boolean subscribe;

//    private List<String> dogNames;

    private int accumulatedSubscribe;

    private LocalDateTime lastLoginDate;

    private boolean longUnconnected;

    private boolean withdrawal;

}
