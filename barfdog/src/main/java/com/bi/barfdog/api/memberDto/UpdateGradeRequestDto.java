package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateGradeRequestDto {

    @NotNull
    private Grade grade;
}
