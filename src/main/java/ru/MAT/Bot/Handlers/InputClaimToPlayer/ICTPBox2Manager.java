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
public class ICTPBox2Manager {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestDraftRepository requestDraftRepository;

    public ICTPBox2Manager(TelegramBot bot, PlayerRepository playerRepository, RequestDraftRepository requestDraftRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.requestDraftRepository = requestDraftRepository;
    }

    public void showICTPBox2(Player player){
        player.setStatus(UserStatus.ICTP_BOX2);
        playerRepository.save(player);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        keyboard.addRow("\uD83D\uDD95 [Гриферство]");
        keyboard.addRow("\uD83C\uDFAE [Читы]");
        keyboard.addRow("\uD83D\uDCAC [Чат]");
        keyboard.addRow("⚔️ [PvP]");
        keyboard.addRow("♻️ [Дюп]");
        keyboard.addRow("\uD83C\uDF10 [VPN]");
        keyboard.addRow("\uD83C\uDFD7️ [Лаг. структуры]");
        keyboard.resizeKeyboard(true);

        bot.execute(new SendMessage(player.getTgId(),"<b>[Укажите тип жалобы]</b>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard));
    }

    public void saveInput(Player player, String text){
        player.getCurrentRequest().setBox2(text);
        playerRepository.save(player);

        requestDraftRepository.save(player.getCurrentRequest());
    }
}
