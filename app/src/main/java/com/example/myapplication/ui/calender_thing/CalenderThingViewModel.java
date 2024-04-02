package com.example.myapplication.ui.calender_thing;

import androidx.lifecycle.ViewModel;

public class CalenderThingViewModel extends ViewModel {
    private String eventName;
    private String eventDescription;


    public void saveEvent(String eventName,
                          String eventDescription,
                          int startYear, int startMonth, int startDay,
                          int startHour, int startMinute,
                          int endYear, int endMonth, int endDay,
                          int endHour, int endMinute) {
        this.eventName = eventName;
    }

    public void editEvent(String eventName, String eventDescription,
                          int startYear, int startMonth, int startDay,
                          int startHour, int startMinute,
                          int endYear, int endMonth, int endDay,
                          int endHour, int endMinute) {
    }

    public CalenderThingViewModel get(Class<CalenderThingViewModel> calenderThingViewModelClass) {
        return null;
    }
}