package com.krafton.api_server.api.game.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FindDiffImage {

    @Id @GeneratedValue
    @Column(name = "find_diff_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "find_diff_game_id")
    private FindDiffGame game;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "find_diff_user_id")
    private FindDiffUser user;

    private String originalUrl;
    private String generatedUrl;

    private int maskX1;
    private int maskY1;
    private int maskX2;
    private int maskY2;

    @Builder
    public FindDiffImage(String originalUrl, String generatedUrl, int maskX1, int maskY1, int maskX2, int maskY2) {
        this.originalUrl = originalUrl;
        this.generatedUrl = generatedUrl;
        this.maskX1 = maskX1;
        this.maskY1 = maskY1;
        this.maskX2 = maskX2;
        this.maskY2 = maskY2;
    }
}
