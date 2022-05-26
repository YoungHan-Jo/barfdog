package com.bi.barfdog.api.rewardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishToPersonalDto {

    @NotEmpty
    private String name;

    @NotNull
    @PositiveOrZero
    private int amount;

    @Builder.Default
    private List<Long> memberIdList = new ArrayList<>();

    @NotNull
    private boolean alimTalk;

}
