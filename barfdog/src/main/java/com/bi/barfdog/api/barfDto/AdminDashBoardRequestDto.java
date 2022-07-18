package com.bi.barfdog.api.barfDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashBoardRequestDto {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

}
