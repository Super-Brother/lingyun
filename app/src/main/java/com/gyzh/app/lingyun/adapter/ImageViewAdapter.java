package com.gyzh.app.lingyun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.NetworkImageView;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/7/15.
 */
public class ImageViewAdapter extends BaseAdapter {
    Context context;
    String urlKey;
    JSONArray array;
    LayoutInflater inflater;

    public ImageViewAdapter(Context context, String urlKey, JSONArray array) {
        this.context = context;
        this.urlKey = urlKey;
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
            convertView = inflater.inflate(R.layout.item_imageview_100, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.image_view);
            viewHolder.imageView.setErrorImageResId(R.mipmap.img_loading);
            viewHolder.imageView.setDefaultImageResId(R.mipmap.img_error);
            convertView.setTag(viewHolder);
        }

        try {
            final JSONObject jsonObject = array.getJSONObject(position);
            String imgPath = jsonObject.getString(urlKey);
            viewHolder.imageView.setImageUrl(!imgPath.startsWith("http://") ? Constant.URL_BASE + imgPath : imgPath, MyApplication.getInstance().getImageLoader());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static final class ViewHolder {
        NetworkImageView imageView;
    }

}
