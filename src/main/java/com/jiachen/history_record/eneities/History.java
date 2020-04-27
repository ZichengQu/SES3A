package com.jiachen.history_record.eneities;

import java.io.Serializable;

public class History implements Serializable {

    private String startPoint;
    private String endPoint;
    private String startTime;
    private String duration;
    private String calories;
    private boolean expanded;
    private String title;

    public History(String startPoint, String endPoint, String startTime, String duration, String calories) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startTime = startTime;
        this.duration = duration;
        this.calories = calories;
        this.expanded = false;
        this.title=startTime;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "History{" +
                "startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", calories='" + calories + '\'' +
                ", expanded=" + expanded +
                '}';
    }
}