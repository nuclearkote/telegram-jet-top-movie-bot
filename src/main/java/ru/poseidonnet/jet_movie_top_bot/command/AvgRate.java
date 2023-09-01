package ru.poseidonnet.jet_movie_top_bot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.poseidonnet.jet_movie_top_bot.kinopoisk.model.KinopoiskResponse;
import ru.poseidonnet.jet_movie_top_bot.service.MovieLinkCacheService;
import ru.poseidonnet.jet_movie_top_bot.service.PollsContainerService;
import ru.poseidonnet.jet_movie_top_bot.utils.FormatUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class AvgRate implements Command {

    private final PollsContainerService pollsContainerService;
    private final MovieLinkCacheService movieLinkCacheService;

    @Override
    public void process(DefaultAbsSender sender, Update update, String commandArgs) {
        Map<Integer, Map<Long, Integer>> polls = pollsContainerService.getPolls();
        List<Integer> avgRatedList = new ArrayList<>(polls.keySet().stream().toList());
        Map<Integer, Float> avgResultsMap = polls.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> (float) entry.getValue().values().stream().mapToInt(i -> i).sum() / entry.getValue().size()));

        avgRatedList.sort(Comparator.comparingDouble(o -> -avgResultsMap.get(o)));
        avgRatedList = avgRatedList.stream().limit(10).toList();

        StringBuilder sb = new StringBuilder();

        Map<Integer, KinopoiskResponse.Movie> links = movieLinkCacheService.getByIds(avgRatedList);
        sb.append("Топ10 фильмов с наибольшим средним рейтингом\n");
        for (int i = 0; i < avgRatedList.size(); i++) {
            Integer movieId = avgRatedList.get(i);
            KinopoiskResponse.Movie movie = links.get(movieId);
            sb.append(i + 1).append(") ")
                    .append(FormatUtils.formatMovie(movie))
                    .append("\nПроголосовало - ")
                    .append(polls.get(movieId).size())
                    .append("\nРейтинг - ")
                    .append(avgResultsMap.get(movieId))
                    .append("\n");
        }
        sendHtmlMessage(sender, update, sb.toString());
    }

    @Override
    public String commandType() {
        return "avgrate";
    }

}
