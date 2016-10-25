package com.gyzh.app.lingyun.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.gyzh.app.lingyun.R;


public class AppDialog extends Dialog {
	private TextView tv_title, tv_content;
	private TextView btn_confirm, btn_cancel;
	private DialogButtonOnClickListener dialogInterface;
	private String titleStr, contentStr, confirmStr, cancelStr;
	View view_vertical;

	public void setDialogInterface(DialogButtonOnClickListener dialogInterface) {
		this.dialogInterface = dialogInterface;
	}

	public AppDialog(Context context) {
		super(context);
	}

	public AppDialog(Context context, int them) {
		super(context, them);
	}

	public AppDialog(Context context, int theme, String titleStr, String contentStr, String confirmStr, String cancelStr) {
		super(context, theme);
		this.titleStr = titleStr;
		this.contentStr = contentStr;
		this.confirmStr = confirmStr;
		this.cancelStr = cancelStr;
	}

	public AppDialog(Context context, int theme, String titleStr, String contentStr, String confirmStr, String cancelStr,
					 DialogButtonOnClickListener dialogInterface) {
		super(context, theme);
		this.titleStr = titleStr;
		this.contentStr = contentStr;
		this.confirmStr = confirmStr;
		this.cancelStr = cancelStr;
		this.dialogInterface = dialogInterface;
	}

	public AppDialog(Context context, String titleStr, String contentStr, String confirmStr, String cancelStr,
					 DialogButtonOnClickListener dialogInterface) {
		super(context, R.style.myDialog);
		this.titleStr = titleStr;
		this.contentStr = contentStr;
		this.confirmStr = confirmStr;
		this.cancelStr = cancelStr;
		this.dialogInterface = dialogInterface;
	}

	public AppDialog(Context context, String titleStr, String contentStr, String confirmStr, DialogButtonOnClickListener dialogInterface) {
		super(context, R.style.myDialog);
		this.titleStr = titleStr;
		this.contentStr = contentStr;
		this.confirmStr = confirmStr;
		this.dialogInterface = dialogInterface;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_app_dialog);
		btn_confirm = (TextView) this.findViewById(R.id.tv_confirm);
		btn_cancel = (TextView) this.findViewById(R.id.tv_cancel);
		tv_content = (TextView) this.findViewById(R.id.tv_content);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		view_vertical = this.findViewById(R.id.view_vertical);
		tv_content.setText("\t\t" + contentStr);
		if (titleStr == null || titleStr.length() < 1) {
			tv_title.setVisibility(View.GONE);
			tv_content.setGravity(Gravity.CENTER);
		} else {
			tv_title.setText(titleStr);
		}
		if (cancelStr == null) {
			btn_cancel.setVisibility(View.GONE);
			view_vertical.setVisibility(View.GONE);
		} else {
			view_vertical.setVisibility(View.VISIBLE);
			btn_cancel.setText(cancelStr);

		}
		btn_confirm.setText(confirmStr);
		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppDialog.this.dismiss();
				if (dialogInterface != null) {
					dialogInterface.clickconfirm();
				}
			}

		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AppDialog.this.dismiss();
				if (dialogInterface != null) {
					dialogInterface.clickcancel();
				}
			}
		});
		this.setCanceledOnTouchOutside(true);
	}

	public interface DialogButtonOnClickListener {
		public void clickconfirm();

		public void clickcancel();
	}
}
