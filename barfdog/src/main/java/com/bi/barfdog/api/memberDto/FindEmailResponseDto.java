package com.bi.barfdog.api.memberDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindEmailResponseDto {
    private String email;
    private String provider;
}
