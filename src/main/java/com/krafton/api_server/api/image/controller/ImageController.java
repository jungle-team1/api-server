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
@RequestMapping("/api/image")
@RestController
public class ImageController {
    private final ImageService imageService;
    private final AwsS3Service awsS3Service;

    @CrossOrigin(origins = "http://localhost:5000")
    @PostMapping("/inpaint")
    public ResponseEntity<Map<String, AwsS3>> inpaintImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("mask_x1") int maskX1,
            @RequestParam("mask_y1") int maskY1,
            @RequestParam("mask_x2") int maskX2,
            @RequestParam("mask_y2") int maskY2,
            @RequestParam("roomId") String roomId,
            @RequestParam("userId") String userId,
            @RequestParam("prompt") String prompt,
            @RequestParam("mode") String mode) {
        try {
            // 원본 이미지 S3에 업로드
            AwsS3 originalUploadResult = awsS3Service.upload(image, mode, roomId, userId + "/og");

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
            AwsS3 generatedUploadResult = awsS3Service.upload(processedImage, mode, roomId, userId + "/gen");

            // 결과를 Map으로 묶어서 반환
            Map<String, AwsS3> result = new HashMap<>();
            result.put("original", originalUploadResult);
            result.put("generated", generatedUploadResult);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing image: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{mode}/{roomId}")
    public ResponseEntity<List<AwsS3>> listImages(
            @PathVariable("mode") String mode,
            @PathVariable("roomId") String roomId) {
        String prefix = mode + "/" + roomId + "/";
        List<AwsS3> images = awsS3Service.listImages(prefix);
        return ResponseEntity.ok(images);
    }

}