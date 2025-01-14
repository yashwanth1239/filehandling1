package com.catchfile.filemngt.service;

import com.catchfile.filemngt.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    Attachment saveAttachments(List<MultipartFile> files) throws Exception;
    List<Attachment> getAttachmentsByParentId(String parentId) throws Exception;
    Attachment getAttachment(String fileId) throws Exception;
    void deleteAttachment(String fileId) throws Exception;
}
