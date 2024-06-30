package com.krafton.api_server.controller;

import com.krafton.api_server.config.KakaoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {
    @Autowired
    KakaoApi kakaoApi;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("kakaoApiKey", kakaoApi.kakaoApiKey);
        model.addAttribute("redirectUri", kakaoApi.redirectUri);
        return "login";
    }
    @GetMapping("/login/oauth2/code/kakao")
    @ResponseBody
    public Map<String, Object> kakaoLogin(@RequestParam String code) {
        Map<String, Object> userInfo = new HashMap<>();
        try {
            userInfo = kakaoApi.getUserInfo(code);
            System.out.println(userInfo);
        } catch (Exception e) {
            e.printStackTrace(); // 로그로 예외 출력
            userInfo.put("error", e.getMessage());
        }
        return userInfo;
    }
}
