package com.bi.barfdog.api.barfDto;

import com.bi.barfdog.api.couponDto.Area;
import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendTalkGroupDto {

    @NotNull
    private int templateNum;

    @Builder.Default
    @NotNull
    @Size(min = 1)
    private List<Grade> gradeList = new ArrayList<>();

    @NotNull
    private boolean subscribe;

    @NotEmpty
    @Positive
    private String birthYearFrom;
    @NotEmpty
    @Positive
    private String birthYearTo;

    @NotNull
    private Area area; // [ALL, METRO, NON_METRO]

    @NotNull
    private boolean longUnconnected;

}
