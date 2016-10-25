package com.gyzh.app.lingyun.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.adapter.MyFragmentAdapter;
import com.gyzh.app.lingyun.fragment.AppointmentOrderFragment;
import com.gyzh.app.lingyun.fragment.FinishOrderFragment;
import com.gyzh.app.lingyun.fragment.OrderCellFragment;

import java.util.ArrayList;

/**
 * 订单
 * Created by Administrator on 2015/7/15.
 */
public class OrderActivity extends FragmentActivity implements View.OnClickListener {
    View line_1, line_2, line_3;
    private ArrayList<Fragment> arrayFragmentlList;
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    MyFragmentAdapter MyFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order);

        //页头
        findViewById(R.id.iv_goback).setOnClickListener(this);
        findViewById(R.id.iv_action).setVisibility(View.GONE);
        findViewById(R.id.tv_action).setVisibility(View.GONE);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的订单");

        initViews();

        String form = getIntent().getStringExtra("form");
        if (form.equals("单池")) {
            showOrderCell();
        } else if (form.equals("预约单")) {
            showAppointmentOrder();
        } else {
            showOrderCell();
        }
    }

    private void initViews() {
        arrayFragmentlList = new ArrayList<Fragment>();
        Fragment fragment_order_cell = new OrderCellFragment();
        Fragment fragment_appointment_order = new AppointmentOrderFragment();
        Fragment fragment_finish_order = new FinishOrderFragment();
        arrayFragmentlList.add(fragment_order_cell);
        arrayFragmentlList.add(fragment_appointment_order);
        arrayFragmentlList.add(fragment_finish_order);

        line_1 = (View) findViewById(R.id.line_1);
        line_2 = (View) findViewById(R.id.line_2);
        line_3 = (View) findViewById(R.id.line_3);

        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        MyFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), arrayFragmentlList);
        viewPager.setAdapter(MyFragmentAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        radioGroup.check(R.id.tv_order_cell);
                        break;
                    case 1:
                        radioGroup.check(R.id.tv_appointment_order);
                        break;
                    case 2:
                        radioGroup.check(R.id.tv_finish_order);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        viewPager.setOffscreenPageLimit(0);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.tv_order_cell:
                        showOrderCell();
                        break;
                    case R.id.tv_appointment_order:
                        showAppointmentOrder();
                        break;
                    case R.id.tv_finish_order:
                        showFinishOrder();
                        break;
                    default:
                        break;
                }
            }
        });
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

    private void showOrderCell() {
        line_1.setBackgroundColor(getResources().getColor(R.color.main_color));
        line_2.setBackgroundColor(getResources().getColor(R.color.base_line));
        line_3.setBackgroundColor(getResources().getColor(R.color.base_line));
        viewPager.setCurrentItem(0, true);
    }

    private void showAppointmentOrder() {
        line_1.setBackgroundColor(getResources().getColor(R.color.base_line));
        line_2.setBackgroundColor(getResources().getColor(R.color.main_color));
        line_3.setBackgroundColor(getResources().getColor(R.color.base_line));
        viewPager.setCurrentItem(1, true);
    }

    private void showFinishOrder() {
        line_1.setBackgroundColor(getResources().getColor(R.color.base_line));
        line_2.setBackgroundColor(getResources().getColor(R.color.base_line));
        line_3.setBackgroundColor(getResources().getColor(R.color.main_color));
        viewPager.setCurrentItem(2, true);
    }

}
