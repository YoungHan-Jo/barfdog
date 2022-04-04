package com.bi.barfdog.directsend;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DirectSendResponseDto {

    private int responseCode;

    private int status;

    private String msg;

}
