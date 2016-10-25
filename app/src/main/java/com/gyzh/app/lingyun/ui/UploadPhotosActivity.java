package com.gyzh.app.lingyun.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.adapter.PhotosUpLoadAdapter;
import com.gyzh.app.lingyun.utils.Constant;
import com.gyzh.app.lingyun.utils.ImageUtil;
import com.gyzh.app.lingyun.utils.MyApplication;
import com.gyzh.app.lingyun.utils.Utils;
import com.gyzh.app.lingyun.views.AppDialog;
import com.gyzh.app.lingyun.views.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * 上传洗车图片
 * Created by Administrator on 2015/7/15.
 */
public class UploadPhotosActivity extends Activity implements View.OnClickListener {
    AlertDialog picDialog;
    private static final int START_FOR_CAMERA = 1;//相机的request code
    private static final int START_FOR_CAT = 3;//剪切图的request code
    public final static int REQUEST_CODE = 4;//相册
    ArrayList<Bitmap> upload_list;
    List<String> uploadString;
    GridView gv_upload_photos;
    String path_upload = "";
    LoadingDialog dialog;
    int total = 0;
    PhotosUpLoadAdapter upAdapter;
    int OrderState = 0;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_photos);

        MyApplication.activities.add(this);

        dialog = new LoadingDialog(this);

        //页头
        findViewById(R.id.iv_goback).setOnClickListener(this);
        findViewById(R.id.iv_action).setVisibility(View.GONE);
        TextView tv_commit = (TextView) findViewById(R.id.tv_action);
        tv_commit.setText("上传");
        tv_commit.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        String form = getIntent().getStringExtra("form");
        if (form.equals("开始")) {
            tv_title.setText("添加洗车前的汽车照片");
            OrderState = 0;
        } else if (form.equals("结束")) {
            tv_title.setText("添加洗车后的汽车照片");
            OrderState = 1;
        }

        //初始化控件
        initViews();

        Utils.showToast(UploadPhotosActivity.this, "长按图片可删除!");

        createDir();
    }

    private void createDir() {
        dir = new File(Utils.initCarPhoto());
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    //初始化控件
    void initViews() {
        //上传
        gv_upload_photos = (GridView) findViewById(R.id.gv_upload_photos);

        upload_list = new ArrayList<>();
        uploadString = new ArrayList<>();

        upAdapter = new PhotosUpLoadAdapter(UploadPhotosActivity.this, upload_list);
        gv_upload_photos.setAdapter(upAdapter);

        gv_upload_photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dir));
                startActivityForResult(intent, START_FOR_CAMERA);
//                if (position == upload_list.size()) {
//                    if (picDialog != null) {
//                        picDialog.show();
//                    } else {
//                        picDialog = new AlertDialog.Builder(UploadPhotosActivity.this).setTitle("选择图片").setSingleChoiceItems(getResources().getStringArray(R.array.pic_source), 0, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                Intent intent;
//                                if (which == 0) {//相机
//                                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                    // 下面这句指定调用相机拍照后的照片存储的路径
//                                    intent.putExtra(
//                                            MediaStore.EXTRA_OUTPUT,
//                                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Constant.DIR_BASE + "/" + Constant.DIR_IMG
//                                                    + "/car_photo" + ".jpg")));
//                                    startActivityForResult(intent, START_FOR_CAMERA);
//                                } else {//图库
//                                    PhotoPickerIntent photoPickerIntent = new PhotoPickerIntent(UploadPhotosActivity.this);
//                                    photoPickerIntent.setPhotoCount(8);
//                                    photoPickerIntent.setShowCamera(false);
//                                    startActivityForResult(photoPickerIntent, REQUEST_CODE);
//                                }
//                            }
//                        }).create();
//                        picDialog.show();
//                    }
//                }
            }
        });

        gv_upload_photos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadPhotosActivity.this);
                builder.setMessage("确认删除吗吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        upload_list.remove(position);
                        upAdapter.notifyDataSetChanged();
                        uploadString.remove(position);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_goback:
                finish();
                break;

            //提交
            case R.id.tv_action:
                total = upload_list.size();
                if (total > 8) {
                    Utils.showToast(UploadPhotosActivity.this, "最多能上传8张,请删掉" + (total - 8) + "张图片!");
                } else if (total < 6) {
                    Utils.showToast(UploadPhotosActivity.this, "最少要上传6张,请添加" + (6 - total) + "张图片!");
                } else {
                    UploadFileAsyncTask asyncTask;
                    //开始
                    for (int i = 0; i < uploadString.size(); i++) {
                        if (i == uploadString.size() - 1) {
                            path_upload += uploadString.get(i);
                        } else {
                            path_upload += uploadString.get(i) + ",";
                        }
                    }
                    if (path_upload.equals("")) {
                        Utils.showToast(UploadPhotosActivity.this, "您还没有添加汽车图片！");
                    } else {
                        String id = getIntent().getStringExtra("id");
                        HashMap<String, String> params = new HashMap<>();
                        params.put("orderid", id);
                        params.put("memberid", Utils.getLoginId(UploadPhotosActivity.this) + "");
                        params.put("picarr", path_upload);
                        if (OrderState == 0) {
                            asyncTask = new UploadFileAsyncTask(Constant.URL_BASE + Constant.PLUS_URL_WASH_BEGIN, params);
                            dialog.show();
                            asyncTask.execute("");
                        } else {
                            asyncTask = new UploadFileAsyncTask(Constant.URL_BASE + Constant.PLUS_URL_WASH_FINISH, params);
                            dialog.show();
                            asyncTask.execute("");
                        }
                    }
                }
                break;
        }
    }

    //实现上传操作
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
                        Utils.showToast(UploadPhotosActivity.this, "操作成功!");
                        finish();
                    } else {
                        new AppDialog(UploadPhotosActivity.this, null, object.getString("Error"), "确定", null).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                new AppDialog(UploadPhotosActivity.this, null, "上传失败.请重试", "确定", null).show();
            }
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

//                    Bitmap photo = ImageUtil.getBitmap(path);
//                    upload_list.add(photo);
//                    uploadString.add(bitmapToBase64(photo));
//                    upAdapter.notifyDataSetChanged();
                break;

            //相册
            case REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    List<String> all_path = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    for (int i = 0; i < all_path.size(); i++) {
                        String file_path = all_path.get(i);
                        Bitmap photo = ImageUtil.getBitmap(file_path);
                        upload_list.add(photo);
                        uploadString.add(bitmapToBase64(photo));
                    }
                    upAdapter.notifyDataSetChanged();
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

                        upload_list.add(photo);
                        uploadString.add(bitmapToBase64(photo));
                        upAdapter.notifyDataSetChanged();

//                        File file = dir;
//                        FileInputStream inputStream = new FileInputStream(file);
//                        byte[] buffer = new byte[(int) file.length() + 100];
//                        int length = inputStream.read(buffer, 0, buffer.length);
//
//                        HashMap<String, String> params = new HashMap<>();
//                        int memberid = Utils.getLoginId(this);
//                        params.put("memberid", memberid + "");
//                        params.put("picstr", Base64.encodeToString(buffer, 0, length, Base64.DEFAULT));
//                        UploadFileAsyncTask asyncTask = new UploadFileAsyncTask(Constant.URL_BASE + Constant.PLUS_URL_CHANGE_USER_ICON, params);
//                        dialog.show();
//                        asyncTask.execute("");
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

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
