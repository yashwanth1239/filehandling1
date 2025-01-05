package com.catchfile.filemngt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Use Long for unique file ID

    private String fileName;
    private String fileType;

    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    public FileEntity(String fileName, String fileType, byte[] data, Attachment attachment) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.attachment = attachment;
    }

    // Default constructor for JPA
    public FileEntity() {
    }

    public Long getId() {
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

    public Attachment getAttachment() {
        return attachment;
    }
}
