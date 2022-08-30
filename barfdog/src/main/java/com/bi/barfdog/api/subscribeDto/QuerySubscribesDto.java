package com.bi.barfdog.api.subscribeDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuerySubscribesDto {

    private SubscribeDto subscribeDto;

    private String recipeNames;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SubscribeDto{
        private Long subscribeId;
        private String pictureUrl;
        private SubscribePlan plan;
        private String dogName;
        private int countSkipOneTime;
        private int countSkipOneWeek;
        private LocalDateTime nextPaymentDate;
        private int nextPaymentPrice;
        private int discountCoupon;
        private int discountGrade;

        public void changeUrl(String filename) {
            if (filename != null) {
                pictureUrl = linkTo(InfoController.class).slash("display/dogs?filename=" + filename).toString();
            }
        }
    }

}
