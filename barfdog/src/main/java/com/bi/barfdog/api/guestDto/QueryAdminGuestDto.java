package com.bi.barfdog.api.guestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminGuestDto {

    private String guestName;
    private String guestEmail;
    private String guestPhoneNumber;
    private LocalDateTime createdDate;

    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhoneNumber;
    private LocalDateTime joinDate;
    private LocalDateTime firstPaymentDate;

    private boolean isPaid;
}
