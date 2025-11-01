package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Bot.Handlers.ClaimToPlayerMenuManager;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

import java.util.List;
import java.util.Optional;

@Component
public class InputClaimToPlayerMenuHandler implements BotHandler {

    private final ClaimToPlayerMenuManager claimToPlayerMenuManager;
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;
    private final ICTPBox0Manager ictpBox0Manager;
    private final ICTPBox1Manager ictpBox1Manager;
    private final ICTPBox2Manager ictpBox2Manager;
    private final ICTPBox3Manager ictpBox3Manager;
    private final ICTPBox4Manager ictpBox4Manager;
    private final ICTPBox5Manager ictpBox5Manager;

    public InputClaimToPlayerMenuHandler(ClaimToPlayerMenuManager claimToPlayerMenuManager, InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager, ICTPBox0Manager ictpBox0Manager, ICTPBox1Manager ictpBox1Manager, ICTPBox2Manager ictpBox2Manager, ICTPBox3Manager ictpBox3Manager, ICTPBox4Manager ictpBox4Manager, ICTPBox5Manager ictpBox5Manager) {
        this.claimToPlayerMenuManager = claimToPlayerMenuManager;
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
        this.ictpBox0Manager = ictpBox0Manager;
        this.ictpBox1Manager = ictpBox1Manager;
        this.ictpBox2Manager = ictpBox2Manager;
        this.ictpBox3Manager = ictpBox3Manager;
        this.ictpBox4Manager = ictpBox4Manager;
        this.ictpBox5Manager = ictpBox5Manager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status){
        if(!status.equals(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU))
            return false;

        List<String> buttons = List.of("[Назад]", "[Сохранить]", "[Отозвать жалобу]", "[Мой ник]",
                "[Его ник]", "[Тип]", "[Коорд.]", "[Фото]", "[Детали]");
        text = Optional.ofNullable(text).orElse("");

        return buttons.stream().anyMatch(text::contains);
    }

    @Override
    public void Handle(Player player, Update update){

        if (update.message().text().contains("[Назад]")){
            inputClaimToPlayerMenuManager.buttonBack(player);
            claimToPlayerMenuManager.showClaimToPlayerMenu(player);
        }
        else if (update.message().text().contains("[Сохранить]")){
            if(inputClaimToPlayerMenuManager.buttonSave(player, update))
                claimToPlayerMenuManager.showClaimToPlayerMenu(player);
        }
        else if(update.message().text().contains("[Отозвать жалобу]"))
            // Логика отзыва
            claimToPlayerMenuManager.showClaimToPlayerMenu(player);
        else if(update.message().text().contains("[Мой ник]"))
            ictpBox0Manager.showICTPBox0(player);
        else if(update.message().text().contains("[Его ник]"))
            ictpBox1Manager.showICTPBox1(player);
        else if(update.message().text().contains("[Тип]"))
            ictpBox2Manager.showICTPBox2(player);
        else if(update.message().text().contains("[Коорд.]"))
            ictpBox3Manager.showICTPBox3(player);
        else if(update.message().text().contains("[Фото]"))
            ictpBox4Manager.showICTPBox4(player);
        else if(update.message().text().contains("[Детали]"))
            ictpBox5Manager.showICTPBox5(player);

    }
}
