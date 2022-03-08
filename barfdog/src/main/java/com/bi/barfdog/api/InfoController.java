package com.bi.barfdog.api;

import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InfoController {

    private final StorageService storageService;

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(MultipartFile file) throws IllegalStateException, IOException {
        if (!file.isEmpty()) {

            System.out.println("file name = " + file.getOriginalFilename());
            System.out.println("file ContentType = " + file.getContentType());
            storageService.store(file);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
