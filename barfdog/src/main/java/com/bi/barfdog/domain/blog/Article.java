package com.bi.barfdog.domain.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
@Entity
public class Article {

    @Id @GeneratedValue
    @Column(name = "article_id")
    private Long id;

    private int number; // 아티클 번호, 아티클은 1,2 두개만 존재함

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog; // 아티클로 선택한 블로그

    public void change(Blog blog) {
        this.blog = blog;
    }
}
