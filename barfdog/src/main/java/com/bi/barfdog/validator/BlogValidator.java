package com.bi.barfdog.validator;

import com.bi.barfdog.api.blogDto.BlogSaveDto;
import com.bi.barfdog.api.blogDto.UpdateArticlesRequestDto;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogImage;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.repository.article.ArticleRepository;
import com.bi.barfdog.repository.blog.BlogImageRepository;
import com.bi.barfdog.repository.blog.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BlogValidator {

    private final BlogRepository blogRepository;
    private final ArticleRepository articleRepository;
    private final BlogImageRepository blogImageRepository;

    public void validateHiddenStatus(UpdateArticlesRequestDto requestDto, Errors errors) {
        Blog blog1 = blogRepository.findById(requestDto.getFirstBlogId()).get();
        Blog blog2 = blogRepository.findById(requestDto.getSecondBlogId()).get();

        if (blog1.getStatus() == BlogStatus.HIDDEN || blog2.getStatus() == BlogStatus.HIDDEN) {
            errors.reject("blog is hidden","숨김상태 블로그는 아티클로 설정할 수 없습니다.");
        }

    }

    public void validateDuplicateBlogId(UpdateArticlesRequestDto requestDto, Errors errors) {
        if (requestDto.getFirstBlogId() == requestDto.getSecondBlogId()) {
            errors.reject("blogs are duplicated each other","선택한 두 블로그가 서로 중복됩니다.");
        }
    }

    public void validateIsNotice(UpdateArticlesRequestDto requestDto, Errors errors) {
        Blog blog1 = blogRepository.findById(requestDto.getFirstBlogId()).get();
        Blog blog2 = blogRepository.findById(requestDto.getSecondBlogId()).get();
        if (blog1.getCategory() == BlogCategory.NOTICE || blog2.getCategory() == BlogCategory.NOTICE) {
            errors.reject("notice can not be article","공지사항은 아티클로 설정할 수 없습니다.");
        }
    }

    public void validateWrongImgId(List<Long> idList, Errors errors) {
        for (Long id : idList) {
            Optional<BlogImage> optionalBlogImage = blogImageRepository.findById(id);
            if (!optionalBlogImage.isPresent()) {
                errors.reject("image id doesn't exist","존재하지 않는 이미지 id 입니다.");
            }
        }
    }

    public void validateDuplicateThumbnail(BlogSaveDto requestDto, Errors errors) {
        Long blogThumbnailId = requestDto.getThumbnailId();
        List<Blog> blogs = blogRepository.findByBlogThumbnailId(blogThumbnailId);
        if (blogs.size() > 0) {
            errors.reject("duplicated blog","블로그 중복 등록 에러.");
        }
    }
}
