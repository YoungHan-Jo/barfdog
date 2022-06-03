package com.bi.barfdog.validator;

import com.bi.barfdog.api.rewardDto.PublishToGroupDto;
import com.bi.barfdog.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
@Component
public class RewardValidator {

    public void validateBirth(PublishToGroupDto requestDto, Errors errors) {
        Integer from = Integer.valueOf(requestDto.getBirthYearFrom());
        Integer to = Integer.valueOf(requestDto.getBirthYearTo());
        if (from > to) {
            errors.reject("birthYear is Wrong","생년 범위의 순서가 잘못 되었습니다.");
        }
    }


}
