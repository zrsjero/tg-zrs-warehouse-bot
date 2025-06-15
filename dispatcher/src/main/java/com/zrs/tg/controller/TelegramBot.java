package com.zrs.tg.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private UpdateController updateController;

    @Value("${bot.name}")
    private String botName;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       UpdateController updateController) {
        super(botToken);
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }

    public void sendAnswerMessage(SendMessage sendMessage) {
        if (nonNull(sendMessage)) {
            try {
                execute(sendMessage);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

}
