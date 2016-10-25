package com.gyzh.app.lingyun.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.adapter.OrderCellAdapter;
import com.gyzh.app.lingyun.listener.OnItemViewClickListener;
import com.gyzh.app.lingyun.ui.OrderDetailsActivity;
import com.gyzh.app.lingyun.ui.UploadPhotosActivity;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.utils.VolleyErrorHelper;
import com.gyzh.app.lingyun.views.AppDialog;
import com.gyzh.app.lingyun.views.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 单池
 */
public class OrderCellFragment extends Fragment {
    SwipeRefreshLayout sfl_refresh;
    ListView lv_list;
    OrderCellAdapter OrderCellAdapter;
    int page = 1;
    LoadingDialog dialog;
    boolean refreshable;
    static final String TAG = OrderCellFragment.class.getName();
    JSONArray array;
    String reason;

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
                JSONObject object = (JSONObject) OrderCellAdapter.getItem(position);
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
        params.put("type", 0 + "");
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
                        array = jsonObject.getJSONArray("Data");
                        if (array.length() < Constant.PAGE_SIZE) {
                            refreshable = false;
                        }
                        if (OrderCellAdapter == null) {
                            OrderCellAdapter = new OrderCellAdapter(getActivity(), array);
                            OrderCellAdapter.setOnItemViewClickListener(onItemViewClickListener);
                            lv_list.setAdapter(OrderCellAdapter);
                        } else if (page == 1) {
                            OrderCellAdapter.refresh(array);
                        } else {
                            OrderCellAdapter.append(array);
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
        public void onItemViewClickListener(int viewId, final int position) {
            final EditText editText = new EditText(getActivity());
            switch (viewId) {
                case R.id.btn_order_kill:
                    new AlertDialog.Builder(getActivity()).setTitle("输入消单原因").setIcon(
                            android.R.drawable.ic_dialog_info).setView(
                            editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            reason = editText.getText().toString();
                            orderKill(position, reason);
                        }
                    })
                            .setNegativeButton("取消", null).show();
                    break;

                case R.id.btn_order_zhuan:
                    new AlertDialog.Builder(getActivity()).setTitle("输入转单原因").setIcon(
                            android.R.drawable.ic_dialog_info).setView(
                            editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            reason = editText.getText().toString();
                            orderTurn(position, reason);
                        }
                    })
                            .setNegativeButton("取消", null).show();
                    break;

                case R.id.btn_finish:
                    JSONObject object = (JSONObject) OrderCellAdapter.getItem(position);
                    try {
                        int state = Integer.parseInt(object.getString("OrderState"));
                        Intent intent;
                        if (state == 2) {
                            orderGoto(position);
                        } else if (state == 3) {
                            intent = new Intent(getActivity(), UploadPhotosActivity.class);
                            intent.putExtra("form", "开始");
                            intent.putExtra("id", object.getString("id"));
                            startActivity(intent);
                        } else if (state == 4) {
                            intent = new Intent(getActivity(), UploadPhotosActivity.class);
                            intent.putExtra("form", "结束");
                            intent.putExtra("id", object.getString("id"));
                            startActivity(intent);
                        } else {
                            Utils.showToast(getActivity(), "错误的操作!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.iv_phone:
                    call(position);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 消单
     *
     * @param position
     * @param reason
     */
    private void orderKill(final int position, String reason) {
        final JSONObject object = (JSONObject) OrderCellAdapter.getItem(position);
        try {
            dialog.show();

            HashMap<String, String> params = new HashMap<>();
            params.put("orderid", object.getString("id"));
            params.put("memberid", Utils.getLoginId(getActivity()) + "");
            params.put("reason", reason);

            JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_KILL_ORDER, params), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        if (jsonObject.getInt("Flag") == 1) {
                            Utils.showToast(getActivity(), "消单成功！");
                            array = Utils.jsonArrayRemove(array, position);
                            OrderCellAdapter = new OrderCellAdapter(getActivity(), array);
                            OrderCellAdapter.setOnItemViewClickListener(onItemViewClickListener);
                            lv_list.setAdapter(OrderCellAdapter);
                        } else {
                            new AppDialog(getActivity(), null, jsonObject.getString("Error"), "确定", null).show();
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
                    new AppDialog(getActivity(), null, VolleyErrorHelper.getMessage(volleyError, getActivity()), "确定", null).show();
                }
            });
            MyApplication.getInstance().addToRequestQueue(request, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转单
     *
     * @param position
     * @param reason
     */
    private void orderTurn(final int position, String reason) {
        final JSONObject object = (JSONObject) OrderCellAdapter.getItem(position);
        try {
            dialog.show();

            HashMap<String, String> params = new HashMap<>();
            params.put("orderid", object.getString("id"));
            params.put("memberid", Utils.getLoginId(getActivity()) + "");
            params.put("reason", reason);

            JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_TURN_ORDER, params), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        if (jsonObject.getInt("Flag") == 1) {
                            Utils.showToast(getActivity(), "转单成功！");
                            array = Utils.jsonArrayRemove(array, position);
                            OrderCellAdapter = new OrderCellAdapter(getActivity(), array);
                            OrderCellAdapter.setOnItemViewClickListener(onItemViewClickListener);
                            lv_list.setAdapter(OrderCellAdapter);
                        } else {
                            new AppDialog(getActivity(), null, jsonObject.getString("Error"), "确定", null).show();
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
                    new AppDialog(getActivity(), null, VolleyErrorHelper.getMessage(volleyError, getActivity()), "确定", null).show();
                }
            });
            MyApplication.getInstance().addToRequestQueue(request, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 出发
     *
     * @param position
     */
    private void orderGoto(final int position) {
        final JSONObject object = (JSONObject) OrderCellAdapter.getItem(position);
        try {
            dialog.show();

            HashMap<String, String> params = new HashMap<>();
            params.put("orderid", object.getString("id"));
            params.put("memberid", Utils.getLoginId(getActivity()) + "");

            JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_ORDER_GOTO, params), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        if (jsonObject.getInt("Flag") == 1) {
                            Utils.showToast(getActivity(), "已出发");
                            object.put("OrderState", 3 + "");
                            OrderCellAdapter.updateItem(position, object);
                        } else {
                            new AppDialog(getActivity(), null, jsonObject.getString("Error"), "确定", null).show();
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
                    new AppDialog(getActivity(), null, VolleyErrorHelper.getMessage(volleyError, getActivity()), "确定", null).show();
                }
            });
            MyApplication.getInstance().addToRequestQueue(request, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打电话
     *
     * @param position
     */
    private void call(int position) {
        final JSONObject object = (JSONObject) OrderCellAdapter.getItem(position);
        String tel = null;
        try {
            tel = object.getString("Phone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        startActivity(intent);
    }

}
