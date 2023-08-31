package ru.poseidonnet.jet_movie_top_bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.poseidonnet.jet_movie_top_bot.bot.JetTopMovieBot;

@Configuration
public class AppConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(JetTopMovieBot jetTopMovieBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(jetTopMovieBot);
        return botsApi;
    }

}
