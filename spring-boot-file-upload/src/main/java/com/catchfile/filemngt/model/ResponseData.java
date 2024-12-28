package com.catchfile.filemngt.model;

import lombok.Data;

@Data
public class ResponseData {

    private String fileName;
    private String downloadURL;
    private String fileType;
    private long fileSize;

    public ResponseData(String fileName, String downloadURL, String fileType, long fileSize) {
        this.fileName = fileName;
        this.downloadURL = downloadURL;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}