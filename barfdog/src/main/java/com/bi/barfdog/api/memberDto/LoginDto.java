package com.bi.barfdog.api.memberDto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {


    private String email;

    private String password;

    private int tokenValidDays;

}
