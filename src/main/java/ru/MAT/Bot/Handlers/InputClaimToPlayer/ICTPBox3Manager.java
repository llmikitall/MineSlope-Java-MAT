package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestDraftRepository;

@Component
public class ICTPBox3Manager {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestDraftRepository requestDraftRepository;

    public ICTPBox3Manager(TelegramBot bot, PlayerRepository playerRepository, RequestDraftRepository requestDraftRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.requestDraftRepository = requestDraftRepository;
    }

    public void showICTPBox3(Player player){
        player.setStatus(UserStatus.ICTP_BOX3);
        playerRepository.save(player);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        keyboard.resizeKeyboard(true);

        bot.execute(new SendMessage(player.getTgId(),"<b>[Введите координаты нарушения]</b>:\nПример: <i>«x:12 y:44 z:33»</i>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard));
    }

    public void saveInput(Player player, String text){
        player.getCurrentRequest().setBox3(text);
        playerRepository.save(player);

        requestDraftRepository.save(player.getCurrentRequest());
    }
}
