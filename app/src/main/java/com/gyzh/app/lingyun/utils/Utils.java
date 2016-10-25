package com.gyzh.app.lingyun.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * Created by Administrator on 2015/7/13.
 */
public class Utils {

    public static final String TAG = "Utils";
    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    protected static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";
    public static String logStringCache = "", agentId;
    static int istask;

    /**
     * 检测是否有某个权限
     *
     * @param context
     * @param metaKey
     * @return
     */

    public static boolean isHavePermission(Context context, String content_permission) {
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(content_permission, "packageName"));
        if (permission) {
            Utils.showToast(context, "有这个权限");
        } else {
            Utils.showToast(context, "木有这个权限");
        }
        return permission;
    }

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }

    //JSONArray的remove方法
    public static JSONArray jsonArrayRemove(JSONArray array, int position) {
        JSONArray newArray = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            if (position != i) {
                try {
                    newArray.put(array.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return newArray;
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }

    public static String getLogText(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString("log_text", "");
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }


    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取6为验证码
     *
     * @return
     */
    public static String getCheckCode() {
        String code = (new Random().nextInt(900000) + 100000) + "";
        return code;
    }

    /**
     * 分享内容
     *
     * @param context
     * @param msg
     */
    public static void share(Context context, String msg) {
        Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
        intent.setType("text/plain"); // 分享发送的数据类型
        intent.putExtra(Intent.EXTRA_TEXT, msg); // 分享的内容
        context.startActivity(Intent.createChooser(intent, "选择分享"));// 目标应用选择对话框的标题
    }

//    public static String getSDPath() {
//        File sdDir = null;
//        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
//        if (sdCardExist) {
//            sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
//        }
//        if (sdDir != null) {
//            return sdDir.toString();
//        } else {
//            return null;
//        }
//    }

    /**
     * 获取一个文件夹里文件的总大小
     *
     * @return
     */
    public static float getDirSize(File file) {
        float size = 0;
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                for (File f : children)
                    size += getDirSize(f);
            } else {// 如果是文件则直接返回其大小,以“兆”为单位
                size = (float) file.length() / 1024 / 1024;
            }
        }
        return size;
    }

    /**
     * 删除一个目录下的所有文件
     *
     * @param file 删除目录
     */
    public static void deleteDir(File file) {
        if (file.isDirectory()) {
            for (File _file : file.listFiles()) {
                if (_file.isDirectory()) {
                    deleteDir(_file);
                } else {
                    _file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    /**
     * 判断用户是否登陆
     *
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        return context.getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE).getBoolean(Constant.SP_KEY_ISLOGIN, false);
    }

    /**
     * @param context
     * @param key     {"id":1,"msg":"登陆成功","UserInfo":[{"agentId":0,"agentstatus":0,"Id":3,"UserName":"18012345678",
     *                "UserLogo":"http://192.168.0.116/UploadFiles/images/3/201508031120475101.jpg",
     *                "UserNick":"","UserCard":"","UserStatus":0,"User_City":1,"User_Country":0,
     *                "User_Store":"","Add_Time":"2015/7/17 17:31:16","UserPhone":"18639577875",
     *                "UserRealname":"??","channel_id":"","Agent_Status":0,"istask":0}],"Agent":null}
     * @return
     */
    public static Object getLoginValue(Context context, String key) {
        String userInfo = context.getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE).getString(Constant.SP_KEU_USER_LOGIN_INFO, null);
        if (!TextUtils.isEmpty(userInfo)) {
            try {
                JSONObject object = new JSONObject(userInfo);
                if (object.has(key) && (object.get(key) != JSONObject.NULL)) {
                    return object.get(key);
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 修改登陆信息
     *
     * @param context
     * @param key
     * @param object
     */
    public static void updateLoginValue(Context context, String key, Object object) {
        SharedPreferences sp_config = context.getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE);
        String userInfo = sp_config.getString(Constant.SP_KEU_USER_LOGIN_INFO, null);
        if (!TextUtils.isEmpty(userInfo)) {
            try {
                JSONObject jsonObject = new JSONObject(userInfo);
                jsonObject.put(key, object);
                SharedPreferences.Editor editor = sp_config.edit();
                editor.putString(Constant.SP_KEU_USER_LOGIN_INFO, jsonObject.toString());
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getLoginId(Context context) {
        Object object = getLoginValue(context, "Id");
        if (object != null) {
            return (Integer) getLoginValue(context, "Id");
        }
        return -1;
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 图片按照宽高压缩
     *
     * @param image
     * @param width
     * @param height
     * @return
     */
    private static Bitmap comp(Bitmap image, int width, int height) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > width) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / width);
        } else if (w < h && h > height) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / height);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;// 降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 面向http协议上传<br>
     * 通过http协议提交数据到服务器,需要知道上传数据的总长度,实际提交的过程如下:<br>
     * ----<FORM METHOD=POST ACTION="" enctype="multipart/form-data"><br>
     * --------<INPUT TYPE="text" NAME="name"><br>
     * --------<INPUT TYPE="text" NAME="id"><br>
     * --------<input type="file" name="imagefile"/><br>
     * --------<input type="file" name="zip"/><br>
     * ----</FORM><br>
     * http协议文件中又去不结构
     *
     * @param actionUrl 上传路径,不要使用loaclhost或者127.0.0.1类似的地址,<br>
     *                  android模拟器会认为是其他模拟器的地址, 而不会做为计算机的地址来解析
     * @param params    请求的参数集合,key为参数名,value为参数值,存放基本数据
     * @param files     上传文集的集合,存放文件
     * @return
     * @throws IOException
     */
    public static String uploadFiles(String actionUrl, Map<String, String> params, Map<String, File> files) {
        try {
            String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";
            URL uri = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);
            conn.setRequestMethod("POST"); // Post方式
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
            // 首先组拼文本类型的参数
            OutputStream outStream = conn.getOutputStream();
            if (params != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINEND);
                    sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
                    sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                    sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                    sb.append(LINEND);
                    sb.append(entry.getValue());
                    sb.append(LINEND);
                }
                outStream.write(sb.toString().getBytes());
            }
            // 发送文件数据
            if (files != null)
                for (String fileName : files.keySet()) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());
                    InputStream is = new FileInputStream(files.get(fileName));
                    Bitmap bitmap = comp(BitmapFactory.decodeStream(is), 640, 960);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    byte[] outData = outputStream.toByteArray();
                    outStream.write(outData, 0, outData.length);

                    // byte[] buffer = new byte[1024];
                    // int len = 0;
                    // while ((len = is.read(buffer)) != -1) {
                    // outStream.write(buffer, 0, len);
                    // }
                    outputStream.close();
                    is.close();
                    outStream.write(LINEND.getBytes());
                }
            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            // 得到响应码
            int res = conn.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String temp = "";
            String result = "";
            while ((temp = reader.readLine()) != null) {
                result = result + temp;
            }
            System.out.println(res + "   " + result);
            outStream.close();
            conn.disconnect();
            if (res == 200) {
                return result;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 替换url中的参数
     *
     * @param baseUrl
     * @param params
     * @return
     */
    public static String getUrl(String baseUrl, HashMap<String, String> params) {

        String url = baseUrl;
        try {
            if (params == null || params.size() < 1) {
                return url;
            }
            String paramsStr = "";
            for (String key : params.keySet()) {
                String value = params.get(key);
                Pattern pattern = Pattern.compile(Constant.MATCHER_WENZI);
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    paramsStr = paramsStr + key + "=" + URLEncoder.encode(params.get(key), "UTF-8") + "&";
                } else {
                    paramsStr = paramsStr + key + "=" + params.get(key) + "&";
                }
            }
            if (url.contains("?")) {
                if (url.contains("=")) {
                    url = url + "&" + paramsStr;
                } else {
                    url = url + paramsStr;
                }
            } else {
                url = url + "?" + paramsStr;
            }
            url = url.substring(0, url.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public static Bitmap getRoundBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String img2Base64(InputStream inputStream) {
        return null;
    }

    public static HashMap<String, String> getSplitPrice(String s) {
        HashMap<String, String> priceMap = new HashMap<>();
        String reg = "[0-9]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(s);
        if (s.startsWith("<")) {
            priceMap.put("min", "0");
            priceMap.put("max", matcher.replaceAll("").trim());
        } else if (s.startsWith(">")) {
            priceMap.put("min", matcher.replaceAll("").trim());
            priceMap.put("max", "0");
        } else {
            matcher = pattern.matcher(s.split("-")[0]);
            priceMap.put("min", matcher.replaceAll("").trim());
            matcher = pattern.matcher(s.split("-")[1]);
            priceMap.put("max", matcher.replaceAll("").trim());
        }
        return priceMap;
    }


    /**
     * post请求方法获取数据
     *
     * @param str
     * @param params
     * @return
     */
    public static String httpPost(String str, HashMap<String, String> params) {
        HttpPost httpPost = new HttpPost(str); /* 建立HTTP Post连线 */
        List<NameValuePair> pairs = new ArrayList<NameValuePair>(); // Post运作传送变数必须用NameValuePair[]阵列储存
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                pairs.add(new BasicNameValuePair(key, params.get(key))); // 传参数,服务端获取的方法为request.getParameter("name")
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));// 把参数充填到请求里面
            // 取得HTTP response
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 初始化 用户图片
    public static String initUserIcon() {
        String path = ImageCache();
        File icon = new File(path, "user_icon.jpg");
        if (!icon.exists()) {
            try {
                icon.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("user_icon", icon.toString());
        return icon.toString();
    }

    // 初始化 用户图片
    public static String initCarPhoto() {
        String path = ImageCache();
        File icon = new File(path, "car_photo.jpg");
        if (!icon.exists()) {
            try {
                icon.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("user_icon", icon.toString());
        return icon.toString();
    }

    public static String ImageCache() {
        String path = getSDPath() + "/img";
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
        return file.toString();
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            File f = new File(sdDir.toString() + "/" + Constant.DIR_BASE);
            if (!f.exists())
                f.mkdir();
            Log.d("getSDPath", f.toString());
            return f.toString();
        } else {
            File F = Environment.getExternalStorageDirectory()
                    .getAbsoluteFile();
            String path = F.getPath() + "/" + Constant.DIR_BASE;
            File f = new File(path);
            if (!f.exists())
                f.mkdir();
            Log.d("path", f.toString());
            return f.toString();
        }
    }

    /**
     * 格式化/Date(-62135596800000)/
     */
    public static String LongDateToString(String longDate) {
        longDate = longDate.replace("/Date(", "").replace(")/", "");
        if (longDate.startsWith("-")) {
            longDate.replace("-", "");
        }
        long dateLong = Long.parseLong(longDate);
        Date date = new Date(dateLong);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = formatter.format(date);
        return dateStr;
    }

    /**
     * 判断是否是白天
     */
    public static boolean isDay() {
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = 7 * 60;// 起始时间 17:20的分钟数
        final int end = 19 * 60;// 结束时间 19:00的分钟数
        if (minuteOfDay >= start && minuteOfDay <= end) {
            return true;
        } else {
            return false;
        }
    }

}
