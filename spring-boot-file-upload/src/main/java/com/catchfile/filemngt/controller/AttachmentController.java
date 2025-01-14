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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT })
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
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Attachment parentAttachment = attachmentService.saveAttachments(files);

            List<ResponseData.FileResponse> fileResponses = parentAttachment.getChildren().stream()
                    .map(child -> {
                        String downloadURL = "/download/" + child.getId();
                        String deleteURL = "/delete/" + child.getId(); // Adding deletion URL
                        return new ResponseData.FileResponse(child.getFileName(), downloadURL, deleteURL, parentAttachment.getId());
                    })
                    .collect(Collectors.toList());

            String bulkDownloadURL = "/download/bulk/" + parentAttachment.getId();

            ResponseData responseData = new ResponseData(fileResponses, parentAttachment.getId(), bulkDownloadURL);
            return new ResponseEntity<>(responseData, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Exception during file upload", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId) {
        try {
            Attachment attachment = attachmentService.getAttachment(fileId);
            if (attachment == null || attachment.getData() == null) {
                return ResponseEntity.notFound().build();
            }
            return getSingleFileResponse(attachment);
        } catch (Exception e) {
            logger.error("Error downloading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading file: " + e.getMessage());
        }
    }

    @GetMapping("/download/bulk/{parentId}")
    public ResponseEntity<?> downloadBulkFiles(@PathVariable String parentId) {
        try {
            List<Attachment> attachments = attachmentService.getAttachmentsByParentId(parentId);
            return ZipFileDownloadUtil.createZipFile(attachments, parentId);
        } catch (Exception e) {
            logger.error("Error downloading bulk files", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading files: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        try {
            attachmentService.deleteAttachment(fileId);
            return ResponseEntity.ok("File and its dependencies have been deleted/updated.");
        } catch (Exception e) {
            logger.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting file: " + e.getMessage());
        }
    }

    private ResponseEntity<Resource> getSingleFileResponse(Attachment attachment) {
        String fileType = attachment.getFileType();
        if (fileType == null || fileType.isEmpty()) {
            fileType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(new ByteArrayResource(attachment.getData()));
        } catch (Exception e) {
            logger.error("Error creating byte array resource", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
