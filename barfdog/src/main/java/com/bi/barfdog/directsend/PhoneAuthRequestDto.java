package com.bi.barfdog.directsend;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter @Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PhoneAuthRequestDto {

    @NotNull
    private String phoneNumber;
}
