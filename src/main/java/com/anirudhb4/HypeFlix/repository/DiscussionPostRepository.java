package com.anirudhb4.HypeFlix.repository;

import com.anirudhb4.HypeFlix.model.DiscussionPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionPostRepository extends JpaRepository<DiscussionPost,Long> {
    // Find all top-level posts for a movie (where parent is null)
    List<DiscussionPost> findByMovieIdAndParentIdIsNullOrderByCreatedAtDesc(Long movieId);

    // Find all replies for a given parent post
    List<DiscussionPost> findByParentIdOrderByCreatedAtAsc(Long parentId);
}
