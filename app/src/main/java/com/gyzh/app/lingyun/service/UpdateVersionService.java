package com.gyzh.app.lingyun.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.utils.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class UpdateVersionService extends Service implements Runnable {
    private Notification downLoadNotification;
    private NotificationManager notificationManager;
    private String downLoadUrl;
    public static final int FLAG_PROGRESS = 0, FLAG_FINISHED = 1;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FLAG_PROGRESS:
                    downLoadNotification.contentView.setTextViewText(R.id.tv_progress, msg.arg1 + "%");
                    downLoadNotification.contentView.setProgressBar(R.id.pb_progress, 100, msg.arg1, false);
                    notificationManager.notify(0, downLoadNotification);
                    break;

                case FLAG_FINISHED:
                    notificationManager.cancel(0);
                    Intent updateIntent = new Intent(Intent.ACTION_VIEW);
                    updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    updateIntent.setDataAndType(
                            Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/" + Constant.DIR_BASE + "/"
                                    + getString(R.string.app_name) + ".apk"), "application/vnd.android.package-archive");
                    startActivity(updateIntent);
                    /**
                     * RemoteViews contentView = downLoadNotification.contentView;
                     * contentView.setProgressBar(R.id.pb_progress, 100, 100,
                     * false); Intent updateIntent = new Intent(Intent.ACTION_VIEW);
                     * updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     * updateIntent.setDataAndType( Uri.parse("file://" +
                     * Environment.getExternalStorageDirectory() + "/" +
                     * Constant.dir_base + "/" + getString(R.string.app_name) +
                     * ".apk"), "application/vnd.android.package-archive");
                     * PendingIntent pendingIntent =
                     * PendingIntent.getActivity(getApplicationContext(), 0,
                     * updateIntent, 0); downLoadNotification.contentIntent =
                     * pendingIntent;
                     * downLoadNotification.contentView.setTextViewText
                     * (R.id.tv_progress, "下载完成.点击安装");
                     * downLoadNotification.contentView
                     * .setProgressBar(R.id.pb_progress, 100, 100, false);
                     * downLoadNotification.vibrate = new long[] { 1000, 500, 1500,
                     * -1 }; downLoadNotification.flags =
                     * Notification.FLAG_AUTO_CANCEL; notificationManager.notify(0,
                     * downLoadNotification);
                     * Toast.makeText(getApplicationContext(), "新版本已下载完成",
                     * Toast.LENGTH_SHORT).show();
                     */
                    // stopSelf();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("url")) {
            downLoadUrl = intent.getExtras().getString("url");
            if (TextUtils.isEmpty(downLoadUrl)) {
                stopSelf();
                return;
            }
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            downLoadNotification = new Notification();
            downLoadNotification.icon = R.mipmap.icon_logo_small;
            downLoadNotification.tickerText = "开始更新";
            downLoadNotification.flags = Notification.DEFAULT_SOUND;
            downLoadNotification.flags = Notification.FLAG_SHOW_LIGHTS;
            downLoadNotification.flags = Notification.FLAG_NO_CLEAR;
            downLoadNotification.contentView = new RemoteViews(getPackageName(), R.layout.view_notification_updateversion);
            notificationManager.notify(0, downLoadNotification);
            new Thread(this).start();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void run() {
        try {
            URL url = new URL(downLoadUrl);
            System.out.println("downLoadUrl: " + downLoadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Charser", "GBK,utf-8;q=0.7,*;q=0.3");
            conn.setRequestProperty("Referer", url.toString());
            InputStream inputStream = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory() + "/" + Constant.DIR_BASE + "/" + getString(R.string.app_name) + ".apk");
            long length = conn.getContentLength();
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            long downLoadSize = 0;
            int currentSize;
            long lastUpdateTime = System.currentTimeMillis();
            while ((currentSize = inputStream.read(buffer)) > 0) {
                downLoadSize += currentSize;
                long currentUpdateTime = System.currentTimeMillis();
                if (currentUpdateTime - lastUpdateTime > 100) {
                    Message msg = new Message();
                    msg.what = FLAG_PROGRESS;
                    msg.arg1 = (int) (((float) downLoadSize / length) * 100);
                    handler.sendMessage(msg);
                    lastUpdateTime = currentUpdateTime;
                }
                outputStream.write(buffer, 0, currentSize);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();
            conn.disconnect();
            handler.sendEmptyMessage(FLAG_FINISHED);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
