package com.krafton.api_server.api.game.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class FindDiffGame {

    @Id
    @GeneratedValue
    @Column(name = "find_diff_game_id")
    private Long id;

    @OneToMany(mappedBy = "game")
    private List<FindDiffUser> users = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    private List<FindDiffImage> images = new ArrayList<>();

    public void addUser(FindDiffUser user) {
        users.add(user);
    }


}
