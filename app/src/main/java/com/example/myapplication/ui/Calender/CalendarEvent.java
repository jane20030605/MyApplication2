package com.example.myapplication.ui.Calender;

public class CalendarEvent {
    private String event_id;
    private String ACCOUNT;
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
            String startTime, String endTime, String companions,
            String ACCOUNT) {

        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.companions = companions;
        this.ACCOUNT = ACCOUNT;
    }
    public String getAccount() {
        return ACCOUNT;
    }
    public void setAccount(String ACCOUNT) {
        this.ACCOUNT = ACCOUNT;
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

}
