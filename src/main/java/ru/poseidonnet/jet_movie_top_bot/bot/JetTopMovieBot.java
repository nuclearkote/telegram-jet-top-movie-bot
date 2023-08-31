package ru.poseidonnet.jet_movie_top_bot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.poseidonnet.jet_movie_top_bot.service.CommandService;
import ru.poseidonnet.jet_movie_top_bot.service.MessageBackupService;
import ru.poseidonnet.jet_movie_top_bot.service.MessageProcessingService;
import ru.poseidonnet.jet_movie_top_bot.service.PollProcessingService;

@Component
public class JetTopMovieBot extends TelegramLongPollingBot {

    private final MessageBackupService messageBackupService;
    private final MessageProcessingService messageProcessingService;
    private final PollProcessingService pollProcessingService;
    private final CommandService commandService;

    public JetTopMovieBot(@Value("${telegram.bot.token}") String token,
                          MessageBackupService messageBackupService,
                          MessageProcessingService messageProcessingService,
                          PollProcessingService pollProcessingService,
                          CommandService commandService) {
        super(token);
        this.messageBackupService = messageBackupService;
        this.messageProcessingService = messageProcessingService;
        this.pollProcessingService = pollProcessingService;
        this.commandService = commandService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getCallbackQuery() != null && update.getCallbackQuery().getData().startsWith("vote:")) {
            pollProcessingService.processPoll(this, update.getCallbackQuery());
        }
        Message message = update.getMessage();
        if (message != null) {
            String messageText = message.getText();
            if (messageText == null) {
                return;
            }
            if (messageText.contains("kinopoisk.ru/film/")) {
                messageProcessingService.processMovieMessage(this, message);
                return;
            }
            if (messageText.equals("/backup")) {
                messageBackupService.backup(this, message);
                return;
            }
            if (messageText.contains("@" + getBotUsername()) && messageText.startsWith("/")) {
                commandService.processCommand(this, messageText.substring(1, messageText.indexOf("@")), message.getChatId());
            }
        }

    }

    @Override
    public String getBotUsername() {
        return "JetMovieTopBot";
    }

}
