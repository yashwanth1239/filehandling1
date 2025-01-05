package com.catchfile.filemngt.controller;

import com.catchfile.filemngt.entity.Attachment;
import com.catchfile.filemngt.model.ResponseData;
import com.catchfile.filemngt.model.ZipFileDownloadUtil;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AttachmentController {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseData> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            if (files == null || files.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Handle empty file list
            }
            Attachment parentAttachment = attachmentService.saveAttachments(files);

            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(parentAttachment.getId())
                    .toUriString();

            List<String> childFileNames = parentAttachment.getChildren().stream()
                    .map(Attachment::getFileName)
                    .collect(Collectors.toList());

            ResponseData responseData = new ResponseData(childFileNames, downloadURL);
            return new ResponseEntity<>(responseData, HttpStatus.CREATED);
        } catch (MultipartException e) {
            logger.error("Multipart Exception during file upload", e);
            return new ResponseEntity<>(HttpStatus.PAYLOAD_TOO_LARGE); // Or other appropriate status
        } catch (Exception e) {
            logger.error("Exception during file upload", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{parentId}")
    public ResponseEntity<?> downloadFiles(@PathVariable String parentId) {
        try {
            List<Attachment> attachments = attachmentService.getAttachmentsByParentId(parentId);
            if (attachments == null || attachments.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (attachments.size() > 1) {
                return ZipFileDownloadUtil.createZipFile(attachments, parentId);
            } else {
                Attachment attachment = attachments.get(0);
                if (attachment != null && attachment.getData() != null) { //Null checks to avoid null pointer exception
                    return getSingleFileResponse(attachment);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (Exception e) {
            logger.error("Error downloading files", e); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading files: " + e.getMessage());
        }
    }

    private ResponseEntity<Resource> getSingleFileResponse(Attachment attachment) {
        String fileType = attachment.getFileType();
        if (fileType == null || fileType.isEmpty()) {
            fileType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        try{
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(new ByteArrayResource(attachment.getData()));
        }catch(Exception e){
            logger.error("Error creating byte array resource", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}