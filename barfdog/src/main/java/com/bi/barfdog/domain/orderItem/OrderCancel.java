package com.bi.barfdog.domain.orderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class OrderCancel {

    private String cancelReason; // 취소 사유
    private String cancelDetailReason; // 취소 상세사유
    private LocalDateTime cancelRequestDate; // 취소요청일
    private LocalDateTime cancelConfirmDate; // 취소요청 관리자 컨펌일

}
