package com.anirudhb4.HypeFlix.repository;

import com.anirudhb4.HypeFlix.model.HypePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HypePointRepository extends JpaRepository<HypePoint,Long> {
    // This lets us check if a HypePoint exists for a specific user/movie combo
    boolean existsByUserIdAndMovieId(UUID userId, Long movieId);
}
