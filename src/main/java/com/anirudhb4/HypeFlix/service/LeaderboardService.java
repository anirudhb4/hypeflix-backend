package com.anirudhb4.HypeFlix.service;

import com.anirudhb4.HypeFlix.model.Movie;
import com.anirudhb4.HypeFlix.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final MovieRepository movieRepository;

    @Autowired
    public LeaderboardService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getGlobalLeaderboard() {
        // 1. Fetch all movies from the database
        List<Movie> allMovies = movieRepository.findAll();

        // 2. Sort them in memory using your getRawHypeScore() logic
        return allMovies.stream()
                // Sort DESCENDING (High score -> Low score)
                .sorted(Comparator.comparingLong(Movie::getRawHypeScore).reversed())
                // 3. Limit to Top 10
                .limit(10)
                .collect(Collectors.toList());
    }
}