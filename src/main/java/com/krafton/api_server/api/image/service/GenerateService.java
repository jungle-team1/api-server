package com.krafton.api_server.api.image.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenerateService {

    private final RestTemplate restTemplate;

//    public MultipartFile processImage(MultipartFile image, int maskX1, int maskY1, int maskX2, int maskY2, String prompt) throws IOException {
//        log.info("Received mask coordinates: x1={}, y1={}, x2={}, y2={}", maskX1, maskY1, maskX2, maskY2);
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("image", new ByteArrayResource(image.getBytes()) {
//            @Override
//            public String getFilename() {
//                return image.getOriginalFilename();
//            }
//        });
//        body.add("mask_x1", String.valueOf(maskX1));
//        body.add("mask_y1", String.valueOf(maskY1));
//        body.add("mask_x2", String.valueOf(maskX2));
//        body.add("mask_y2", String.valueOf(maskY2));
//        body.add("prompt", prompt);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        String flaskServerUrl = "http://localhost:5000";
//        ResponseEntity<String> response = restTemplate.postForEntity(flaskServerUrl + "/inpaint", requestEntity, String.class);
//
//        if (response.getStatusCode() != HttpStatus.OK) {
//            throw new RuntimeException("Failed to process image: " + response.getBody());
//        }
//
//        // Parse JSON response
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(response.getBody());
//
//        if (!root.has("status") || !root.get("status").asText().equals("success")) {
//            throw new RuntimeException("Failed to process image: " + response.getBody());
//        }
//
//        String base64Image = root.get("image").asText();
//        byte[] processedImageBytes = Base64.getDecoder().decode(base64Image);
//
//        // Create a DiskFileItem
//        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem(
//                "file",
//                "image/png",
//                true,
//                "processed_image.png"
//        );
//
//        // Write the decoded byte array to the file item
//        try (OutputStream os = fileItem.getOutputStream()) {
//            os.write(processedImageBytes);
//        }
//
//        // Create and return a CommonsMultipartFile
//        return new CommonsMultipartFile(fileItem);
//    }
}