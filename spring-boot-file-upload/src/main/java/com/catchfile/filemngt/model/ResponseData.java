package com.catchfile.filemngt.model;

import lombok.Data;
import java.util.List;

@Data
public class ResponseData {
    private List<FileResponse> files;
    private String parentId;

    @Data
    public static class FileResponse {
        private String fileName;
        private String downloadURL;
        private String parentId;

        public FileResponse(String fileName, String downloadURL, String parentId) {
            this.fileName = fileName;
            this.downloadURL = downloadURL;
            this.parentId = parentId;
        }

        // Getters and Setters
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDownloadURL() {
            return downloadURL;
        }

        public void setDownloadURL(String downloadURL) {
            this.downloadURL = downloadURL;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }
    }

    public ResponseData(List<FileResponse> files, String parentId) {
        this.files = files;
        this.parentId = parentId;
    }

    public ResponseData() {}

    // Getters and Setters
    public List<FileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<FileResponse> files) {
        this.files = files;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}