package com.bi.barfdog.api.noticeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryNoticesDto {

    private Long id;

    private String title;

    private LocalDateTime createdDate;

}
