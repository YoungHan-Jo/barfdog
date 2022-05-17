package com.bi.barfdog.api.memberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryMembersCond {

    private String email;

    private String name;

    @NotNull
    @PastOrPresent @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @NotNull
    @PastOrPresent @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

}
