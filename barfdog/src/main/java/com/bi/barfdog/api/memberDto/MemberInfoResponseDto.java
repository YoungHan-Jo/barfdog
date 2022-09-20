package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponseDto {

    private Long memberId;

    private String name;

    private String email;

    private String phoneNumber;

    private Address address;

    private String birthday;

    private Gender gender;

    private String provider;
    private String providerId;

    private boolean receiveSms;
    private boolean receiveEmail;

}
