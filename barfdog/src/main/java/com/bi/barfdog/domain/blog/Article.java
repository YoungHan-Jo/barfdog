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

    private int number;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    public void change(Blog blog) {
        this.blog = blog;
    }
}
