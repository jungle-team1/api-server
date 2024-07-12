package com.krafton.api_server.api.game.repository;

import com.krafton.api_server.api.game.domain.FindDiffUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindDiffUserRepository extends JpaRepository<FindDiffUser, Long> {
}
