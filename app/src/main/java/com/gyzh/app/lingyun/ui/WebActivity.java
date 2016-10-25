package com.gyzh.app.lingyun.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.utils.WebViewBaseClient;

/**
 * Web页
 * Created by Administrator on 2015/7/27.
 */
public class WebActivity extends Activity implements View.OnClickListener {
    SwipeRefreshLayout sfl_refresh;
    WebView wv_web;
    ProgressBar web_progress;
    private ValueCallback mUploadMessage;
    final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //解决输入法挡住页面的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.activity_web);

        //页头
        findViewById(R.id.iv_goback).setOnClickListener(this);
        findViewById(R.id.iv_action).setVisibility(View.GONE);
        findViewById(R.id.tv_action).setVisibility(View.GONE);
        String title = getIntent().getStringExtra("form");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);

        MyApplication.activities.add(this);
        //初始化控件
        initview();
    }

    //初始化控件
    private void initview() {
        sfl_refresh = (SwipeRefreshLayout) findViewById(R.id.sfl_refresh);
        sfl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                wv_web.reload();
            }
        });

        web_progress = (ProgressBar) findViewById(R.id.web_progress);

        wv_web = (WebView) findViewById(R.id.wv_web);
        wv_web.setWebViewClient(new WebViewBaseClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent;
                if (url.contains("UploadPic")) {
                    intent = new Intent(WebActivity.this, UploadPhotosActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("form", "房源");
                    startActivity(intent);
                    finish();
                } else if (url.startsWith("sms")) {
                    String sms = url.split(":")[1];
                    Uri smsToUri = Uri.parse("smsto:" + sms);
                    intent = new Intent(
                            Intent.ACTION_SENDTO, smsToUri);
                    startActivity(intent);
                } else if (url.startsWith("tel")) {
                    String tel = url.split(":")[1];
                    //用intent启动拨打电话
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                    startActivity(intent);
                } else if (url.contains("share")) {
                    String share_content = url.split("share:")[1];
                    Utils.share(WebActivity.this, share_content);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                web_progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("#jump")) {
                    finish();
                } else {
                    super.onPageFinished(view, url);
                    web_progress.setVisibility(View.GONE);
                }
            }
        });

        wv_web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                web_progress.setProgress(newProgress);
                if (newProgress < 80) {
                    sfl_refresh.setRefreshing(false);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("提示")
                        .setMessage(message)
                        .setPositiveButton("确定", null);

                // 不需要绑定按键事件
                // 屏蔽keycode等于84之类的按键
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });
                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
                // return super.onJsAlert(view, url, message, result);
            }

//            @Override
//            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
//                builder.setTitle("提示");
//                builder.setPositiveButton("确定", null);
//                builder.setIcon(android.R.drawable.ic_dialog_info);
//                builder.setMessage(message);
//                builder.show();
//                return super.onJsConfirm(view, url, message, result);
//            }

            public void openFileChooser(ValueCallback uploadMsg, String acceptType, String capture) {
                if (acceptType == null && capture == null) {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                } else if (capture == null) {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
                } else {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                }
            }
        });
        WebSettings settings = wv_web.getSettings();
        settings.setJavaScriptEnabled(true);
        wv_web.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wv_web.canGoBack()) {
            wv_web.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_goback:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv_web.destroy();
        MyApplication.activities.remove(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

}
