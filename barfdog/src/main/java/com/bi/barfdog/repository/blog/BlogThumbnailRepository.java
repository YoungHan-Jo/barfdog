package com.bi.barfdog.repository.blog;

import com.bi.barfdog.domain.blog.BlogThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogThumbnailRepository extends JpaRepository<BlogThumbnail, Long> {
}
