package com.example.androidmobilestock.Settings;

public class SettingsClass {
    private String title;
    private String subtitle;
    private int icon;
    private boolean containsSub = false;

    SettingsClass(String title, int icon, String subtitle) {
        this.title = title;
        this.icon = icon;
        this.subtitle = subtitle;
        containsSub = true;
    }

    SettingsClass(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public boolean isContainsSub(){
        return containsSub;
    }

    public String getTitle(){
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getIcon() {
        return icon;
    }
}
