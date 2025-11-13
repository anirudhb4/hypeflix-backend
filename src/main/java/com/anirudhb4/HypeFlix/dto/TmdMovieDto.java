package com.anirudhb4.HypeFlix.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

// Ignore any other fields in the JSON we don't care about
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdMovieDto {

    // @JsonProperty maps the JSON field "id" to our field "id"
    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("overview")
    private String overview;

    // The JSON gives "release_date", we map it to "releaseDate"
    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("popularity")
    private Double popularity;

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getPosterPath() { return posterPath; }
    public Double getPopularity() { return popularity; }
}