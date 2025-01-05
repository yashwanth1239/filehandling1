package com.catchfile.filemngt.model;

import com.catchfile.filemngt.entity.Attachment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileDownloadUtil {

    public static ResponseEntity<?> createZipFile(List<Attachment> attachments, String parentId) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) { // Try-with-resources for auto-closing

            for (Attachment attachment : attachments) {
                if(attachment.getData() != null) { //check if attachment data is null to avoid null pointer exception
                    ZipEntry zipEntry = new ZipEntry(attachment.getFileName());
                    zipEntry.setSize(attachment.getData().length);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(attachment.getData());
                    zipOutputStream.closeEntry();
                }
            }
        } // zipOutputStream is automatically closed here

        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files_" + parentId + ".zip\"")
                .body(resource);
    }
}