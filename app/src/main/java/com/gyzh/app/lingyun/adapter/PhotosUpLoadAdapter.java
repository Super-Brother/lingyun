package com.gyzh.app.lingyun.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.listener.OnItemViewClickListener;

import java.util.ArrayList;

/**
 * 添加洗车图片
 * Created by Administrator on 2015/8/11.
 */
public class PhotosUpLoadAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Bitmap> list;
    OnItemViewClickListener listener;

    public void setOnItemViewClickListener(OnItemViewClickListener listener) {
        this.listener = listener;
    }

    public PhotosUpLoadAdapter(Context context, ArrayList<Bitmap> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = inflater.inflate(R.layout.item_photo_upload, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_room_photo = (ImageView) convertView.findViewById(R.id.iv_room_photo);
            convertView.setTag(viewHolder);
        }

        if (position < list.size()) {
            viewHolder.iv_room_photo.setImageBitmap(list.get(position));
        } else {
            viewHolder.iv_room_photo.setImageResource(R.mipmap.add_photo);
        }
        return convertView;
    }

    private static final class ViewHolder {
        ImageView iv_room_photo;
    }

}
