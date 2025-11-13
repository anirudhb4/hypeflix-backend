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
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

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

    private Double tmdbPopularity; // Stores the "Internet Presence" score

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

    @JsonProperty("rawHypeScore") // Useful if you need to sort by numbers later
    public long getRawHypeScore() {
        int userHypes = (hypePoints == null) ? 0 : hypePoints.size();
        double internetHype = (tmdbPopularity == null) ? 0.0 : tmdbPopularity;

        // THE FORMULA:
        // 1 User Click = 1000 Points (Make user actions feel HUGE)
        // 1 TMDb Point = 500 Points (Internet presence is the baseline)
        return (long) ((userHypes * 1000) + (internetHype * 500));
    }

    @JsonProperty("hypeCount") // This replaces your old integer hypeCount
    public String getFormattedHype() {
        return formatCompact(getRawHypeScore());
    }

    // --- Helper Method for K, M, B formatting ---
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
    }

    private String formatCompact(long value) {
        if (value == Long.MIN_VALUE) return formatCompact(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatCompact(-value);
        if (value < 1000) return Long.toString(value); // e.g. 999

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); // the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
