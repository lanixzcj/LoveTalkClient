package com.example.lovetalk.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


public abstract class MyAsyncTask extends AsyncTask<Void, Void, Void> {
	ProgressDialog dialog;
	protected Context context;
	boolean openDialog = true;
	Exception exception;

	protected MyAsyncTask(Context context) {
		this.context = context;
	}

	protected MyAsyncTask(Context context, boolean openDialog) {
		this.context = context;
		this.openDialog = openDialog;
	}

	public MyAsyncTask setOpenDialog(boolean openDialog) {
		this.openDialog = openDialog;
		return this;
	}

	public ProgressDialog getDialog() {
		return dialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (openDialog) {
			dialog = Utils.showSpinnerDialog((Activity) context);
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			doInBack();
		} catch (Exception e) {
			e.printStackTrace();
			exception = e;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		if (openDialog) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		onPost(exception);
	}

	protected abstract void doInBack() throws Exception;

	protected abstract void onPost(Exception e);
}
