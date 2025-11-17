package com.anirudhb4.HypeFlix.repository;

import com.anirudhb4.HypeFlix.model.Movie;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Long> {
    /**
     * Finds all movies where the release date is on or after the provided date.
     */
    List<Movie> findByReleaseDateGreaterThanEqual(LocalDate releaseDate, Sort sort);
}
