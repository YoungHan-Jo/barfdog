package com.bi.barfdog.domain.setting;

import com.bi.barfdog.api.settingDto.UpdateSettingDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Setting extends BaseTimeEntity { // 바프독 전역 설정

    @Id
    @GeneratedValue
    @Column(name = "setting_id")
    private Long id;

    @Embedded
    private ActivityConstant activityConstant; // 활동량 관련

    @Embedded
    private SnackConstant snackConstant; // 간식량 관련

    @Embedded
    private DeliveryConstant deliveryConstant; // 배송 관련


    public void update(UpdateSettingDto requestDto) {
        ActivityConstant activityConstant = ActivityConstant.builder()
                .activityVeryLittle(requestDto.getActivityVeryLittle())
                .activityLittle(requestDto.getActivityLittle())
                .activityNormal(requestDto.getActivityNormal())
                .activityMuch(requestDto.getActivityMuch())
                .activityVeryMuch(requestDto.getActivityVeryMuch())
                .build();


        SnackConstant snackConstant = SnackConstant.builder()
                .snackLittle(requestDto.getSnackLittle())
                .snackNormal(requestDto.getSnackNormal())
                .snackMuch(requestDto.getSnackMuch())
                .build();

        DeliveryConstant deliveryConstant = DeliveryConstant.builder()
                .price(requestDto.getPrice())
                .freeCondition(requestDto.getFreeCondition())
                .build();

        this.activityConstant = activityConstant;
        this.snackConstant = snackConstant;
        this.deliveryConstant = deliveryConstant;

    }
}
