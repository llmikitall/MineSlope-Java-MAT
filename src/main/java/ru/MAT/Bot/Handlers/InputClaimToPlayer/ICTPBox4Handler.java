package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Bot.Handlers.BotPhotoHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

import java.util.List;
import java.util.Optional;

@Component
public class ICTPBox4Handler implements BotPhotoHandler, BotHandler {
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;
    private final ICTPBox4Manager ictpBox4Manager;

    public ICTPBox4Handler(InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager, ICTPBox4Manager ictpBox4Manager) {
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
        this.ictpBox4Manager = ictpBox4Manager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status){
        if(status != UserStatus.ICTP_BOX4)
            return false;

        List<String> buttons = List.of("[Назад]");
        text = Optional.ofNullable(text).orElse("");

        return buttons.stream().anyMatch(text::contains);
    }

    @Override
    public boolean CanPhotoHandle(UserStatus status){
        return status == UserStatus.ICTP_BOX4;
    }

    @Override
    public void Handle (Player player, Update update){
        if(update.message().photo() != null)
            ictpBox4Manager.savePhoto(player, update.message());
        else if(update.message().text().contains("[Назад]"))
            inputClaimToPlayerMenuManager.showInputClaimToPlayerMenu(player, update);
    }

}
