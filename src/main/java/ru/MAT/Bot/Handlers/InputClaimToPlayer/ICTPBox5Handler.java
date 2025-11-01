package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

@Component
public class ICTPBox5Handler implements BotHandler {
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;
    private final ICTPBox5Manager ictpBox5Manager;

    public ICTPBox5Handler(InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager, ICTPBox5Manager ictpBox5Manager) {
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
        this.ictpBox5Manager = ictpBox5Manager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status){
        return status == UserStatus.ICTP_BOX5;
    }

    @Override
    public void Handle (Player player, Update update){

        if(!update.message().text().contains("[Назад]"))
            ictpBox5Manager.saveInput(player, update.message().text());

        inputClaimToPlayerMenuManager.showInputClaimToPlayerMenu(player, update);
    }

}
