package ru.poseidonnet.jet_movie_top_bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageBackupService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final PollsContainerService pollsContainerService;

    public void backup(TelegramLongPollingBot bot, Message message) {
        try {
            pollsContainerService.saveDb();
            SendMessage backupMessage = new SendMessage();
            backupMessage.setChatId(message.getChatId());
            backupMessage.setText(MAPPER.writeValueAsString(pollsContainerService.getPollsDbList()));
            bot.execute(backupMessage);

            backupMessage.setText(MAPPER.writeValueAsString(pollsContainerService.getMovieMessagesDbList()));
            bot.execute(backupMessage);
        } catch (Exception e) {
            log.error("Error on backup", e);
        }
    }


}
