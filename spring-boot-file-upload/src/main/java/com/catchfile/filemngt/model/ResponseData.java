package com.catchfile.filemngt.model;

import java.util.List;

public class ResponseData {
    private List<FileResponse> files;
    private String parentId;
    private String bulkDownloadUrl;

    public ResponseData(List<FileResponse> files, String parentId, String bulkDownloadUrl) {
        this.files = files;
        this.parentId = parentId;
        this.bulkDownloadUrl = bulkDownloadUrl;
    }

    public ResponseData() {}

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

    public String getBulkDownloadUrl() {
        return bulkDownloadUrl;
    }

    public void setBulkDownloadUrl(String bulkDownloadUrl) {
        this.bulkDownloadUrl = bulkDownloadUrl;
    }

    public static class FileResponse {
        private String fileName;
        private String downloadURL;
        private String deleteURL; // New field for deletion URL
        private String parentId;

        public FileResponse(String fileName, String downloadURL, String deleteURL, String parentId) {
            this.fileName = fileName;
            this.downloadURL = downloadURL;
            this.deleteURL = deleteURL; // New field for deletion URL
            this.parentId = parentId;
        }

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

        public String getDeleteURL() {
            return deleteURL;
        }

        public void setDeleteURL(String deleteURL) {
            this.deleteURL = deleteURL;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }
    }
}
