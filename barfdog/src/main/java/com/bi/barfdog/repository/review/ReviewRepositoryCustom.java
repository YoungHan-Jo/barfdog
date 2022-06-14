package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.reviewDto.QueryReviewDto;
import com.bi.barfdog.api.reviewDto.QueryReviewsDto;
import com.bi.barfdog.api.reviewDto.QueryWriteableReviewsDto;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<QueryWriteableReviewsDto> findWriteableReviewDto(Member member, Pageable pageable);

    Page<QueryReviewsDto> findReviewsDtoByMember(Pageable pageable, Member member);

    QueryReviewDto findReviewDtoById(Long id);
}
