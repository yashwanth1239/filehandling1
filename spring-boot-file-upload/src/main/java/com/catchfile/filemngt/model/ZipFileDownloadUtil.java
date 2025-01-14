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
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (Attachment attachment : attachments) {
                ZipEntry entry = new ZipEntry(attachment.getFileName());
                entry.setSize(attachment.getData().length);
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(attachment.getData());
                zipOutputStream.closeEntry();
            }
        }
        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + parentId + ".zip\"")
                .body(resource);
    }
}
