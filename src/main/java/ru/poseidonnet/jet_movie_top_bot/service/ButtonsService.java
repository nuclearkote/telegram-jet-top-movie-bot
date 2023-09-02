package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
public class ButtonsService {

    private final PollsContainerService pollsContainerService;

    public void reindexButtons(List<InlineKeyboardButton> buttons, Integer movieId) {
        Map<Integer, AtomicInteger> count = new HashMap<>();
        Map<Long, Integer> moviePolls = pollsContainerService.getMoviePolls(movieId);
        if (moviePolls == null) {
            return;
        }
        for (Integer pollRate : moviePolls.values()) {
            count.computeIfAbsent(pollRate, k -> new AtomicInteger(0)).incrementAndGet();
        }
        for (int i = 0; i < buttons.size(); i++) {
            AtomicInteger pollCount = count.get(i + 1);
            InlineKeyboardButton inlineKeyboardButton = buttons.get(i);
            String buttonText = String.valueOf(i + 1);
            if (!(pollCount == null || pollCount.get() == 0)) {
                buttonText += " (" + pollCount.get() + ")";
            }
            inlineKeyboardButton.setText(buttonText);
        }
    }

}
