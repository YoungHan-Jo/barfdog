package com.bi.barfdog.api.reviewDto;

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
public class UpdateReviewDto {

    @NotNull
    @Min(1)
    @Max(5)
    private int star;

    @NotEmpty
    @Size(min = 10)
    private String contents;

    @Builder.Default
    private List<Long> addImageIdList = new ArrayList<>();

    @Builder.Default
    private List<Long> deleteImageIdList = new ArrayList<>();

}
