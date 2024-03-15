package com.example.myapplication.ui.medicine_allbox;

public class MyItem {
    private int imageResource;
    private String text;

    public MyItem(int imageResource, String text) {
        this.imageResource = imageResource;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {
        return text;
    }
}
