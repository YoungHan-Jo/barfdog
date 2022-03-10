package com.bi.barfdog.api;

import com.bi.barfdog.service.file.StorageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InfoController {

    private final StorageService storageService;

    @Value("${spring.servlet.multipart.location}") // yml에 있는거 읽어 옴
    private String uploadRootPath;

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(MultipartFile file) throws IllegalStateException, IOException {
        if (!file.isEmpty()) {

            System.out.println("file name = " + file.getOriginalFilename());
            System.out.println("file ContentType = " + file.getContentType());
            storageService.store(file);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> serveFile(@RequestParam(value="filename") String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/display")
    public ResponseEntity<Resource> display(@RequestParam(value = "filename") String filename) {

        String path = uploadRootPath;
        String folder = "/banners/";

        Resource resource = storageService.loadAsResource(filename);

        if (!resource.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders header = new HttpHeaders();
        Path filePath = null;

        try {
            filePath = Paths.get(path + folder + filename);
            header.add("Content-type", Files.probeContentType(filePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }





    @GetMapping("/fileList")
    public ResponseEntity<List<FileData>> getListFiles() {
        List<FileData> fileInfos = storageService.loadAll()
                .map(path ->{
                    FileData data = new FileData();
                    String filename = path.getFileName().toString();
                    data.setFilename(filename);
                    data.setUrl(MvcUriComponentsBuilder.fromMethodName(InfoController.class,
                            "display", filename).build().toString());
                    try {
                        data.setSize(Files.size(path));
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    return data;
                })
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @Getter
    @Setter
    @ToString
    public class FileData {
        private String filename;
        private String url;
        private Long size;
    }

}
