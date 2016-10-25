package com.gyzh.app.lingyun.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.ui.LoginActivity;
import com.gyzh.app.lingyun.ui.UserInfoActivity;
import com.gyzh.app.lingyun.ui.WebActivity;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.views.CircleNetworkImageView;

/**
 * 我的
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    CircleNetworkImageView iv_user_icon;
    TextView tv_username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null);
        iv_user_icon = (CircleNetworkImageView) view.findViewById(R.id.iv_user_icon);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        view.findViewById(R.id.view_userinfo).setOnClickListener(this);
        view.findViewById(R.id.view_share).setOnClickListener(this);
        view.findViewById(R.id.view_about_us).setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isLogin(getActivity())) {
            //用户名
            String username = Utils.getLoginValue(getActivity(), "Name").toString();
            tv_username.setText(username);
            //头像
            String imgPath = Utils.getLoginValue(getActivity(), "Logo").toString();
            iv_user_icon.setImageUrl(imgPath.startsWith("http") ? imgPath : Constant.URL_BASE + imgPath, MyApplication.getInstance().getImageLoader());
        } else {
            Utils.showToast(getActivity(), "你没有登录,请先登录!");
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
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
            case R.id.view_userinfo:
                intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.view_share:
                Utils.share(getActivity(), "http://down.palmapp.cn/down/25");
                break;

            case R.id.view_about_us:
                intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", Constant.URL_BASE + Constant.URL_ABOUT_US);
                intent.putExtra("form", "关于我们");
                startActivity(intent);
                break;
        }
    }

}
