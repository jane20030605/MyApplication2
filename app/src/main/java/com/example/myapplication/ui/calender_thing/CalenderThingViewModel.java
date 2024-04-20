package com.example.myapplication.ui.calender_thing;

import androidx.lifecycle.ViewModel;

public class CalenderThingViewModel extends ViewModel {
    private String eventName;
    private String eventDescription;

    // 保存事件
    public void saveEvent(String eventName, String eventDescription,
                          int startYear, int startMonth, int startDay,
                          int startHour, int startMinute,
                          int endYear, int endMonth, int endDay,
                          int endHour, int endMinute) {
        // 在此方法中，你可以將事件數據保存到你想要的地方，比如數據庫或共享首選項
        // 這裡只是將事件名稱保存到ViewModel的屬性中作為示例
        this.eventName = eventName;
    }

    // 編輯事件
    public void editEvent(String eventName, String eventDescription,
                          int startYear, int startMonth, int startDay,
                          int startHour, int startMinute,
                          int endYear, int endMonth, int endDay,
                          int endHour, int endMinute) {
        // 在此方法中，你可以編輯已保存的事件，比如更新數據庫中的事件信息
        // 這裡只是一個空方法作為示例
    }

    // 獲取ViewModel實例
    public static CalenderThingViewModel get() {
        return new CalenderThingViewModel();
    }
}
