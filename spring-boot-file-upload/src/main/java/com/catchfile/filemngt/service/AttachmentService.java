package com.catchfile.filemngt.service;

import com.catchfile.filemngt.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface AttachmentService {
    Attachment saveAttachments(List<MultipartFile> files) throws Exception;
    Attachment addAttachmentsToParent(String parentId, List<MultipartFile> files) throws Exception;
    Map<String, Object> deleteAttachment(String fileId) throws Exception;
    Attachment getAttachment(String fileId) throws Exception;
    List<Attachment> getAttachmentsByParentId(String parentId) throws Exception;
    int getAttachmentCount(String parentId);
}