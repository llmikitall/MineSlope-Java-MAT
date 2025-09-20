package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestRepository;

import java.util.List;

@Component
public class ICTPBox0 implements BotHandler {

    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;
    private final TelegramBot bot;

    public ICTPBox0(PlayerRepository playerRepository, RequestRepository requestRepository, TelegramBot bot) {
        this.playerRepository = playerRepository;
        this.requestRepository = requestRepository;
        this.bot = bot;
    }

    @Override
    public boolean CanHandle(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);
        if (player == null){
            return false;
        }
        boolean status = player.getStatus().equals(UserStatus.ICTP_BOX0);

        return status;
    }

    @Override
    public void Handle(Update update) {
        if (update.message().text().contains("[Назад]")){
            ButtonBack(update);
        }
        else {
            Long chatId = update.message().chat().id();
            Player player = playerRepository.findByTgId(chatId);

            player.setStatus(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU);
            playerRepository.save(player);

            Request request = requestRepository.findByRequestIdAndRequestType(player.getRequestId(), player.getRequestType());

            request.setBox0(update.message().text());
            requestRepository.save(request);

            List<SendMessage> messages = InputClaimToPlayerMenu.OutputInputClaimToPlayerMenu(update,request);

            bot.execute(messages.get(0));
            bot.execute(messages.get(1));
        }



    }

    public void ButtonBack(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU);

        Request request = requestRepository.findByRequestIdAndRequestType(player.getRequestId(), player.getRequestType());
        List<SendMessage> messages = InputClaimToPlayerMenu.OutputInputClaimToPlayerMenu(update,request);

        playerRepository.save(player);

        bot.execute(messages.get(0));
        bot.execute(messages.get(1));
    }

    public static SendMessage OutputICTPBox0(Long chatId){
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        keyboard.resizeKeyboard(true);
        return new SendMessage(chatId, "<b>[Введите Ваш никнейм]</b>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard);
    }

}
