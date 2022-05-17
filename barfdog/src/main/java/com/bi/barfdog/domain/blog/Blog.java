package com.bi.barfdog.domain.blog;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
@Entity
public class Blog extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "blog_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BlogStatus status; // [LEAKED, HIDDEN]

    private String title;

    @Enumerated(EnumType.STRING)
    private BlogCategory category; // [NUTRITION,HEALTH,LIFE]

    @Column(columnDefinition = "TEXT")
    private String contents; // 상세내용

}
