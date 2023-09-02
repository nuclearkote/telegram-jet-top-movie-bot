package ru.poseidonnet.jet_movie_top_bot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.poseidonnet.jet_movie_top_bot.service.PollsContainerService;
import ru.poseidonnet.jet_movie_top_bot.service.WaitArgsService;

@RequiredArgsConstructor
@Component
public class LoadBackup implements Command {

    private final WaitArgsService waitArgsService;
    private final PollsContainerService pollsContainerService;

    @Override
    public void process(DefaultAbsSender sender, Update update, String commandArgs) throws Exception {
        Long userId = update.getMessage().getFrom().getId();
        String backup;
        if (commandArgs == null) {
            sendMessage(sender, update, "Backup?");
            backup = waitArgsService.waitForArgs(userId, 60);
        } else {
            backup = commandArgs;
        }
        pollsContainerService.loadBackup(backup);
        pollsContainerService.saveBackup();
    }

    @Override
    public String commandType() {
        return "loadbackup";
    }

}
