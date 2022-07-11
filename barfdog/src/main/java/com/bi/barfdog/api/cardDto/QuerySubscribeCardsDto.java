package com.bi.barfdog.api.cardDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuerySubscribeCardsDto {

    private Long subscribeId;

    private String customerUid;

    private String cardName;

    private String cardNumber;

}
