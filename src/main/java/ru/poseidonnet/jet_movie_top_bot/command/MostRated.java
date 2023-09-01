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

@RequiredArgsConstructor
@Component
public class MostRated implements Command {

    private final PollsContainerService pollsContainerService;
    private final MovieLinkCacheService movieLinkCacheService;

    @Override
    public void process(DefaultAbsSender sender, Update update, String commandArgs) {
        Map<Integer, Map<Long, Integer>> polls = pollsContainerService.getPolls();
        List<Integer> mostRatedList = new ArrayList<>(polls.keySet().stream().toList());
        mostRatedList.sort(Comparator.comparingInt(o -> -polls.get(o).size()));

        StringBuilder sb = new StringBuilder();
        sb.append("<b>Топ10 наиболее оцениваемых фильмов</b>\n");
        Map<Integer, KinopoiskResponse.Movie> links = movieLinkCacheService.getByIds(mostRatedList);
        for (int i = 0; i < mostRatedList.size() && i < 10; i++) {
            Integer movieId = mostRatedList.get(i);
            sb.append(i + 1).append(") ")
                    .append(FormatUtils.formatMovie(links.get(movieId)))
                    .append("\nПроголосовало - ")
                    .append(polls.get(movieId).size())
                    .append("\n");
        }
        sendHtmlMessage(sender, update, sb.toString());
    }

    @Override
    public String commandType() {
        return "mostrated";
    }

}
