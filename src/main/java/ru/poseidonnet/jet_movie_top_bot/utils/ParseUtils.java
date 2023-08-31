package ru.poseidonnet.jet_movie_top_bot.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ParseUtils {

    private static final Pattern MOVIE_ID_PATTERN = Pattern.compile("kinopoisk.ru/film/(\\d+)", Pattern.CASE_INSENSITIVE);

    public static Integer getMovieId(String message) {
        Matcher matcher = MOVIE_ID_PATTERN.matcher(message);
        if (!matcher.find()) {
            return null;
        }
        return Integer.parseInt(matcher.group(1));
    }

}
