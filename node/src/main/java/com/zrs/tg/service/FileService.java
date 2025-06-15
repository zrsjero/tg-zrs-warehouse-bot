package com.zrs.tg.service;

import com.zrs.tg.entity.AppDocument;
import com.zrs.tg.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {

    AppDocument processDoc(Message externalMessage);

    AppPhoto processPhoto(Message externalMessage);

}
