package com.bi.barfdog.domain.blog;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class BlogImage extends BaseTimeEntity { // 블로그 내용에 사용된 이미지 파일

    @Id @GeneratedValue
    @Column(name = "blog_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog; // 해당하는 블로그

    private String folder;
    private String filename;

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}
