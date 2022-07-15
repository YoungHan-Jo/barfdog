package com.bi.barfdog.api.iamportDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebHookRequestDto {

    private String imp_uid;
    private String merchant_uid;
    private String status;
}
