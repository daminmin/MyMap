package com.example.administrator.mymap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Info implements Serializable {
    private static final long serialVersionUID = -7898039750007359273L;
    private double latitude;
    private double longitude;
    private int imgId;
    private String name;
    private String distance;
    private String phonenumber;

    public static List<Info> infos = new ArrayList<Info>();

    static {
        infos.add(new Info(22.255266,113.542591,R.drawable.icon_gcoding,"高敏","距离505米","13143125912"));
    }

    public Info(double latitude, double longitude, int imgId, String name, String distance, String phonenumber) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.phonenumber = phonenumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
