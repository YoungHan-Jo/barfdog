package com.bi.barfdog.domain.blog;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class BlogThumbnail {

    @Id @GeneratedValue
    @Column(name = "blog_thumbnail_id")
    private Long id;

    @OneToOne(mappedBy = "blogThumbnail")
    private Blog blog;

    private String folder;
    private String filename;

}
