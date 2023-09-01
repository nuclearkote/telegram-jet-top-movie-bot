package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.poseidonnet.jet_movie_top_bot.command.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommandService {

    private final Map<String, Command> commands;

    public CommandService(List<Command> commands) {
        this.commands = commands.stream().collect(Collectors.toMap(Command::commandType, e -> e));
    }

    public void processCommand(DefaultAbsSender sender, Update update, String commandType, String commandArgs) throws Exception {
        Command command = commands.get(commandType.toLowerCase());
        if (command == null) {
            throw new IllegalArgumentException("Unknown command type " + commandType);
        }
        command.process(sender, update, commandArgs);
    }

}
