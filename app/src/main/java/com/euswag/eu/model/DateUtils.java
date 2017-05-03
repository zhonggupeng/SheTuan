package com.euswag.eu.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ASUS on 2017/4/5.
 */

public class DateUtils {

    //调用此方法返回当前时间
    public static String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        return sdf.format(new java.util.Date());
    }

    public static String getCurrentTime2(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(new java.util.Date());
    }
    //调用此方法返回当前年份
    public static String getCurrentYear(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new java.util.Date());
    }

    /**
     *  
     *      * 掉此方法输入所要转换的时间输入例如（"2014年06月14日16时09分00秒"）返回时间戳 
     *      *  
     *      * @param time 
     *      * @return 
     *      *1402733340
     */
    public static String data(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
//            String stf = String.valueOf(l);
//            times = stf.substring(0, 10);
            times=String.valueOf(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    public static String data2(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日 HH:mm",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
//            String stf = String.valueOf(l);
//            times = stf.substring(0, 10);
            times=String.valueOf(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     *  
     *      * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16:09"） 
     *      *  
     *      * @param time 
     *      * @return 
     *      
     */
    public static String timet(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        long lcc;
        if (time!=null&&!time.equals("")) {
            lcc = Long.parseLong(time);
        }
        else lcc=0;
        String times = sdr.format(new Date(lcc));
        return times;

    }
    public static String timet2(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        long lcc;
        if (time!=null&&!time.equals("")) {
            lcc = Long.parseLong(time);
        }
        else lcc=0;
        String times = sdr.format(new Date(lcc));
        return times;
    }

    /*
    *    输入时间与当前时间的差距
    */
    public static long timediff(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        try {
            Date d1 = sdf.parse(time);
            Date d2 = sdf.parse(getCurrentTime2());
            long diff = d1.getTime()-d2.getTime();
            if (diff>=0){
                return diff/(1000*60*60*24);
            }else {
                return -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -2;
    }
}
