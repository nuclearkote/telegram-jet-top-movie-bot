package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommandService {

    private final PollsContainerService pollsContainerService;

    public void processCommand(TelegramLongPollingBot bot, String command, Long chatId) {
        Map<Integer, Map<Long, Integer>> polls = pollsContainerService.getPolls();
        SendMessage sendMessage = null;
        if ("mostrated".equalsIgnoreCase(command)) {
            sendMessage = makeMostRated(polls, chatId);
        }
        if ("avgrate".equalsIgnoreCase(command)) {
            sendMessage = makeAvgRate(polls, chatId);
        }

        try {
            if (sendMessage != null) {
                bot.execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            log.error("Error on processing command", e);
        }
    }

    private SendMessage makeAvgRate(Map<Integer, Map<Long, Integer>> polls, Long chatId) {
        List<Integer> avgRatedList = new ArrayList<>(polls.keySet().stream().toList());
        Map<Integer, Float> avgResultsMap = polls.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> (float) entry.getValue().values().stream().mapToInt(i -> i).sum() / entry.getValue().size()));

        avgRatedList.sort(Comparator.comparingDouble(o -> -avgResultsMap.get(o)));

        SendMessage sendMessage = new SendMessage();
        StringBuilder sb = new StringBuilder();
        sb.append("Топ10 фильмов с наибольшим средним рейтингом\n");
        for (int i = 0; i < avgRatedList.size() && i < 10; i++) {
            Integer movieId = avgRatedList.get(i);
            sb.append(i + 1).append(") ")
                    .append(" https://www.kinopoisk.ru/film/")
                    .append(movieId)
                    .append("\nПроголосовало - ")
                    .append(polls.get(movieId).size())
                    .append("\nРейтинг - ")
                    .append(avgResultsMap.get(movieId))
                    .append("\n");
        }
        sendMessage.setChatId(chatId);
        sendMessage.setText(sb.toString());
        return sendMessage;
    }

    private SendMessage makeMostRated(Map<Integer, Map<Long, Integer>> polls, Long chatId) {
        List<Integer> mostRatedList = new ArrayList<>(polls.keySet().stream().toList());
        mostRatedList.sort(Comparator.comparingInt(o -> -polls.get(o).size()));

        SendMessage sendMessage = new SendMessage();
        StringBuilder sb = new StringBuilder();
        sb.append("Топ10 наиболее оцениваемых фильмов\n");
        for (int i = 0; i < mostRatedList.size() && i < 10; i++) {
            Integer movieId = mostRatedList.get(i);
            sb.append(i + 1).append(") ")
                    .append(" https://www.kinopoisk.ru/film/")
                    .append(movieId)
                    .append("\nПроголосовало - ")
                    .append(polls.get(movieId).size())
                    .append("\n");
        }
        sendMessage.setChatId(chatId);
        sendMessage.setText(sb.toString());
        return sendMessage;
    }

}
