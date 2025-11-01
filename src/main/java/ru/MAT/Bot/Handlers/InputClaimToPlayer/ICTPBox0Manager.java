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
public class ICTPBox0Manager {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestDraftRepository requestDraftRepository;

    public ICTPBox0Manager(TelegramBot bot, PlayerRepository playerRepository, RequestDraftRepository requestDraftRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.requestDraftRepository = requestDraftRepository;
    }

    public void showICTPBox0(Player player){
        player.setStatus(UserStatus.ICTP_BOX0);
        playerRepository.save(player);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        keyboard.resizeKeyboard(true);
        bot.execute(new SendMessage(player.getTgId(),"<b>[Введите Ваш никнейм]</b>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard));
    }

    public void saveInput(Player player, String text){
        player.getCurrentRequest().setBox0(text);
        playerRepository.save(player);

        requestDraftRepository.save(player.getCurrentRequest());
    }
}
