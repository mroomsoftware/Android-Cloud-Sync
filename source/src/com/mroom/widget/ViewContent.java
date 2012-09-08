package com.mroom.widget;

import android.content.Context;
import android.widget.RelativeLayout;

public class ViewContent extends RelativeLayout {

	public ViewContent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public interface OnItemSelectionListener {
		public void onItemSelected(int action);
	}
}
