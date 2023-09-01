package ru.poseidonnet.jet_movie_top_bot.command;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

    void process(DefaultAbsSender sender, Update update, String commandArgs) throws Exception;
    String commandType();

    @SneakyThrows
    default void sendMessage(DefaultAbsSender sender, Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        sender.execute(sendMessage);
    }

    @SneakyThrows
    default void sendHtmlMessage(DefaultAbsSender sender, Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        sendMessage.enableHtml(true);
        sender.execute(sendMessage);
    }

}
