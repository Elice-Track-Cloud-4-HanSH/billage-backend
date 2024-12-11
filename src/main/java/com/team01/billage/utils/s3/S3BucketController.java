package com.team01.billage.utils.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "테스트용 이미지 업로드", description = "s3 이미지 업로드 테스트입니다.", tags = {"S3"})
    @ApiResponse(responseCode = "200", description = "이미지 업로드 성공")
    @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"S3 업로드 중 오류가 발생했습니다.\",\"code\":\"PUT_OBJECT_EXCEPTION\"}")))
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = s3BucketService.upload(file);
        return ResponseEntity.status(HttpStatus.OK).body(fileUrl);
    }

    @DeleteMapping("/test")
    @Operation(summary = "테스트용 이미지 삭제", description = "s3 이미지 삭제 테스트입니다.", tags = {"S3"})
    @ApiResponse(responseCode = "200", description = "이미지 삭제 성공")
    @ApiResponse(responseCode = "500", description = "이미지 삭제 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"이미지 삭제 중 오류가 발생했습니다.\",\"code\":\"IO_EXCEPTION_ON_FILE_DELETE\"}")))
    public ResponseEntity<String> deleteFile(@RequestParam("fileAddress") String fileAddress) {
        s3BucketService.delete(fileAddress);
        return ResponseEntity.status(HttpStatus.OK).body("이미지가 성공적으로 삭제되었습니다.");
    }

}
