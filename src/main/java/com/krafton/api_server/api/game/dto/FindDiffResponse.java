package com.krafton.api_server.api.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class FindDiffResponse {
    @Getter
    @AllArgsConstructor
    public static class FindDiffGeneratedImageResponseDto {
        private String generatedUrl;
        private int maskX1;
        private int maskY1;
        private int maskX2;
        private int maskY2;
    }
}