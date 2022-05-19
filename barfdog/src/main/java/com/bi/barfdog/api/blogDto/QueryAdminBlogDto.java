package com.bi.barfdog.api.blogDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminBlogDto {

    private BlogAdminDto blogAdminDto;

    private List<AdminBlogImageDto> adminBlogImageDtos;
}
