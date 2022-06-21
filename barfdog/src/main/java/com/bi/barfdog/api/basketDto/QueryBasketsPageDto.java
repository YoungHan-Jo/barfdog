package com.bi.barfdog.api.basketDto;

import com.bi.barfdog.domain.setting.DeliveryConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryBasketsPageDto {

    private DeliveryConstant deliveryConstant;
    private List<EntityModel<QueryBasketsDto>> entityModels;
}
