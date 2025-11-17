package com.anirudhb4.HypeFlix.service;

import com.anirudhb4.HypeFlix.dto.TmdMovieDto;
import com.anirudhb4.HypeFlix.dto.TmdUpcomingResponse;
import com.anirudhb4.HypeFlix.model.Movie;
import com.anirudhb4.HypeFlix.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
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

        // --- 1. ADD THIS WARM-UP CALL ---
        try {
            System.out.println("Warming up TMDb connection...");
            String url = tmdbBaseUrl + "/configuration?api_key=" + tmdbApiKey;
            // We don't care about the response, just make the call to establish SSL
            restTemplate.getForObject(url, String.class);
            System.out.println("TMDb connection is warm.");
        } catch (Exception e) {
            System.err.println("TMDb connection warm-up failed. This might be okay: " + e.getMessage());
            // Don't stop the app from starting. The main call will try again.
        }
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

        // Fetch 20 pages (approx 400 movies)
        int pagesToFetch = 20;

        for (int page = 1; page <= pagesToFetch; page++) {

            // 1. Construct URL dynamically with the current 'page' number
            String url = tmdbBaseUrl + "/discover/movie?" +
                    "api_key=" + tmdbApiKey +
                    "&region=IN" +
                    "&with_original_language=" + languages +
                    "&primary_release_date.gte=" + today +
                    "&sort_by=primary_release_date.asc" +
                    "&language=en-US" +
                    "&page=" + page; // <--- Use the loop variable here

            try {
                // 2. Call the API
                TmdUpcomingResponse response = restTemplate.getForObject(url, TmdUpcomingResponse.class);

                saveMoviesFromResponse(response); // Use helper method
                System.out.println("Fetched page " + page + " successfully.");

            } catch (Exception e) {
                // --- 2. ADD THIS RETRY LOGIC ---
                System.err.println("Error fetching page " + page + ": " + e.getMessage());

                // Simple retry logic specifically for this error
                if (e.getMessage().contains("Remote host terminated the handshake")) {
                    try {
                        System.out.println("Retrying page " + page + " after 1 second...");
                        Thread.sleep(1000); // Wait 1 second

                        TmdUpcomingResponse response = restTemplate.getForObject(url, TmdUpcomingResponse.class);

                        saveMoviesFromResponse(response); // Use helper
                        System.out.println("Fetched page " + page + " successfully on retry.");

                    } catch (Exception retryException) {
                        System.err.println("Failed to fetch page " + page + " on retry: " + retryException.getMessage());
                    }
                }
                // --- END RETRY LOGIC ---
            }
        }
    }

    // --- 3. ADD THIS HELPER METHOD (to avoid code duplication) ---
    private void saveMoviesFromResponse(TmdUpcomingResponse response) {
        if (response != null && response.getResults() != null) {
            for (TmdMovieDto dto : response.getResults()) {
                Movie movie = new Movie();
                movie.setId(dto.getId());
                movie.setTitle(dto.getTitle());
                movie.setOverview(dto.getOverview());
                movie.setReleaseDate(dto.getReleaseDate());
                movie.setPosterPath(dto.getPosterPath());
                movie.setTmdbPopularity(dto.getPopularity());
                movie.setOriginalLanguage(dto.getOriginalLanguage());

                movieRepository.save(movie);
            }
        }
    }


    /**
     * Gets all UPCOMING movies from our database.
     */
    public List<Movie> getAllMovies() {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Call the new repository method to find movies on or after today
        return movieRepository.findByReleaseDateGreaterThanEqual(
                today,
                Sort.by(Sort.Direction.ASC, "releaseDate")
        );
    }

    /**
     * Gets a single movie by its ID.
     */
    public Optional<Movie> getMovieById(Long id) {
        // JpaRepository gives us findById for free!
        return movieRepository.findById(id);
    }
}