package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Agreement;
import com.bi.barfdog.domain.member.Gender;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSaveRequestDto {

    private String provider;
    private String providerId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String confirmPassword;
    @NotEmpty
    private String phoneNumber;

    private Address address;
    @NotEmpty
    private String birthday;

    private Gender gender;

    private String recommendCode;

    private Agreement agreement;

}
