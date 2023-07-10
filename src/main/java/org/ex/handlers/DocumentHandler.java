package org.ex.handlers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.ex.api.TgApiClient;
import org.ex.repos.UserRepository;
import org.ex.utility.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Document;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentHandler {
    @Autowired
    FileProcessor fileProcessor;
    @Autowired
    TgApiClient tgApiClient;


    public void handle(Document document, String chatId) {
        java.io.File getFile = tgApiClient.getDocument(document.getFileId());

        File translatedFile = fileProcessor.translateFile(getFile, chatId);
        if (translatedFile == null) {
            return;
        }
        try {
            ByteArrayResource byteArrayResource = new ByteArrayResource(Files.readAllBytes(translatedFile.toPath())) {
                @Override
                public String getFilename() {
                    return translatedFile.getName();
                }
            };
            tgApiClient.uploadFile(chatId, byteArrayResource);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
