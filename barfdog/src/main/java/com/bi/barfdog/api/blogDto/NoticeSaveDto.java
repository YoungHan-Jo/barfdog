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
public class NoticeSaveDto {

    @NotNull
    private BlogStatus status;

    @NotEmpty
    private String title;

    @NotEmpty
    private String contents;

    @Builder.Default
    private List<Long> noticeImageIdList = new ArrayList<>();

}
