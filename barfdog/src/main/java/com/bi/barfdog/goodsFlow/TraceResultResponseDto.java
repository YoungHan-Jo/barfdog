package com.bi.barfdog.goodsFlow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceResultResponseDto {

    private String method;
    private boolean success;
    private String id;

    private InnerData data;

    private InnerError error;

    private String context;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class InnerError {
        private String status;
        private String message;
        private String detail;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class InnerData {
        private List<InnerItem> items = new ArrayList<>();

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class InnerItem {
            private String transUniqueCd;
            private String uniqueCd;
            private String seq;
            private String dlvStatCode;
        }
    }
}
