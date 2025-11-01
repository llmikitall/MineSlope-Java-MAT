package ru.MAT.Bot.Callbacks.ClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Callbacks.BotCallbackHandler;

@Component
public class CTPViewingHandler implements BotCallbackHandler {
    private final CTPActionsManager ctpActionsManager;
    private final CTPViewingManager ctpViewingManager;

    public CTPViewingHandler(CTPActionsManager ctpActionsManager, CTPViewingManager ctpViewingManager) {
        this.ctpActionsManager = ctpActionsManager;
        this.ctpViewingManager = ctpViewingManager;
    }

    @Override
    public boolean CanHandle(String data){
        return data.equals("claim_to_player_viewing");
    }

    @Override
    public void Handle(Update update){
        ctpViewingManager.showViewing(update.callbackQuery().maybeInaccessibleMessage(), update.callbackQuery().id());

    }
}
