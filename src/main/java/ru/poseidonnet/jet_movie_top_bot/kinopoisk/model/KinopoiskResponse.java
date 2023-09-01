package ru.poseidonnet.jet_movie_top_bot.kinopoisk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class KinopoiskResponse {

    @JsonProperty("docs")
    private List<Movie> movies;

    @JsonProperty("total")
    private int total;

    @JsonProperty("limit")
    private int limit;

    @JsonProperty("page")
    private int page;

    @JsonProperty("pages")
    private int totalPages;


    @Data
    public static class Movie {
        @JsonProperty("id")
        private int id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("year")
        private int year;

    }

}
