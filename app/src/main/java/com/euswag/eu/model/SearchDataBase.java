package com.euswag.eu.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ASUS on 2017/3/25.
 */

public class SearchDataBase {
    private RccordSQLiteOpenHelper helper ;
    private SQLiteDatabase db ;
    public SearchDataBase(Context context){
        helper = new RccordSQLiteOpenHelper(context);
    }
    //插入数据
    public void insertData(String searchtext){
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('"+searchtext+"')");
        db.close();
    }
    //模糊查询数据
    public Cursor queryData(String searchtext){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name like '%"+searchtext+"%' order by id desc",null);
        return cursor ;
    }
    //检查数据库中是否已经有该条记录
    public boolean hasData(String searchtext){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select id as _id,name from records where name =?",new String[]{searchtext});
        return cursor.moveToNext();
    }
    //清空数据
    public void deleteData(){
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }
    //统计存入数据库的数据条数
    public int totalData(){
        Cursor cursor = helper.getReadableDatabase().rawQuery("select * from records",null);
        return cursor.getCount();
    }
    //删除数据库第一条数据，即最早写入的数据
    public void deleteFirstData(){
        db = helper.getWritableDatabase();
        db.execSQL("delete from records where rowid in(select rowid from records limit 1)");
        db.close();
    }
}
