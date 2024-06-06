package com.example.myapplication.ui.Calender;// CalendarEvent.java
// CalendarEvent.java

import java.util.UUID;

public class CalendarEvent {
    private String event_id;
    private String account;
    private String eventName;
    private String eventDescription;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String companions;

    public CalendarEvent(
            String eventName, String eventDescription,
            String startDate, String endDate,
            String startTime, String endTime, String companions) {
        this.event_id = generateUniqueId();
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.companions = companions;
    }

    public String getEventId() {
        return event_id;
    }

    public void setEventId(String event_id) {
        this.event_id = event_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCompanions() {
        return companions;
    }

    public void setCompanions(String companions) {
        this.companions = companions;
    }

    // 生成唯一的事件ID
    public String generateUniqueId() {
        // 這裡使用當前時間的毫秒數和一個隨機數來生成唯一ID
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
    }
}
