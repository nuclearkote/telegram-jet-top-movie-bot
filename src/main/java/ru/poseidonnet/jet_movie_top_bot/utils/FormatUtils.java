package ru.poseidonnet.jet_movie_top_bot.utils;

import lombok.experimental.UtilityClass;
import ru.poseidonnet.jet_movie_top_bot.kinopoisk.model.KinopoiskResponse;

@UtilityClass
public class FormatUtils {

    public static String formatMovie(KinopoiskResponse.Movie movie) {
        StringBuilder sb = new StringBuilder();
         sb.append("<a href='")
                .append("https://www.kinopoisk.ru/film/")
                .append(movie.getId())
                .append("'>")
                .append(movie.getName())
                .append(" (").append(movie.getYear()).append(")")
                .append("</a>");
         return sb.toString();
    }
}
