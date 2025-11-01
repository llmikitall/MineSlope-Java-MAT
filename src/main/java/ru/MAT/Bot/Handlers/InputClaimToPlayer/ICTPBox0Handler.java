package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

@Component
public class ICTPBox0Handler implements BotHandler {
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;
    private final ICTPBox0Manager ictpBox0Manager;

    public ICTPBox0Handler(InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager, ICTPBox0Manager ictpBox0Manager) {
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
        this.ictpBox0Manager = ictpBox0Manager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status){
        return status == UserStatus.ICTP_BOX0;
    }

    @Override
    public void Handle (Player player, Update update){

        if(!update.message().text().contains("[Назад]"))
            ictpBox0Manager.saveInput(player, update.message().text());

        inputClaimToPlayerMenuManager.showInputClaimToPlayerMenu(player, update);
    }

}
