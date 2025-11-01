package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.PhotoService;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.RequestMediaDraft;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestDraftRepository;

@Component
public class ICTPBox4Manager {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final PhotoService photoService;
    private final RequestDraftRepository requestDraftRepository;

    public ICTPBox4Manager(TelegramBot bot, PlayerRepository playerRepository, PhotoService photoService, RequestDraftRepository requestDraftRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.photoService = photoService;
        this.requestDraftRepository = requestDraftRepository;
    }

    public void showICTPBox4(Player player){
        player.setStatus(UserStatus.ICTP_BOX4);
        playerRepository.save(player);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        keyboard.resizeKeyboard(true);

        bot.execute(new SendMessage(player.getTgId(),"<b>[Отправьте фотографии]</b>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard));
    }

    public void savePhoto(Player player, Message message){
        String fileId = photoService.extractFileIdFromMessage(message);

        String path = photoService.downloadPhoto(fileId);

        RequestMediaDraft requestMediaDraft = new RequestMediaDraft();
        requestMediaDraft.setFileId(fileId);
        requestMediaDraft.setLocalPath(path);

        player.getCurrentRequest().getBox4().add(requestMediaDraft);

        requestDraftRepository.save(player.getCurrentRequest());

    }
}
