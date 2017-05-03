package com.euswag.eu.weight.dateselector;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.euswag.eu.R;
import com.euswag.eu.activity.funct.PressActivityActivity;
import com.euswag.eu.weight.dateselector.adapter.NumericWheelAdapter;
import com.euswag.eu.weight.dateselector.widget.WheelView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ASUS on 2017/4/4.
 */

public class DataSelect {
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView hour;
    private WheelView mins;
    private Context context;

    private final int STARTTIMEID = 10;
    private final int ENDTIMEID = 11;
    private final int ENROLLDEADTIME = 110;

    public DataSelect(Context context){
        this.context = context;
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }
    /**
     * 初始化年
     */
    private void initYear() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,1950, 2050);
        numericWheelAdapter.setLabel(" 年");
        year.setViewAdapter(numericWheelAdapter);
        year.setCyclic(true);
    }

    /**
     * 初始化月
     */
    private void initMonth() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,1, 12, "%02d");
        numericWheelAdapter.setLabel(" 月");
        month.setViewAdapter(numericWheelAdapter);
        month.setCyclic(true);
    }

    /**
     * 初始化天
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(context,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel(" 日");
        day.setViewAdapter(numericWheelAdapter);
        day.setCyclic(true);
    }

    /**
     * 初始化时
     */
    private void initHour() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,0, 23, "%02d");
        numericWheelAdapter.setLabel(" 时");
        hour.setViewAdapter(numericWheelAdapter);
        hour.setCyclic(true);
    }

    /**
     * 初始化分
     */
    private void initMins() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,0, 59, "%02d");
        numericWheelAdapter.setLabel(" 分");
        mins.setViewAdapter(numericWheelAdapter);
        mins.setCyclic(true);
    }


    /**
     * 显示全部日期
     */
    public void showDateAndTime(final int contextid){
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        int curHour = c.get(Calendar.HOUR_OF_DAY);
        int curMin = c.get(Calendar.MINUTE);


        final AlertDialog dialog = new AlertDialog.Builder(context)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.date_time_picker_layout);
        // 设置宽高
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.AnimBottom);

        year = (WheelView) window.findViewById(R.id.new_year);
        initYear();
        month = (WheelView) window.findViewById(R.id.new_month);
        initMonth();
        day = (WheelView) window.findViewById(R.id.new_day);
        initDay(curYear,curMonth);
        hour = (WheelView) window.findViewById(R.id.new_hour);
        initHour();
        mins = (WheelView) window.findViewById(R.id.new_mins);
        initMins();

        // 设置当前时间
        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        hour.setCurrentItem(curHour);
        mins.setCurrentItem(curMin);

        month.setVisibleItems(7);
        day.setVisibleItems(7);
        hour.setVisibleItems(7);
        mins.setVisibleItems(7);

        // 设置监听
        TextView ok = (TextView) window.findViewById(R.id.set);
        TextView cancel = (TextView) window.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = String.format(Locale.CHINA,"%04d年%02d月%02d日%02d时%02d分",year.getCurrentItem()+1950,
                        month.getCurrentItem()+1,day.getCurrentItem()+1,hour.getCurrentItem(),mins.getCurrentItem());
//                Toast.makeText(context, time, Toast.LENGTH_LONG).show();
                if (contextid == STARTTIMEID) {
                    ((PressActivityActivity) context).getBinding().pressActivitySelectStarttime.setText(time);
                    ((PressActivityActivity) context).getBinding().pressActivitySelectEnrolldeadtime.setText(time);
                }
                else if (contextid == ENDTIMEID){
                    ((PressActivityActivity) context).getBinding().pressActivitySelectEndtime.setText(time);
                }
                else if (contextid == ENROLLDEADTIME){
                    ((PressActivityActivity) context).getBinding().pressActivitySelectEnrolldeadtime.setText(time);
                }
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
}
