package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMedia;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMediaGroup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.MessagesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.PhotoService;
import ru.MAT.Entities.*;
import ru.MAT.Repository.RequestMediaRepository;
import ru.MAT.Repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestProcessing {
    private final TelegramBot bot;
    private final RequestRepository requestRepository;
    private final RequestMediaRepository requestMediaRepository;
    private final PhotoService photoService;


    public RequestProcessing(TelegramBot bot, RequestRepository requestRepository, RequestMediaRepository requestMediaRepository, PhotoService photoService) {
        this.bot = bot;
        this.requestRepository = requestRepository;
        this.requestMediaRepository = requestMediaRepository;
        this.photoService = photoService;
    }

    public boolean checkEditable(Player player){
        RequestDraft requestDraft = player.getCurrentRequest();
        Request request = requestRepository.findByRequestTypeAndRequestId(requestDraft.getRequestType(), requestDraft.getRequestId());

        if(!request.isEditable()){
            bot.execute(new SendMessage(player.getTgId(), "Запрос уже утверждён, нет возможности его отредактировать!"));
            return false;
        }

        return true;
    }

    public SendResponse sendMessage(String output, Long chatId, Integer threadId){
        SendMessage message = new SendMessage(chatId, output)
                .parseMode(ParseMode.HTML);

        message.messageThreadId(threadId);
        return bot.execute(message);
    }

    public SendResponse sendMessage(String output, Long chatId, Integer threadId, InlineKeyboardMarkup keyboard){
        SendMessage message = new SendMessage(chatId, output)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard);;

        message.messageThreadId(threadId);
        return bot.execute(message);
    }

    public MessagesResponse sendMediaGroup(String output, List<RequestMediaDraft> filesId, Long chatId, Integer threadId){
        return sendMediaGroupWithRetry(output, filesId, chatId, threadId, 0);
    }

    public MessagesResponse sendMediaGroupWithRetry(String output, List<RequestMediaDraft> filesId, Long chatId, Integer threadId, int retry){

        if(retry >= 2)
            return null;

        InputMedia<?>[] inputMedia = new InputMedia[filesId.size()];

        for(int i = 0; i < filesId.size(); i++){
            String fileId = filesId.get(i).getFileId().trim();

            inputMedia[i] = new InputMediaPhoto(fileId)
                    .caption(i == 0 ? output : null)
                    .parseMode(ParseMode.HTML);
        }

        SendMediaGroup message = new SendMediaGroup(chatId, inputMedia);
        if(threadId != null)
            message.messageThreadId(threadId);
        MessagesResponse messagesResponse = bot.execute(message);

        if(messagesResponse.isOk())
            return messagesResponse;

        int isOk = 0;
        List<RequestMediaDraft> validList = new ArrayList<>();
        for(RequestMediaDraft requestMedia : filesId){
            String fileId = photoService.updateFileId(requestMedia.getLocalPath());
            if(fileId != null) {
                requestMedia.setFileId(fileId);
                validList.add(requestMedia);
                isOk++;
            }
        }

        filesId.clear();
        filesId.addAll(validList);



        if(isOk == filesId.size())
            return sendMediaGroupWithRetry(output, filesId, chatId, threadId, ++retry);

        return null;
    }

    public RequestDraft convertRequest(Request request){
        RequestDraft requestDraft = new RequestDraft();

        requestDraft.setRequestType(request.getRequestType());
        requestDraft.setRequestId(request.getRequestId());
        requestDraft.setRequestStatus(request.getRequestStatus());
        requestDraft.setEditable(request.isEditable());
        requestDraft.setTelegramMessageId(request.getTelegramMessageId());
        requestDraft.setMediaGroupId(request.getMediaGroupId());
        requestDraft.setHtmlMainText(request.getHtmlMainText());
        requestDraft.setHtmlStatusText(request.getHtmlStatusText());
        requestDraft.setBox0(request.getBox0());
        requestDraft.setBox1(request.getBox1());
        requestDraft.setBox2(request.getBox2());
        requestDraft.setBox3(request.getBox3());
        for(RequestMedia requestMedia : request.getBox4()){
            RequestMediaDraft requestMediaDraft = new RequestMediaDraft();

            requestMediaDraft.setFileId(requestMedia.getFileId());
            requestMediaDraft.setLocalPath(requestMedia.getLocalPath());

            requestDraft.getBox4().add(requestMediaDraft);
        }
        requestDraft.setBox5(request.getBox5());

        return requestDraft;
    }

    public Request convertRequestDraft(Request request, RequestDraft requestDraft){
        request.setRequestType(requestDraft.getRequestType());
        request.setRequestId(requestDraft.getRequestId());
        request.setRequestStatus(requestDraft.getRequestStatus());
        request.setEditable(requestDraft.isEditable());
        request.setTelegramMessageId(requestDraft.getTelegramMessageId());
        request.setMediaGroupId(requestDraft.getMediaGroupId());
        request.setHtmlMainText(requestDraft.getHtmlMainText());
        request.setHtmlStatusText(requestDraft.getHtmlStatusText());
        request.setBox0(requestDraft.getBox0());
        request.setBox1(requestDraft.getBox1());
        request.setBox2(requestDraft.getBox2());
        request.setBox3(requestDraft.getBox3());

        request.getBox4().clear();
        for(RequestMediaDraft requestMediaDraft : requestDraft.getBox4()){
            RequestMedia requestMedia = new RequestMedia();

            requestMedia.setFileId(requestMediaDraft.getFileId());
            requestMedia.setLocalPath(requestMediaDraft.getLocalPath());

            request.getBox4().add(requestMedia);
        }
        request.setBox5(requestDraft.getBox5());

        return request;
    }
}
