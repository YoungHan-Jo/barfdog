package com.bi.barfdog.api.blogDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateArticlesRequestDto {

    @NotNull
    @Positive
    private Long firstBlogId;

    @NotNull
    @Positive
    private Long secondBlogId;

}
