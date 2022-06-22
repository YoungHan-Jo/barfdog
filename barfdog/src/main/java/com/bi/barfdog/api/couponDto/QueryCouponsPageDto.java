package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.api.resource.CouponsDtoResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.PagedModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryCouponsPageDto {

    private Long totalCount;
    private Long availableCount;
    private PagedModel<CouponsDtoResource> couponsPageDto;

}
