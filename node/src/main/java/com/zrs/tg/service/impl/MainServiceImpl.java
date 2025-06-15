package com.zrs.tg.service.impl;

import com.zrs.tg.dao.AppUserRepository;
import com.zrs.tg.dao.RawDataRepository;
import com.zrs.tg.entity.AppUser;
import com.zrs.tg.entity.RawData;
import com.zrs.tg.service.MainService;
import com.zrs.tg.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

import static com.zrs.tg.enums.UserState.BASIC_STATE;
import static com.zrs.tg.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.zrs.tg.service.enums.ServiceCommand.CANCEL;
import static com.zrs.tg.service.enums.ServiceCommand.HELP;
import static com.zrs.tg.service.enums.ServiceCommand.REGISTRATION;
import static com.zrs.tg.service.enums.ServiceCommand.START;

@Slf4j
@Service
public class MainServiceImpl implements MainService {

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;

    public MainServiceImpl(RawDataRepository rawDataRepository,
                           ProducerService producerService,
                           AppUserRepository appUserRepository) {
        this.rawDataRepository = rawDataRepository;
        this.producerService = producerService;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if (CANCEL.equals(text)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            // output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            var answer = "Документ успешно загружен! "
                    + "Ссылка для скачивания: " + "null";
            sendAnswer(answer, chatId);
        } catch (Exception ex) {
            log.error("", ex);
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            var answer = "Фоото успешно загружено! "
                    + "Ссылка для скачивания: " + "null";
            sendAnswer(answer, chatId);
        } catch (Exception ex) {
            log.error("", ex);
            String error = "К сожалению, загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте "
                    + "свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        producerService.produceAnswer(SendMessage.builder()
                .chatId(chatId)
                .text(output)
                .build());
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {
            // return appUserService.registerUser(appUser);
            return "Временно недоступно!";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser existentUser = appUserRepository.findAppUserByTelegramUserId(telegramUser.getId());
        if (Objects.isNull(existentUser)) {
            AppUser newAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    // todo изменить значение по-умолчанию после добавление регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserRepository.save(newAppUser);
        }

        return existentUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }

}
