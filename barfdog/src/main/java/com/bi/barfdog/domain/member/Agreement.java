package com.bi.barfdog.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Agreement {

    private boolean servicePolicy;
    private boolean privacyPolicy;

    private boolean receiveSms;
    private boolean receiveEmail;

    private boolean over14YearsOld;
}
