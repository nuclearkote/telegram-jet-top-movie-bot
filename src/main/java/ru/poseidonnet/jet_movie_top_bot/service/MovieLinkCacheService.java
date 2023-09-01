package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.poseidonnet.jet_movie_top_bot.kinopoisk.api.MovieFeignClient;
import ru.poseidonnet.jet_movie_top_bot.kinopoisk.model.KinopoiskResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.poseidonnet.jet_movie_top_bot.kinopoisk.api.MovieFeignClient.DAFAULT_FIELDS;

@RequiredArgsConstructor
@Service
public class MovieLinkCacheService {

    private final Cache<Integer, KinopoiskResponse.Movie> cache = new Cache2kBuilder<Integer, KinopoiskResponse.Movie>() {
    }
            .expireAfterWrite(5, TimeUnit.HOURS)
            .build();
    private final MovieFeignClient movieFeignClient;
    @Value("${kinopoisk.token}")
    private String apiKey;

    public Map<Integer, KinopoiskResponse.Movie> getByIds(List<Integer> ids) {
        List<Integer> newIds = ids.stream().filter(v -> !cache.containsKey(v)).toList();
        if (!newIds.isEmpty()) {
            KinopoiskResponse byIds = movieFeignClient.findByIds(apiKey, DAFAULT_FIELDS, newIds.size(), newIds);
            if (byIds.getTotal() > 0) {
                byIds.getMovies().forEach(m -> {
                    cache.put(m.getId(), m);
                });
            }
        }
        return ids.stream().collect(Collectors.toMap(i -> i, cache::get));
    }


}
