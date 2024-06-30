package com.krafton.api_server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class KakaoApi {

    @Value("${kakao.api_key}")
    public String kakaoApiKey;

    @Value("${kakao.redirect_uri}")
    public String redirectUri;

    @Value("${kakao.clientSecrect}")
    public String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        System.out.println("Access Token: " + accessToken); // 로그 추가
        return getUserInfoByAccessToken(accessToken);
    }

    public String getAccessToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tokenUri)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", kakaoApiKey)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code)
                .queryParam("client_secret", clientSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uriBuilder.toUriString(), HttpMethod.POST, request, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode body = response.getBody();
                return body != null ? body.get("access_token").asText() : null;
            } else {
                throw new RuntimeException("Failed to get access token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting access token", e);
        }
    }

    private Map<String, Object> getUserInfoByAccessToken(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, JsonNode.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return objectMapper.convertValue(response.getBody(), Map.class);
            } else {
                throw new RuntimeException("Failed to get user info: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting user info", e);
        }
    }
}
