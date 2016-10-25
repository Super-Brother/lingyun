package com.gyzh.app.lingyun.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;


public class LoadingDialog extends Dialog {
	private TextView tv_context;

	public LoadingDialog(Context context) {
		super(context, R.style.NoTitle);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loding);
		tv_context = (TextView) findViewById(R.id.tv_progress);
		setCanceledOnTouchOutside(false);
	}

	public void setProgressText(String text) {
		tv_context.setText(text);
	}

	public void initProgressText() {
		tv_context.setText(R.string.progressbar_title);
	}
}
