package com.gyzh.app.lingyun.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.gyzh.app.lingyun.views.CircleNetworkImageView;
import com.gyzh.app.lingyun.views.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * 个人信息
 * Created by Administrator on 2015/7/15.
 */
public class UserInfoActivity extends Activity implements View.OnClickListener {
    static final String TAG = UserInfoActivity.class.getName();
    CircleNetworkImageView iv_user_icon;
    LoadingDialog dialog;
    AlertDialog picDialog;
    private static final int START_FOR_CAMERA = 1;//相机的request code
    private static final int START_FOR_IMAGES = 2;//图库的request code
    private static final int START_FOR_CAT = 3;//剪切图的request code
    File dir;
    Button btn_quit;
    EditText et_nickname, et_phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userinfo);

        //解决输入法挡住页面的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        MyApplication.activities.add(this);

        dialog = new LoadingDialog(this);

        //页头
        findViewById(R.id.iv_goback).setOnClickListener(this);
        findViewById(R.id.iv_action).setVisibility(View.GONE);
        TextView tv_action = (TextView) findViewById(R.id.tv_action);
        tv_action.setText("保存");
        tv_action.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("修改个人资料");

        initViews();

        createDir();
    }

    private void initViews() {
        iv_user_icon = (CircleNetworkImageView) findViewById(R.id.iv_user_icon);
        iv_user_icon.setOnClickListener(this);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
        btn_quit = (Button) findViewById(R.id.btn_quit);
        btn_quit.setOnClickListener(this);
    }

    private void createDir() {
        dir = new File(Utils.initUserIcon());
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isLogin(UserInfoActivity.this)) {
            //头像
            String imgPath = Utils.getLoginValue(UserInfoActivity.this, "Logo").toString();
            iv_user_icon.setImageUrl(imgPath.startsWith("http") ? imgPath : Constant.URL_BASE + imgPath, MyApplication.getInstance().getImageLoader());
            et_nickname.setText(Utils.getLoginValue(UserInfoActivity.this, "Name").toString());
            et_phonenumber.setText(Utils.getLoginValue(UserInfoActivity.this, "PhoneNumber").toString());
        } else {
            Utils.showToast(UserInfoActivity.this, "你没有登录,请先登录!");
            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_goback:
                finish();
                break;

            case R.id.tv_action:
                dialog.show();

                String nickname = et_nickname.getText().toString();
                String phonenumber = et_phonenumber.getText().toString();

                HashMap<String, String> params = new HashMap<>();
                params.put("memberid", Utils.getLoginId(this) + "");
                params.put("name", nickname);
                params.put("phone", phonenumber);
                JsonObjectRequest request = new JsonObjectRequest(Utils.getUrl(Constant.URL_BASE + Constant.PLUS_URL_UPDATE_USERINFO, params), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {
                            if (jsonObject.getInt("Flag") == 1) {
                                Utils.showToast(UserInfoActivity.this, "保存成功!");
                                Utils.updateLoginValue(UserInfoActivity.this, "Name", jsonObject.getString("Name"));
                                Utils.updateLoginValue(UserInfoActivity.this, "PhoneNumber", jsonObject.getString("Phone"));
                                finish();
                            } else {
                                new AppDialog(UserInfoActivity.this, null, "保存失败!", "确定", null).show();
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
                        new AppDialog(UserInfoActivity.this, null, VolleyErrorHelper.getMessage(volleyError, UserInfoActivity.this), "确定", null).show();
                    }
                });
                MyApplication.getInstance().addToRequestQueue(request, TAG);
                break;

            case R.id.iv_user_icon:
                if (picDialog != null) {
                    picDialog.show();
                } else {
                    picDialog = new AlertDialog.Builder(this).setTitle("选择图片").setSingleChoiceItems(getResources().getStringArray(R.array.pic_source), 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (which == 0) {//相机
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 下面这句指定调用相机拍照后的照片存储的路径
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dir));
                                startActivityForResult(intent, START_FOR_CAMERA);
                            } else {//图库
                                Intent intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent, START_FOR_IMAGES);
                            }
                        }
                    }).create();
                    picDialog.show();
                }
                break;

            case R.id.btn_quit:
                SharedPreferences.Editor editor = getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE).edit();
                editor.putBoolean(Constant.SP_KEY_ISLOGIN, false);
                editor.putString(Constant.SP_KEU_USER_LOGIN_INFO, "");
                editor.commit();
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File tempFile = null;
        switch (requestCode) {
            //相机
            case START_FOR_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    tempFile = dir;
                    startPhotoZoom(Uri.fromFile(tempFile));
                }
                break;

            //图库
            case START_FOR_IMAGES:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    }
                }
                break;

            case START_FOR_CAT:
                /**
                 * 非空判断一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃 当前功能时，会报NullException
                 *
                 */
                try {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        tempFile = dir;
                        Bitmap photo = extras.getParcelable("data");
                        FileOutputStream out = new FileOutputStream(tempFile);
                        photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();

                        File file = dir;
                        FileInputStream inputStream = new FileInputStream(file);
                        byte[] buffer = new byte[(int) file.length() + 100];
                        int length = inputStream.read(buffer, 0, buffer.length);

                        HashMap<String, String> params = new HashMap<>();
                        int memberid = Utils.getLoginId(this);
                        params.put("memberid", memberid + "");
                        params.put("picstr", Base64.encodeToString(buffer, 0, length, Base64.DEFAULT));
                        UploadFileAsyncTask asyncTask = new UploadFileAsyncTask(Constant.URL_BASE + Constant.PLUS_URL_CHANGE_USER_ICON, params);
                        dialog.show();
                        asyncTask.execute("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, START_FOR_CAT);
    }

    private class UploadFileAsyncTask extends AsyncTask<String, Integer, String> {
        HashMap<String, String> map;
        String url;

        public UploadFileAsyncTask(String url, HashMap<String, String> map) {
            this.map = map;
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            return Utils.httpPost(url, map);
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("Flag") == 1) {
                        Utils.showToast(UserInfoActivity.this, "头像修改成功!");
                        String imgPath=object.getString("PicUrl");
                        Utils.updateLoginValue(UserInfoActivity.this, "Logo", imgPath);
                        iv_user_icon.setImageUrl(imgPath.startsWith("http") ? imgPath : Constant.URL_BASE + imgPath, MyApplication.getInstance().getImageLoader());
                    } else {
                        new AppDialog(UserInfoActivity.this, null, "上传失败.请重试", "确定", null).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                new AppDialog(UserInfoActivity.this, null, "上传失败.请重试", "确定", null).show();
            }
        }
    }

}
