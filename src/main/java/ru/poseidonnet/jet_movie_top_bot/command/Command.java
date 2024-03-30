package ru.poseidonnet.jet_movie_top_bot.command;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public interface Command {

    void process(DefaultAbsSender sender, Update update, String commandArgs) throws Exception;
    String commandType();

    @SneakyThrows
    default void sendMessage(DefaultAbsSender sender, Update update, String text) {
        int cursor = 0;
        int pageSize = 4096;
        do {
            String substring = text.substring(cursor, Math.min(cursor + pageSize, text.length()));

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(substring);
            sender.execute(sendMessage);
            cursor += pageSize;
        } while (cursor < text.length());
    }

    @SneakyThrows
    default void sendHtmlMessage(DefaultAbsSender sender, Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        sendMessage.enableHtml(true);
        sender.execute(sendMessage);
    }

    @SneakyThrows
    default void sendButtons(DefaultAbsSender sender, Update update, String title, List<List<InlineKeyboardButton>> buttons) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(title);
        int pageSize = 13;
        List<List<InlineKeyboardButton>> batch = new ArrayList<>();
        int count = 0;
        for (List<InlineKeyboardButton> row : buttons) {
            List<InlineKeyboardButton> batchRow = new ArrayList<>();
            batch.add(batchRow);
            for (InlineKeyboardButton button : row) {
                count++;
                batchRow.add(button);
                if (count >= pageSize) {
                    count = 0;
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    inlineKeyboardMarkup.setKeyboard(batch);
                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                    sender.execute(sendMessage);
                    batch = new ArrayList<>();
                }
            }
        }
        if (batch.size() > 0) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(buttons);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sender.execute(sendMessage);
        }
    }

}
