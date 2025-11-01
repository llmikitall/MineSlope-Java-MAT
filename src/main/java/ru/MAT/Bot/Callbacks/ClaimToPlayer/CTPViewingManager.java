package ru.MAT.Bot.Callbacks.ClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Enums.RequestStatus;
import ru.MAT.Repository.RequestRepository;

@Component
public class CTPViewingManager {
    private final TelegramBot bot;
    private final RequestRepository requestRepository;

    public CTPViewingManager(TelegramBot bot, RequestRepository requestRepository) {
        this.bot = bot;
        this.requestRepository = requestRepository;
    }

    public void showViewing(MaybeInaccessibleMessage message, String callbackId){
        Request request = requestRepository.findByTelegramMessageId(message.messageId());
        if(request == null){
            bot.execute(new AnswerCallbackQuery(callbackId).text("Жалоба не найдена"));
            return;
        }
        String editText = request.getHtmlStatusText().replaceFirst("(?<=: ).*", "\uD83D\uDD0D На рассмотрении");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(new InlineKeyboardButton("⚙ Действия").callbackData("claim_to_player_actions"));

        EditMessageText editMessageText =new EditMessageText(message.chat().id(), message.messageId(), editText)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML);

        BaseResponse response = bot.execute(editMessageText);

        if(!response.isOk()){
            bot.execute(new AnswerCallbackQuery(callbackId).text("Ошибка редактирования"));
            return;
        } else if (request.getRequestStatus().equals(RequestStatus.VIEWING)) {
            bot.execute(new AnswerCallbackQuery(callbackId).text("Жалоба уже была на рассмотрении!"));
            return;
        }

        Player player = request.getPlayer();

        bot.execute(new SendMessage(player.getTgId(),
                String.format("🔍 Ваш <b>запрос №%03d</b> принят на рассмотрение администрацией! 🔍", request.getRequestId()))
                .parseMode(ParseMode.HTML));

        request.setRequestStatus(RequestStatus.VIEWING);
        request.setEditable(false);
        request.setHtmlStatusText(editText);
        requestRepository.save(request);

        bot.execute(new AnswerCallbackQuery(callbackId));
    }

}
