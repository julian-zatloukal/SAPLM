package com.example.SAPLM.settingsActivities;

public class AvailableSetting {
    private String title, subtitle;
    private int iconResourceID;
    private Class targetActivity;

    public AvailableSetting() {
    }

    public AvailableSetting(String title, String subtitle, int iconResourceID, Class targetActivity) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconResourceID = iconResourceID;
        this.targetActivity = targetActivity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getIconResourceID() {
        return iconResourceID;
    }

    public void setIconResourceID(int iconResourceID) {
        this.iconResourceID = iconResourceID;
    }

    public Class getTargetActivity() { return targetActivity; }

    public void setTargetActivity(Class targetActivity) { this.targetActivity = targetActivity; }
}
