package com.example.asus.shetuan.bean;

/**
 * Created by ASUS on 2017/4/24.
 */

public class ShetuanRequest {
    private int cmid;
    private int position;
    private String reason;
    private String cmname;
    private int lastselect;

    public ShetuanRequest(int cmid,int position,String reason,String cmname,int lastselect){
        this.cmid = cmid;
        this.position = position;
        this.reason = reason;
        this.cmname = cmname;
        this.lastselect = lastselect;
    }

    public int getCmid() {
        return cmid;
    }

    public void setCmid(int cmid) {
        this.cmid = cmid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCmname() {
        return cmname;
    }

    public void setCmname(String cmname) {
        this.cmname = cmname;
    }

    public int getLastselect() {
        return lastselect;
    }

    public void setLastselect(int lastselect) {
        this.lastselect = lastselect;
    }

}
