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
public class UpdateNoticeRequestDto {

    @NotNull
    private BlogStatus status; // [LEAKED, HIDDEN]
    @NotEmpty
    private String title;
    @NotEmpty
    private String contents; // 상세내용

    @Builder.Default
    private List<Long> addImageIdList = new ArrayList<>();
    @Builder.Default
    private List<Long> deleteImageIdList = new ArrayList<>();
}
