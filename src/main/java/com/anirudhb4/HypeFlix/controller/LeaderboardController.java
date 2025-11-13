package com.anirudhb4.HypeFlix.controller;

import com.anirudhb4.HypeFlix.model.Movie;
import com.anirudhb4.HypeFlix.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Autowired
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /**
     * GET /api/leaderboard
     * Returns the top 10 movies based on User Hype + Internet Popularity.
     */
    @GetMapping
    public List<Movie> getGlobalLeaderboard() {
        return leaderboardService.getGlobalLeaderboard();
    }
}