package com.gyzh.app.lingyun.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.adapter.MessageAdapter;
import com.gyzh.app.lingyun.utils.Constant;

/**
 * 消息
 * Created by Administrator on 2015/7/15.
 */
public class MessageActivity extends Activity implements View.OnClickListener {
    SwipeRefreshLayout sfl_refresh;
    ListView lv_list;
    int page = 1;
    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        //页头
        findViewById(R.id.iv_goback).setOnClickListener(this);
        findViewById(R.id.iv_action).setVisibility(View.GONE);
        findViewById(R.id.tv_action).setVisibility(View.GONE);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("消息");

        initViews();

        initData();
    }

    private void initViews() {
        sfl_refresh = (SwipeRefreshLayout) findViewById(R.id.sfl_refresh);
        //下拉刷新
        sfl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                initData();
            }
        });
        sfl_refresh.setRefreshing(true);

        lv_list = (ListView) findViewById(R.id.lv_list);
        //上拉加载
        lv_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && totalItemCount >= Constant.PAGE_SIZE) {
                    page++;
                    initData();
                }
            }
        });
    }

    private void initData() {
        adapter = new MessageAdapter(MessageActivity.this);
        lv_list.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_goback:
                finish();
                break;
        }
    }

}
