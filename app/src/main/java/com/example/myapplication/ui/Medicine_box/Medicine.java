package com.example.myapplication.ui.Medicine_box;

import java.util.ArrayList;
import java.util.List;

public class Medicine {
    private String name;
    private String imageUrl;
    private String atccode;
    private String manufacturer;
    private List<String> timeList;
    private List<String> numList;

    public Medicine(String name, String imageUrl, String atccode, String manufacturer, String time, String num) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.atccode = atccode;
        this.manufacturer = manufacturer;
        this.timeList = new ArrayList<>();
        this.numList = new ArrayList<>();
        this.timeList.add(time);
        this.numList.add(num);
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAtccode() {
        return atccode;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public List<String> getTimeList() {
        return timeList;
    }

    public List<String> getNumList() {
        return numList;
    }

    public void addDose(String time, String num) {
        this.timeList.add(time);
        this.numList.add(num);
    }
}
