package org.ex.controllers;

import org.ex.bots.TransBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class BotController {
    private final TransBot transBot;

    public BotController(TransBot transBot) {
        this.transBot = transBot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onWebhookUpdate(@RequestBody Update update) {
        return this.transBot.onWebhookUpdateReceived(update);

    }
}
