package ru.MAT.Bot.Callbacks.ClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import org.springframework.stereotype.Component;

@Component
public class CTPActionsManager {
    private final TelegramBot bot;

    public CTPActionsManager(TelegramBot bot) {
        this.bot = bot;
    }

    public void showActions(MaybeInaccessibleMessage message, String callbackId){
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(new InlineKeyboardButton("✅ Принять").callbackData("claim_to_player_accept"), new InlineKeyboardButton("❌ Отказ").callbackData("claim_to_player_denial"));
        keyboard.addRow(new InlineKeyboardButton("\uD83D\uDD0D На рассмотрении").callbackData("claim_to_player_viewing"));

        keyboard.addRow();
        EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup(message.chat().id(), message.messageId()).replyMarkup(keyboard);
        bot.execute(editMessage);

        bot.execute(new AnswerCallbackQuery(callbackId));
    }
}
