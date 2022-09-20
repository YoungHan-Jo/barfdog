package com.bi.barfdog.api.deliveryDto;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class UpdateDeliveryNumberDto {

    @Builder.Default
    @Valid
    private List<DeliveryNumberDto> deliveryNumberDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder @Getter
    public static class DeliveryNumberDto {
        @NotEmpty
        private String transUniqueCd;
        @NotEmpty
        private String deliveryNumber;
    }
}

