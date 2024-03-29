package com.bi.barfdog.domain.blog;

import com.bi.barfdog.api.blogDto.UpdateBlogRequestDto;
import com.bi.barfdog.api.blogDto.UpdateNoticeRequestDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
@Entity
public class Blog extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BlogStatus status; // [LEAKED, HIDDEN]

    private String title;

    @Enumerated(EnumType.STRING)
    private BlogCategory category; // [NUTRITION,HEALTH,LIFE,NOTICE]

    @Column(columnDefinition = "TEXT")
    private String contents; // 상세내용, html을 그대로 저장

    @OneToOne(fetch = LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "blog_thumbnail_id")
    private BlogThumbnail blogThumbnail; // 블로그 썸네일 이미지

    public void update(UpdateBlogRequestDto requestDto, BlogThumbnail blogThumbnail) {
        this.status = requestDto.getStatus();
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.contents = requestDto.getContents();
        this.blogThumbnail = blogThumbnail;
    }

    public void update(UpdateNoticeRequestDto requestDto) {
        this.status = requestDto.getStatus();
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }
}
