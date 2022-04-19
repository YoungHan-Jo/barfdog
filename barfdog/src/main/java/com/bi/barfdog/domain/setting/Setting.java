package com.bi.barfdog.domain.setting;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Setting extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "setting_id")
    private Long id;

    @Embedded
    private ActivityConstant activityConstant;

    @Embedded
    private SnackConstant snackConstant;

    @Embedded
    private DeliveryConstant deliveryConstant;


}
