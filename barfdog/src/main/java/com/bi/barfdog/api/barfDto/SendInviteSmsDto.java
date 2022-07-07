package com.bi.barfdog.api.barfDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendInviteSmsDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String phone;
    @NotEmpty
    private String homePageUrl;

}
