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

    @OneToMany(mappedBy = "findDiffGame")
    private List<FindDiffUser> findDiffUsers = new ArrayList<>();

    @OneToMany(mappedBy = "findDiffGame")
    private List<FindDiffImage> findDiffImages = new ArrayList<>();

    public void addUser(FindDiffUser user) {
        findDiffUsers.add(user);
    }
}
