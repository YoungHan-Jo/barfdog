package com.bi.barfdog.domain.blog;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class BlogThumbnail { // 블로그 썸네일 사진

    @Id @GeneratedValue
    @Column(name = "blog_thumbnail_id")
    private Long id;

    @OneToOne(mappedBy = "blogThumbnail")
    private Blog blog;

    private String folder;
    private String filename;

}
