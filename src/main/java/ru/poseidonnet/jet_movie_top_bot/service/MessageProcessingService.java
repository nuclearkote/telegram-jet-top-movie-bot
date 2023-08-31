package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.poseidonnet.jet_movie_top_bot.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageProcessingService {

    private final ButtonsService buttonsService;

    public void processMovieMessage(TelegramLongPollingBot bot, Message message) {
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();
        Integer movieId = ParseUtils.getMovieId(message.getText());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message.getText());
        sendMessage.setReplyMarkup(makeButtons(movieId));
        try {
            bot.execute(deleteMessage);
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup makeButtons(Integer movieId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            buttons.add(makeInlineKeyboardButton(String.valueOf(i)));
        }
        buttonsService.reindexButtons(buttons, movieId);
        inlineKeyboardMarkup.setKeyboard(List.of(buttons));
        return inlineKeyboardMarkup;
    }

    @NotNull
    private InlineKeyboardButton makeInlineKeyboardButton(String text) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData("vote:" + text);
        return inlineKeyboardButton;
    }

}
