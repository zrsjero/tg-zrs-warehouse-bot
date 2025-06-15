package com.zrs.tg.service;

import com.zrs.tg.entity.AppDocument;
import com.zrs.tg.entity.AppPhoto;
import com.zrs.tg.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);

    FileSystemResource getFileSystemResource(BinaryContent binaryContent);

}
