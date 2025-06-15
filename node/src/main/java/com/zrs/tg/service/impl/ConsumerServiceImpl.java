package com.zrs.tg.service.impl;

import com.zrs.tg.service.ConsumerService;
import com.zrs.tg.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.zrs.tg.model.RabbitQueue.DOC_MESSAGE_UPDATE;
import static com.zrs.tg.model.RabbitQueue.PHOTO_MESSAGE_UPDATE;
import static com.zrs.tg.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.info("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.info("NODE: Doc message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.info("NODE: Photo message is received");
        mainService.processPhotoMessage(update);
    }

}
