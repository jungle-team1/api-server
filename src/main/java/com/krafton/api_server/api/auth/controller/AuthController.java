package com.krafton.api_server.api.auth.controller;

import com.krafton.api_server.api.auth.dto.UserResponseDto;
import com.krafton.api_server.api.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @CrossOrigin(origins = "http://localhost:5173") // React 클라이언트 도메인에 대한 CORS 설정
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<UserResponseDto> kakaoLogin(@RequestParam("code") String code) {
        String kakaoAccessToken = authService.getKakaoAccessToken(code);
        UserResponseDto response = authService.kakaoLogin(kakaoAccessToken);
        System.out.println("response = " + response.getUser_id());
        return ResponseEntity.ok(response);
    }
}