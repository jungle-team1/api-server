package com.krafton.api_server.api.image.controller;

import com.krafton.api_server.api.image.domain.AwsS3;
import com.krafton.api_server.api.image.service.AwsS3Service;
import com.krafton.api_server.api.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
@RequestMapping("/api/image")
@RestController
public class ImageController {

    private final ImageService imageService;
    private final AwsS3Service awsS3Service;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, AwsS3>> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("roomId") String roomId,
            @RequestParam("userId") String userId
    ) {
        try {
            // 원본 이미지 S3에 업로드
            AwsS3 originalUploadResult = awsS3Service.upload(image, roomId + "/og", userId);

            // 결과를 Map으로 묶어서 반환
            Map<String, AwsS3> result = new HashMap<>();
            result.put("original", originalUploadResult);
            System.out.println("result = " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing image: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/inpaint")
    public ResponseEntity<Map<String, AwsS3>> inpaintImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("mask_x1") int maskX1,
            @RequestParam("mask_y1") int maskY1,
            @RequestParam("mask_x2") int maskX2,
            @RequestParam("mask_y2") int maskY2,
            @RequestParam("roomId") String roomId,
            @RequestParam("userId") String userId,
            @RequestParam("prompt") String prompt
    ) {
        try {
            // 이미지 처리
            MultipartFile processedImage = imageService.processImage(
                    image,
                    maskX1,
                    maskY1,
                    maskX2,
                    maskY2,
                    prompt
            );

            // 생성된 이미지 S3에 업로드
            AwsS3 generatedUploadResult = awsS3Service.upload(processedImage, roomId + "/gen", userId);

            // 결과를 Map으로 묶어서 반환
            Map<String, AwsS3> result = new HashMap<>();
            result.put("generated", generatedUploadResult);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing image: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{roomId}/{sort}")
    public ResponseEntity<List<AwsS3>> getImages(
            @PathVariable("roomId") String roomId,
            @PathVariable("sort") String sort,
            @RequestParam(value = "excludeUserId", required = false) Long excludeUserId) {
        List<AwsS3> images = awsS3Service.getImages(roomId, sort, excludeUserId);
        return ResponseEntity.ok(images);
    }

}