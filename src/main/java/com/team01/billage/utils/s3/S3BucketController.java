package com.team01.billage.utils.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class S3BucketController {

    private final S3BucketService s3BucketService;

    @PostMapping(value = "/test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = s3BucketService.upload(file);
        return ResponseEntity.status(HttpStatus.OK).body(fileUrl);
    }

    @DeleteMapping("/test")
    public ResponseEntity<String> deleteFile(@RequestParam("fileAddress") String fileAddress) {
        s3BucketService.delete(fileAddress);
        return ResponseEntity.status(HttpStatus.OK).body("이미지가 성공적으로 삭제되었습니다.");
    }

}
