package com.bi.barfdog.domain.guest;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Guest extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "guest_id")
    private Long id;

    private String name;
    private String email;
    private String phoneNumber;

}
