package com.bi.barfdog.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Dog {

    @Id
    @GeneratedValue
    @Column(name = "dog_id")
    private Long id;

    private String name;
    private int birth;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
