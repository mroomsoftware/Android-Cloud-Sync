package com.mroom.widget;

import java.text.DecimalFormat;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.mroom.cloudapp.CloudComputing;
import com.mroom.cloudapp.R;
import com.mroom.request.PhotoData;
import com.mroom.request.TaskListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAccountView extends ViewContent {
	
	private static final String TAG = "MyAccountView";
	private OnItemSelectionListener listener;
	DropboxAPI<AndroidAuthSession> mApi;
	
	private TextView txtName, txtSpaceUsed, txtCountry, txtNoPhotos, txtNoVoices, txtNoNotes;
	DecimalFormat df = new DecimalFormat("###.##");
	Context mContext;
	
	public MyAccountView(Context context, DropboxAPI<AndroidAuthSession> mApi) {
		super(context);
		
		mContext = context;
		this.mApi = mApi;
		// TODO Auto-generated constructor stub
		initView();
	}

	public void initView() {
		try {
			View myView = LayoutInflater.from(getContext()).inflate(
					R.layout.myaccount, this, true);
			Button btnUnlink = (Button) myView.findViewById(R.id.btnUnlink);
			btnUnlink.setOnClickListener(btnUnlinkClicked);
			
			txtName = (TextView)myView.findViewById(R.id.txtAccountName);
			txtSpaceUsed = (TextView)myView.findViewById(R.id.txtSpaceUsed);
			txtCountry = (TextView)myView.findViewById(R.id.txtCountry);
			txtNoPhotos = (TextView)myView.findViewById(R.id.txtNoPhotos);
			txtNoVoices = (TextView)myView.findViewById(R.id.txtNoVoices);
			txtNoNotes = (TextView)myView.findViewById(R.id.txtNoNotes);
			
			// update data
			updateUserInfo();
			
			// get metadata
			getPhotosMetadata();
			getVoicesMetadata();
			getNotesMetadata();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	private void getPhotosMetadata() {
		new Thread() {
			public void run() {
				try {
					Entry photosEntry = mApi.metadata("/Photos", CloudComputing.nDownloadedFiles, null, true, null);
					((Activity)mContext).runOnUiThread(new TextViewpDisplayer(txtNoPhotos, String.valueOf(photosEntry.contents.size())));
				} catch (DropboxException e) {
					CloudComputing.i(TAG, e.toString());
				}
			}
		}.start();
	}
	
	private void getVoicesMetadata() {
		new Thread() {
			public void run() {
				try {
					Entry photosEntry = mApi.metadata("/Voices", CloudComputing.nDownloadedFiles, null, true, null);
					((Activity)mContext).runOnUiThread(new TextViewpDisplayer(txtNoVoices, String.valueOf(photosEntry.contents.size())));
				} catch (DropboxException e) {
					CloudComputing.i(TAG, e.toString());
				}
			}
		}.start();
	}
	
	private void getNotesMetadata() {
		new Thread() {
			public void run() {
				try {
					Entry photosEntry = mApi.metadata("/Notes", CloudComputing.nDownloadedFiles, null, true, null);
					((Activity)mContext).runOnUiThread(new TextViewpDisplayer(txtNoNotes, String.valueOf(photosEntry.contents.size())));
				} catch (DropboxException e) {
					CloudComputing.i(TAG, e.toString());
				}
			}
		}.start();
	}
	
	public void setOnItemSelectionListener(OnItemSelectionListener listener ) {
		this.listener = listener;
	}

	private View.OnClickListener btnUnlinkClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			callUnlink();
		}
	};
	
	private void callUnlink() {
		this.listener.onItemSelected(1);
	}
	
	private void updateUserInfo() {
		try {
			Account account = mApi.accountInfo();
			txtSpaceUsed.setText(df.format((((float)account.quotaNormal / account.quota) *100 + ((float)account.quotaShared / account.quota) *100)) + "% of "
					+ df.format((float)account.quota/(1024*1024*1024)) + "GB");
			txtName.setText(account.displayName);
			txtCountry.setText(account.country);
			
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class TextViewpDisplayer implements Runnable
    {
        TextView textView;
        String text;
        public TextViewpDisplayer(TextView t, String s){textView=t; text = s;}
        public void run()
        {
        	textView.setText(text);
        }
    }

}
