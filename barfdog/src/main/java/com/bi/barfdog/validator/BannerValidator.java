package com.bi.barfdog.validator;

import com.bi.barfdog.domain.banner.MainBanner;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

@Component
public class BannerValidator {

    public void validate(Errors errors,MultipartFile... file) {
        for (MultipartFile multipartFile : file) {
            if (multipartFile == null) {
                errors.reject("File is Empty","파일이 존재하지 않습니다.");
                break;
            }
        }
    }

}
