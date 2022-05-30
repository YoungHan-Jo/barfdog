package com.bi.barfdog.api.itemDto;

import com.bi.barfdog.domain.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryItemsAdminRequestDto {

    @NotNull
    private ItemType itemType; // [RAW, TOPPING, GOODS]
}
