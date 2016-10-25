package com.gyzh.app.lingyun.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.adapter.ImageViewAdapter;
import com.gyzh.app.lingyun.adapter.PreviewViewPagerAdapter;
import com.gyzh.app.lingyun.listener.OnItemViewClickListener;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.utils.VolleyErrorHelper;
import com.gyzh.app.lingyun.views.HorizontalListView;
import com.gyzh.app.lingyun.views.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 订单详情
 * Created by Administrator on 2015/7/15.
 */
public class OrderDetailsActivity extends Activity implements View.OnClickListener {
    static final String TAG = OrderDetailsActivity.class.getName();
    LoadingDialog dialog;
    TextView tv_username, tv_phone, tv_carbrand_carype, tv_car_model, tv_car_number, tv_car_color, tv_address;
    NetworkImageView iv_service_picture;
    TextView tv_wash_type, tv_wash_content, tv_service_price;
    TextView tv_total_price;
    TextView tv_order_remarks;
    TextView tv_time_finish;
    TextView tv_time_start;
    LinearLayout ll_wash_finish, ll_wash_before;
    ViewPager vp_pager;
    PreviewViewPagerAdapter imagePagerAdapter_finish;
    ImageViewAdapter adapter_Finish, adapter_Before;
    HorizontalListView hlv_wash_finish, hlv_wash_before;
    JSONArray array_finish, array_before;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);

        dialog = new LoadingDialog(OrderDetailsActivity.this);

        //页头
        findViewById(R.id.iv_goback).setOnClickListener(this);
        findViewById(R.id.iv_action).setVisibility(View.GONE);
        findViewById(R.id.tv_action).setVisibility(View.GONE);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单详情");

        initViews();

        String id = getIntent().getStringExtra("id");
        initData(id);//初始化数据
    }

    private void initViews() {
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_carbrand_carype = (TextView) findViewById(R.id.tv_carbrand_carype);
        tv_car_model = (TextView) findViewById(R.id.tv_car_model);
        tv_car_number = (TextView) findViewById(R.id.tv_car_number);
        tv_car_color = (TextView) findViewById(R.id.tv_car_color);
        tv_address = (TextView) findViewById(R.id.tv_address);

        iv_service_picture = (NetworkImageView) findViewById(R.id.iv_service_picture);
        iv_service_picture.setErrorImageResId(R.mipmap.img_loading);
        iv_service_picture.setDefaultImageResId(R.mipmap.img_loading);
        tv_wash_type = (TextView) findViewById(R.id.tv_wash_type);
        tv_wash_content = (TextView) findViewById(R.id.tv_wash_content);
        tv_service_price = (TextView) findViewById(R.id.tv_service_price);

        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_order_remarks = (TextView) findViewById(R.id.tv_order_remarks);
        tv_time_finish = (TextView) findViewById(R.id.tv_time_finish);
        tv_time_start = (TextView) findViewById(R.id.tv_time_start);
        ll_wash_finish = (LinearLayout) findViewById(R.id.ll_wash_finish);
        ll_wash_before = (LinearLayout) findViewById(R.id.ll_wash_start);
        hlv_wash_finish = (HorizontalListView) findViewById(R.id.hlv_photos_finish);
        hlv_wash_finish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imagePagerAdapter_finish = new PreviewViewPagerAdapter(OrderDetailsActivity.this, "Path", array_finish);
                vp_pager.setAdapter(imagePagerAdapter_finish);
                vp_pager.setCurrentItem(position);
                imagePagerAdapter_finish.setOnItemViewClickListener(new OnItemViewClickListener() {
                    @Override
                    public void onItemViewClickListener(int viewId, int position) {
                        vp_pager.setVisibility(View.GONE);
                    }
                });
                vp_pager.setVisibility(View.VISIBLE);
            }
        });
        hlv_wash_before = (HorizontalListView) findViewById(R.id.hlv_photo_before);
        hlv_wash_before.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imagePagerAdapter_finish = new PreviewViewPagerAdapter(OrderDetailsActivity.this, "Path", array_before);
                vp_pager.setAdapter(imagePagerAdapter_finish);
                vp_pager.setCurrentItem(position);
                imagePagerAdapter_finish.setOnItemViewClickListener(new OnItemViewClickListener() {
                    @Override
                    public void onItemViewClickListener(int viewId, int position) {
                        vp_pager.setVisibility(View.GONE);
                    }
                });
                vp_pager.setVisibility(View.VISIBLE);
            }
        });
        vp_pager = (ViewPager) findViewById(R.id.vp_pager);
    }

    private void initData(String id) {
        dialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("OrderId", id);

        JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.URL_ORDER_DETAILS, params), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    if (jsonObject.getInt("Flag") == 1) {
                        JSONObject object = new JSONObject(jsonObject.getString("OrderDetail"));
                        JSONObject member_obj = new JSONObject(object.getString("MemberInfo"));
                        tv_username.setText(member_obj.getString("Name"));
                        tv_phone.setText(object.getString("Phone"));
                        tv_carbrand_carype.setText(object.getString("CarBrand") + object.getString("CarType"));
                        tv_car_model.setText(object.getString("CarModel"));
                        tv_car_number.setText(object.getString("CarNumber"));
                        tv_car_color.setText(object.getString("Color"));
                        tv_address.setText(object.getString("Address"));

                        JSONObject service_info=new JSONObject(object.getString("ItemInfo"));
                        String imgPath = service_info.getString("Picture");
                        iv_service_picture.setImageUrl(!imgPath.startsWith("http://") ? Constant.URL_BASE + imgPath : imgPath, MyApplication.getInstance().getImageLoader());
                        tv_wash_type.setText(service_info.getString("PackageName"));
                        tv_wash_content.setText(service_info.getString("Remark"));

                        tv_total_price.setText("实付款:" + object.getString("TotalMoney") + "元");
                        tv_order_remarks.setText("订单备注:" + object.getString("Remark"));

                        String time_end = object.getString("WashEnd");
                        time_end = Utils.LongDateToString(time_end);
                        tv_time_finish.setText(time_end);
                        String time_start = object.getString("WashStart");
                        time_start = Utils.LongDateToString(time_start);
                        tv_time_start.setText(time_start);

                        if (object.getString("ListPictureAfter").equals("null")) {
                            ll_wash_finish.setVisibility(View.GONE);
                        } else {
                            array_finish = object.getJSONArray("ListPictureAfter");
                            adapter_Finish = new ImageViewAdapter(OrderDetailsActivity.this, "Path", array_finish);
                            hlv_wash_finish.setAdapter(adapter_Finish);
                        }

                        if (object.getString("ListPictureBefore").equals("null")) {
                            ll_wash_before.setVisibility(View.GONE);
                        } else {
                            array_before = object.getJSONArray("ListPictureBefore");
                            adapter_Before = new ImageViewAdapter(OrderDetailsActivity.this, "Path", array_before);
                            hlv_wash_before.setAdapter(adapter_Before);
                        }
                    } else {
                        Utils.showToast(OrderDetailsActivity.this, jsonObject.getString("Error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Utils.showToast(OrderDetailsActivity.this, VolleyErrorHelper.getMessage(volleyError, OrderDetailsActivity.this));
            }
        });
        MyApplication.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (vp_pager.getVisibility() == View.VISIBLE) {
            vp_pager.setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
