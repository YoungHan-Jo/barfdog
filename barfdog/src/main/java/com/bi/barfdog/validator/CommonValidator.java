package com.bi.barfdog.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CommonValidator {

    public void numberValidatorInString(String str, Errors errors) {
        for (char c : str.toCharArray()) {
            if (c < 46 || c > 57 || c == 47) {
                errors.reject("not Number","입력한 값이 숫자가 아닙니다.");
                return;
            }
        }
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c>=48 && c<=57) {
                count++;
            }
        }
        if (count == 0) {
            errors.reject("not Number","입력한 값이 숫자가 아닙니다.");
        }
    }

    public void validateFiles(Errors errors, MultipartFile... file) {
        for (MultipartFile multipartFile : file) {
            if (multipartFile == null) {
                errors.reject("File is Empty","파일이 존재하지 않습니다.");
                break;
            }
        }
    }
}
