package com.bi.barfdog.repository;

import com.bi.barfdog.domain.blog.Blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long>, BlogRepositoryCustom {

    Optional<Blog> findByTitle(String title);

}