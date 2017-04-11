package com.example.asus.shetuan.activity;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.SearchRecordAdapter;
import com.example.asus.shetuan.bean.Search;
import com.example.asus.shetuan.bean.SearchRecord;
import com.example.asus.shetuan.databinding.ActivitySearchBinding;
import com.example.asus.shetuan.model.SearchDataBase;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private SearchDataBase searchDataBase ;
    private ArrayList<SearchRecord> mData = new ArrayList<>();
    private SearchRecordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        System.out.println("1");
        searchDataBase = new SearchDataBase(this);
        System.out.println("2");
        Search search = new Search(this);
        System.out.println("3");
        search.setTotaldata(searchDataBase.totalData());
        System.out.println("4");
        binding.setSearch(search);
        System.out.println("5");
        Cursor cursor = searchDataBase.queryData("");
        System.out.println("6");
        if (cursor.moveToNext()) {

            do {
                System.out.println("7");
                mData.add(new SearchRecord(cursor.getString(1)));
                System.out.println("8");
            }while (cursor.moveToNext());
            binding.searchPageRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            adapter = new SearchRecordAdapter(mData);
            binding.searchPageRecyclerview.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }
    }
}
