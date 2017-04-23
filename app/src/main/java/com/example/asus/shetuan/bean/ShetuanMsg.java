package com.example.asus.shetuan.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import com.example.asus.shetuan.BR;

/**
 * Created by ASUS on 2017/3/31.
 */

public class ShetuanMsg extends BaseObservable {
    @Bindable
    public int getShetuanid() {
        return shetuanid;
    }

    public void setShetuanid(int shetuanid) {
        this.shetuanid = shetuanid;
        notifyPropertyChanged(BR.shetuanid);
    }

    public int getShetuantype() {
        return shetuantype;
    }

    public void setShetuantype(int shetuantype) {
        this.shetuantype = shetuantype;
    }

    public int getShetuanattr() {
        return shetuanattr;
    }

    public void setShetuanattr(int shetuanattr) {
        this.shetuanattr = shetuanattr;
    }

    public int getShetuanrecruit() {
        return shetuanrecruit;
    }

    public void setShetuanrecruit(int shetuanrecruit) {
        this.shetuanrecruit = shetuanrecruit;
    }

    public int getShetuanheat() {
        return shetuanheat;
    }

    public void setShetuanheat(int shetuanheat) {
        this.shetuanheat = shetuanheat;
    }

    @Bindable
    public String getShtuanannouncement() {
        return shtuanannouncement;
    }

    public void setShtuanannouncement(String shtuanannouncement) {
        this.shtuanannouncement = shtuanannouncement;
        notifyPropertyChanged(BR.shtuanannouncement);
    }

    @Bindable
    public String getShetuanschool() {
        return shetuanschool;
    }

    public void setShetuanschool(String shetuanschool) {
        this.shetuanschool = shetuanschool;
        notifyPropertyChanged(BR.shetuanschool);
    }

    private int shetuanid;
//    private String shetuandetail;     //好像重复了
    private int shetuantype;
    private int shetuanattr;       //社团属性
    private int shetuanrecruit;     //招新状态
    private int shetuanheat;    //社团热度
    private String shtuanannouncement;      //社团公告
    private String shetuanschool;   //校级填学校，院级填学院

    public ShetuanMsg(String name,String briefintroduction,String backgroundimage,String logoimage){
        this.name = name;
        this.briefintroduction = briefintroduction;
        this.backgroundimage = backgroundimage;
        this.logoimage = logoimage;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getBriefintroduction() {
        return briefintroduction;
    }

    public void setBriefintroduction(String briefintroduction) {
        this.briefintroduction = briefintroduction;
        notifyPropertyChanged(BR.briefintroduction);
    }

    @Bindable
    public String getBackgroundimage() {
        return backgroundimage;
    }

    public void setBackgroundimage(String backgroundimage) {
        this.backgroundimage = backgroundimage;
        notifyPropertyChanged(BR.backgroundimage);
    }

    @Bindable
    public String getLogoimage() {
        return logoimage;
    }

    public void setLogoimage(String logoimage) {
        this.logoimage = logoimage;
        notifyPropertyChanged(BR.logoimage);
    }

    private String name;
    private String briefintroduction;
    private String backgroundimage;
    private String logoimage;

    public long getShetuanboss() {
        return shetuanboss;
    }

    public void setShetuanboss(long shetuanboss) {
        this.shetuanboss = shetuanboss;
    }

    private long shetuanboss;

    private String shetuanJsonString;
    public void setShetuanJsonString(String string){
        this.shetuanJsonString = string;
    }
    public String getShetuanJsonString(){
        return shetuanJsonString;
    }
}
