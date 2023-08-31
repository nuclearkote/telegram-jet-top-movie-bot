package ru.poseidonnet.jet_movie_top_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.poseidonnet.jet_movie_top_bot.utils.ParseUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PollProcessingService {

    private final PollsContainerService pollsContainerService;
    private final ButtonsService buttonsService;

    public void processPoll(TelegramLongPollingBot bot, CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getFrom().getId();
        String callbackQueryData = callbackQuery.getData();
        int pollRate = Integer.parseInt(callbackQueryData.substring("vote:".length()));
        Message message = callbackQuery.getMessage();
        String messageText = message.getText();
        Integer movieId = ParseUtils.getMovieId(messageText);
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();

        pollsContainerService.add(movieId, userId, pollRate, messageId);

        InlineKeyboardMarkup replyMarkup = message.getReplyMarkup();
        List<List<InlineKeyboardButton>> keyboard = replyMarkup.getKeyboard();
        List<InlineKeyboardButton> buttons = keyboard.get(0);

        buttonsService.reindexButtons(buttons, movieId);
        for (Integer linkedMessageId : pollsContainerService.getLinkedMessages(movieId)) {
            rebuildButtons(bot, linkedMessageId, chatId, replyMarkup);
        }
    }


    private void rebuildButtons(TelegramLongPollingBot bot, Integer messageId,  Long chatId, InlineKeyboardMarkup replyMarkup) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setReplyMarkup(replyMarkup);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setChatId(chatId);
        try {
            bot.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Error on buttons rebuilding", e);
        }
    }

}
