package com.catchfile.filemngt.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Attachment {
    @Id
    private String id;
    private String fileName;
    private String fileType;
    @Lob
    private byte[] data;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> children;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Attachment parent;

    public Attachment(String id, String fileName, String fileType, byte[] data, Attachment parent) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.parent = parent;
    }

    public Attachment() {}

    // Getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public List<Attachment> getChildren() {
        return children;
    }
    public void setChildren(List<Attachment> children) {
        this.children = children;
    }
    public Attachment getParent() {
        return parent;
    }
    public void setParent(Attachment parent) {
        this.parent = parent;
    }
}
