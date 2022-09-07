package com.bi.barfdog.repository.blog;

import com.bi.barfdog.domain.blog.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogImageRepository extends JpaRepository<BlogImage,Long>, BlogImageRepositoryCustom {

    void deleteAllByBlogId(Long id);

}
