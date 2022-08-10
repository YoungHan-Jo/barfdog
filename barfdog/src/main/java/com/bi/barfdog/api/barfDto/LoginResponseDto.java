package com.bi.barfdog.api.barfDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private String name;
    private String email;
    @Builder.Default
    private List<String> roleList = new ArrayList<>();
    private LocalDateTime expiresAt;
    private boolean isTemporaryPassword;

}
