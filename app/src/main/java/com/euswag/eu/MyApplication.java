package com.euswag.eu;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ASUS on 2017/3/9.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        //初始化sdk
        JPushInterface.setDebugMode(false);//正式版的时候设置false，关闭调试
        JPushInterface.init(this);
        //设置别名
//        JPushInterface.setAlias(this,"",null);
        //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
//        Set<String> set = new HashSet<>();
//        set.add("av");//名字任意，可多添加几个
//        set.add("applycm");
//        set.add("oraltest");
//        set.add("interview");
//        set.add("cm");
//        JPushInterface.setTags(this, set, null);//设置标签
    }
}
