package com.bi.barfdog.api.rewardDto;

import com.bi.barfdog.api.couponDto.Area;
import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishToGroupDto {

    @NotEmpty
    private String name;

    @NotNull
    @PositiveOrZero
    private int amount;

    @NotNull
    private boolean subscribe;
    @NotNull
    private boolean longUnconnected;

    @Builder.Default
    @NotNull
    @Size(min = 1)
    private List<Grade> gradeList = new ArrayList<>(); // [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]

    @NotNull
    private Area area; // [ALL, METRO, NON_METRO]

    @NotEmpty
    @Positive
    private String birthYearFrom; // 'yyyy'
    @NotEmpty
    @Positive
    private String birthYearTo; // 'yyyy'

    @NotNull
    private boolean alimTalk;


}
