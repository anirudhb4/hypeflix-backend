package com.anirudhb4.HypeFlix.service;

import com.anirudhb4.HypeFlix.model.HypePoint;
import com.anirudhb4.HypeFlix.model.Movie;
import com.anirudhb4.HypeFlix.model.UserProfile;
import com.anirudhb4.HypeFlix.repository.HypePointRepository;
import com.anirudhb4.HypeFlix.repository.MovieRepository;
import com.anirudhb4.HypeFlix.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HypeService {

    private final HypePointRepository hypePointRepository;
    private final MovieRepository movieRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public HypeService(HypePointRepository hypePointRepository,
                       MovieRepository movieRepository,
                       UserProfileRepository userProfileRepository) {
        this.hypePointRepository = hypePointRepository;
        this.movieRepository = movieRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public void addHype(Long movieId, UUID userId) {
        // 1. Check if the user has already hyped this movie
        if (hypePointRepository.existsByUserIdAndMovieId(userId, movieId)) {
            // You can throw an exception to send a 400 error
            throw new IllegalStateException("User has already hyped this movie");
        }

        // 2. Get the Movie and UserProfile to link them
        // .getReferenceById() is efficient. It doesn't hit the DB
        // unless we access its fields.
        Movie movie = movieRepository.getReferenceById(movieId);
        UserProfile user = userProfileRepository.getReferenceById(userId);

        // 3. Create the new HypePoint
        HypePoint newHype = new HypePoint();
        newHype.setMovie(movie);
        newHype.setUser(user);

        // We'll add the region later from the user profile
        // newHype.setRegion(user.getRegion());

        // 4. Save to the database
        hypePointRepository.save(newHype);
    }

    @Transactional
    public void removeHype(Long movieId, UUID userId) {
        // 1. Check if the hype entry exists (optional, but good practice)
        if (!hypePointRepository.existsByUserIdAndMovieId(userId, movieId)) {
            // Or just return silently
            throw new IllegalStateException("User has not hyped this movie");
        }

        // 2. Delete the entry using our new repository method
        hypePointRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    public List<Movie> getMoviesHypedByUser(UUID userId) {
        return hypePointRepository.findMoviesHypedByUser(userId);
    }
}
