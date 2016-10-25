package com.gyzh.app.lingyun.utils;

/**
 * Created by Administrator on 2015/7/13.
 */
public class Constant {
    public static final String URL_UPDATE = "http://down.palmapp.cn/getappinfo.aspx?appid=25";//更新接口

    public static final String URL_BASE = "http://27.50.145.117:9030";//URL_BASE
//    public static final String URL_BASE = "http://lingyun.dz.palmapp.cn/EmployeeInfo/";//URL_BASE
//    public static final String URL_BASE = "http://192.168.0.141/EmployeeInfo/";//URL_BASE

    public static final String PLUS_URL_LOGIN = "/EmployeeInfo/AjaxEmployee/memberlogin";//登录接口
    public static final String PLUS_URL_CHANGE_USER_ICON = "/EmployeeInfo/AjaxEmployee/ChangeAvatar";//更换头像
    public static final String PLUS_URL_GET_ORDER = "/EmployeeInfo/AjaxOrder/PageMemberOrder";//获取订单
    public static final String PLUS_URL_TURN_ORDER = "/EmployeeInfo/AjaxOrder/TurnOrder";//转单
    public static final String PLUS_URL_KILL_ORDER = "/EmployeeInfo/AjaxOrder/DispelOrder";//消单
    public static final String PLUS_URL_ORDER_GOTO = "/EmployeeInfo/AjaxOrder/InformGoto";//出发
    public static final String PLUS_URL_WASH_BEGIN = "/EmployeeInfo/AjaxOrder/BeginWash";//开始洗车
    public static final String PLUS_URL_WASH_FINISH = "/EmployeeInfo/AjaxOrder/FinishOrder";//结束洗车
    public static final String PLUS_URL_UPDATE_LOCATION = "/EmployeeInfo/AjaxEmployee/CoordinateUpdate";//更新位置
    public static final String PLUS_URL_GET_WORK_STATE = "/EmployeeInfo/AjaxEmployee/GetWordState";//获取工作状态
    public static final String PLUS_URL_UPDATE_WORK_STATE = "/EmployeeInfo/AjaxEmployee/WordState";//更改工作状态
    public static final String PLUS_URL_UPDATE_USERINFO = "/EmployeeInfo/AjaxEmployee/UpdateEmployeeInfo";//修改个人信息
    public static final String URL_ABOUT_US = "/CommonInfo/AjaxMessage/About";//关于我们
    public static final String URL_RULES_REGULATION = "/CommonInfo/AjaxMessage/Regulation";//规章制度
    public static final String URL_ORDER_DETAILS = "/MemberInfo/AjaxOrder/OrderDetail";//订单详情

    public static final String SP_CONFIG = "sp_config";
    public static final String SP_KEU_USER_LOGIN_INFO = "user_info";
    public static final String SP_KEY_ISLOGIN = "islogin";
    public static final String MATCHER_WENZI = "[\\u4E00-\\u9FFF]";
    public static final String SERVICE_UPDATE = "com.gyzh.app.lingyun.service.updateversionservice";

    public static final String DIR_BASE = "lingyun";
    public static final String DIR_DOWNLOADS = "download";
    public static final String DIR_IMG = "imgs";

    public static final String SP_KEY_OLD_VERSION = "old_version";

    public static final int PAGE_SIZE = 10;

    public static final String url_weather_left = "http://api.map.baidu.com/telematics/v3/weather?location=";
    public static final String url_weather_right = "&output=json&ak=sOuvx1NOn8akA46QSHctQS9G";

    //推送设备ID
    public static String CHANNEL_ID = "";//推送设备ID
    public static String USER_ID = "";
}
