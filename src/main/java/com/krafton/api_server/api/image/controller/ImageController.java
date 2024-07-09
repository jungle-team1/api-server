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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/img")
@RestController
public class ImageController {
    private final ImageService imageService;
    private final AwsS3Service awsS3Service;

    @CrossOrigin(origins = "http://localhost:5000")
    @PostMapping("/inpaint")
    public ResponseEntity<AwsS3> inpaintImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("mask_x1") int maskX1,
            @RequestParam("mask_y1") int maskY1,
            @RequestParam("mask_x2") int maskX2,
            @RequestParam("mask_y2") int maskY2,
            @RequestParam("prompt") String prompt) {
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

            // S3에 업로드
            AwsS3 uploadResult = awsS3Service.upload(processedImage, "inpainted");

            return ResponseEntity.ok(uploadResult);
        } catch (Exception e) {
            log.error("Error processing image: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}