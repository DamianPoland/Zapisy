package com.wolfmobileapps.zapisy;

public class DaneTrasy {

    private float distance;
    private String fullTime;
    private float speed;
    private String mapPoints;

    public DaneTrasy(float distance, String fullTime, float speed, String mapPoints) {
        this.distance = distance;
        this.fullTime = fullTime;
        this.speed = speed;
        this.mapPoints = mapPoints;
    }

    public DaneTrasy() {

    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getFullTime() {
        return fullTime;
    }

    public void setFullTime(String fullTime) {
        this.fullTime = fullTime;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getMapPoints() {
        return mapPoints;
    }

    public void setMapPoints(String mapPoints) {
        this.mapPoints = mapPoints;
    }
}
