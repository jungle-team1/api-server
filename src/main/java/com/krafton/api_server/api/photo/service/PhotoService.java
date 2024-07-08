package com.krafton.api_server.api.photo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PhotoService {

    private final RestTemplate restTemplate;

    public Map<String, Object> inpaintImage(MultipartFile image, int maskX1, int maskY1, int maskX2, int maskY2, String prompt) throws IOException {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("image", image.getBytes());
        requestBody.add("mask_x1", maskX1);
        requestBody.add("mask_y1", maskY1);
        requestBody.add("mask_x2", maskX2);
        requestBody.add("mask_y2", maskY2);
        requestBody.add("prompt", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String flaskServerUrl = "https://ed5e-34-19-16-65.ngrok-free.app";
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                flaskServerUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        return responseEntity.getBody();
    }
}