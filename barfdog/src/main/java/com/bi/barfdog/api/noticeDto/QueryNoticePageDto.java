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
public class QueryNoticePageDto {

    private NoticeDto noticeDto;

    private AnotherNotice previous;

    private AnotherNotice next;


    @Data
    @AllArgsConstructor
    @Builder
    public static class AnotherNotice{

        private Long id;
        private String title;
        private String _link;

    }

    @Data
    @AllArgsConstructor
    public static class NoticeDto{

        private Long id;
        private String title;
        private LocalDateTime createdDate;
        private String contents;

    }
}
