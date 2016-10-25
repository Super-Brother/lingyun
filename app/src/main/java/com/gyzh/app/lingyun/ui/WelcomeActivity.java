package com.gyzh.app.lingyun.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;

import java.io.File;

public class WelcomeActivity extends Activity {
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.bg_welcome);
        setContentView(imageView);

        MyApplication.activities.add(this);

        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(3000);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File sdcardDir = Environment.getExternalStorageDirectory();
                    String path_downloads = sdcardDir.getPath() + "/" + Constant.DIR_BASE + "/" + Constant.DIR_DOWNLOADS;
                    String path_images = sdcardDir.getPath() + "/" + Constant.DIR_BASE + "/" + Constant.DIR_IMG;
                    File dir_downloads = new File(path_downloads);
                    if (!dir_downloads.exists()) {
                        dir_downloads.mkdirs();
                    }
                    File dir_images = new File(path_images);
                    if (!dir_images.exists()) {
                        dir_images.mkdirs();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (sp.getInt(Constant.SP_KEY_OLD_VERSION, 0) != Utils.getAppVersionCode(WelcomeActivity.this)) {
                    Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                } else if (!sp.getBoolean(Constant.SP_KEY_ISLOGIN, false)) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            }
        });
        sp = getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE);
        imageView.setAnimation(animationSet);
        imageView.startAnimation(animationSet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.activities.remove(this);
    }

}
