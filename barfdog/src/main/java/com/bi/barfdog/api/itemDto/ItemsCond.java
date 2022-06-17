package com.bi.barfdog.api.itemDto;

import com.bi.barfdog.domain.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemsCond {

    private ItemType itemType; // [ALL, RAW, TOPPING, GOODS]
    private String sortBy; // [recent, registration, saleAmount]

}
