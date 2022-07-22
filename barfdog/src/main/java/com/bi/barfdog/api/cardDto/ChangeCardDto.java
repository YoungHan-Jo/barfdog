package com.bi.barfdog.api.cardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeCardDto {

    @NotEmpty
    private String customerUid;
    @NotEmpty
    private String cardName;
    @NotEmpty
    private String cardNumber;
}
