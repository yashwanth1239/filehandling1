package com.catchfile.filemngt.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {

    private List<String> fileNames;
    private String downloadURL;

    public ResponseData(List<String> fileNames, String downloadURL) {
        this.fileNames = fileNames;
        this.downloadURL = downloadURL;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
    public ResponseData() {} // Add this


}