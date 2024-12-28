package com.catchfile.filemngt.controller;

import com.catchfile.filemngt.entity.Attachment;
import com.catchfile.filemngt.model.ResponseData;
import com.catchfile.filemngt.service.AttachmentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class AttachmentController {

    private AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData uploadFile(@RequestParam("file")MultipartFile file) throws Exception {
        Attachment attachment = null;
        String downloadURL = "";
        attachment = attachmentService.saveAttachment(file);
        downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId())
                .toUriString();

        return new ResponseData(attachment.getFileName(),
                downloadURL,
                file.getContentType(),
                file.getSize());
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        try {
            // Fetch the attachment
            Attachment attachment = attachmentService.getAttachment(fileId);

            if (attachment == null) {
                throw new RuntimeException("Attachment not found for ID: " + fileId);
            }

            // Validate file type and provide a fallback
            String fileType = attachment.getFileType();
            if (fileType == null || fileType.isEmpty()) {
                fileType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback to binary type
            }

            // Validate file data
            byte[] data = attachment.getData();
            if (data == null || data.length == 0) {
                throw new RuntimeException("File data is empty or corrupt for ID: " + fileId);
            }

            // Return the file as a response
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(new ByteArrayResource(data));

        } catch (RuntimeException e) {
            // Handle runtime exceptions gracefully
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource(("Error: " + e.getMessage()).getBytes()));
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Unexpected error: " + e.getMessage()).getBytes()));
        }
    }

}