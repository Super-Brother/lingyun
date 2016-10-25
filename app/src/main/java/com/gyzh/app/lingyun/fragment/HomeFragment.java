package com.gyzh.app.lingyun.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.adapter.HomeOrderListAdapter;
import com.gyzh.app.lingyun.adapter.ImagePagerAdapter;
import com.gyzh.app.lingyun.autoscrollviewpager.AutoScrollViewPager;
import com.gyzh.app.lingyun.location.PollingService;
import com.gyzh.app.lingyun.location.PollingUtils;
import com.gyzh.app.lingyun.ui.MessageActivity;
import com.gyzh.app.lingyun.ui.OrderActivity;
import com.gyzh.app.lingyun.ui.OrderDetailsActivity;
import com.gyzh.app.lingyun.ui.WebActivity;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.DateString;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.utils.VolleyErrorHelper;
import com.gyzh.app.lingyun.views.LoadingDialog;
import com.gyzh.app.lingyun.views.NoScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 主页
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    static final String TAG = HomeFragment.class.getName();
    LoadingDialog dialog;
    AutoScrollViewPager vp_page;
    NoScrollListView nslv_list;
    HomeOrderListAdapter adapter;
    TextView tv_weather, tv_date;
    TextView tv_more;
    ImageView iv_work_state;
    NetworkImageView iv_weather;
    int workState;
    ImagePagerAdapter imagePagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new LoadingDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //首页
        View view = inflater.inflate(R.layout.fragment_home, null);
        //ViewPager
        vp_page = (AutoScrollViewPager) view.findViewById(R.id.vp_pager);
        //开始工作
        iv_work_state = (ImageView) view.findViewById(R.id.iv_start_work);
        iv_work_state.setOnClickListener(this);
        iv_weather = (NetworkImageView) view.findViewById(R.id.iv_weather);
        iv_weather.setErrorImageResId(R.mipmap.img_loading);
        iv_weather.setDefaultImageResId(R.mipmap.img_error);
        //单池
        view.findViewById(R.id.ll_order_cell).setOnClickListener(this);
        //预约单
        view.findViewById(R.id.ll_appointment_order).setOnClickListener(this);
        //消息
        view.findViewById(R.id.ll_information).setOnClickListener(this);
        //制度
        view.findViewById(R.id.ll_system).setOnClickListener(this);
        //当日待办订单
        nslv_list = (NoScrollListView) view.findViewById(R.id.nslv_list);
        nslv_list.setFocusable(false);
        nslv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                startActivity(intent);
            }
        });
        nslv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject object = (JSONObject) adapter.getItem(position);
                String orderId = "";
                try {
                    orderId = object.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), OrderDetailsActivity.class);
                intent.putExtra("id", orderId);
                startActivity(intent);
            }
        });

        tv_weather = (TextView) view.findViewById(R.id.tv_weather);//天气
        tv_date = (TextView) view.findViewById(R.id.tv_date);//日期
        tv_date.setText(DateString.StringData());

        tv_more = (TextView) view.findViewById(R.id.tv_more);//更多
        tv_more.setOnClickListener(this);

        initImagePager();//初始化轮播图

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //初始化工作状态
        initWorkState();
        //初始化天气
        initWeather();
        //初始化首页数据
        initData();
    }

    //初始化轮播图
    private void initImagePager() {
        JsonObjectRequest request = new JsonObjectRequest("http://lingyun.dz.palmapp.cn/CommonInfo/AjaxPoster/GetPoster?postertype=1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONArray array = jsonObject.getJSONArray("Data");
                    imagePagerAdapter = new ImagePagerAdapter(getActivity(), "picurl", array);
                    vp_page.setAdapter(imagePagerAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        MyApplication.getInstance().addToRequestQueue(request, TAG);
    }

    //初始化工作状态
    private void initWorkState() {
        HashMap<String, String> params = new HashMap<>();
        params.put("memberid", Utils.getLoginId(getActivity()) + "");

        JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_GET_WORK_STATE, params), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt("Flag") == 1) {
                        workState = Integer.parseInt(jsonObject.getString("WorkState"));
                        if (workState == 0) {
                            iv_work_state.setImageResource(R.mipmap.icon_resting);
                            PollingUtils.stopPollingService(getActivity(), PollingService.class, PollingService.ACTION);
                        } else if (workState == 1) {
                            iv_work_state.setImageResource(R.mipmap.icon_working);
                            PollingUtils.startPollingService(getActivity(), 180, PollingService.class, PollingService.ACTION);
                        }
                    } else {
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
                Utils.showToast(getActivity(), VolleyErrorHelper.getMessage(volleyError, getActivity()));
            }
        });
        MyApplication.getInstance().addToRequestQueue(request, TAG);
    }

    /**
     * 初始化首页数据
     */
    private void initData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", 1 + "");
        params.put("memberid", Utils.getLoginId(getActivity().getApplicationContext()) + "");
        params.put("pageindex", 1 + "");
        params.put("pagesize", Constant.PAGE_SIZE + "");

        JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_GET_ORDER, params), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    if (jsonObject.getInt("Flag") == 1) {
                        JSONArray array = jsonObject.getJSONArray("Data");
                        adapter = new HomeOrderListAdapter(getActivity(), array);
                        nslv_list.setAdapter(adapter);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Utils.showToast(getActivity(), VolleyErrorHelper.getMessage(volleyError, getActivity()));
            }
        });
        MyApplication.getInstance().addToRequestQueue(request, TAG);
    }

    /**
     * 初始化天气
     */
    void initWeather() {
        try {
            JsonObjectRequest request = new JsonObjectRequest(Constant.url_weather_left + URLEncoder.encode("郑州", "UTF-8") + Constant.url_weather_right, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInt("error") == 0) {
                            JSONArray result_array = jsonObject.getJSONArray("results");
                            JSONObject result_object = new JSONObject(result_array.get(0).toString());
                            JSONArray data_array = result_object.getJSONArray("weather_data");
                            JSONObject data_obj = new JSONObject(data_array.get(0).toString());
                            boolean isDay = Utils.isDay();
                            String imgPath = "";
                            if (isDay) {
                                imgPath = data_obj.getString("dayPictureUrl");
                            } else {
                                imgPath = data_obj.getString("nightPictureUrl");
                            }
                            iv_weather.setImageUrl(!imgPath.startsWith("http://") ? "http://192.168.0.141" + imgPath : imgPath, MyApplication.getInstance().getImageLoader());
                            tv_weather.setText(data_obj.getString("temperature") + " " + data_obj.getString("weather"));
                        } else {
                            Utils.showToast(getActivity(), "天气信息加载失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Utils.showToast(getActivity(), VolleyErrorHelper.getMessage(volleyError, getActivity()));
                }
            });
            MyApplication.getInstance().addToRequestQueue(request, TAG);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_start_work:
                dialog.show();

                if (workState == 0) {
                    PollingUtils.startPollingService(getActivity(), 180, PollingService.class, PollingService.ACTION);
                } else if (workState == 1) {
                    PollingUtils.stopPollingService(getActivity(), PollingService.class, PollingService.ACTION);
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("memberid", Utils.getLoginId(getActivity()) + "");

                JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_UPDATE_WORK_STATE, params), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {
                            if (jsonObject.getInt("Flag") == 1) {
                                workState = Integer.parseInt(jsonObject.getString("WorkState"));
                                if (workState == 0) {
                                    iv_work_state.setImageResource(R.mipmap.icon_resting);
                                } else if (workState == 1) {
                                    iv_work_state.setImageResource(R.mipmap.icon_working);
                                }
                            } else {
                                Utils.showToast(getActivity(), "工作状态获取失败");
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
                        Utils.showToast(getActivity(), VolleyErrorHelper.getMessage(volleyError, getActivity()));
                    }
                });
                MyApplication.getInstance().addToRequestQueue(request, TAG);
                break;

            //单池
            case R.id.ll_order_cell:
                intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("form", "单池");
                startActivity(intent);
                break;

            //预约单
            case R.id.ll_appointment_order:
                intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("form", "预约单");
                startActivity(intent);
                break;

            //消息
            case R.id.ll_information:
                intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;

            //制度
            case R.id.ll_system:
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", Constant.URL_BASE+Constant.URL_RULES_REGULATION);
                intent.putExtra("form", "规章制度");
                startActivity(intent);
                break;

            //更多
            case R.id.tv_more:
                intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("form", "预约单");
                startActivity(intent);
                break;
        }
    }

}
