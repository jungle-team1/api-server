package com.krafton.api_server.api.photo.controller;

import com.krafton.api_server.api.photo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(value = "/inpaint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> inpaintImage(
            @RequestPart("image") MultipartFile image,
            @RequestParam("mask_x1") int maskX1,
            @RequestParam("mask_y1") int maskY1,
            @RequestParam("mask_x2") int maskX2,
            @RequestParam("mask_y2") int maskY2,
            @RequestParam("prompt") String prompt
    ) {
        try {
            Map<String, Object> responseData = photoService.inpaintImage(image, maskX1, maskY1, maskX2, maskY2, prompt);
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            log.error("Error processing image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error processing image"));
        }
    }
}