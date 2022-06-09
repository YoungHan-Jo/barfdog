package com.bi.barfdog.domain.blog;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class BlogImage extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "blog_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    private String folder;
    private String filename;

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}