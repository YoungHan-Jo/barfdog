package com.bi.barfdog.repository;

import com.bi.barfdog.domain.blog.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogImageRepository extends JpaRepository<BlogImage,Long>, BlogImageRepositoryCustom {

}
