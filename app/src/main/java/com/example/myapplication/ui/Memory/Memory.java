package com.example.myapplication.ui.Memory;

public class Memory {
    private String recordId;
    private String ipcamName;
    private String fallDate;
    private String userId;
    private String pictureUrl;

    public Memory(String recordId, String ipcamName, String fallDate, String userId, String pictureUrl) {
        this.recordId = recordId;
        this.ipcamName = ipcamName;
        this.fallDate = fallDate;
        this.userId = userId;
        this.pictureUrl = pictureUrl;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getIpcamName() {
        return ipcamName;
    }

    public String getFallDate() {
        return fallDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }
}
