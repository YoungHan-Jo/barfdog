package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberUpdateRequestDto{

    @NotEmpty
    private String name;
    @NotEmpty
    private String password;
    @NotEmpty
    private String phoneNumber;

    private Address address;

    @NotEmpty
    private String birthday;
    private Gender gender;
    private boolean receiveSms;
    private boolean receiveEmail;
}
