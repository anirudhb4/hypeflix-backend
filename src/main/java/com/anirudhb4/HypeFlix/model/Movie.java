package com.anirudhb4.HypeFlix.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    private Long id; // Primary Key - We will use the TMDb ID here

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private LocalDate releaseDate;

    private String posterPath;

    @UpdateTimestamp
    private Instant lastUpdated; // So we know when to refresh from TMDb

    // --- Relationships ---

    @OneToMany(mappedBy = "movie")
    private Set<HypePoint> hypePoints;

    @OneToMany(mappedBy = "movie")
    private Set<DiscussionPost> posts;
}
