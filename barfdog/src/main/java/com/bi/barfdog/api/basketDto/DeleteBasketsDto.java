package com.bi.barfdog.api.basketDto;

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
public class DeleteBasketsDto {

    @Builder.Default
    private List<Long> deleteBasketIdList = new ArrayList<>();
}
