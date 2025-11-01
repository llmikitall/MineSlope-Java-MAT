package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Entities.RequestDraft;
import ru.MAT.Enums.RequestType;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestDraftRepository;
import ru.MAT.Repository.RequestRepository;

import java.util.List;

@Component
public class ClaimToPlayerMenuManager {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;
    private final RequestProcessing requestProcessing;
    private final RequestDraftRepository requestDraftRepository;

    public ClaimToPlayerMenuManager(TelegramBot bot, PlayerRepository playerRepository, RequestRepository requestRepository, RequestProcessing requestProcessing, RequestDraftRepository requestDraftRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.requestRepository = requestRepository;
        this.requestProcessing = requestProcessing;
        this.requestDraftRepository = requestDraftRepository;
    }

    public void showClaimToPlayerMenu(Player player){
        player.setStatus(UserStatus.CLAIM_TO_PLAYER_MENU);

        playerRepository.save(player);

        List<Request> requests = requestRepository.findByPlayerAndRequestType(player, RequestType.CLAIM_ABOUT_PLAYER);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("\uD83D\uDCDD [Создать новую жалобу]");
        keyboard.addRow("◀ [Назад]");
        for(Request request: requests){
            String button;
            switch (request.getRequestStatus()){
                case DENY -> {
                    button = String.format("❌ Жалоба №%03d", request.getRequestId());
                }
                case AWAIT -> {
                    button = String.format("⚙ Жалоба №%03d", request.getRequestId());
                }
                case ACCEPT -> {
                    button = String.format("✅ Жалоба №%03d", request.getRequestId());
                }
                case VIEWING -> {
                    button = String.format("\uD83D\uDD0D Жалоба №%03d", request.getRequestId());
                }
                default -> {
                    button = String.format("(?) Жалоба №%03d", request.getRequestId());
                }
            }
            keyboard.addRow(button);
        }

        keyboard.resizeKeyboard(true);

        String output = "<b>[Выберите жалобу]</b>:";

        bot.execute(new SendMessage(player.getTgId(), output)
                        .replyMarkup(keyboard)
                        .parseMode(ParseMode.HTML));
    }

    public void createNewClaim(Player player){

        Request request = new Request();

        request.setPlayer(player);
        request.setRequestType(RequestType.CLAIM_ABOUT_PLAYER);

        int requestId = requestRepository.findMaxRequestId(RequestType.CLAIM_ABOUT_PLAYER) == null ? 1 :
                requestRepository.findMaxRequestId(RequestType.CLAIM_ABOUT_PLAYER) + 1;

        request.setRequestId(requestId);
        requestRepository.save(request);

        RequestDraft requestDraft = requestProcessing.convertRequest(request);
        requestDraftRepository.save(requestDraft);

        player.setCurrentRequest(requestDraft);
        playerRepository.save(player);
    }

    public void viewingClaim(Player player, Long requestId){
        // Тут будет прогресс!
    }
}
