package ru.poseidonnet.jet_movie_top_bot.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.poseidonnet.jet_movie_top_bot.service.PollsContainerService;

@Slf4j
@RequiredArgsConstructor
@Component
public class Backup implements Command {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final PollsContainerService pollsContainerService;

    @Override
    public void process(DefaultAbsSender sender, Update update, String commandArgs) throws Exception {
        try {
            Message message = update.getMessage();
            pollsContainerService.saveDb();
            SendMessage backupMessage = new SendMessage();
            backupMessage.setChatId(message.getChatId());
            backupMessage.setText(MAPPER.writeValueAsString(pollsContainerService.getPollsDbList()));
            sender.execute(backupMessage);

            backupMessage.setText(MAPPER.writeValueAsString(pollsContainerService.getMovieMessagesDbList()));
            sender.execute(backupMessage);
        } catch (Exception e) {
            log.error("Error on backup", e);
        }
    }

    @Override
    public String commandType() {
        return "backup";
    }
}
