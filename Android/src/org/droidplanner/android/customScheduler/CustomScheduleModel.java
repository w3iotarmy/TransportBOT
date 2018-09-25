package org.droidplanner.android.customScheduler;

public class CustomScheduleModel {
    String id;
    String name;
    String date;
    String startTime;
    String flyTime;
    String endTime;
    String distance;

    public CustomScheduleModel(String id, String name, String date, String startTime, String flyTime, String endTime, String distance) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.flyTime = flyTime;
        this.endTime = endTime;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return startTime;
    }

    public void setTime(String time) {
        this.startTime = time;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFlyTime() {
        return flyTime;
    }

    public void setFlyTime(String onTime) {
        this.flyTime = onTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
