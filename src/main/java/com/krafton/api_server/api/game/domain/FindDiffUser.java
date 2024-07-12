package com.krafton.api_server.api.game.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FindDiffUser {

    @Id @GeneratedValue
    @Column(name = "find_diff_user_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "find_diff_game_id")
    private FindDiffGame game;

    @OneToOne(mappedBy = "user")
    private FindDiffImage image;

    private int score;

    @Builder
    public FindDiffUser(Long userId) {
        this.userId = userId;
        this.score = 0;
    }

    public void addScore(int gameScore) {
        this.score += gameScore;
    }

    public void updateImage(FindDiffImage image) {
        this.image = image;
    }


}
