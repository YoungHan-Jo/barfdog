package com.bi.barfdog.validator;

import com.bi.barfdog.api.blogDto.UpdateArticlesRequestDto;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
@Component
public class BlogValidator {

    private final BlogRepository blogRepository;

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
}
