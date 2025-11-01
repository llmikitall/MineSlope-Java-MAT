package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

@Component
public class ICTPBox1Handler implements BotHandler {
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;
    private final ICTPBox1Manager ictpBox1Manager;

    public ICTPBox1Handler(InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager, ICTPBox1Manager ictpBox1Manager) {
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
        this.ictpBox1Manager = ictpBox1Manager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status){
        return status == UserStatus.ICTP_BOX1;
    }

    @Override
    public void Handle (Player player, Update update){

        if(!update.message().text().contains("[Назад]"))
            ictpBox1Manager.saveInput(player, update.message().text());

        inputClaimToPlayerMenuManager.showInputClaimToPlayerMenu(player, update);
    }

}
