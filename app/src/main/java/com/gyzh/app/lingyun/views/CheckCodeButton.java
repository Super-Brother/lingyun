package com.gyzh.app.lingyun.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;
import com.gyzh.app.lingyun.utils.Utils;

public class CheckCodeButton extends LinearLayout implements Runnable, View.OnClickListener {
    TextView tv_text, tv_timer;
    int valideTime = 60;
    String defaultStr = "获取验证码";
    String passedStr = "重新获取";
    long LAST_CLICK_TIMER;
    int HANDLER_UPDATE = 1;
    int HANDLER_FINISH = 2;
    EditText checkView;
    OnCheckCodeButtonClick onCheckCodeButtonClick;

    public void setOnSendCheckCodeButtonClick(OnCheckCodeButtonClick onCheckCodeButtonClick) {
        this.onCheckCodeButtonClick = onCheckCodeButtonClick;
    }

    public EditText getCheckView() {
        return checkView;
    }

    public void setCheckView(EditText checkView) {
        this.checkView = checkView;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HANDLER_UPDATE) {
                tv_timer.setText("(" + msg.arg1 + ")");
            } else if (msg.what == HANDLER_FINISH) {
                tv_timer.setVisibility(View.GONE);
                tv_timer.setTextColor(getResources().getColor(android.R.color.white));
                tv_text.setTextColor(getResources().getColor(android.R.color.white));
                setFocusable(true);
                setClickable(true);
            }
        }

    };

    public CheckCodeButton(Context context) {
        super(context);
        init(context);
    }

    public CheckCodeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_send_check_code, this);
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_timer.setVisibility(View.GONE);
        tv_timer.setText("(" + valideTime + ")");
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        System.out.println("点击了");
        String check = checkView.getText().toString();
        if (TextUtils.isEmpty(check)) {
            Utils.showToast(getContext(), "请输入手机号码");
            return;
        } else if (!check.matches("1[0-9]{10}")) {
            Utils.showToast(getContext(), "请输入正确的手机号码");
            return;
        } else {
            LAST_CLICK_TIMER = System.currentTimeMillis();
            this.setFocusable(false);
            this.setClickable(false);
            tv_timer.setVisibility(View.VISIBLE);
            tv_timer.setTextColor(getResources().getColor(R.color.text_gary));
            tv_text.setTextColor(getResources().getColor(R.color.text_gary));
            tv_text.setText(passedStr);
            if (onCheckCodeButtonClick != null) {
                onCheckCodeButtonClick.onClick();
            }
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        try {
            int timer = valideTime;
            while (timer-- > 0) {
                Thread.sleep(1000);
                Message message = new Message();
                message.what = HANDLER_UPDATE;
                message.arg1 = timer--;
                handler.sendMessage(message);
            }
            handler.sendEmptyMessage(HANDLER_FINISH);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface OnCheckCodeButtonClick {
        void onClick();
    }
}
