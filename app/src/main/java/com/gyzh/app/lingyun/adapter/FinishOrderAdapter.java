package com.gyzh.app.lingyun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.listener.OnItemViewClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 已完成订单
 * Created by Administrator on 2015/7/15.
 */
public class FinishOrderAdapter extends BaseAdapter {
    Context context;
    JSONArray array;
    LayoutInflater inflater;
    OnItemViewClickListener onItemViewClickListener;

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public FinishOrderAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return array.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refresh(JSONArray array) {
        this.array = array;
        this.notifyDataSetChanged();
    }

    public void append(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                this.array.put(array.getJSONObject(i));
            }
            this.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.item_finish_order, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_order_number = (TextView) convertView.findViewById(R.id.tv_order_number);
            viewHolder.tv_order_state = (TextView) convertView.findViewById(R.id.tv_order_state);
            viewHolder.tv_customer_name = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.iv_phone = (ImageView) convertView.findViewById(R.id.iv_phone);
            viewHolder.tv_order_time = (TextView) convertView.findViewById(R.id.tv_expected_time);
            viewHolder.tv_service_name = (TextView) convertView.findViewById(R.id.tv_service_name);
            viewHolder.tv_carbrand_cartype = (TextView) convertView.findViewById(R.id.tv_carbrand_carype);
            viewHolder.tv_car_model = (TextView) convertView.findViewById(R.id.tv_car_model);
            viewHolder.tv_car_number = (TextView) convertView.findViewById(R.id.tv_car_number);
            viewHolder.tv_car_color = (TextView) convertView.findViewById(R.id.tv_car_color);
            viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            viewHolder.tv_service_change = (TextView) convertView.findViewById(R.id.tv_service_added);
            viewHolder.tv_service_price = (TextView) convertView.findViewById(R.id.tv_service_price);
            viewHolder.ll_service_add = (LinearLayout) convertView.findViewById(R.id.ll_service_add);
            convertView.setTag(viewHolder);
        }

        if (onItemViewClickListener != null) {
            viewHolder.iv_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemViewClickListener.onItemViewClickListener(v.getId(), position);
                }
            });
        }

        try {
            JSONObject object = array.getJSONObject(position);
            viewHolder.tv_order_number.setText(object.getString("OrderNumber"));
            int state = Integer.parseInt(object.getString("OrderState"));
            if (state == -2) {
                viewHolder.tv_order_state.setText("用户取消");
            } else if (state == -1) {
                viewHolder.tv_order_state.setText("员工取消");
            } else if (state == 0) {
                viewHolder.tv_order_state.setText("未支付");
            } else if (state == 1) {
                viewHolder.tv_order_state.setText("待派单");
            } else if (state == 2) {
                viewHolder.tv_order_state.setText("接单");
            } else if (state == 3) {
                viewHolder.tv_order_state.setText("已出发");
            } else if (state == 4) {
                viewHolder.tv_order_state.setText("洗车中");
            } else if (state == 5) {
                viewHolder.tv_order_state.setText("完成");
            }
            viewHolder.tv_customer_name.setText(object.getString("Contact"));
            viewHolder.tv_order_time.setText(object.getString("ExpectedTime"));
            viewHolder.tv_service_name.setText(object.getString("ItemName"));
            viewHolder.tv_carbrand_cartype.setText(object.getString("CarBrand") + object.getString("CarType"));
            viewHolder.tv_car_model.setText(object.getString("CarModel"));
            viewHolder.tv_car_number.setText(object.getString("CarNumber"));
            viewHolder.tv_car_color.setText(object.getString("Color"));
            viewHolder.tv_address.setText(object.getString("Address"));
            String AddService = object.getString("ChangeServicePackageName");
            if (AddService.equals("")) {
                viewHolder.ll_service_add.setVisibility(View.GONE);
            } else {
                viewHolder.tv_service_change.setText(AddService);
            }
            viewHolder.tv_service_price.setText("￥ " + object.getString("TotalMoney"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static final class ViewHolder {
        TextView tv_order_number;
        TextView tv_order_state;
        TextView tv_customer_name;
        ImageView iv_phone;
        TextView tv_order_time;
        TextView tv_service_name;
        TextView tv_carbrand_cartype;
        TextView tv_car_model;
        TextView tv_car_number;
        TextView tv_car_color;
        TextView tv_address;
        TextView tv_service_change;
        TextView tv_service_price;
        LinearLayout ll_service_add;
    }

}
