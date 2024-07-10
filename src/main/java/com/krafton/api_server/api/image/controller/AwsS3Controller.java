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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/s3")
@RestController
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("mode") String mode,
            @RequestParam("roomId") String roomId,
            @RequestParam("userId") String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            AwsS3 result = awsS3Service.upload(file, mode, roomId, userId);
            response.put("success", true);
            response.put("data", result);
            log.info("File uploaded successfully: {}", result.getKey());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error uploading file to S3: ", e);
            response.put("success", false);
            response.put("error", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/image/{key}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("key") String key) {
        Map<String, Object> response = new HashMap<>();
        try {
            awsS3Service.delete(key);
            response.put("success", true);
            response.put("message", "File deleted successfully");
            log.info("File deleted successfully: {}", key);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting file from S3: ", e);
            response.put("success", false);
            response.put("error", "File deletion failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/image/{mode}/{roomId}")
    public ResponseEntity<List<AwsS3>> listImages(
            @PathVariable("mode") String mode,
            @PathVariable("roomId") String roomId) {
        String prefix = mode + "/" + roomId + "/";
        List<AwsS3> images = awsS3Service.listImages(prefix);
        return ResponseEntity.ok(images);
    }
}