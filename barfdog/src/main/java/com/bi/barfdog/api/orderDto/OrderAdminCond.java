package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAdminCond {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    private String merchantUid;
    private String memberName;
    private String memberEmail;
    private String recipientName;
    
    @Builder.Default
    private List<OrderStatus> statusList = new ArrayList<>();

    @NotNull
    private OrderType orderType; // GENERAL,SUBSCRIBE;

}
