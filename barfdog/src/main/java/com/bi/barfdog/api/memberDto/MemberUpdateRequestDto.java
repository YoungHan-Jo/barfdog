package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    @NotNull
    private Gender gender;
    @NotNull
    private boolean receiveSms;
    @NotNull
    private boolean receiveEmail;
}
