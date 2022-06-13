package com.bi.barfdog.repository.blog;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.AdminBlogImageDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.blog.QBlogImage.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Repository
public class BlogImageRepositoryImpl implements BlogImageRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AdminBlogImageDto> findAdminDtoByBlogId(Long id) {


        List<AdminBlogImageDto> result = queryFactory
                .select(Projections.constructor(AdminBlogImageDto.class,
                        blogImage.id,
                        blogImage.filename,
                        blogImage.filename
                ))
                .from(blogImage)
                .where(blogImage.blog.id.eq(id))
                .fetch();

        String url = linkTo(InfoController.class).slash("display").slash("blogs?filename=").toString();
        for (AdminBlogImageDto adminBlogImageDto : result) {
            adminBlogImageDto.setDisplayUrl(url);
        }


        return result;
    }
}
