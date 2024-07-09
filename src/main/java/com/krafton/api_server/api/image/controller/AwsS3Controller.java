package com.krafton.api_server.api.image.controller;

import com.krafton.api_server.api.image.domain.AwsS3;
import com.krafton.api_server.api.image.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/s3")
@RestController
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping("/resource")
    public ResponseEntity<AwsS3> upload(@RequestPart("file") MultipartFile file) {
        try {
            AwsS3 result = awsS3Service.upload(file, "upload");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/resource/{key}")
    public ResponseEntity<Void> delete(@PathVariable("key") String key) {
        try {
            awsS3Service.delete(key);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}