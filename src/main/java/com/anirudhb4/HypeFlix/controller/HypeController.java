package com.anirudhb4.HypeFlix.controller;

import com.anirudhb4.HypeFlix.model.Movie;
import com.anirudhb4.HypeFlix.service.HypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class HypeController {

    private final HypeService hypeService;

    @Autowired
    public HypeController(HypeService hypeService) {
        this.hypeService = hypeService;
    }

    /**
     * POST /api/movies/{movieId}/hype
     * Secured endpoint to "hype" a movie.
     */
    @PostMapping("/{movieId}/hype")
    public ResponseEntity<?> hypeMovie(
            @PathVariable Long movieId,
            @AuthenticationPrincipal Jwt jwt) { // <-- Spring injects the user's token

        // 1. Get the User ID from the JWT token.
        // The "subject" (sub) of a Supabase JWT is the user's UUID.
        String supabaseUserId = jwt.getSubject();
        UUID userId = UUID.fromString(supabaseUserId);

        try {
            // 2. Call the service to add the hype
            hypeService.addHype(movieId, userId);
            // Return 200 OK (empty body) on success
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            // Return 400 Bad Request if user already hyped
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Catch other potential errors (e.g., movie not found)
            return ResponseEntity.status(500).body("An internal error occurred.");
        }
    }

    /**
     * GET /api/movies/hyped
     * Returns a list of ALL movies the currently logged-in user has hyped.
     */
    @GetMapping("/hyped")
    public List<Movie> getUserHypedMovies(@AuthenticationPrincipal Jwt jwt) {
        // 1. Get the User ID from the token
        UUID userId = UUID.fromString(jwt.getSubject());

        // 2. Fetch and return the list
        return hypeService.getMoviesHypedByUser(userId);
    }
}