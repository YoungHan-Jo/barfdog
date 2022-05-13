package com.bi.barfdog.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CommonUtils {

    public static void sendErrorMessageDto(HttpServletResponse response, HttpStatus httpStatus, String errorMessage) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .status(httpStatus.value())
                .reason(errorMessage)
                .build();
        String result = objectMapper.writeValueAsString(errorMessageDto);
        response.getWriter().write(result);
    }
}
