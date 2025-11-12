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

        // 1. Get today's date in YYYY-MM-DD format
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 2. Define the languages (TMDb uses ISO 639-1 codes)
        // The pipe "|" acts as an "OR" operator
        String languages = "ta|ml|hi|te|kn"; // ta=Tamil, ml=Malayalam, hi=Hindi, te=Telugu, kn=Kannada

        // 3. Build the new URL using the /discover/movie endpoint
        String url = tmdbBaseUrl + "/discover/movie?" +
                "api_key=" + tmdbApiKey +
                "&region=IN" + // Specify India as the region
                "&with_original_language=" + languages + // Filter by our languages
                "&primary_release_date.gte=" + today + // Get movies releasing on or after today
                "&sort_by=popularity.desc" + // Get the most popular ones first
                "&language=en-US&page=1";

        // 4. Call the API
        TmdUpcomingResponse response = restTemplate.getForObject(url, TmdUpcomingResponse.class);

        if (response != null && response.getResults() != null) {
            // 5. Loop and save the movies to our database
            for (TmdMovieDto dto : response.getResults()) {
                Movie movie = new Movie();
                movie.setId(dto.getId());
                movie.setTitle(dto.getTitle());
                movie.setOverview(dto.getOverview());
                movie.setReleaseDate(dto.getReleaseDate());
                movie.setPosterPath(dto.getPosterPath());

                movieRepository.save(movie);
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
