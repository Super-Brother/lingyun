package com.gyzh.app.lingyun.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.adapter.FinishOrderAdapter;
import com.gyzh.app.lingyun.listener.OnItemViewClickListener;
import com.gyzh.app.lingyun.ui.OrderDetailsActivity;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.utils.VolleyErrorHelper;
import com.gyzh.app.lingyun.views.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 完成订单
 */
public class FinishOrderFragment extends Fragment {
    SwipeRefreshLayout sfl_refresh;
    ListView lv_list;
    FinishOrderAdapter FinishOrderAdapter;
    int page = 1;
    LoadingDialog dialog;
    boolean refreshable;
    static final String TAG = FinishOrderFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        dialog = new LoadingDialog(getActivity());

        sfl_refresh = (SwipeRefreshLayout) view.findViewById(R.id.sfl_refresh);
        //下拉刷新
        sfl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                initData();
            }
        });

        lv_list = (ListView) view.findViewById(R.id.lv_list);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject object = (JSONObject) FinishOrderAdapter.getItem(position);
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
        return view;
    }

    private void initData() {
        dialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("type", 2 + "");
        params.put("memberid", Utils.getLoginId(getActivity().getApplicationContext()) + "");
        params.put("pageindex", page + "");
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
                        if (array.length() < Constant.PAGE_SIZE) {
                            refreshable = false;
                        }
                        if (FinishOrderAdapter == null) {
                            FinishOrderAdapter = new FinishOrderAdapter(getActivity(), array);
                            FinishOrderAdapter.setOnItemViewClickListener(onItemViewClickListener);
                            lv_list.setAdapter(FinishOrderAdapter);
                        } else if (page == 1) {
                            FinishOrderAdapter.refresh(array);
                        } else {
                            FinishOrderAdapter.append(array);
                        }
                    } else {
                        Utils.showToast(getActivity(), jsonObject.getString("Error"));
                    }
                    if (sfl_refresh.isRefreshing()) {
                        sfl_refresh.setRefreshing(false);
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
                if (sfl_refresh.isRefreshing()) {
                    sfl_refresh.setRefreshing(false);
                }
                Utils.showToast(getActivity(), VolleyErrorHelper.getMessage(volleyError, getActivity()));
            }
        });
        MyApplication.getInstance().addToRequestQueue(request, TAG);
    }

    OnItemViewClickListener onItemViewClickListener = new OnItemViewClickListener() {
        @Override
        public void onItemViewClickListener(int viewId, int position) {
            switch (viewId) {
                case R.id.iv_phone:
                    final JSONObject object = (JSONObject) FinishOrderAdapter.getItem(position);
                    String tel = null;
                    try {
                        tel = object.getString("Phone");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }
}
