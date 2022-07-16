package com.bi.barfdog.api.subscribeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateGramDto {

    private int gram;
    private int totalPrice;

}
