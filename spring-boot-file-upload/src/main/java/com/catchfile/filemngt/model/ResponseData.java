package com.catchfile.filemngt.model;

import java.util.List;

public class ResponseData {
    private List<FileResponse> files;
    private String parentId;
    private String bulkDownloadUrl;
    private int remainingCount;

    public ResponseData(List<FileResponse> files, String parentId, String bulkDownloadUrl, int remainingCount) {
        this.files = files;
        this.parentId = parentId;
        this.bulkDownloadUrl = bulkDownloadUrl;
        this.remainingCount = remainingCount;
    }

    // Getters and setters
    public List<FileResponse> getFiles() { return files; }
    public void setFiles(List<FileResponse> files) { this.files = files; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getBulkDownloadUrl() { return bulkDownloadUrl; }
    public void setBulkDownloadUrl(String bulkDownloadUrl) { this.bulkDownloadUrl = bulkDownloadUrl; }
    public int getRemainingCount() { return remainingCount; }
    public void setRemainingCount(int remainingCount) { this.remainingCount = remainingCount; }

    public static class FileResponse {
        private String fileName;
        private String downloadURL;
        private String deleteURL;
        private String parentId;

        public FileResponse(String fileName, String downloadURL, String deleteURL, String parentId) {
            this.fileName = fileName;
            this.downloadURL = downloadURL;
            this.deleteURL = deleteURL;
            this.parentId = parentId;
        }

        // Getters and setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getDownloadURL() { return downloadURL; }
        public void setDownloadURL(String downloadURL) { this.downloadURL = downloadURL; }
        public String getDeleteURL() { return deleteURL; }
        public void setDeleteURL(String deleteURL) { this.deleteURL = deleteURL; }
        public String getParentId() { return parentId; }
        public void setParentId(String parentId) { this.parentId = parentId; }
    }
}
