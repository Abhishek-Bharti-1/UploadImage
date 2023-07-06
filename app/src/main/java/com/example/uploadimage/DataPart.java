package com.example.uploadimage;

public class DataPart {
    private String fileName;
    private byte[] content;
    private String mimeType;

    DataPart(String fileName, byte[] content, String mimeType) {
        this.fileName = fileName;
        this.content = content;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getMimeType() {
        return mimeType;
    }
}