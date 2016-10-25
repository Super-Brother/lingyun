package com.gyzh.app.lingyun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Home页预约单
 * Created by Administrator on 2015/7/15.
 */
public class HomeOrderListAdapter extends BaseAdapter {
    Context context;
    JSONArray array;
    LayoutInflater inflater;

    public HomeOrderListAdapter(Context context, JSONArray array) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.item_appointment_order_home, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_customer_name = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tv_order_time = (TextView) convertView.findViewById(R.id.tv_order_time);
            viewHolder.tv_appointment_time = (TextView) convertView.findViewById(R.id.tv_appointment_time);
            viewHolder.tv_carbrand_cartype = (TextView) convertView.findViewById(R.id.tv_carbrand_carype);
            viewHolder.tv_car_model = (TextView) convertView.findViewById(R.id.tv_car_model);
            viewHolder.tv_car_number = (TextView) convertView.findViewById(R.id.tv_car_number);
            viewHolder.tv_car_color = (TextView) convertView.findViewById(R.id.tv_car_color);
            viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(viewHolder);
        }

        try {
            JSONObject object = array.getJSONObject(position);
            viewHolder.tv_customer_name.setText(object.getString("Contact"));
            viewHolder.tv_order_time.setText(object.getString("ExpectedTime"));
            viewHolder.tv_appointment_time.setText(object.getString("WashStart"));
            viewHolder.tv_carbrand_cartype.setText(object.getString("CarBrand") + object.getString("CarType"));
            viewHolder.tv_car_model.setText(object.getString("CarModel"));
            viewHolder.tv_car_number.setText(object.getString("CarNumber"));
            viewHolder.tv_car_color.setText(object.getString("Color"));
            viewHolder.tv_address.setText(object.getString("Address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static final class ViewHolder {
        TextView tv_customer_name;
        TextView tv_order_time;
        TextView tv_appointment_time;
        TextView tv_carbrand_cartype;
        TextView tv_car_model;
        TextView tv_car_number;
        TextView tv_car_color;
        TextView tv_address;
    }

}
