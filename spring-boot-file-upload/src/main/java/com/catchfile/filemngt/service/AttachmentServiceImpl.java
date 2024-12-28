package com.catchfile.filemngt.service;

import com.catchfile.filemngt.entity.Attachment;
import com.catchfile.filemngt.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence " + fileName);
            }

            // Generate a unique 4-digit random number
            String randomId = generateUniqueId();

            Attachment attachment = new Attachment(randomId, fileName, file.getContentType(), file.getBytes());
            return attachmentRepository.save(attachment);

        } catch (Exception e) {
            throw new Exception("Could not save File: " + fileName, e);
        }
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id: " + fileId));
    }

    private String generateUniqueId() {
        Random random = new Random();
        String id;
        do {
            id = String.format("%04d", random.nextInt(10000)); // Generate a random 4-digit number
        } while (attachmentRepository.existsById(id)); // Ensure the ID is unique
        return id;
    }
}
