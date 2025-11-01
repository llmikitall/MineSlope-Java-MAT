package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

@Component
public class ICTPBox3Handler implements BotHandler {
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;
    private final ICTPBox3Manager ictpBox3Manager;

    public ICTPBox3Handler(InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager, ICTPBox3Manager ictpBox3Manager) {
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
        this.ictpBox3Manager = ictpBox3Manager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status){
        return status == UserStatus.ICTP_BOX3;
    }

    @Override
    public void Handle (Player player, Update update){

        if(!update.message().text().contains("[Назад]"))
            ictpBox3Manager.saveInput(player, update.message().text());

        inputClaimToPlayerMenuManager.showInputClaimToPlayerMenu(player, update);
    }

}
