package com.catchfile.filemngt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Attachment {
    @Id
    private String id;

    private String fileName;
    private String fileType;
    @Lob
    private byte[] data;

    public Attachment(String id, String fileName, String fileType, byte[] data) {
        this.id = id; // Set the ID during object creation
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    // Default constructor for JPA
    public Attachment() {
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public byte[] getData() {
        return data;
    }
}
