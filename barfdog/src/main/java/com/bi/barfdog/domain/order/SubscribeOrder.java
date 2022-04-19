package com.bi.barfdog.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("subscribe")
@Entity
public class SubscribeOrder extends Order{




}
