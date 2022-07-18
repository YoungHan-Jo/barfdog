package com.bi.barfdog.api.cardDto;

import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuerySubscribeCardsDto {


    private SubscribeCardDto subscribeCardDto;

    @Builder.Default
    private List<String> recipeNameList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscribeCardDto{

        private Long subscribeId;
        private Long cardId;
        private String cardName;
        private String cardNumber;
        private String dogName;
        private SubscribePlan plan;
        private LocalDateTime nextPaymentDate;
        private int nextPaymentPrice;

    }

}
