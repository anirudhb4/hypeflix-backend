package com.anirudhb4.HypeFlix.repository;

import com.anirudhb4.HypeFlix.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie,Long> {
}
