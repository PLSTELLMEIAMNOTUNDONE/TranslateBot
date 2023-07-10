package org.ex.bots;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.ex.api.TgApiClient;
import org.ex.handlers.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import org.ex.handlers.DocumentHandler;
import org.ex.utility.FileProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransBot extends SpringWebhookBot {
    String BotUsername;
    String BotPath;
    String BotId;
    @Autowired
    DocumentHandler documentHandler;

    @Autowired
    MessageHandler messageHandler;


    public TransBot(SetWebhook setWebhook) {
        super(setWebhook);
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            sendMessage.setChatId(message.getChatId());
            if (message.hasDocument()) {

                documentHandler.handle(message.getDocument(), message.getChatId().toString());
                return sendMessage;
            }

            sendMessage.setChatId(message.getChatId());
            if (message.hasText()) {
                return messageHandler.handle(message);
            }

        }

        return sendMessage;

    }


    @Override
    public void onRegister() {
        super.onRegister();
    }
}
