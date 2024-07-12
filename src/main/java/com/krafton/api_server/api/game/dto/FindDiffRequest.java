package com.krafton.api_server.api.game.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class FindDiffRequest {
    @Getter
    @Setter
    public static class FindDiffRequestDto {
        private Long roomId;
    }

    @Getter
    @Setter
    public static class FindDiffUserRequestDto {
        private Long userId;
    }

    @Getter
    @Setter
    public static class FindDiffImageRequestDto {
        private Long userId;
        private Long roomId;
        private MultipartFile image;
    }

    @Getter
    @Setter
    public static class FindDiffGeneratedImageRequestDto {
        private Long userId;
        private Long roomId;
        private MultipartFile image;
        private String prompt;
        private int maskX1;
        private int maskY1;
        private int maskX2;
        private int maskY2;

    }

}
