package com.bi.barfdog.api.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindPasswordRequestDto {

    @NotEmpty
    private String email;
    @NotEmpty
    private String name;
    @NotEmpty
    private String phoneNumber;

}
