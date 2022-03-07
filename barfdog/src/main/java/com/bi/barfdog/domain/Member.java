package com.bi.barfdog.domain;

import com.bi.barfdog.domain.dog.Dog;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phone;
    private String birthday;
    private String recCode;
    private LocalDateTime createDate;
    private LocalDateTime lastLoginDate;
    private String receiveSms;
    private String receiveEmail;
    private int rewardPoint;

    @Enumerated(EnumType.STRING)
    private Grade grade; // [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Dog> dogs = new ArrayList<>();





}
