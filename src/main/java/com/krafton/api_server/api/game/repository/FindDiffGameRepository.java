package com.krafton.api_server.api.game.repository;

import com.krafton.api_server.api.game.domain.FindDiffGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindDiffGameRepository extends JpaRepository<FindDiffGame, Long> {
}
