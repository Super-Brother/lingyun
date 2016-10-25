package com.gyzh.app.lingyun.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.listener.OnItemViewClickListener;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/7/15.
 */
public class PreviewViewPagerAdapter extends PagerAdapter {
    String urlKey;
    Context context;
    JSONArray array;

    OnItemViewClickListener onItemViewClickListener;

    public PreviewViewPagerAdapter(Context context, String urlKey, JSONArray array) {
        this.urlKey = urlKey;
        this.context = context;
        this.array = array;
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        NetworkImageView imageView = new NetworkImageView(context);
        imageView.setErrorImageResId(R.mipmap.img_loading);
        imageView.setDefaultImageResId(R.mipmap.img_error);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        try {
            final JSONObject jsonObject = array.getJSONObject(position);
            String imgPath = jsonObject.getString(urlKey);
            imageView.setImageUrl(!imgPath.startsWith("http://") ? Constant.URL_BASE + imgPath : imgPath, MyApplication.getInstance().getImageLoader());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onItemViewClickListener != null) {
                    onItemViewClickListener.onItemViewClickListener(-1, position);
                }
            }
        });
        container.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public Object getItem(int position) {
        try {
            return array.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
