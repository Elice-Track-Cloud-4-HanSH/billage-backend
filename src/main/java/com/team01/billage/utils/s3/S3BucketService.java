package com.team01.billage.utils.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.team01.billage.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.UUID;

import static com.team01.billage.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class S3BucketService {

    private final AmazonS3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile file) {
        return uploadToS3(file);
    }

    private String uploadToS3(MultipartFile file) {
        // 파일이름을 uuid와 합쳐서 만들어서 s3에 저장
        String originalFileName = file.getOriginalFilename(); // 원본 파일명
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFileName; // 변경된 파일명

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            s3Client.putObject(bucketName, s3FileName, file.getInputStream(), metadata);
        } catch (Exception e) {
            throw new CustomException(PUT_OBJECT_EXCEPTION);
        }

        // s3에 저장된 파일 경로(public url)
        return s3Client.getUrl(bucketName, s3FileName).toString();
    }

    // 이미지의 public url을 이용하여 s3에서 해당 이미지 제거
    public void delete(String fileAddress) {
        String fileName = getKeyFromFileAddress(fileAddress);
        try {
            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new CustomException(IO_EXCEPTION_ON_FILE_DELETE);
        }
    }

    private String getKeyFromFileAddress(String fileAddress) {
        try {
            URL url = new URL(fileAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8"); // '/+파일명'
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new CustomException(IO_EXCEPTION_ON_FILE_DELETE);
        }
    }

}
