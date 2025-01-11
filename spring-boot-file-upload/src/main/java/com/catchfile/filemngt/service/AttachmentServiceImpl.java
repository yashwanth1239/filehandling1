package com.catchfile.filemngt.service;

import com.catchfile.filemngt.entity.Attachment;
import com.catchfile.filemngt.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Attachment saveAttachments(List<MultipartFile> files) throws Exception {
        String parentId = generateUniqueId();
        Attachment parentAttachment = new Attachment(parentId, "parent", null, null, null);
        List<Attachment> children = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " + fileName);
            }
            String childId = parentId + "_" + children.size();
            Attachment childAttachment = new Attachment(
                    childId,
                    fileName,
                    file.getContentType(),
                    file.getBytes(),
                    parentAttachment
            );
            children.add(childAttachment);
        }
        parentAttachment.setChildren(children);
        return attachmentRepository.save(parentAttachment);
    }

    @Override
    public List<Attachment> getAttachmentsByParentId(String parentId) throws Exception {
        return attachmentRepository.findByParentId(parentId);
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id: " + fileId));
    }

    @Override
    @Transactional
    public void deleteAttachment(String fileId) throws Exception {
        Attachment attachment = getAttachment(fileId);

        if (attachment.getParent() != null) {
            attachment.getParent().getChildren().remove(attachment);
            if (attachment.getParent().getChildren().isEmpty()) {
                attachmentRepository.delete(attachment.getParent());
            } else {
                attachmentRepository.save(attachment.getParent());
            }
        }
    }

    private String generateUniqueId() {
        Random random = new Random();
        String id;
        do {
            id = String.format("%04d", random.nextInt(10000));
        } while (attachmentRepository.existsById(id));
        return id;
    }
}