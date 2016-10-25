package com.gyzh.app.lingyun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.listener.OnItemViewClickListener;

/**
 * 消息Adapter
 * Created by Administrator on 2015/7/15.
 */
public class MessageAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    OnItemViewClickListener onItemViewClickListener;

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public MessageAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
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
            convertView = inflater.inflate(R.layout.item_message, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    private static final class ViewHolder {
    }
}
