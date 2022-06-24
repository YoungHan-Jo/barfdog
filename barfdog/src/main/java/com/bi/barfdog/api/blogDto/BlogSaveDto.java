package com.bi.barfdog.api.blogDto;

import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogSaveDto {

    @NotNull
    private BlogStatus status;

    @NotEmpty
    private String title;

    @NotNull
    private BlogCategory category;

    @NotEmpty
    private String contents;

    @NotNull
    private Long thumbnailId;

    @Builder.Default
    private List<Long> blogImageIdList = new ArrayList<>();

}
