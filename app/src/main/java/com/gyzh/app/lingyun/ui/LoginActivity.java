package com.gyzh.app.lingyun.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.utils.VolleyErrorHelper;
import com.gyzh.app.lingyun.views.AppDialog;
import com.gyzh.app.lingyun.views.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 登录
 * Created by Administrator on 2015/7/15.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    static final String TAG = LoginActivity.class.getName();
    LoadingDialog dialog;
    EditText et_account, et_password;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        dialog = new LoadingDialog(this);

        MyApplication.activities.add(this);
        //页头
        findViewById(R.id.iv_goback).setVisibility(View.GONE);
        findViewById(R.id.iv_action).setVisibility(View.GONE);
        TextView tv_action = (TextView) findViewById(R.id.tv_action);
        tv_action.setVisibility(View.GONE);
        tv_action.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("登 录");

        initView();
    }

    private void initView() {
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        findViewById(R.id.tv_reset_password).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.activities.remove(this);
        MyApplication.getInstance().cancelPendingRequests(TAG);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //注册
            case R.id.tv_action:
                break;

            //忘记密码
            case R.id.tv_reset_password:
                break;

            //登录
            case R.id.btn_login:
                String account = et_account.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    Utils.showToast(this, "账号不能为空!");
                    return;
                }

                String password = et_password.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Utils.showToast(this, "密码不能为空!");
                    return;
                }

                dialog.show();

                HashMap<String, String> params = new HashMap<>();
                params.put("phone", account);
                params.put("password", password);
                params.put("channeid", Constant.CHANNEL_ID);
                params.put("passageid", Constant.USER_ID);

                JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_LOGIN, params), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {
                            if (jsonObject.getInt("Flag") == 1) {
                                Utils.showToast(LoginActivity.this, "登录成功!");
                                editor = getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE).edit();
                                editor.putBoolean(Constant.SP_KEY_ISLOGIN, true);
                                editor.putString(Constant.SP_KEU_USER_LOGIN_INFO, jsonObject.getString("Data"));
                                editor.commit();
                                finish();
                            } else {
                                new AppDialog(LoginActivity.this, null, "登录失败!", "确定", null).show();
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
                        new AppDialog(LoginActivity.this, null, VolleyErrorHelper.getMessage(volleyError, LoginActivity.this), "确定", null).show();
                    }
                });
                MyApplication.getInstance().addToRequestQueue(request, TAG);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            new AppDialog(LoginActivity.this, null, "确认退出程序么?", "退出", "取消", new AppDialog.DialogButtonOnClickListener() {
                @Override
                public void clickconfirm() {
                    MyApplication.exitApp();
                }

                @Override
                public void clickcancel() {
                }
            }).show();
        }
        return super.onKeyDown(keyCode, event);
    }

}
