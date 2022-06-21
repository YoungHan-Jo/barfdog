package com.bi.barfdog.repository.setting;

import com.bi.barfdog.domain.setting.DeliveryConstant;
import com.bi.barfdog.domain.setting.QSetting;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.bi.barfdog.domain.setting.QSetting.*;

@RequiredArgsConstructor
@Repository
public class SettingRepositoryImpl implements SettingRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public DeliveryConstant findDeliveryConstant() {
        return queryFactory
                .select(Projections.constructor(DeliveryConstant.class,
                        setting.deliveryConstant.price,
                        setting.deliveryConstant.freeCondition
                        ))
                .from(setting)
                .fetchOne()
                ;
    }
}
