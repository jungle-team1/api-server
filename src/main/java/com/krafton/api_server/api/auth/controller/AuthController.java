package com.krafton.api_server.api.auth.controller;

import com.krafton.api_server.api.auth.domain.User;
import com.krafton.api_server.api.auth.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final String FRONTEND_URL = "http://localhost:5203"; // 프론트엔드 URL을 상수로 정의

    @GetMapping("/loginSuccess")
    public void loginSuccess(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                             HttpSession session,
                             HttpServletResponse response) throws IOException {
        User user = customOAuth2User.getUser();
        session.setAttribute("user", user);

        String redirectUrl = buildRedirectUrl(user);
        response.sendRedirect(redirectUrl);
    }

    private String buildRedirectUrl(User user) {
        StringBuilder urlBuilder = new StringBuilder(FRONTEND_URL);
        urlBuilder.append("/loginSuccess?");

        appendParameter(urlBuilder, "username", user.getUsername());
        appendParameter(urlBuilder, "nickname", user.getNickname());

        return urlBuilder.toString();
    }

    private void appendParameter(StringBuilder urlBuilder, String key, String value) {
        if (value != null && !value.isEmpty()) {
            if (urlBuilder.charAt(urlBuilder.length() - 1) != '?') {
                urlBuilder.append('&');
            }
            urlBuilder.append(key)
                    .append('=')
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
    }

    @GetMapping("/loginFailure")
    public ResponseEntity<String> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Login failed. Please try again.");
    }

    @GetMapping("/api/user")
    public ResponseEntity<?> getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not logged in"));
        }
        return ResponseEntity.ok(user);
    }
}