package com.bi.barfdog.repository.blog;

import com.bi.barfdog.domain.blog.QBlogThumbnail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.blog.QBlogThumbnail.*;

@RequiredArgsConstructor
@Repository
public class BlogThumbnailRepositoryImpl implements BlogThumbnailRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<String> findFilename() {
        return queryFactory
                .select(blogThumbnail.filename)
                .from(blogThumbnail)
                .fetch()
                ;
    }
}
