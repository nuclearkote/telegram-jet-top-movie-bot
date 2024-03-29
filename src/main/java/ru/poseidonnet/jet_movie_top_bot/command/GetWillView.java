package ru.poseidonnet.jet_movie_top_bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.poseidonnet.jet_movie_top_bot.kinopoisk.model.KinopoiskResponse;
import ru.poseidonnet.jet_movie_top_bot.service.MovieLinkCacheService;
import ru.poseidonnet.jet_movie_top_bot.service.PollsContainerService;
import ru.poseidonnet.jet_movie_top_bot.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class GetWillView implements Command {

    private final PollsContainerService pollsContainerService;

    private final MovieLinkCacheService movieLinkCacheService;
    @Override
    public void process(DefaultAbsSender sender, Update update, String commandArgs) throws Exception {
        Long userId = update.getMessage().getFrom().getId();
        List<Integer> willView = new ArrayList<>(pollsContainerService.getWillView(userId));
        if (willView.isEmpty()) {
            sendMessage(sender, update, "Вы не добавили ни одного фильма.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        Map<Integer, KinopoiskResponse.Movie> links = movieLinkCacheService.getByIds(willView);
        sb.append("Ваши избранные фильмы:\n");
        for (int i = 0; i < willView.size(); i++) {
            Integer movieId = willView.get(i);
            KinopoiskResponse.Movie movie = links.get(movieId);
            String filmPart = (i + 1) + ") " + FormatUtils.formatMovie(movie) + "\n";
            if (sb.length() + filmPart.length() > 4096) {
                sendHtmlMessage(sender, update, sb.toString());
                sb.setLength(0);
            }
            sb.append(filmPart);
        }
        sendHtmlMessage(sender, update, sb.toString());
    }

    @Override
    public String commandType() {
        return "getwillview";
    }
}
