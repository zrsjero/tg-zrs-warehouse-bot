package com.zrs.tg.service.impl;

import com.zrs.tg.controller.UpdateController;
import com.zrs.tg.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.zrs.tg.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerServiceImpl implements AnswerConsumer {

    private final UpdateController updateController;

    public AnswerConsumerServiceImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

}
