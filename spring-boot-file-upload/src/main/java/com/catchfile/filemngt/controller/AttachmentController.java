package com.catchfile.filemngt.controller;

import com.catchfile.filemngt.entity.Attachment;
import com.catchfile.filemngt.model.ResponseData;
import com.catchfile.filemngt.service.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AttachmentController {
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseData> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Attachment parentAttachment = attachmentService.saveAttachments(files);
            ResponseData responseData = createResponseData(parentAttachment);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
        } catch (Exception e) {
            logger.error("Upload failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload/{parentId}")
    public ResponseEntity<ResponseData> uploadFilesToParent(
            @PathVariable String parentId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Attachment parentAttachment = attachmentService.addAttachmentsToParent(parentId, files);
            ResponseData responseData = createResponseData(parentAttachment);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
        } catch (Exception e) {
            logger.error("Upload to parent failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        try {
            Map<String, Object> deleteResponse = attachmentService.deleteAttachment(fileId);
            return ResponseEntity.ok(deleteResponse);
        } catch (Exception e) {
            logger.error("Deletion failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId) {
        try {
            Attachment attachment = attachmentService.getAttachment(fileId);
            return createDownloadResponse(attachment);
        } catch (Exception e) {
            logger.error("Download failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/files/{parentId}")
    public ResponseEntity<ResponseData> getFilesByParentId(@PathVariable String parentId) {
        try {
            List<Attachment> attachments = attachmentService.getAttachmentsByParentId(parentId);
            if (attachments.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ResponseData responseData = createResponseDataForChildren(attachments, parentId);
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            logger.error("Retrieval failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseData createResponseData(Attachment parentAttachment) {
        List<ResponseData.FileResponse> fileResponses = parentAttachment.getChildren().stream()
                .map(this::createFileResponse)
                .collect(Collectors.toList());

        return new ResponseData(
                fileResponses,
                parentAttachment.getId(),
                "/download/bulk/" + parentAttachment.getId(),
                attachmentService.getAttachmentCount(parentAttachment.getId())
        );
    }

    private ResponseData createResponseDataForChildren(List<Attachment> attachments, String parentId) {
        List<ResponseData.FileResponse> fileResponses = attachments.stream()
                .map(this::createFileResponse)
                .collect(Collectors.toList());

        return new ResponseData(
                fileResponses,
                parentId,
                "/download/bulk/" + parentId,
                attachmentService.getAttachmentCount(parentId)
        );
    }

    private ResponseData.FileResponse createFileResponse(Attachment attachment) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return new ResponseData.FileResponse(
                attachment.getFileName(),
                baseUrl + "/download/" + attachment.getId(),
                baseUrl + "/delete/" + attachment.getId(),
                attachment.getParent().getId()
        );
    }

    private ResponseEntity<Resource> createDownloadResponse(Attachment attachment) {
        ByteArrayResource resource = new ByteArrayResource(attachment.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }
}