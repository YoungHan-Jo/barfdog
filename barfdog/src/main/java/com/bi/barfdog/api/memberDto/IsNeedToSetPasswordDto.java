package com.bi.barfdog.api.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IsNeedToSetPasswordDto {

    boolean isNeedToSetPassword;
}
