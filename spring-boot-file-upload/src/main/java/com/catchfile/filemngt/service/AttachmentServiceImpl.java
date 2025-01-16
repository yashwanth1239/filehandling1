package com.catchfile.filemngt.service;

import com.catchfile.filemngt.entity.Attachment;
import com.catchfile.filemngt.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final Set<String> activeIds;
    private final Map<String, Integer> attachmentCounts;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.activeIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.attachmentCounts = new ConcurrentHashMap<>();

        // Initialize with existing data
        attachmentRepository.findAll().forEach(attachment -> {
            activeIds.add(attachment.getId());
            if (attachment.getParent() != null) {
                attachmentCounts.merge(attachment.getParent().getId(), 1, Integer::sum);
            }
        });
    }

    @Override
    @Transactional
    public Attachment saveAttachments(List<MultipartFile> files) throws Exception {
        String parentId = generateUniqueId();
        Attachment parentAttachment = new Attachment(parentId, "parent", null, null, null);
        List<Attachment> children = new ArrayList<>();

        for (MultipartFile file : files) {
            validateFileName(file);
            String childId = generateChildId(parentId, children.size());
            Attachment childAttachment = createAttachment(childId, file, parentAttachment);
            children.add(childAttachment);
            activeIds.add(childId);
        }

        parentAttachment.setChildren(children);
        attachmentCounts.put(parentId, children.size());
        activeIds.add(parentId);

        return attachmentRepository.save(parentAttachment);
    }

    @Override
    @Transactional
    public Attachment addAttachmentsToParent(String parentId, List<MultipartFile> files) throws Exception {
        Attachment parentAttachment = getAttachment(parentId);
        List<Attachment> existingChildren = parentAttachment.getChildren();
        int startIndex = existingChildren.size();

        for (MultipartFile file : files) {
            validateFileName(file);
            String childId = generateChildId(parentId, startIndex++);
            Attachment childAttachment = createAttachment(childId, file, parentAttachment);
            existingChildren.add(childAttachment);
            activeIds.add(childId);
        }

        attachmentCounts.merge(parentId, files.size(), Integer::sum);
        parentAttachment.setChildren(existingChildren);
        return attachmentRepository.save(parentAttachment);
    }

    @Override
    @Transactional
    public Map<String, Object> deleteAttachment(String fileId) throws Exception {
        Attachment attachment = getAttachment(fileId);
        String parentId = attachment.getParent() != null ? attachment.getParent().getId() : null;

        Map<String, Object> response = new HashMap<>();
        if (parentId != null) {
            int newCount = attachmentCounts.merge(parentId, -1, Integer::sum);
            response.put("remainingCount", newCount);
            response.put("parentId", parentId);

            if (newCount <= 0) {
                attachmentRepository.deleteById(parentId);
                activeIds.remove(parentId);
                attachmentCounts.remove(parentId);
            } else {
                response.put("remainingFiles", getRemainingFilesInfo(parentId));
            }
        }

        activeIds.remove(fileId);
        attachmentRepository.delete(attachment);
        return response;
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found: " + fileId));
    }

    @Override
    public List<Attachment> getAttachmentsByParentId(String parentId) throws Exception {
        return attachmentRepository.findByParentId(parentId);
    }

    @Override
    public int getAttachmentCount(String parentId) {
        return attachmentCounts.getOrDefault(parentId, 0);
    }

    private String generateUniqueId() {
        Random random = new Random();
        String id;
        do {
            id = String.format("%04d", random.nextInt(10000));
        } while (activeIds.contains(id) || attachmentRepository.existsById(id));
        return id;
    }

    private String generateChildId(String parentId, int index) {
        String childId;
        do {
            childId = parentId + "_" + index;
            index++;
        } while (activeIds.contains(childId));
        return childId;
    }

    private void validateFileName(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.contains("..")) {
            throw new Exception("Invalid filename: " + fileName);
        }
    }

    private Attachment createAttachment(String id, MultipartFile file, Attachment parent) throws Exception {
        return new Attachment(
                id,
                StringUtils.cleanPath(file.getOriginalFilename()),
                file.getContentType(),
                file.getBytes(),
                parent
        );
    }

    private List<Map<String, String>> getRemainingFilesInfo(String parentId) throws Exception {
        List<Map<String, String>> remainingFiles = new ArrayList<>();
        List<Attachment> attachments = getAttachmentsByParentId(parentId);

        for (Attachment attachment : attachments) {
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("fileId", attachment.getId());
            fileInfo.put("fileName", attachment.getFileName());
            fileInfo.put("downloadUrl", "/download/" + attachment.getId());
            fileInfo.put("deleteUrl", "/delete/" + attachment.getId());
            remainingFiles.add(fileInfo);
        }

        return remainingFiles;
    }
}
