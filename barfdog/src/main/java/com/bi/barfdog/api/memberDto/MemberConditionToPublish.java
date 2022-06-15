package com.bi.barfdog.api.memberDto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberConditionToPublish {

    @Email
    private String email;

    private String name;

}
