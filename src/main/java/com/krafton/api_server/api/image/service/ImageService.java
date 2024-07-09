package com.krafton.api_server.api.image.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

    private final RestTemplate restTemplate;

    public MultipartFile processImage(MultipartFile image, int maskX1, int maskY1, int maskX2, int maskY2, String prompt) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        body.add("mask_x1", String.valueOf(maskX1));
        body.add("mask_y1", String.valueOf(maskY1));
        body.add("mask_x2", String.valueOf(maskX2));
        body.add("mask_y2", String.valueOf(maskY2));
        body.add("prompt", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String flaskServerUrl = "http://localhost:5000";
        ResponseEntity<String> response = restTemplate.postForEntity(flaskServerUrl + "/inpaint", requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to process image: " + response.getBody());
        }

        // Parse JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        if (!root.has("status") || !root.get("status").asText().equals("success")) {
            throw new RuntimeException("Failed to process image: " + response.getBody());
        }

        String base64Image = root.get("image").asText();
        byte[] processedImageBytes = Base64.getDecoder().decode(base64Image);
        return new MockMultipartFile("processed_image.png", "processed_image.png", "image/png", processedImageBytes);
    }
}