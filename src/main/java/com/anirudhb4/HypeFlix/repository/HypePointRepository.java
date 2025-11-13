package com.anirudhb4.HypeFlix.repository;

import com.anirudhb4.HypeFlix.model.HypePoint;
import com.anirudhb4.HypeFlix.model.Movie;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface HypePointRepository extends JpaRepository<HypePoint,Long> {
    // This lets us check if a HypePoint exists for a specific user/movie combo
    boolean existsByUserIdAndMovieId(UUID userId, Long movieId);

    // This custom SQL query selects the "Movie" object from the HypePoint table
    // where the user ID matches the one we provide.
    @Query("SELECT h.movie FROM HypePoint h WHERE h.user.id = :userId")
    List<Movie> findMoviesHypedByUser(UUID userId);

    @Transactional
    void deleteByUserIdAndMovieId(UUID userId, Long movieId);
}
