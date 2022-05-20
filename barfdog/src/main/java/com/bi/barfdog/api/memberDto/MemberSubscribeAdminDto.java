package com.bi.barfdog.api.memberDto;

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
public class MemberSubscribeAdminDto {

    private QuerySubscribeAdminDto querySubscribeAdminDto;

    private List<String> recipeNames = new ArrayList<>();

}
