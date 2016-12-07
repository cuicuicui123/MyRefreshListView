package com.example.refreshlistview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RefreshListView mRefreshListView;
    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefreshListView = (RefreshListView) findViewById(R.id.listView);
        mAdapter = new MyAdapter(getData(), this);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();//模拟耗时操作
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshListView.refreshComplete();
                    }
                }, 3000);
            }
        });
    }
    //填充数据
    private List<Integer> getData(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0;i < 10;i ++) {
            list.add(i);
        }
        return list;
    }

}
