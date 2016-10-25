package com.gyzh.app.lingyun.ui;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushNotificationBuilder;
import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.fragment.HomeFragment;
import com.gyzh.app.lingyun.fragment.MineFragment;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.views.AppDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 主页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = MainActivity.class.getName();
    LinearLayout ll_view_bottom;
    LinearLayout tab_home, tab_order, tab_mine;
    ImageView iv_tab_home, iv_tab_order, iv_tab_mine;
    TextView tv_tab_home, tv_tab_order, tv_tab_mine;
    FragmentManager fm;
    FragmentTransaction ft;
    Fragment homeFragment, mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MyApplication.activities.add(this);

        fm = getSupportFragmentManager();

        initView();

        initPush();//初始化推送

        checkVersion();//检查更新

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    //初始化控件
    private void initView() {
        ll_view_bottom = (LinearLayout) findViewById(R.id.view_buttom);
        tab_home = (LinearLayout) findViewById(R.id.tab_home);
        tab_home.setOnClickListener(this);
        tab_order = (LinearLayout) findViewById(R.id.tab_order);
        tab_order.setOnClickListener(this);
        tab_mine = (LinearLayout) findViewById(R.id.tab_mine);
        tab_mine.setOnClickListener(this);
        iv_tab_home = (ImageView) findViewById(R.id.iv_tab_home);
        tv_tab_home = (TextView) findViewById(R.id.tv_tab_home);
        iv_tab_order = (ImageView) findViewById(R.id.iv_tab_order);
        tv_tab_order = (TextView) findViewById(R.id.tv_tab_order);
        iv_tab_mine = (ImageView) findViewById(R.id.iv_tab_mine);
        tv_tab_mine = (TextView) findViewById(R.id.tv_tab_mine);
        showHome();
    }

    //初始化推送
    private void initPush() {
        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
        // "api_key")
        //        ！！ 请将AndroidManifest.xml 128 api_key 字段值修改为自己的 api_key 方可使用 ！！
        //        ！！ ATTENTION：You need to modify the value of api_key to your own at row 128 in AndroidManifest.xml to use this Demo !!
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(MainActivity.this, "api_key"));
        PushManager.setDefaultNotificationBuilder(MainActivity.this, new PushNotificationBuilder() {
            @Override
            public Notification construct(Context context) {
                return null;
            }
        });
        // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // PushManager.enableLbs(getApplicationContext());

        // Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
//        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(R.layout.notification_custom_builder, R.id.notification_icon, R.id.notification_title, R.id.notification_text);
//        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
//        cBuilder.setLayoutDrawable(R.mipmap.icon_logo);
//        // 推送高级设置，通知栏样式设置为下面的ID
//        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }

    //检查软件版本
    private void checkVersion() {
        JsonObjectRequest request = new JsonObjectRequest(Constant.URL_UPDATE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String netVeriosn = jsonObject.getString("version");
                    int version = Integer.parseInt(netVeriosn);
                    final String url = jsonObject.getString("url");
                    if (version > Utils.getAppVersionCode(MainActivity.this)) {
                        new AppDialog(MainActivity.this, null, getString(R.string.app_name) + "  发现新版本", "更新", "稍后", new AppDialog.DialogButtonOnClickListener() {
                            @Override
                            public void clickconfirm() {
                                Intent intent = new Intent(Constant.SERVICE_UPDATE);
                                intent.setPackage(getPackageName());
                                intent.putExtra("url", url);
                                startService(intent);
                            }

                            @Override
                            public void clickcancel() {

                            }
                        }).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.activities.remove(this);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (!PushManager.isPushEnabled(getApplicationContext())) {
//            initPush();
//        }
//        int flag = intent.getIntExtra(Constant.MAIN_INTENT_START_FLAG, -1);
//        switch (flag) {
//            case Constant.MAIN_INTENT_START_FLAG_PUSH:
//                showPush();
//                break;
//        }
//    }

    //返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            new AppDialog(MainActivity.this, null, "确认退出程序么?", "退出", "取消", new AppDialog.DialogButtonOnClickListener() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //主页
            case R.id.tab_home:
                showHome();
                break;

            //发布
            case R.id.tab_order:
                showOrder();
                break;

            //我的
            case R.id.tab_mine:
                showMine();
                break;
        }
    }

    //首页
    private void showHome() {
        tab_home.setBackgroundColor(getResources().getColor(R.color.bg_main_tab_pressed));
        tab_order.setBackgroundColor(getResources().getColor(R.color.bg_main_tab));
        tab_mine.setBackgroundColor(getResources().getColor(R.color.bg_main_tab));
        iv_tab_home.setImageResource(R.mipmap.icon_home_pressed);
        tv_tab_home.setTextColor(getResources().getColor(R.color.main_tab_text_passed));
        iv_tab_order.setImageResource(R.mipmap.icon_order_noramal);
        tv_tab_order.setTextColor(getResources().getColor(R.color.main_tab_text_normal));
        iv_tab_mine.setImageResource(R.mipmap.icon_mine_normal);
        tv_tab_mine.setTextColor(getResources().getColor(R.color.main_tab_text_normal));
        ft = fm.beginTransaction();
        if (homeFragment != null) {
            for (Fragment fragment : fm.getFragments()) {
                ft.hide(fragment);
            }
            ft.show(homeFragment);
        } else {
            homeFragment = new HomeFragment();
            ft.add(R.id.fl_content, homeFragment);
        }
        ft.commit();
    }

    //订单
    private void showOrder() {
        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
        intent.putExtra("form", "单池");
        startActivity(intent);
//        ft = fm.beginTransaction();
//        if (orderFragment != null) {
//            for (Fragment fragment : fm.getFragments()) {
//                ft.hide(fragment);
//            }
//            ft.show(orderFragment);
//        } else {
//            orderFragment = new OrderFragment();
//            ft.add(R.id.fl_content, orderFragment);
//
//        }
//        ft.commit();
    }

    //我的
    private void showMine() {
        tab_home.setBackgroundColor(getResources().getColor(R.color.bg_main_tab));
        tab_mine.setBackgroundColor(getResources().getColor(R.color.bg_main_tab_pressed));
        iv_tab_home.setImageResource(R.mipmap.icon_home_normal);
        tv_tab_home.setTextColor(getResources().getColor(R.color.main_tab_text_normal));
        iv_tab_mine.setImageResource(R.mipmap.icon_mine_pressed);
        tv_tab_mine.setTextColor(getResources().getColor(R.color.main_tab_text_passed));
        ft = fm.beginTransaction();
        if (mineFragment != null) {
            for (Fragment fragment : fm.getFragments()) {
                ft.hide(fragment);
            }
            ft.show(mineFragment);
        } else {
            mineFragment = new MineFragment();
            ft.add(R.id.fl_content, mineFragment);
        }
        ft.commit();
    }

}
