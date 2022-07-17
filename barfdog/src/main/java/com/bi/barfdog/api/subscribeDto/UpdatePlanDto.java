package com.bi.barfdog.api.subscribeDto;

import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePlanDto {

    private SubscribePlan plan; // [FULL, HALF, TOPPING]

    private int nextPaymentPrice;

    @Builder.Default
    private List<Long> recipeIdList = new ArrayList<>();
}
