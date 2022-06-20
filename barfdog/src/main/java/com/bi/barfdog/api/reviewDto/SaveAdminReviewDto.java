package com.bi.barfdog.api.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveAdminReviewDto {

    @NotNull
    private ReviewType type; // ITEM,SUBSCRIBE
    @NotNull
    private Long id; // 아이템 id or 레시피 id
    @NotNull
    private LocalDate writtenDate; // 2022-06-20
    @NotNull
    private int star;
    @NotEmpty
    private String contents;
    @NotEmpty
    private String username;
    @Builder.Default
    private List<Long> reviewImageIdList = new ArrayList<>();

}
