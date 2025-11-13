package com.anirudhb4.HypeFlix.service;

import com.anirudhb4.HypeFlix.dto.TmdMovieDto;
import com.anirudhb4.HypeFlix.dto.TmdUpcomingResponse;
import com.anirudhb4.HypeFlix.model.Movie;
import com.anirudhb4.HypeFlix.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled; // Make sure this is imported
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate; // Import this
import java.time.format.DateTimeFormatter; // Import this
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    private final String tmdbApiKey;
    private final String tmdbBaseUrl;

    @Autowired
    public MovieService(MovieRepository movieRepository,
                        RestTemplate restTemplate,
                        @Value("${tmdb.api.key}") String tmdbApiKey,
                        @Value("${tmdb.api.base-url}") String tmdbBaseUrl) {
        this.movieRepository = movieRepository;
        this.restTemplate = restTemplate;
        this.tmdbApiKey = tmdbApiKey;
        this.tmdbBaseUrl = tmdbBaseUrl;
    }

    /**
     * Fetches upcoming INDIAN movies from TMDb and saves/updates them in our database.
     * This will run automatically "at 2:00 AM every day".
     */
//    @PostConstruct
    @Scheduled(cron = "0 0 2 * * ?")
    public void fetchAndSaveUpcomingMovies() {

        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String languages = "ta|ml|hi|te|kn";

        // Fetch 5 pages (approx 100 movies)
        int pagesToFetch = 5;

        for (int page = 1; page <= pagesToFetch; page++) {

            // 1. Construct URL dynamically with the current 'page' number
            String url = tmdbBaseUrl + "/discover/movie?" +
                    "api_key=" + tmdbApiKey +
                    "&region=IN" +
                    "&with_original_language=" + languages +
                    "&primary_release_date.gte=" + today +
                    "&sort_by=popularity.desc" +
                    "&language=en-US" +
                    "&page=" + page; // <--- Use the loop variable here

            try {
                // 2. Call the API
                TmdUpcomingResponse response = restTemplate.getForObject(url, TmdUpcomingResponse.class);

                if (response != null && response.getResults() != null) {
                    for (TmdMovieDto dto : response.getResults()) {
                        Movie movie = new Movie();
                        movie.setId(dto.getId());
                        movie.setTitle(dto.getTitle());
                        movie.setOverview(dto.getOverview());
                        movie.setReleaseDate(dto.getReleaseDate());
                        movie.setPosterPath(dto.getPosterPath());
                        movie.setTmdbPopularity(dto.getPopularity());

                        movieRepository.save(movie);
                    }
                    System.out.println("Fetched page " + page + " successfully.");
                }
            } catch (Exception e) {
                System.err.println("Error fetching page " + page + ": " + e.getMessage());
                // Continue to the next page even if one fails
            }
        }
    }

    /**
     * Gets all movies currently in our database.
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Gets a single movie by its ID.
     */
    public Optional<Movie> getMovieById(Long id) {
        // JpaRepository gives us findById for free!
        return movieRepository.findById(id);
    }
}
