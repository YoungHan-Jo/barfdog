package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.itemDto.ItemReviewsDto;
import com.bi.barfdog.api.reviewDto.*;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<QueryWriteableReviewsDto> findWriteableReviewDto(Member member, Pageable pageable);

    Page<QueryReviewsDto> findReviewsDtoByMember(Pageable pageable, Member member);

    QueryReviewDto findReviewDtoById(Long id);

    Page<QueryAdminReviewsDto> findAdminReviewsDto(Pageable pageable, AdminReviewsCond cond);

    QueryAdminReviewDto findAdminReviewDto(Long id);

    Page<ItemReviewsDto> findItemReviewsDtoByItemId(Pageable pageable, Long id);

    Page<QueryCommunityReviewsDto> findCommunityReviewsDto(Pageable pageable);

    QueryCommunityReviewDto findCommunityReviewDtoById(Long id);
}
