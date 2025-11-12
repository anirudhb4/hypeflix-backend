package com.anirudhb4.HypeFlix.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdUpcomingResponse {

    @JsonProperty("page")
    private int page;

    @JsonProperty("results")
    private List<TmdMovieDto> results;

    // Getters
    public int getPage() { return page; }
    public List<TmdMovieDto> getResults() { return results; }
}