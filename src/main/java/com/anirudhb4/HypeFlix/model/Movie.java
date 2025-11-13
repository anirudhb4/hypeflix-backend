package com.anirudhb4.HypeFlix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "movie")
    @JsonIgnore // --- CHANGE 2: Break the JSON Loop ---
    private Set<HypePoint> hypePoints;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "movie")
    @JsonIgnore // We also don't want all posts downloading automatically
    private Set<DiscussionPost> posts;

    @JsonProperty("hypeCount")
    public int getHypeCount() {
        return hypePoints == null ? 0 : hypePoints.size();
    }
}
