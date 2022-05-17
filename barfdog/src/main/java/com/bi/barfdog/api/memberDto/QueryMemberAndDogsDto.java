package com.bi.barfdog.api.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryMemberAndDogsDto {

    private QueryMemberDto memberDto;

    private List<String> dogNames;
}
