package com.bi.barfdog.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CommonValidator {

    public void numberValidatorInString(String str, Errors errors) {
        for (char c : str.toCharArray()) {
            if (c < 46 || c > 57 || c == 47) { // 숫자와 '.' 이외의 값이 존재할 경우
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
        if (count == 0) { // 숫자가 하나도 포함되지 않는 경우
            errors.reject("not Number","입력한 값이 숫자가 아닙니다.");
            return;
        }

        int dotCount = 0;
        for (char c : str.toCharArray()) {
            if (c == 46) {
                dotCount++;
            }
        }
        if (dotCount > 1) { // '.' 이 2개 이상인 경우
            errors.reject("not Number","입력한 값이 숫자가 아닙니다.");
            return;
        }

        if (count == 1 && dotCount == 1) { // 숫자와 점'.'이 각각 하나만 존재하는 경우
            errors.reject("not Number","입력한 값이 숫자가 아닙니다.");
            return;
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
