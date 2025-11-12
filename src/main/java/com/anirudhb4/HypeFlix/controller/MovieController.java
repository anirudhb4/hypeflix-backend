package com.anirudhb4.HypeFlix.controller;

import com.anirudhb4.HypeFlix.model.Movie;
import com.anirudhb4.HypeFlix.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Tells Spring this is a REST API controller
@RequestMapping("/api/movies") // All endpoints in this class will start with /api/movies
public class MovieController {

    private final MovieService movieService;

    // Spring will automatically inject the MovieService we already built
    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * GET /api/movies
     * Gets the full list of all movies in our database.
     */
    @GetMapping
    public List<Movie> getAllMovies() {
        // We just call the service method we already wrote
        return movieService.getAllMovies();
    }

    /**
     * GET /api/movies/{id}
     * Gets a single movie by its ID (the TMDb ID).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        // ResponseEntity gives us more control over the response (like sending 404)
        return movieService.getMovieById(id)
                .map(movie -> ResponseEntity.ok(movie)) // If found, return 200 OK with the movie
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }
}