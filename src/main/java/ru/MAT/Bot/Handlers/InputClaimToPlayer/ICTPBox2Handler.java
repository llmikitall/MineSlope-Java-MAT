package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

@Component
public class ICTPBox2Handler implements BotHandler {
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;
    private final ICTPBox2Manager ictpBox2Manager;

    public ICTPBox2Handler(InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager, ICTPBox2Manager ictpBox2Manager) {
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
        this.ictpBox2Manager = ictpBox2Manager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status){
        return status == UserStatus.ICTP_BOX2;
    }

    @Override
    public void Handle (Player player, Update update){

        if(!update.message().text().contains("[Назад]"))
            ictpBox2Manager.saveInput(player, update.message().text());

        inputClaimToPlayerMenuManager.showInputClaimToPlayerMenu(player, update);
    }

}
