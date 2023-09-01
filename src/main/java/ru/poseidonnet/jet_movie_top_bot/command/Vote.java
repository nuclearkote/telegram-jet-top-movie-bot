package ru.poseidonnet.jet_movie_top_bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.poseidonnet.jet_movie_top_bot.service.ButtonsService;
import ru.poseidonnet.jet_movie_top_bot.service.PollsContainerService;
import ru.poseidonnet.jet_movie_top_bot.utils.ParseUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class Vote implements Command {

    private final PollsContainerService pollsContainerService;
    private final ButtonsService buttonsService;

    @Override
    public void process(DefaultAbsSender sender, Update update, String commandArgs) throws Exception {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long userId = callbackQuery.getFrom().getId();
        Message message = callbackQuery.getMessage();
        String[] args = commandArgs.split(";");
        int pollRate = Integer.parseInt(args[0]);
        Integer movieId = Integer.parseInt(args[1]);
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();

        pollsContainerService.add(movieId, userId, pollRate, messageId);

        InlineKeyboardMarkup replyMarkup = message.getReplyMarkup();
        List<List<InlineKeyboardButton>> keyboard = replyMarkup.getKeyboard();
        List<InlineKeyboardButton> buttons = keyboard.get(0);

        buttonsService.reindexButtons(buttons, movieId);
        for (Integer linkedMessageId : pollsContainerService.getLinkedMessages(movieId)) {
            try {
                rebuildButtons(sender, linkedMessageId, chatId, replyMarkup);
            } catch (Exception e) {
                log.error("Error on rebuilding buttons", e);
            }
        }
    }


    private void rebuildButtons(DefaultAbsSender bot, Integer messageId, Long chatId, InlineKeyboardMarkup replyMarkup) {
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


    @Override
    public String commandType() {
        return "vote";
    }
}
